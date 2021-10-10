package com.teammander.salamander.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity(name = "ELECTION_DATA")
public class ElectionData {

    int electionDataId;
    List<Election> elections;

    @Id
    @GeneratedValue
    @Column(name = "ID")
    public int getElectionDataId() {
        return electionDataId;
    }

    public void setElectionDataId(int electionDataId) {
        this.electionDataId = electionDataId;
    }

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    public List<Election> getElections() {
        return this.elections;
    }

    public void setElections(List<Election> elections) {
        this.elections = elections;
    }

    public Election findElection(ElectionType type, Year year) {
        for (Election e : getElections()) {
            if (e.getYear() == year && e.getType() == type) {
                return e;
            }
        }
        return null;
    }

    public static ElectionData mergeElectionData(List<ElectionData> elecDatas) {
        HashMap<String, Election> dataTracker = new HashMap<>();
        // Populate the "tracker"
        for (ElectionData ed : elecDatas) {
            List<Election> elections = ed.getElections();

            for (Election elec : elections) {
                String sKey = elec.getType().toString() + elec.getYear().toString();

                if (dataTracker.containsKey(sKey)) {
                    Election mergedElection = dataTracker.get(sKey);

                    mergedElection.setDemocraticVotes(mergedElection.getDemocraticVotes() + elec.getDemocraticVotes());
                    mergedElection.setRepublicanVotes(mergedElection.getRepublicanVotes() + elec.getRepublicanVotes());
                    mergedElection.setGreenVotes(mergedElection.getGreenVotes() + elec.getGreenVotes());
                    mergedElection.setLibertarianVotes(mergedElection.getLibertarianVotes() + elec.getLibertarianVotes());
                    mergedElection.setOtherVotes(mergedElection.getOtherVotes() + elec.getOtherVotes());
                }
                else {
                    dataTracker.put(sKey, elec);
                }
            }
        }

        // Create a new ElectionData and return that
        ElectionData mergedED = new ElectionData();
        List<Election> mergedElections = new ArrayList<>(dataTracker.values());
        mergedED.setElections(mergedElections);
        return mergedED;
    }
}
