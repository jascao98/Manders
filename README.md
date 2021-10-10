# Manders

An interactive website that allows for analyzing, displaying and correcting political data of U.S precincts based on the precinct's political, demographic and coordinate information taken from the Census precinct dataset. Preprocessed census data related to the political analysis for map presentation.
Repository for frontend + backend, created as a team project for the Software Engineering Course at Stony Brook University, as part of Team Manders.

## Backend
### Endpoints

#### State Controller
 -  ```
    @GetMapping("/getAllStates") 
    public List<State> getAllStates()
    ```
 -  ```
    @GetMapping("/getState/{stateCanonName}")
    public State getState(@PathVariable String stateCanonName)
    ```
-   ```
    @PostMapping("/uploadState")
    public void uploadState(@RequestBody State state)
    ```

#### District Controller
-   ```
    @GetMapping("/getDistrict/{districtCanonName}")
    public District getDistrict(@PathVariable String districtCanonName)
    ```
-   ```
    @PostMapping("/uplaodDistrict")
    public void uploadDistrict(@RequestBody District district)
    ```

#### Precinct Controller
-   ```
    @GetMapping("/getPrecinct/{precinctCanonName}")
    public Precinct getPrecinct(@PathVariable String canonName)
    ```
-   ```
    @GetMapping("/addNeighbor?p1={precinctName1}&p2={precinctName2}")
    public void addNeighbor(@PathVariable String precinctName1, @PathVariable String precinctName2)
    ```
-   ```
    @GetMapping("/deleteNeighbor?p1={precinctName1}&p2={precinctName2}")
    ```
-   ```
    @GetMapping("/mergePrecinct?p1={precinctName1}&p2={precinctName2}")
    public Precinct mergePrecinct(String precinctName1, String precinctName2)
    ```
-   ```
    @GetMapping("/removePrecinct/{precinct1}")
    public void remove(String precinct1)
    ```
-   ```
    @PostMapping("/uploadPrecinct")
    public void uploadPrecinct(@RequestBody Precinct precinct)
    ```
