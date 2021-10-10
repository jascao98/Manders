package com.teammander.salamander.controller;

import java.util.List;

import com.teammander.salamander.map.DataError;
import com.teammander.salamander.service.ErrorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/error")
public class ErrorController {
    ErrorService es;

    @Autowired
    public ErrorController(ErrorService es) {
        this.es = es;
    }

    public ErrorService getEs() {
        return this.es;
    }

    @GetMapping("/{errorId}")
    public DataError getError(@PathVariable int eid) {
        ErrorService es = getEs();
        DataError error = es.getError(eid);
        return error;
    }

    @GetMapping("/resolved")
    public List<DataError> getResolvedErrors() {
        ErrorService es = getEs();
        List<DataError> resolvedErrs = es.getResolvedErrors();
        return resolvedErrs;
    }

    @GetMapping("/unresolved")
    public List<DataError> getUnresolvedErrors() {
        ErrorService es = getEs();
        List<DataError> unresolvedErrs = es.getUnresolvedErrors();
        return unresolvedErrs;
    }

    @GetMapping("/stateErrors/{stateName}/{resolved}")
    public List<DataError> getStateErrors(@PathVariable String stateName, @PathVariable boolean resolved) {
        ErrorService es = getEs();
        List<DataError> stateErrs = es.getStateErrors(stateName, resolved);
        return stateErrs;
    }

    @GetMapping("/districtErrors/{districtName}/{resolved}")
    public List<DataError> getDistrictErrors(@PathVariable String districtName, @PathVariable boolean resolved) {
        ErrorService es = getEs();
        List<DataError> stateErrs = es.getDistrictErrors(districtName, resolved);
        return stateErrs;
    }

    @GetMapping("/precinctErrors/{precinctName}/{resolved}")
    public List<DataError> getPrecinctErrors(@PathVariable String precinctName, @PathVariable boolean resolved) {
        ErrorService es = getEs();
        List<DataError> stateErrs = es.getPrecinctErrors(precinctName, resolved);
        return stateErrs;
    }

    @GetMapping("/setErrorStatus/{eid}")
    public void setErrorStatus(@PathVariable int eid, @RequestParam boolean resolved) {
        ErrorService es = getEs();
        DataError targetError = es.getError(eid);
        es.changeErrStatus(targetError, resolved);
    }

    @PostMapping("/uploadError")
    public void uploadError(@RequestBody DataError err) {
        ErrorService es = getEs();
        es.addError(err);
    }
}