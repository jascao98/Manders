package com.teammander.salamander.map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "DATA_ERRORS")
public class DataError {

    int eid;
    ErrorType eType;
    boolean resolved;
    String affectedPrct;
    String affectedState;
    String affectedDistrict;
    String precinctDisplayName;

    @Id
    @GeneratedValue
    @Column(name = "ID")
    public int getEid() {
        return eid;
    }

    public void setEid(int eid) {
        this.eid = eid;
    }

    @Column(name = "resolved_status")
    public boolean getResolved() {
        return this.resolved;
    }

    public void setResolved(boolean newStatus) {
        this.resolved = newStatus;
    }

    @Enumerated(EnumType.STRING)
    public ErrorType getEType() {
        return eType;
    }

    public void setEType(ErrorType eType) {
        this.eType = eType;
    }

    @Column(name = "affected_prcts")
    public String getAffectedPrct() {
        return affectedPrct;
    }

    public void setAffectedPrct(String affectedPrct) {
        this.affectedPrct = affectedPrct;
    }

    @Column(name = "affected_district")
    public String getAffectedDistrict() {
        return this.affectedDistrict;
    }

    public void setAffectedDistrict(String affectedDistrict) {
        this.affectedDistrict = affectedDistrict;
    }

    @Column(name = "affected_state")
    public String getAffectedState() {
        return this.affectedState;
    }

    public void setAffectedState(String affectedState) {
        this.affectedState = affectedState;
    }

    @Column(name = "precinct_display_name")
    public String getPrecinctDisplayName() {
        return this.precinctDisplayName;
    }

    public void setPrecinctDisplayName(String precinctDisplayName) {
        this.precinctDisplayName = precinctDisplayName;
    }
}
