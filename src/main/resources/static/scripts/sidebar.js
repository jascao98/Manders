const SideBar = {
    errorNameMapping : {"Multi Polygon" : "MULTI_POLYGON", "Enclosed" : "ENCLOSED", "Overlap": "OVERLAP", "Unclosed": "UNCLOSED", "Zero Population": "ZERO_POPULATION", "Unproportional Election": "UNPROPORTIONAL_ELEC", "Ghost": "GHOST"},

    init: () => {
        SideBar.initSideBarButtons();
        SideBar.initErrors();
    },

    initSideBarButtons: () => {
        $("#collapse-menu-btn").click(() => {
            $("#mapid").toggleClass('showSide');
            $("#sidebar").toggleClass('showSide');
        });

        $('#submit-demo-data').click(() => {
            let popType = $('#popType')[0].value;
            let inputValue = $('#popValue')[0].value;

            if (inputValue != "" && LeafletMap.currentProps != null) {
                let demoID = LeafletMap.currentProps.demoData.demographicDataID;
                Object.entries(LeafletMap.currentProps.demoData).forEach(([key, ]) => {
                    if (key == popType) {
                        LeafletMap.currentProps.demoData[key] = parseInt(inputValue);
                    }
                });
                let precinctName = LeafletMap.currentProps.canonName;
                DataHandler.uploadDemoData(precinctName, demoID, popType, inputValue);
            }
        });

        $('#submit-elec-data').click(() => {
            let partyType = $('#partyType')[0].value;
            let inputValue = $('#partyValue')[0].value;

            if (inputValue != "" && LeafletMap.currentProps != null) {
                let elecID = null;
                let selectedElection = ToolBar.getSelectedElection();

                let elections = LeafletMap.currentProps.elecData.elections;
                for (let i = 0; i < elections.length; i++) {
                    if (elections[i].year == selectedElection.year && elections[i].type == selectedElection.type) {
                        Object.entries(elections[i]).forEach(([key, value]) => {
                            if (key.toLowerCase() == partyType.toLowerCase() + "votes") {
                                elecID = LeafletMap.currentProps.elecData.elections[i].electionId;
                                LeafletMap.currentProps.elecData.elections[i][key] = inputValue;
                            }
                        });
                    }
                }
                let precinctName = LeafletMap.currentProps.canonName;
                DataHandler.uploadElecData(precinctName, elecID, partyType + "Votes", inputValue);
            }
        });
    },

    initErrors: () => {
        DataHandler.getAllErrorData();
    },
    
    displaySelectedTypeErrors: () => {
        let selectElement = $('#error-type-selection')[0];
        let errorType = SideBar.getErrorName(selectElement.value);
        let precinctCName = $('#error-selection')[0].value;
        if (errorType == "GHOST" && LeafletMap.currentProps != null && precinctCName == LeafletMap.currentProps.canonName) {
            $('#verify').show();
        }
        else {
            $('#verify').hide();
        }
        let errorSelection = $('#error-selection').empty()[0];
        Object.entries(LeafletMap.errors).forEach(([key, value]) => {
            if (value.etype == errorType && value.affectedDistrict == LeafletMap.currentDistrict) {
                let opt = document.createElement('option');
                opt.value = value.affectedPrct;
                opt.innerHTML = value.precinctDisplayName;
                errorSelection.appendChild(opt);
            }
        });
        
    },

    getErrorName: (name) => {
        let errorName = null;
        Object.entries(SideBar.errorNameMapping).forEach(([key, value]) => {
            if (key == name) {
                errorName = value;
            }
        });
        return errorName;
    },

    findPrecinct: () => {
        let precinctCName = $('#error-selection')[0].value;
        let selectElement = $('#error-type-selection')[0];
        let errorType = SideBar.getErrorName(selectElement.value);
        let layers = LeafletMap.precinctLayer._layers;
        Object.entries(layers).forEach(([, value]) => {
            let cName = value.feature.properties.canonName;
            if (precinctCName == cName) {
                LeafletMap.map.fitBounds(value.getBounds());
                LeafletMap.highlightNeighbors(precinctCName);
                LeafletMap.highlightPrecinct(value, LeafletMap.highlightColors.blue, true);
                LeafletMap.changeInfoBoxName(value.feature.properties.displayName);
                if (errorType == "GHOST" && LeafletMap.currentProps != null && precinctCName == LeafletMap.currentProps.canonName) {
                    $('#verify').show();
                }
                else {
                    $('#verify').hide();
                }
            }
        });
    },

    errorResolved: () => {
        let precinctCName = $('#error-selection')[0].value;
        Object.entries(LeafletMap.errors).forEach(([key, value]) => {
            if (precinctCName == value.affectedPrct) {
                DataHandler.uploadErrorStatus(value.eid, true);
            }
        });
    },

    uploadNewName: () => {
        let newName = $('#new-name')[0].value;
        let precinctName = LeafletMap.currentProps.canonName;
        DataHandler.uploadNewName(precinctName, newName);
    },
}

SideBar.init();



