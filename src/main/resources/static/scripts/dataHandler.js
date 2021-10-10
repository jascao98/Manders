const DataHandler = {
    options: {addNeighbor: 'add', removeNeighbor: 'delete'},

    getAllStateData: () => {
        fetch('/state/getAllStates').then((response) => {
            return response.text();
        }).then((text) => {
            let serverData = JSON.parse(text);
            for (let i = 0; i < serverData.length; i++) {
                let canonName = serverData[i].canonName;
                LeafletMap.states[canonName] = serverData[i];
                LeafletMap.statesGeojson.push(JsonHandler.convertToGeojson(serverData[i]));
            }
            LeafletMap.stateLayer = L.geoJson(LeafletMap.statesGeojson, {
                onEachFeature: LeafletMap.onEachFeature
            }).addTo(LeafletMap.map);
        });
    },

    getAllDistrictData: (stateCName) => {
        fetch(`/state/${stateCName}/districts`).then((response) => {
            return response.text();
        }).then((text) => {
            LeafletMap.districtGeojson = [];
            let serverData  = JSON.parse(text);
            for (let i = 0; i < serverData.length; i++) {
                let canonName = serverData[i].canonName;
                LeafletMap.districts[canonName] = serverData[i];
                LeafletMap.districtGeojson.push(JsonHandler.convertToGeojson(serverData[i]));
            }
            LeafletMap.updateDistrictLayer();
        });
    },

    getAllPrecinctData: (districtCName) => {
        fetch(`/district/${districtCName}/precincts`).then((response) => {
            return response.text();
        }).then((text) => {
            LeafletMap.precinctGeojson = [];
            let serverData  = JSON.parse(text);
            for (let i = 0; i < serverData.length; i++) {
                let canonName = serverData[i].canonName;
                LeafletMap.precincts[canonName] = serverData[i];
                LeafletMap.precinctGeojson.push(JsonHandler.convertToGeojson(serverData[i]));
            }
            LeafletMap.updatePrecinctLayer();
        });
    },

    getAllErrorData: () => {
        fetch('/error/unresolved').then((response) => {
            return response.text();
        }).then((text) => {
            LeafletMap.errors = {};
            let errorData = JSON.parse(text);
            for (let i = 0; i < errorData.length; i++) {
                let errorID = errorData[i].eid;
                LeafletMap.errors[errorID] = errorData[i];
            }
            SideBar.displaySelectedTypeErrors();
        });
    },

    getDistrictData: (districtList) => {
        if (districtList != null) {
            let postTemplate = {
                method: 'post',
                headers: {
                    "Content-type": "application/json; charset=UTF-8"
                },
                body: JSON.stringify(districtList)
            }
            fetch('/district/getMultipleDistricts', postTemplate).then((response) => {
                return response.text();
            }).then((text) => {
                let serverData  = JSON.parse(text);
                for (let i = 0; i < serverData.length; i++) {
                    let canonName = serverData[i].canonName;
                    LeafletMap.districts[canonName] = serverData[i];
                    LeafletMap.districtGeojson.push(JsonHandler.convertToGeojson(serverData[i]));
                }
                LeafletMap.updateDistrictLayer();
            });
        }
    },
   
    getPrecinctData: (precinctList) => {
        if (precinctList != null) {
            let postTemplate = {
                method: 'post',
                headers: {
                    "Content-type": "application/json; charset=UTF-8"
                },
                body: JSON.stringify(precinctList)
            }
            fetch('/precinct/getMultiplePrecincts', postTemplate).then((response) => {
                return response.text();
            }).then((text) => {
                let serverData = JSON.parse(text);
                for (let i = 0; i < serverData.length; i++) {
                    let canonName = serverData[i].canonName;
                    LeafletMap.precincts[canonName] = serverData[i];
                    LeafletMap.precinctGeojson.push(JsonHandler.convertToGeojson(serverData[i]));
                }
                LeafletMap.updatePrecinctLayer();
            });
        }
    },

    changePrecinctNeighbor: (precinctName, precinctNeighbors, option) => {
        let postTemplate = {
            method: 'post',
            headers: {
                "Content-type": "application/json; charset=UTF-8"
            },
            body: JSON.stringify(precinctNeighbors)
        }
        fetch('/precinct/modifyNeighbor?p=' + precinctName + '&op=' + option, postTemplate).then((response) => {
            if(response.ok) {
                // Request the updated precincts
                DataHandler.getAllPrecinctData(LeafletMap.currentDistrict);
            }
        })
    },

    mergePrecincts: (precinctList) => {
        let postTemplate = {
            method: 'post',
            headers: {
                "Content-type": "application/json; charset=UTF-8"
            },
            body: JSON.stringify(precinctList)
        }
        fetch('/precinct/mergePrecinct', postTemplate).then((response) => {
            if(!response.ok) {
                alert("Invalid Merge! Result would be a MultiPolygon")
            }
            return response.text();
        }).then(() => {
            DataHandler.getAllPrecinctData(LeafletMap.currentDistrict); 
        });
    },

    uploadPrecinctName: (precinctCName, newVal) => {
    },

    uploadPrecinctBoundary: (precinctCName, geometry) => {
        let postTemplate = {
            method: 'post',
            headers: {
                "Content-type": "application/json; charset=UTF-8"
            },
            body: JSON.stringify(geometry)
        }
        fetch('/precinct/updateBoundary?pCName=' + precinctCName, postTemplate).then((response) => {
            if (!response.ok) {
                alert("Invalid Geometry!");
            }
            return response.text();
        }).then(() => {
            DataHandler.getAllPrecinctData(LeafletMap.currentDistrict);
        })
    },

    uploadErrorStatus: (errorID, status) => {
        fetch(`error/setErrorStatus/${errorID}?resolved=true`).then((response) => {
            return response.text();
        }).then(() => {
            DataHandler.getAllErrorData();
        });
    },

    uploadDemoData: (precinctName, id, field, newVal) => {
        fetch(`precinct/${precinctName}/updateDemoData/${id}/${field}/${newVal}`).then((response) => {
            return response.text();
        }).then(() => {
            LeafletMap.infoBox.update(LeafletMap.currentProps);
        })
    },

    uploadElecData: (precinctName, id, field, newVal) => {
        fetch(`precinct/${precinctName}/updateElecData/${id}/${field}/${newVal}`).then((response) => {
            return response.text();
        }).then(() => {
            LeafletMap.infoBox.update(LeafletMap.currentProps);
        })
    },

    uploadNewPrecinct: (displayName, geometry) => {
        let postTemplate = {
            method: 'post',
            headers: {
                "Content-type": "application/json; charset=UTF-8"
            },
            body: JSON.stringify({"displayName": displayName, "geometry" : JSON.stringify(geometry)})
        }
        fetch(`/precinct/newPrecinct/${LeafletMap.currentDistrict}`, postTemplate).then((response) => {
            if (!response.ok) {
                alert("Invalid Geometry!");
            }
            return response.text();
        }).then(() => {
            DataHandler.getAllPrecinctData(LeafletMap.currentDistrict);
        });
    },

    uploadNewName: (precinctName, newName)  => {
        fetch(`precinct/${precinctName}/rename/${newName}`).then((response) => {
            return response.text();
        }).then(() => {
            DataHandler.getAllPrecinctData(LeafletMap.currentDistrict);
            setTimeout(() => {
                let precinctCName = LeafletMap.currentProps.canonName;
                let layers = LeafletMap.precinctLayer._layers;
                Object.entries(layers).forEach(([, value]) => {
                    let cName = value.feature.properties.canonName;
                    if (precinctCName == cName) {
                        LeafletMap.highlightNeighbors(precinctCName);
                        LeafletMap.highlightPrecinct(value, LeafletMap.highlightColors.blue, true);
                        LeafletMap.currentProps = LeafletMap.precincts[LeafletMap.currentProps.canonName];
                        LeafletMap.infoBox.update(LeafletMap.currentProps);
                        LeafletMap.changeInfoBoxName(LeafletMap.currentProps.displayName);
                    }
                });

            },1000);
        })
    },

    verifyGhostPrecinct: () => {
        let precinctName = LeafletMap.currentProps.canonName
        fetch(`/precinct/${precinctName}/setGhost`).then((response) => {
            return response.text();
        }).then(() => {
            DataHandler.getAllPrecinctData(LeafletMap.currentDistrict);
            setTimeout(() => {
                let precinctCName = LeafletMap.currentProps.canonName;
                let layers = LeafletMap.precinctLayer._layers;
                Object.entries(layers).forEach(([, value]) => {
                    let cName = value.feature.properties.canonName;
                    if (precinctCName == cName) {
                        LeafletMap.highlightNeighbors(precinctCName);
                        LeafletMap.highlightPrecinct(value, LeafletMap.highlightColors.blue, true);
                        LeafletMap.currentProps = LeafletMap.precincts[LeafletMap.currentProps.canonName];
                        LeafletMap.infoBox.update(LeafletMap.currentProps);
                        LeafletMap.changeInfoBoxName(LeafletMap.currentProps.displayName);
                    }
                });

            },1000);
        })
    },

    updatePrecinctData: () => {
        switch(LeafletMap.currentMode) {
            case LeafletMap.modes.insert: {
                // Add all the new precincts to the current list of precincts
                for (let i = 0; i < LeafletMap.tempPrecinctGeojson.length; i++) {
                    DataHandler.uploadNewPrecinct(LeafletMap.tempPrecinctGeojson[i].properties.displayName, LeafletMap.tempPrecinctGeojson[i].geometry);
                }
                break;
            }
            case LeafletMap.modes.merge: {
                DataHandler.mergePrecincts(LeafletMap.selectedPrecincts);
                break;
            }
            case LeafletMap.modes.modify: {
                // Replace precinctCoordinates with the new ones from the precinctLayer 
                // Replace only the precincts that were changed
                DataHandler.replacePrecinctCoordinates();
                break;
            }
            case LeafletMap.modes.add: {
                DataHandler.updateNeighbors(DataHandler.options.addNeighbor);
                break;        
            }
            case LeafletMap.modes.remove: {
                DataHandler.updateNeighbors(DataHandler.options.removeNeighbor);
            }
            default:
                console.log("INVALID CURRENT MODE");
        }
    },

    replacePrecinctCoordinates: () => {
        DataHandler.updatePrecinctGeojson();
        for (let i in LeafletMap.precinctLayer._layers) {
            let precinctName = LeafletMap.precinctLayer._layers[i].feature.properties.canonName;
            if (LeafletMap.modifiedPrecincts.indexOf(precinctName) != -1) {
                let newPrecinctCoordinates = [];
                let coordinatesList = LeafletMap.precinctLayer._layers[i]._latlngs[0];
                // Leaflet stores as latlng but server uses lnglat
                for (let k in coordinatesList) {
                    newPrecinctCoordinates.push([coordinatesList[k].lng, coordinatesList[k].lat]);
                };
                newPrecinctCoordinates.push([coordinatesList[0].lng, coordinatesList[0].lat]);
                let geometry = {"type": LeafletMap.precinctLayer._layers[i].feature.geometry.type, "coordinates": [newPrecinctCoordinates]}
                DataHandler.uploadPrecinctBoundary(precinctName, geometry);
            }
        };
    
        
    },

    updatePrecincts: (precinctList) => {
        LeafletMap.precinctGeojson = [];
        for (let i = 0; i < precinctList.length; i++) {
            let canonName = precinctList[i];
            let geojson = JsonHandler.convertToGeojson(LeafletMap.precincts[canonName]);
            LeafletMap.precinctGeojson.push(geojson);
        }
        LeafletMap.updatePrecinctLayer();
    },

    updateDistricts: (districtList) => {
        LeafletMap.districtGeojson = [];
        for (let i = 0; i < districtList.length; i++) {
            let canonName = districtList[i];
            let geojson = JsonHandler.convertToGeojson(LeafletMap.districts[canonName]);
            LeafletMap.districtGeojson.push(geojson);
        }
        LeafletMap.updateDistrictLayer();
    },

    updateNeighbors: (option) => {
        let precinctNeighbors = LeafletMap.precincts[LeafletMap.precinctBeingChanged].neighborCNames;
        switch (option) {
            case DataHandler.options.addNeighbor: {
                LeafletMap.precincts[LeafletMap.precinctBeingChanged].neighborCNames = precinctNeighbors.concat(LeafletMap.selectedPrecincts);
                DataHandler.changePrecinctNeighbor(LeafletMap.precinctBeingChanged, LeafletMap.selectedPrecincts, DataHandler.options.addNeighbor);
                break;
            }
            case DataHandler.options.removeNeighbor: {
                // Get the difference between the precinctNeighbors list and selectedPrecincts
                LeafletMap.precincts[LeafletMap.precinctBeingChanged].neighborCNames = precinctNeighbors.filter(cName => !LeafletMap.selectedPrecincts.includes(cName));
                DataHandler.changePrecinctNeighbor(LeafletMap.precinctBeingChanged, LeafletMap.selectedPrecincts, DataHandler.options.removeNeighbor);
                break;
            }
            default:
                console.log("Error updating neighbors");
        }
    },

    updatePrecinctGeojson: () => {
        LeafletMap.precinctGeojson = [];
        let precinctCNames = LeafletMap.districts[LeafletMap.currentDistrict].precinctCNames;
        for (let i in precinctCNames) {
            let precinctGeojson = JsonHandler.convertToGeojson(LeafletMap.precincts[precinctCNames[i]]);
            LeafletMap.precinctGeojson.push(precinctGeojson);
        }
    }, 

    numberWithCommas: (x) => {
        return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
    },

}