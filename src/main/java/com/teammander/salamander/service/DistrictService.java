package com.teammander.salamander.service;

import java.util.List;
import java.util.Optional;

import com.teammander.salamander.map.District;
import com.teammander.salamander.map.Precinct;
import com.teammander.salamander.repository.DistrictRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DistrictService {
    DistrictRepository dr;

    @Autowired
    public DistrictService(DistrictRepository dr) {
        this.dr = dr;
    }

    public DistrictRepository getDr() {
        return this.dr;
    }

    public District getDistrict(String canonName) {
        DistrictRepository dr = getDr();
        Optional<District> queryResult = dr.findById(canonName);
        District foundDistrict = queryResult.orElse(null);
        return foundDistrict;
    }

    public List<District> getAllDistricts() {
        DistrictRepository dr = getDr();
        List<District> allDistricts = dr.findAll();
        return allDistricts;
    }

    public void insertDistrict(District district) {
        DistrictRepository dr = getDr();
        dr.saveAndFlush(district);
    }

    public void insertMultipleDistricts(List<District> districts) {
        DistrictRepository dr = getDr();
        dr.saveAll(districts);
        dr.flush();
    }

    public boolean insertChildPrecinct(String districtName, Precinct precinct) {
        DistrictRepository dr = getDr();
        District targetDistrict = dr.findById(districtName).orElse(null);
        if (targetDistrict != null) {
            targetDistrict.getChildPrecincts().add(precinct);
            dr.flush();
        } else {
            return false;
        }
        return true;
    }

    public boolean insertMultipleChildPrecincts(String districtName, List<Precinct> precincts) {
        DistrictRepository dr = getDr();
        District targetDistrict = dr.findById(districtName).orElse(null);
        if (targetDistrict != null) {
            targetDistrict.getChildPrecincts().addAll(precincts);
            dr.flush();
        } else {
            return false;
        }
        return true;
    }
}
