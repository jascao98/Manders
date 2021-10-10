package com.teammander.salamander.repository;

import java.util.List;

import com.teammander.salamander.map.DataError;
import com.teammander.salamander.map.ErrorType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ErrorRepository extends JpaRepository<DataError, Integer> {
    List<DataError> findAllByResolved(boolean status);
    List<DataError> findAllByEType(ErrorType type);
    List<DataError> findAllByAffectedState(String stateName);
    List<DataError> findAllByAffectedDistrict(String districtName);
    List<DataError> findAllByAffectedPrct(String precinctName);

    List<DataError> findAllByAffectedStateAndResolvedEquals(String stateName, boolean status);
    List<DataError> findAllByAffectedDistrictAndResolvedEquals(String districtName, boolean status);
    List<DataError> findAllByAffectedPrctAndResolvedEquals(String precinctName, boolean status);
}
