package com.teammander.salamander.service;

import java.util.List;
import java.util.Optional;

import com.teammander.salamander.map.DataError;
import com.teammander.salamander.map.ErrorType;
import com.teammander.salamander.repository.ErrorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ErrorService {
    ErrorRepository er;
    TransactionService ts;

    @Autowired
    public ErrorService(ErrorRepository er, TransactionService ts) {
        this.er = er;
        this.ts = ts;
    }

    public ErrorRepository getEr() {
        return this.er;
    }

    public TransactionService getTs() {
        return this.ts;
    }

    public DataError getError(int id) {
        ErrorRepository er = getEr();
        Optional<DataError> queryResult = er.findById(id);
        DataError foundError = queryResult.orElse(null);
        return foundError;
    }

    /* Get Error By Type */
    public List<DataError> getUnresolvedErrors() {
        ErrorRepository er = getEr();
        List<DataError> result = er.findAllByResolved(false);
        return result; 
    }

    public List<DataError> getResolvedErrors() {
        ErrorRepository er = getEr();
        List<DataError> result = er.findAllByResolved(true);
        return result;
    }

    public List<DataError> getErrorsByType(ErrorType type) {
        ErrorRepository er = getEr();
        List<DataError> result = er.findAllByEType(type);
        return result;
    }

    /* Get Errors by Region */
    public List<DataError> getStateErrors(String stateName) {
        ErrorRepository er = getEr();
        List<DataError> result = er.findAllByAffectedState(stateName);
        return result;
    }

    public List<DataError> getDistrictErrors(String districtName) {
        ErrorRepository er = getEr();
        List<DataError> result = er.findAllByAffectedDistrict(districtName);
        return result;
    }

    public List<DataError> getPrecinctErrors(String precinctName) {
        ErrorRepository er = getEr();
        List<DataError> result = er.findAllByAffectedPrct(precinctName);
        return result;
    }

    public List<DataError> getStateErrors(String stateName, boolean status) {
        ErrorRepository er = getEr();
        List<DataError> result = er.findAllByAffectedStateAndResolvedEquals(stateName, status);
        return result;
    }

    public List<DataError> getDistrictErrors(String districtName, boolean status) {
        ErrorRepository er = getEr();
        List<DataError> result = er.findAllByAffectedDistrictAndResolvedEquals(districtName, status);
        return result;
    }

    public List<DataError> getPrecinctErrors(String precinctName, boolean status) {
        ErrorRepository er = getEr();
        List<DataError> result = er.findAllByAffectedPrctAndResolvedEquals(precinctName, status);
        return result;
    }

    /* Add/Delete Errors */
    public void deleteError(DataError err) {
        ErrorRepository er = getEr();
        er.delete(err);
    }

    public void addError(DataError err) {
        ErrorRepository er = getEr();
        er.saveAndFlush(err);
    }

    public void changeErrStatus(DataError err, boolean status) {
        ErrorRepository er = getEr();
        boolean oldStatus = err.getResolved();
        err.setResolved(status);
        er.flush();

        TransactionService ts = getTs();
        ts.logErrorStatusChange(err, oldStatus, status);
    }
}
