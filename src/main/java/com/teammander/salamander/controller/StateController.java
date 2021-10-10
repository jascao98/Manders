package com.teammander.salamander.controller;

import com.teammander.salamander.map.District;
import com.teammander.salamander.map.State;
import com.teammander.salamander.service.StateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/state")
public class StateController {
    StateService ss;

    @Autowired
    public StateController(StateService ss) {
        this.ss = ss;
    }

    public StateService getSs() {
        return this.ss;
    }

    //when page loads and we get all states to display
    @GetMapping("/getAllStates")
    public List<State> getAllStates() {
        StateService ss = getSs();
        List<State> allStates = ss.getAllStates();
        for (State s : allStates) {
            s.setDistricts(null);
        }
        return allStates;
    }

    //get specific state data for clicking on state on map/dropdown
    @GetMapping("/getState/{stateCanonName}")
    public State getState(@PathVariable String stateCanonName) {
        StateService ss = getSs();
        State foundState = ss.getState(stateCanonName);
        return foundState;
    }

    @PostMapping("/uploadState")
    public void uploadState(@RequestBody State state) {
        StateService ss = getSs();
        ss.insertState(state);
    }

    @GetMapping("/{stateCanonName}/districts")
    public List<District> getDistricts(@PathVariable String stateCanonName) {
        StateService ss = getSs();
        State foundState = ss.getState(stateCanonName);
        List<District> districts = new ArrayList<>(foundState.getDistricts());
        for (District d : districts) {
            d.setChildPrecincts(null);
        }
        return new ArrayList<>(foundState.getDistricts());
    }

    @PostMapping("/multiUploadStates")
    public void multiUploadState(@RequestBody List<State> states) {
        StateService ss = getSs();
        ss.insertMultipleStates(states);
    }
    
}
