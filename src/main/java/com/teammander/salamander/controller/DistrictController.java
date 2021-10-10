package com.teammander.salamander.controller;

import java.util.ArrayList;
import java.util.List;

import com.teammander.salamander.map.District;
import com.teammander.salamander.map.Precinct;
import com.teammander.salamander.map.State;
import com.teammander.salamander.service.DistrictService;
import com.teammander.salamander.service.StateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/district")
public class DistrictController {
    StateService ss;
    DistrictService ds;

    @Autowired
    public DistrictController(StateService ss, DistrictService ds) {
        this.ss = ss;
        this.ds = ds;
    }

    public DistrictService getDs() {
        return this.ds;
    }

    public StateService getSs() {
        return this.ss;
    }

    @GetMapping("/getAllDistricts")
    public List<District> getAllDistricts() {
        DistrictService ds = getDs();
        List<District> allDistricts = ds.getAllDistricts();
        return allDistricts;
    }

    // Change query
    @PostMapping("/getMultipleDistricts")
    public List<District> getMultipleDistricts(@RequestBody List<String> query) {
        DistrictService ds = getDs();
        List<District> queryResponse = new ArrayList<>();
        for (String s : query) {
            District d = ds.getDistrict(s);
            queryResponse.add(d);
        }
        return queryResponse;
    } 

    @GetMapping("/getDistrict/{districtCanonName}")
    public District getDistrict(@PathVariable String districtCanonName) {
        DistrictService ds = getDs();
        District foundDistrict = ds.getDistrict(districtCanonName);
        return foundDistrict;
    }

    @GetMapping("/{districtCanonName}/precincts")
    public List<Precinct> getPrecincts(@PathVariable String districtCanonName) {
        DistrictService ds = getDs();
        District foundDistrict = ds.getDistrict(districtCanonName);
        return new ArrayList<>(foundDistrict.getChildPrecincts());
    }

    @PostMapping("/uploadDistrict/{stateName}")
    public void uploadDistrict(@PathVariable String stateName, @RequestBody District district) {
        DistrictService ds = getDs();
        StateService ss = getSs();
        State parentState = ss.getState(stateName);
        district.setParentState(parentState);
        ss.addChildDistrict(stateName, district);
        ds.insertDistrict(district);
    }

    @PostMapping("/multiUploadDistricts/{stateName}")
    public void multiUploadDistricts(@PathVariable String stateName, @RequestBody List<District> districts) {
        DistrictService ds = getDs();
        StateService ss = getSs();
        State parentState = ss.getState(stateName);
        for (District d : districts) {
            d.setParentState(parentState);
        }
        ss.addMultipleChildDistricts(stateName, districts);
        ds.insertMultipleDistricts(districts);
    }
}
