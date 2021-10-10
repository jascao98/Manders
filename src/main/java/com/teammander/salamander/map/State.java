package com.teammander.salamander.map;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity(name = "STATES")
public class State extends Region {

    Set<District> districts;

    @OneToMany(mappedBy = "parentState", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    public Set<District> getDistricts() {
        return this.districts;
    }

    public void setDistricts(Set<District> districts) {
        this.districts = districts;
    }
}
