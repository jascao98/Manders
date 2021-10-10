package com.teammander.salamander.map;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity(name = "DISTRICTS")
public class District extends Region{

    State parentState;
    Set<Precinct> childPrecincts;
   
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "parent_state")
    @JsonBackReference
    public State getParentState() {
        return this.parentState;
    }

    public void setParentState(State state) {
        this.parentState = state;
    }

    @OneToMany(mappedBy = "parentDistrict", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    public Set<Precinct> getChildPrecincts() {
        return this.childPrecincts;
    }

    public void setChildPrecincts(Set<Precinct> precinct) {
        this.childPrecincts = precinct;
    }

    public void addPrecinctChild(Precinct precinct) {
        Set<Precinct> children = getChildPrecincts();
        children.add(precinct);
    }

    public void removePrecinctChild(Precinct precinct) {
        Set<Precinct> children = getChildPrecincts();
        children.remove(precinct);
    }
}
