package com.teammander.salamander.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.teammander.salamander.data.DemographicData;
import com.teammander.salamander.data.Election;
import com.teammander.salamander.data.ElectionData;
import com.teammander.salamander.data.ElectionType;
import com.teammander.salamander.data.Year;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.MultiPolygon;
import org.wololo.geojson.GeoJSON;
import org.wololo.jts2geojson.GeoJSONReader;
import org.wololo.jts2geojson.GeoJSONWriter;

@Entity(name = "PRECINCTS")
public class Precinct extends Region{

    District parentDistrict;
    Set<String> neighborCNames;
    PrecinctType type;

    public boolean isNeighbor(Precinct neighbor) {
        String neighCName = neighbor.getCanonName();
        Boolean ret = neighborCNames.contains(neighCName);
        return ret;
    }

    public void addNeighbor(Precinct neighbor) {
        String neighCName = neighbor.getCanonName();
        neighborCNames.add(neighCName);
    }

    public void deleteNeighbor(Precinct neighbor){
        String neighCName = neighbor.getCanonName();
        neighborCNames.remove(neighCName);
    }

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH, CascadeType.REFRESH})
    @JoinColumn(name = "parent_district")
    @JsonBackReference
    public District getParentDistrict() {
        return this.parentDistrict;
    }

    public void setParentDistrict(District district) {
        this.parentDistrict = district;
    }

    @ElementCollection
    @Column(name = "neighbor_cname")
    public Set<String> getNeighborCNames() {
        return this.neighborCNames;
    }

    public void setNeighborCNames(Set<String> neighbors) {
        this.neighborCNames = neighbors;
    }

    @Enumerated(EnumType.STRING)
    public PrecinctType getType() {
        return this.type;
    }

    public void setType(PrecinctType type) {
        this.type = type;
    }

    public Election findElection(int eid) {
        ElectionData ed = getElecData();
        List<Election> elecs = ed.getElections();
        for (Election e : elecs) {
            if (e.getElectionId() == eid) {
                return e;
            }
        }
        throw new NoSuchElementException("Cannot find election with eid: " + eid);
    }

    /**
     * Initializes the demo/election data for a precinct with zero.
     */
    public void initialize() {
        ElectionData newED = new ElectionData();
        DemographicData newDD = new DemographicData();
        Election pres16 = new Election();
        Election cong16 = new Election();
        Election cong18 = new Election();

        pres16.setType(ElectionType.PRESIDENTIAL);
        pres16.setYear(Year.SIXTEEN);
        cong16.setType(ElectionType.CONGRESSIONAL);
        cong16.setYear(Year.SIXTEEN);
        cong18.setType(ElectionType.CONGRESSIONAL);
        cong18.setYear(Year.EIGHTEEN);
        newED.setElections(new ArrayList<>(Arrays.asList(pres16, cong16, cong18)));

        this.setElecData(newED);
        this.setDemoData(newDD);
    }

    public static boolean verifyIsValid(String geometry) {
        GeoJSONReader reader = new GeoJSONReader();

        Geometry geom = reader.read(geometry);
        return geom.isValid();
    }

    public static boolean verifyIsSimple(String geometry) {
        GeoJSONReader reader = new GeoJSONReader();

        Geometry geom = reader.read(geometry);
        return geom.isSimple();
    }

    public static Precinct mergePrecincts(List<Precinct> precincts) {
        List<Geometry> allGeoms = new ArrayList<>();
        List<ElectionData> allED = new ArrayList<>();
        List<DemographicData> allDD = new ArrayList<>();
        GeoJSONReader reader = new GeoJSONReader();
        GeoJSONWriter writer = new GeoJSONWriter();
        // Aggregate all the fields into lists
        for (Precinct p : precincts) {
            Geometry newGeom = reader.read(p.getGeometry());
            allGeoms.add(newGeom);
            allED.add(p.getElecData());
            allDD.add(p.getDemoData());
        }
        // Merge Geometries
        GeometryFactory gf = new GeometryFactory();
        GeometryCollection collection = gf.createGeometryCollection(allGeoms.toArray(new Geometry[] {}));
        Geometry mergedGeometry = collection.union();
        if (mergedGeometry.getGeometryType().equalsIgnoreCase("multipolygon")) {
            return null;
        }
        GeoJSON mergedJSON = writer.write(mergedGeometry);
        String mergedString = mergedJSON.toString();

        // Merge Election Data & Demo Data
        ElectionData mergedED = ElectionData.mergeElectionData(allED);
        DemographicData mergedDD = DemographicData.mergeDemoData(allDD);

        Precinct mergedPrecinct = new Precinct();
        mergedPrecinct.setDisplayName("Merged Precinct");
        mergedPrecinct.setType(PrecinctType.NORMAL);
        mergedPrecinct.setElecData(mergedED);
        mergedPrecinct.setDemoData(mergedDD);

        mergedPrecinct.setGeometry(mergedString);
        mergedPrecinct.setParentDistrict(precincts.get(0).getParentDistrict());
        mergedPrecinct.setNeighborCNames(new HashSet<>());

        // Merge election 
        return mergedPrecinct;
    }
}
