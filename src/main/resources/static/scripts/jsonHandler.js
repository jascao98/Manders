/* Takes a region json (received from server) and formats it for geoJson */
const JsonHandler = {
    convertToGeojson: function(regionJson) {
        let geoJson = {
            "type": "Feature",
            "geometry": {
                "type": "",
                "coordinates": []
            },
            "properties": {}
        };
        geoJson["geometry"] = JSON.parse(regionJson["geometry"]);

        // Place each field besides the geometry inside properties
        let keys = Object.keys(regionJson);
        for (let x in keys) {
            if (keys[x] != "geometry") {
                let prop = keys[x]
                geoJson["properties"][prop] = regionJson[prop];
            }
        }

        return geoJson;
    },
}