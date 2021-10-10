const LeafletMap = {
    
    modes: {default: 0, insert: 1, merge: 2, modify: 3, add: 4, remove: 5},
    currentMode: 0,
    stateLayer: null,
    districtLayer: null,
    precinctLayer: null,
    tempLayer: null,
    statesGeojson: [],
    districtGeojson: [],
    precinctGeojson: [],
    tempPrecinctGeojson: [],
    infoBox: L.control(),
    states: {},
    districts: {},
    precincts: {},
    errors: {},
    ghostCounter: 0,
    currentState: null,
    currentDistrict: null,
    currentPrecinct: null,
    precinctBeingChanged: null,
    selectedPrecincts: [],
    modifiedPrecincts: [],
    currentProps: null,
    usaCoordinates: [39.51073, -96.4247],

    highlightColors: {red: '#FF0000', blue: '#1a0aff', green: '#32CD32', pink: '#eb57ff'},
    // The iteractive map that is going to be displayed on the webpage
    map: L.map('mapid', { minZoom: 5, maxZoom: 18, maxBounds: [[20.396308, -135.848974], [49.384358, -55.885444]] }),

    init: () => {
        LeafletMap.map.setView(LeafletMap.usaCoordinates, 5);
        LeafletMap.initData();
        LeafletMap.initLeafletLayers();
        LeafletMap.initZoomHandlers();
        LeafletMap.initInfoBox();
        LeafletMap.initLayerHandlers();
    },

    initData: () => {
        DataHandler.getAllStateData();
    },

    initLeafletLayers: () => {
        L.tileLayer('https://api.mapbox.com/styles/v1/{id}/tiles/{z}/{x}/{y}?access_token={accessToken}', {
            attribution: 'Map data &copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a> contributors, <a href="https://creativecommons.org/licenses/by-sa/2.0/">CC-BY-SA</a>, Imagery © <a href="https://www.mapbox.com/">Mapbox</a>',
            id: 'mapbox/streets-v11',
            accessToken: 'pk.eyJ1IjoiZGFuZzk4IiwiYSI6ImNrNmlsbGZqNTAyYzgzZHFtcjczMmI2Z3EifQ.N6aBfLfiwLfyTn_Iz0TvIw'
        }).addTo(LeafletMap.map);
    },

    initZoomHandlers: () => {
        LeafletMap.map.on('zoomend', function (e) {
            // Display only the state borders when zoomed out too far from the districts
            let zoomLevel = LeafletMap.map.getZoom();
            if (zoomLevel <= 6) {
                LeafletMap.enableStateLayer(true);
                LeafletMap.enableDistrictLayer(false);
                LeafletMap.enablePrecinctLayer(false);
                ToolBar.enableAllFilters(true);
                LeafletMap.currentDistrict = null;
                LeafletMap.currentPrecinct = null;
            }    
            // Display only the district borders when zoomed out too far from the precincts
            else if (zoomLevel == 7 && LeafletMap.map.hasLayer(LeafletMap.precinctLayer)) {
                LeafletMap.enableStateLayer(false);
                LeafletMap.enableDistrictLayer(true);
                LeafletMap.enablePrecinctLayer(false);
                ToolBar.enableAllFilters(true);
                let filter = $('#district-filter')[0];
                filter.className = filter.className.replace(/active/g, "");
                LeafletMap.currentPrecinct = null;
            }  
            console.log("Current Zoom Level =" + zoomLevel)
         });
    },

    initInfoBox: () => {
        // Create an function that creates a information box in the top right corner and populates it with the given props
        LeafletMap.infoBox.update = (props) => {
            if (props != null) {
                let election = ToolBar.getSelectedElection();
                // Need to match the election data with the selected election 
                let index = 0;
                let data = props.elecData.elections;
                for (let i = 0; i < data.length; i++) {
                    if (data[i].year == election.year && data[i].type == election.type) {
                        index = i;
                        break;
                    }
                }
                Window._div.innerHTML = '<h4>U.S State Data</h4>' + (props ?
                    'Name: ' + props.displayName + '<br/>' + 
                    '<b>Demographic Data</b>' +
                    '<br/>Asian: ' + DataHandler.numberWithCommas(props.demoData.asianPop) +
                    '<br/> Black: ' + DataHandler.numberWithCommas(props.demoData.blackPop) + 
                    '<br/>White: ' + DataHandler.numberWithCommas(props.demoData.whitePop) +
                    '<br/>Other: ' + DataHandler.numberWithCommas(props.demoData.otherPop) + '<br/>' +
                    '<b>Electon Data</b>' +
                    '<br/>Democratic: ' + DataHandler.numberWithCommas(props.elecData.elections[index].democraticVotes) +
                    '<br/>Republican: ' + DataHandler.numberWithCommas(props.elecData.elections[index].republicanVotes) +
                    '<br/>Libertarian: ' + DataHandler.numberWithCommas(props.elecData.elections[index].libertarianVotes) +
                    '<br/>Green: ' + DataHandler.numberWithCommas(props.elecData.elections[index].greenVotes) +
                    '<br/>Other: ' + DataHandler.numberWithCommas(props.elecData.elections[index].otherVotes) 
                    : '');

                //  '<b>' + props.name + '</b><br />' + props.density + ' people / mi<sup>2</sup>'
                //  : 'Hover states for more details');
            }
        };
        LeafletMap.infoBox.onAdd = () => {
            Window._div = L.DomUtil.create('div', 'info');
            LeafletMap.infoBox.update();
            return Window._div;
        };
        LeafletMap.infoBox.addTo(LeafletMap.map);

    },

    initLayerHandlers: () => {
        LeafletMap.map.on('pm:create', e => {
            LeafletMap.map.removeLayer(e.layer);
            let precinctShape = e.shape;
            let newPrecinctCoordinates = [];
            let coordinatesList = e.layer._latlngs[0];
            for (let i = 0; i < coordinatesList.length; i++) {
                newPrecinctCoordinates.push([coordinatesList[i].lng, coordinatesList[i].lat]);
            };
            newPrecinctCoordinates.push([coordinatesList[0].lng, coordinatesList[0].lat]);
            LeafletMap.tempPrecinctGeojson.push( 
            {
                "type": "Feature",
                "properties": 
                {
                    'district': LeafletMap.currentDistrict,
                    'canonName': "ghost_" + LeafletMap.ghostCounter,
                    "displayName": "Ghost Precinct",
                },
                "geometry": 
                {
                    "type": precinctShape,
                    "coordinates": [newPrecinctCoordinates]
                }
            });
            LeafletMap.updateTempLayer();
            LeafletMap.map.pm.enableDraw('Polygon');
        });
    },

    onEachFeature: (feature, layer) => {
        layer.on({
            //mouseover: LeafletMap.highlightFeature,
            //mouseout: LeafletMap.resetHighlight,
            click: LeafletMap.onClickHandler
        });
    },
  
    highlightFeature: (event, hexColor, bringToFront) => {
        let layer = event.target;
        layer.setStyle({
            weight: 4,
            color: hexColor,
            dashArray: '',
            fillOpacity: 0.2
        });
        if (bringToFront) {
            layer.bringToFront();  
        }
        else {
            layer.bringToBack();
        }
        LeafletMap.currentProps = layer.feature.properties;
        LeafletMap.infoBox.update(layer.feature.properties);
    },

    highlightPrecinct: (layer, hexColor, bringToFront) => {
        layer.setStyle({
            weight: 4,
            color: hexColor,
            dashArray: '',
            fillOpacity: 0.2
        });
        if (bringToFront) {
            layer.bringToFront();  
        }
        else {
            layer.bringToBack();
        }
        LeafletMap.currentProps = layer.feature.properties;
        LeafletMap.infoBox.update(layer.feature.properties);
    },

    highlightNeighbors: (precinctCName) => {
        LeafletMap.precinctLayer.resetStyle();
        let neighborCNames = LeafletMap.precincts[precinctCName].neighborCNames;
        let layers = LeafletMap.precinctLayer._layers;
        Object.entries(layers).forEach(([, value]) => {
            let cName = value.feature.properties.canonName;
            if (neighborCNames.indexOf(cName) != -1) {
                value.setStyle({
                    weight: 4,
                    color: LeafletMap.highlightColors.red,
                    dashArray: '',
                    fillOpacity: 0.2
                });
            }
        });
    },

    resetHighlight: (event) => {
        LeafletMap.currentProps = event.target.feature.properties
        if (LeafletMap.map.hasLayer(LeafletMap.stateLayer)) {
            LeafletMap.stateLayer.resetStyle(event.target);
            LeafletMap.infoBox.update(event.target.feature.properties);
        }
        else if (LeafletMap.map.hasLayer(LeafletMap.precinctLayer)) {
            LeafletMap.precinctLayer.resetStyle(event.target);
            LeafletMap.infoBox.update(event.target.feature.properties);
        }
        else if (LeafletMap.map.hasLayer(LeafletMap.districtLayer)) {
            LeafletMap.districtLayer.resetStyle(event.target);
            LeafletMap.infoBox.update(event.target.feature.properties);
        }
    },

    togglePrecinctSelection: (event) => {
        if (LeafletMap.selectedPrecincts.indexOf(LeafletMap.currentPrecinct) === -1) {
            LeafletMap.highlightFeature(event, LeafletMap.highlightColors.green, false);
            LeafletMap.selectedPrecincts.push(LeafletMap.currentPrecinct);
        }
        else {
            LeafletMap.precinctLayer.resetStyle(event.target);
            let index = LeafletMap.selectedPrecincts.indexOf(LeafletMap.currentPrecinct);
            LeafletMap.selectedPrecincts.splice(index, 1);
        }
    },

    allowPrecinctModification: (event) => {
        LeafletMap.precinctLayer.pm.disable();
        event.target.pm.enable();
        LeafletMap.precinctLayer.on('pm:edit', e => {
            let canonName = e.sourceTarget.feature.properties.canonName;
            let index = LeafletMap.modifiedPrecincts.indexOf(canonName);
            // indexOf() returns -1 if the element is not found
            if (index === -1) {
                LeafletMap.modifiedPrecincts.push(canonName);
            }
        });
    },

    onClickHandler: (event) => {
        LeafletMap.currentProps = event.target.feature.properties;
        LeafletMap.infoBox.update(event.target.feature.properties);
        const canonicalName = event.target.feature.properties.canonName;
        // The clicked layer is a state layer
        if (LeafletMap.states[canonicalName] != null) {
            LeafletMap.currentState = canonicalName;
            // Responsible for updating toolbar GUI and updating districtGeoson
            LeafletMap.stateLayerHandler(canonicalName);
            LeafletMap.enableStateLayer(false)
            ToolBar.enableAllFilters(true);
            // Disable district filter 
            let filter = $('#district-filter')[0];
            filter.className = filter.className.replace(/active/g, "");
            LeafletMap.zoomToFeature(event);
            SideBar.displaySelectedTypeErrors();
        }
        // The clicked layer is a district
        else if (LeafletMap.districts[canonicalName] != null) {
            LeafletMap.currentDistrict = canonicalName;
            // Responsible for updating precinctGeoson
            LeafletMap.districtLayerHandler(canonicalName);
            LeafletMap.enableDistrictLayer(false);
            LeafletMap.enablePrecinctLayer(true);
            ToolBar.enableAllFilters(true);
            // Disable precinct filter 
            let filter = $('#precinct-filter')[0];
            filter.className = filter.className.replace(/active/g, "");
            LeafletMap.zoomToFeature(event);
            SideBar.displaySelectedTypeErrors();
        }
          // The clicked layer is a precinct
        else if (LeafletMap.precincts[canonicalName] != null) {
            LeafletMap.currentPrecinct = canonicalName;
            LeafletMap.precinctLayerHandler(canonicalName, event);
        }
        LeafletMap.changeInfoBoxName(event.target.feature.properties.displayName);
    },

    stateLayerHandler: (stateCanonName) => {
        // Update the states dropdown menu UI
        const statesDropdownElements = $('#states').find(".dropdown-item");
        for (let i = 0; i < statesDropdownElements.length; i++) {
            if (statesDropdownElements[i].text.toLowerCase().replace(" ", "") == stateCanonName) {
                ToolBar.unselectState();
                statesDropdownElements[i].className += " active";
                DataHandler.getAllDistrictData(stateCanonName);
                break;
            }
        }
    },

    districtLayerHandler: (districtCanonName) => {
        DataHandler.getAllPrecinctData(districtCanonName);
        LeafletMap.updatePrecinctLayer();
    },

    precinctLayerHandler: (precinctCanonName, event) => {
        switch(LeafletMap.currentMode) {
            case LeafletMap.modes.default: {
                LeafletMap.highlightNeighbors(precinctCanonName);
                LeafletMap.highlightFeature(event, LeafletMap.highlightColors.blue, true);
                break;
            }
            case LeafletMap.modes.merge: {
                LeafletMap.togglePrecinctSelection(event);
                break;
            }
            case LeafletMap.modes.modify: {
                LeafletMap.allowPrecinctModification(event);
                break;
            }
            case LeafletMap.modes.add: {
                
                let neighborCNames = LeafletMap.precincts[LeafletMap.precinctBeingChanged].neighborCNames;
                // Only allow the precinct to be selected if it is not a neighbor already and it is not the precinct being changed
                if (neighborCNames.indexOf(LeafletMap.currentPrecinct) === -1 && LeafletMap.currentPrecinct != LeafletMap.precinctBeingChanged) {
                    LeafletMap.togglePrecinctSelection(event);
                }
                break;
            }
            case LeafletMap.modes.remove: {
                let neighborCNames = LeafletMap.precincts[LeafletMap.precinctBeingChanged].neighborCNames;
                if(neighborCNames.indexOf(LeafletMap.currentPrecinct != -1) && LeafletMap.currentPrecinct != LeafletMap.precinctBeingChanged) {
                    LeafletMap.togglePrecinctSelection(event);
                }
            }
        }
    },

    zoomToFeature: (e) => {
        LeafletMap.map.fitBounds(e.target.getBounds());
    },

    panMap: (lat, long, zoom) => {
        LeafletMap.map.setView([lat, long], zoom);
    },
    
    enableStateLayer: (option) => {
        switch(option) {
            case true:
                if (!LeafletMap.map.hasLayer(LeafletMap.stateLayer)) {
                    LeafletMap.stateLayer = L.geoJson(LeafletMap.statesGeojson, { 
                        onEachFeature: LeafletMap.onEachFeature 
                    }).addTo(LeafletMap.map); }
                break;
            case false:
                if (LeafletMap.map.hasLayer(LeafletMap.stateLayer)) { 
                    LeafletMap.map.removeLayer(LeafletMap.stateLayer); }
                break;
        }
    },

    enableDistrictLayer: (option) => {
        switch(option) {
            case true:
                if (!LeafletMap.map.hasLayer(LeafletMap.districtLayer)) { 
                    LeafletMap.districtLayer = L.geoJson(LeafletMap.districtGeojson, {
                        onEachFeature: LeafletMap.onEachFeature 
                    }).addTo(LeafletMap.map).bringToFront(); 
                    LeafletMap.districtLayer.setStyle({
                        color: LeafletMap.highlightColors.pink
                    });
                }
                    
                break;
            case false:
                if (LeafletMap.map.hasLayer(LeafletMap.districtLayer)) { 
                    LeafletMap.map.removeLayer(LeafletMap.districtLayer); 
                }
                break;
        }
    },

    enablePrecinctLayer: (option) => {
        switch(option) {
            case true:
                if (!LeafletMap.map.hasLayer(LeafletMap.precinctLayer)) {
                    LeafletMap.precinctLayer = L.geoJson(LeafletMap.precinctGeojson, {
                         onEachFeature: LeafletMap.onEachFeature 
                    }).addTo(LeafletMap.map);
                    LeafletMap.precinctLayer.bringToFront(); }
                if (!LeafletMap.map.hasLayer(LeafletMap.tempLayer)) {
                    LeafletMap.tempLayer = L.geoJson(LeafletMap.tempPrecinctGeojson, {
                        onEachFeature: LeafletMap.onEachFeature 
                    }).addTo(LeafletMap.map); }
                break;
                
            case false:
                if (LeafletMap.map.hasLayer(LeafletMap.precinctLayer)) {
                    LeafletMap.map.removeLayer(LeafletMap.precinctLayer); }
                if (LeafletMap.map.hasLayer(LeafletMap.tempLayer)) {
                    LeafletMap.map.removeLayer(LeafletMap.tempLayer); }
                break;
        }
    },

    updateTempLayer: () => {
        if (LeafletMap.map.hasLayer(LeafletMap.tempLayer)) { 
            LeafletMap.map.removeLayer(LeafletMap.tempLayer);
        }
        LeafletMap.tempLayer = L.geoJson(LeafletMap.tempPrecinctGeojson,{ 
            onEachFeature: LeafletMap.onEachFeature 
        }).addTo(LeafletMap.map);
    },

    updatePrecinctLayer: () => {
        if (LeafletMap.map.hasLayer(LeafletMap.precinctLayer)) { 
            LeafletMap.map.removeLayer(LeafletMap.precinctLayer); 
        }
        LeafletMap.precinctLayer = L.geoJson(LeafletMap.precinctGeojson, {
            onEachFeature: LeafletMap.onEachFeature 
        }).addTo(LeafletMap.map);
    },

    updateDistrictLayer: () => {
        if (LeafletMap.map.hasLayer(LeafletMap.districtLayer)) { 
            LeafletMap.map.removeLayer(LeafletMap.districtLayer); 
        }
        LeafletMap.districtLayer = L.geoJson(LeafletMap.districtGeojson, {
            onEachFeature: LeafletMap.onEachFeature
        }).addTo(LeafletMap.map);
        LeafletMap.districtLayer.setStyle({
            color: LeafletMap.highlightColors.pink
        });
    },

    resetMapFunctionalities: () => {
        // Reset all temp variables and revert back to normal map functionality
        switch(LeafletMap.currentMode) {
            case LeafletMap.modes.insert:
                // Need to remove all tempJson precincts from map and empty tempPrecinctGeojson
                if (LeafletMap.map.hasLayer(LeafletMap.tempLayer)) { 
                    LeafletMap.map.removeLayer(LeafletMap.tempLayer);
                }
                LeafletMap.tempPrecinctGeojson = [];
                LeafletMap.tempLayer = null;
                LeafletMap.updatePrecinctLayer();
                LeafletMap.map.pm.disableDraw();
                break;
            case LeafletMap.modes.modify:
                LeafletMap.modifiedPrecincts = [];
                LeafletMap.updatePrecinctLayer();
                break;
            case LeafletMap.modes.merge:
                LeafletMap.selectedPrecincts = [];
                LeafletMap.updatePrecinctLayer();
                break;
            case LeafletMap.modes.add:
                LeafletMap.selectedPrecincts = [];
                LeafletMap.precinctBeingChanged = null;
                LeafletMap.updatePrecinctLayer();
                break;
            case LeafletMap.modes.remove:
                LeafletMap.selectedPrecincts = [];
                LeafletMap.precinctBeingChanged = null;
                LeafletMap.updatePrecinctLayer();
            default:
                console.log("INVALID CURRENT MODE");
        }
        LeafletMap.currentPrecinct = null;
        LeafletMap.currentMode = LeafletMap.modes.default;
        ToolBar.toggleEditButtons();
    },

    changeInfoBoxName: (stateName) => {
        $("#state-name").text(stateName);
    }
}
LeafletMap.init();