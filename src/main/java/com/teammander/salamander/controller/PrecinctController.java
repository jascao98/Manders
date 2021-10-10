package com.teammander.salamander.controller;

import java.util.ArrayList;
import java.util.List;

import com.teammander.salamander.map.District;
import com.teammander.salamander.map.Precinct;
import com.teammander.salamander.service.DistrictService;
import com.teammander.salamander.service.PrecinctService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/precinct")
public class PrecinctController {
    PrecinctService ps;
    DistrictService ds;

    @Autowired
    public PrecinctController(DistrictService ds, PrecinctService ps) {
        this.ps = ps;
        this.ds = ds;
    }

    public PrecinctService getPs() {
        return this.ps;
    }

    public DistrictService getDs() {
        return this.ds;
    }

    @GetMapping("/getPrecinct/{canonName}")
    public Precinct getPrecinct(@PathVariable String canonName) {
        PrecinctService ps = getPs();
        Precinct foundPrecinct = ps.getPrecinct(canonName);
        return foundPrecinct;
    }

    @GetMapping("/getAllPrecincts")
    public List<Precinct> getAllPrecincts() {
        PrecinctService ps = getPs();
        List<Precinct> foundPrecincts = ps.getAllPrecincts();
        return foundPrecincts;
    }

    @PostMapping("/mergePrecinct")
    public ResponseEntity<?> mergePrecinct(@RequestBody List<String> precincts) {
        PrecinctService ps = getPs();
        Precinct mergedPrecinct = ps.mergePrecincts(precincts);
        if (mergedPrecinct == null) {
            ResponseEntity<String> re = new ResponseEntity<>("Merge would result in MultiPolygon", HttpStatus.BAD_REQUEST);
            return re;
        }
        ResponseEntity<Precinct> re = ResponseEntity.ok(mergedPrecinct);
        return re;
    }

    @GetMapping("/removePrecinct/{precinct1}")
    public void remove(@PathVariable String precinct1) {
        PrecinctService ps = getPs();
        ps.remove(precinct1);
    }

    @GetMapping("/{precinctName}/rename/{newName}")
    public void renamePrecinctDisplay(@PathVariable String precinctName, @PathVariable String newName) {
        PrecinctService ps = getPs();
        ps.renamePrecinctDisplay(precinctName, newName);
    }

    /**
     * Inserts/Deletes neighbors from a specified precinct. Will propogate changes
     * to the neighbors to maintain proper state.
     * @param p the precinct to modify
     * @param op the operation as a string, add/delete
     * @param newNeighbors the list of new neighbors
     * @return a ResponseEntity to the user signifying success/failure
     */
    @PostMapping("/modifyNeighbor")
    public ResponseEntity<String> modifyNeighbors(@RequestParam String p, 
    @RequestParam String op, @RequestBody List<String> neighbors) {
        PrecinctService ps = getPs();
        ResponseEntity<String> ret = null;
        String badQuery;
        String errMsg = null;

        if (op.equals("add")) {
            badQuery = ps.addMultiNeighbors(p, neighbors);
            if (badQuery != null) {
                errMsg = ErrorMsg.unableToFindMsg(badQuery);
            }
        } else if (op.equals("delete")) {
            badQuery = ps.deleteMultiNeighbors(p, neighbors);
            if (badQuery != null) {
                errMsg = ErrorMsg.unableToFindMsg(badQuery);
            }
        } else {
            errMsg = ErrorMsg.badQueryMsg("op", op);
        }
         
        // Check if operation was successful and handle accordingly
        if (errMsg != null) {
            ret = new ResponseEntity<>(errMsg, HttpStatus.BAD_REQUEST);
        } else {
            ret = ResponseEntity.ok(null);
        }
        return ret;
    }

    @PostMapping("/getMultiplePrecincts")
    public List<Precinct> getMultiplePrecincts(@RequestBody List<String> query) {
        PrecinctService ps = getPs();
        List<Precinct> queryResponse = new ArrayList<>();
        for (String s : query) {
            Precinct foundPrecinct = ps.getPrecinct(s);
            if (foundPrecinct != null) {
                queryResponse.add(foundPrecinct);
            }
        }
        return queryResponse;
    }

    @GetMapping("/{precinctName}/updateDemoData/{id}/{field}/{newVal}")
    public ResponseEntity<?> updateDemoData(@PathVariable String precinctName, 
    @PathVariable int id, @PathVariable String field, @PathVariable int newVal) {
        PrecinctService ps = getPs();
        Precinct targetPrecinct = ps.updateDemoData(precinctName, id, field, newVal);
        if (targetPrecinct == null) {
            String errMsg = ErrorMsg.unableToFindMsg(precinctName);
            ResponseEntity<String> re = new ResponseEntity<>(errMsg, HttpStatus.NOT_FOUND);
            return re;
        }
        ResponseEntity<Precinct> re = ResponseEntity.ok(targetPrecinct);
        return re;
    }

    @GetMapping("/{precinctName}/setGhost")
    public ResponseEntity<?> setGhostPrecinct(@PathVariable String precinctName) {
        PrecinctService ps = getPs();
        Precinct targetPrecinct = ps.setGhostPrecinct(precinctName);
        if (targetPrecinct == null) {
            ResponseEntity<Void> re = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return re;
        }
        ResponseEntity<Precinct> re = ResponseEntity.ok(targetPrecinct);
        return re;
    }

    @GetMapping("/{precinctName}/updateElecData/{id}/{field}/{newVal}")
    public ResponseEntity<?> updateElecData(@PathVariable String precinctName, 
    @PathVariable int id, @PathVariable String field, @PathVariable int newVal) {
        Precinct targetPrecinct = getPs().updateElection(precinctName, id, field, newVal);
        if (targetPrecinct == null) {
            String errMsg = ErrorMsg.unableToFindMsg(precinctName);
            ResponseEntity<String> re = new ResponseEntity<>(errMsg, HttpStatus.NOT_FOUND);
            return re;
        }
        return new ResponseEntity<>(targetPrecinct, HttpStatus.OK);
    }

    @PostMapping("/updateBoundary")
    public ResponseEntity<?> updateBoundary(@RequestParam String pCName, @RequestBody String geometry) {
        Precinct targetPrecinct = getPs().updateBoundary(pCName, geometry);
        if (targetPrecinct == null) {
            ResponseEntity<String> re = new ResponseEntity<>("Invalid Geometry", HttpStatus.BAD_REQUEST);
            return re;
        }
        ResponseEntity<Precinct> re = ResponseEntity.ok(targetPrecinct);
        return re;
    }

    @PostMapping("/newPrecinct/{parentName}")
    public ResponseEntity<?> createNewPrecinct(@PathVariable String parentName, @RequestBody Precinct precinct) {
        PrecinctService ps = getPs();
        Precinct newPrecinct = ps.createNewPrecinct(precinct, parentName);
        if (newPrecinct == null) {
            ResponseEntity<String> re = new ResponseEntity<>("Invalid Geometry", HttpStatus.BAD_REQUEST);
            return re;
        }
        ResponseEntity<Precinct> re = ResponseEntity.ok(newPrecinct);
        return re;
    }

    @PostMapping("/uploadPrecinct/{parentName}")
    public void uploadPrecinct(@PathVariable String parentName, @RequestBody Precinct precinct) {
        PrecinctService ps = getPs();
        DistrictService ds = getDs();
        District parentDistrict = ds.getDistrict(parentName);
        precinct.setParentDistrict(parentDistrict);
        ds.insertChildPrecinct(parentName, precinct);
        ps.insertPrecinct(precinct, true);
        
    }
    
    @PostMapping("/multiUploadPrecincts/{parentName}")
    public void multiUploadPrecincts(@PathVariable String parentName, @RequestBody List<Precinct> precincts) {
        PrecinctService ps = getPs();
        DistrictService ds = getDs();
        District parentDistrict = ds.getDistrict(parentName);
        for (Precinct p : precincts) {
            p.setParentDistrict(parentDistrict);
        }
        ds.insertMultipleChildPrecincts(parentName, precincts);
        ps.insertMultiplePrecincts(precincts);
    }

}
