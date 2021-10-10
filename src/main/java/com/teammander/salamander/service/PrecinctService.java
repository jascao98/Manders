package com.teammander.salamander.service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import com.teammander.salamander.data.DemographicData;
import com.teammander.salamander.data.Election;
import com.teammander.salamander.map.District;
import com.teammander.salamander.map.Precinct;
import com.teammander.salamander.map.PrecinctType;
import com.teammander.salamander.repository.ElectionRepository;
import com.teammander.salamander.repository.PrecinctRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PrecinctService {
    PrecinctRepository pr;
    ElectionRepository er;
    DistrictService ds;
    TransactionService ts;

    @Autowired
    public PrecinctService(PrecinctRepository pr, ElectionRepository er, DistrictService ds, TransactionService ts) {
        this.pr = pr;
        this.er = er;
        this.ds = ds;
        this.ts = ts;
    }

    public PrecinctRepository getPr() {
        return this.pr;
    }

    public ElectionRepository getEr() {
        return this.er;
    }

    public DistrictService getDs() {
        return this.ds;
    }

    public TransactionService getTs() {
        return this.ts;
    }

    public Precinct getPrecinct(String canonName) {
        PrecinctRepository pr = getPr();
        Optional<Precinct> queryResult = pr.findById(canonName);
        Precinct foundPrecint = queryResult.orElse(null);
        return foundPrecint;
    }

    public List<Precinct> getAllPrecincts() {
        PrecinctRepository pr = getPr();
        List<Precinct> allPrecincts = pr.findAll();
        return allPrecincts;
    }

    public void rmPrecinct(Precinct precinct) {
        PrecinctRepository pr = getPr();
        String targetCName = precinct.getCanonName();
        Set<String> neighbors = new HashSet<>(precinct.getNeighborCNames());
        District parent = precinct.getParentDistrict();

        for (String neighbor : neighbors) {
            deleteNeighborUntracked(targetCName, neighbor);
        }
        parent.removePrecinctChild(precinct);
        pr.delete(precinct);
        pr.flush();
    }

    public void deleteNeighborUntracked(String precinctName1, String precinctName2) {
        PrecinctRepository pr = getPr();
        Precinct p1 = getPrecinct(precinctName1);
        Precinct p2 = getPrecinct(precinctName2);

        if (p1 == null || p2 == null) {
            return;
        }

        p1.deleteNeighbor(p2);
        p2.deleteNeighbor(p1);
        pr.flush();
    }

    public void addNeighborUntracked(String precinctName1, String precinctName2) {
        PrecinctRepository pr = getPr();
        Precinct p1 = getPrecinct(precinctName1);
        Precinct p2 = getPrecinct(precinctName2);

        if (p1 == null || p2 == null) {
            return;
        }
        p1.addNeighbor(p2);
        p2.addNeighbor(p1);
        pr.flush();
    }

    /**
     * Adds a neighbor edge between two precincts
     * @param precinctName1 the canon name of the first precinct
     * @param precinctName2 the canon name of the second precinct
     * @return The canon name of the precinct that could not be found, null otherwise
     */
    public String addNeighbor(String precinctName1, String precinctName2) {
        PrecinctRepository pr = getPr();
        Precinct p1 = getPrecinct(precinctName1);
        Precinct p2 = getPrecinct(precinctName2);

        if (p1 == null) {
            return precinctName1;
        } else if (p2 == null) {
            return precinctName2;
        }

        p1.addNeighbor(p2);
        p2.addNeighbor(p1);
        pr.flush();

        TransactionService ts = getTs();
        ts.logChangeNeighbor(p1, p2, true);

        return null;
    }

    /**
     * Deletes a neighbor edge between two precincts
     * @param precinctName1 the canon name of the first precinct
     * @param precinctName2 the canon name of the second precinct
     * @return the canon name of the precinct that could not be found, null otherwise
     */
    public String deleteNeighbor(String precinctName1, String precinctName2) {
        PrecinctRepository pr = getPr();
        Precinct p1 = getPrecinct(precinctName1);
        Precinct p2 = getPrecinct(precinctName2);

        if (p1 == null) {
            return precinctName1;
        } else if (p2 == null) {
            return precinctName2;
        }
        p1.deleteNeighbor(p2);
        p2.deleteNeighbor(p1);

        pr.flush();

        TransactionService ts = getTs();
        ts.logChangeNeighbor(p1, p2, false);

        return null;
    }

    /**
     * Adds multiple neighbors to a given precinct
     * @param precinctName the canon name of the precinct to add neighbors to
     * @param neighbors the list of neighbors to add
     * @return null if successful, else the canon name of the precinct that could not be found
     */
    public String addMultiNeighbors(String precinctName, List<String> neighbors) {
        Precinct queryResult;
        
        // Check if the target precinct exists
        queryResult = getPrecinct(precinctName);
        if (queryResult == null)
            return precinctName;
        
        // Check if neighbors exist
        for (String neighbor : neighbors) {
            queryResult = getPrecinct(neighbor);
            if (queryResult == null)
                return neighbor;
        }

        // Add all neighbors, at this point we know that the neighbors exist
        for (String neighbor : neighbors) {
            addNeighbor(precinctName, neighbor);
        }
        return null;
    }

    /**
     * Deletes multiple neighbors from a given precinct
     * @param precinctName the canon name of the precinct to delete neighbors from
     * @param neighbors the list of neighbors to delete
     * @return null if successful, else the canon name of the precinct that could not be found
     */
    public String deleteMultiNeighbors(String precinctName, List<String> neighbors) {
        Precinct queryResult;
        
        // Check if the target precinct exists
        queryResult = getPrecinct(precinctName);
        if (queryResult == null)
            return precinctName;
        
        // Check if neighbors exist
        for (String neighbor : neighbors) {
            queryResult = getPrecinct(neighbor);
            if (queryResult == null)
                return neighbor;
        }

        // Delete all neighbors, at this point we know that the neighbors exist
        for (String neighbor : neighbors) {
            deleteNeighbor(precinctName, neighbor);
        }

        return null;
    }


    // Returns the result of merge to controller
    public Precinct mergePrecincts(List<String> precinctNames) {
        PrecinctRepository pr = getPr();
        List<Precinct> precincts = pr.findAllById(precinctNames);
        Precinct mergedPrecinct = Precinct.mergePrecincts(precincts);
        if (mergedPrecinct == null) {
            return null;
        }
        Random rand = new Random();

        // Generate a unique canonical name for it
        String canonName = String.format("MergedPrecinct_%d",Math.abs(rand.nextLong()));
        while(pr.existsById(canonName)) {
            canonName = String.format("MergedPrecinct_%d",Math.abs(rand.nextLong()));
        }
        mergedPrecinct.setCanonName(canonName);

        // Delete all the mergees and conglomerate their neighbor lists
        Set<String> neighbors = new HashSet<>();
        Set<String> mergedNames = new HashSet<>();
        for (Precinct p : precincts) {
            neighbors.addAll(p.getNeighborCNames());
            mergedNames.add(p.getCanonName());
        }

        for (Precinct p : precincts) {
            rmPrecinct(p);
        }

        // Create neighbor links for the new precinct
        neighbors.removeAll(mergedNames);
        pr.saveAndFlush(mergedPrecinct);
        for (String s : neighbors) {
            addNeighborUntracked(canonName, s);
        }
        pr.flush();

        TransactionService ts = getTs();
        Iterator<String> iter = mergedNames.iterator();
        String beforeString = "";
        if (iter.hasNext()) {
            beforeString += iter.next();
        }
        while (iter.hasNext()) {
            beforeString += String.format(", %s", iter.next());
        }
        ts.logMergePrecinct(mergedPrecinct, beforeString);

        return mergedPrecinct;
    }

    public void remove(String precinctCanonName) {
        Precinct target = getPrecinct(precinctCanonName);
        if (target != null) {
            rmPrecinct(target);
        }
    }

    public Precinct updateDemoData(String pCName, int demoId, String field, int newVal) {
        PrecinctRepository pr = getPr();
        Precinct targetPrecinct = getPrecinct(pCName);
        int beforeVal;

        if (targetPrecinct == null) {
            return null;
        }

        DemographicData targetDD = targetPrecinct.getDemoData();
        if (field.equals("whitePop")) {
            beforeVal = targetDD.getWhitePop();
            targetDD.setWhitePop(newVal);
        }
        else if (field.equals("blackPop")) {
            beforeVal = targetDD.getBlackPop();
            targetDD.setBlackPop(newVal);
        }
        else if (field.equals("asianPop")) {
            beforeVal = targetDD.getAsianPop();
            targetDD.setAsianPop(newVal);
        }
        else if (field.equals("otherPop")) {
            beforeVal = targetDD.getOtherPop();
            targetDD.setOtherPop(newVal);
        } else {
            throw new IllegalArgumentException(field);
        }

        pr.flush();

        TransactionService ts = getTs();
        ts.logDemoDataChange(targetPrecinct, field, Integer.toString(beforeVal), Integer.toString(newVal));

        return targetPrecinct;
    }

    public Precinct updateBoundary(String pCName, String geometry) {
        PrecinctRepository pr = getPr();
        Precinct targetPrecinct = getPrecinct(pCName);

        if (targetPrecinct == null) {
            return null;
        }
        String oldGeometry = targetPrecinct.getGeometry();
        if(!Precinct.verifyIsValid(geometry)) {
            return null;
        }
        targetPrecinct.setGeometry(geometry);
        pr.flush();

        TransactionService ts = getTs();
        ts.logBoundaryChange(targetPrecinct, oldGeometry, geometry);

        return targetPrecinct;
    }

    public Precinct updateElection(String pCName, int eid, String field, int newVal) {
        PrecinctRepository pr = getPr();
        Precinct targetPrecinct = this.getPrecinct(pCName);
        if (targetPrecinct == null) {
            return null;
        }

        Election elec = targetPrecinct.findElection(eid);
        int beforeVal;
        if (field.equals("democraticVotes")) {
            beforeVal = elec.getDemocraticVotes();
            elec.setDemocraticVotes(newVal); 
        }
        else if (field.equals("republicanVotes")) {
            beforeVal = elec.getRepublicanVotes();
            elec.setRepublicanVotes(newVal);
        }
        else if (field.equals("libertarianVotes")) {
            beforeVal = elec.getLibertarianVotes();
            elec.setLibertarianVotes(newVal);
        }
        else if (field.equals("greenVotes")) {
            beforeVal = elec.getGreenVotes();
            elec.setGreenVotes(newVal);
        }
        else if (field.equals("otherVotes")) {
            beforeVal = elec.getOtherVotes();
            elec.setOtherVotes(newVal);
        }
        else {
            throw new IllegalArgumentException(field);
        }

        pr.flush();

        TransactionService ts = getTs();
        String bv = Integer.toString(beforeVal);
        String nv = Integer.toString(newVal);
        ts.logElecDataChange(targetPrecinct, elec, field, bv, nv);
        
        return targetPrecinct;
    }

    public Precinct setGhostPrecinct(String precinctName) {
        PrecinctRepository pr = getPr();
        Precinct precinct = getPrecinct(precinctName);
        if (precinct == null) {
            return null;
        }
        PrecinctType beforeType = precinct.getType();
        precinct.initialize();
        precinct.setDisplayName("Ghost Precinct");
        precinct.setType(PrecinctType.GHOST);

        pr.flush();
        TransactionService ts = getTs();
        ts.logInitializeGhost(precinct, beforeType);

        return precinct;
    }

    public Precinct createNewPrecinct(Precinct precinct, String parentName) {
        PrecinctRepository pr = getPr();
        DistrictService ds = getDs();

        String geometry = precinct.getGeometry();
        if(!Precinct.verifyIsValid(geometry)) {
            return null;
        }

        District parentDistrict = ds.getDistrict(parentName);
        Random rand = new Random();
        precinct.initialize();

        String canonName = String.format("ClientGenerated_%d",Math.abs(rand.nextLong()));
        while(pr.existsById(canonName)) {
            canonName = String.format("ClientGenerated_%d",Math.abs(rand.nextLong()));
        }
        precinct.setParentDistrict(parentDistrict);
        precinct.setCanonName(canonName);
        precinct.setType(PrecinctType.GHOST);
        ds.insertChildPrecinct(parentName, precinct);
        pr.saveAndFlush(precinct);

        TransactionService ts = getTs();
        ts.logNewPrecinct(precinct);

        return precinct;
    }

    public void renamePrecinctDisplay(String precinctCName, String newName) {
        PrecinctRepository pr = getPr();
        Precinct targetPrecinct = getPrecinct(precinctCName);
        String beforeName = targetPrecinct.getDisplayName();
        targetPrecinct.setDisplayName(newName);
        pr.flush();

        TransactionService ts = getTs();
        ts.logRenamePrecinct(targetPrecinct, beforeName, newName);
    }

    public void insertPrecinct(Precinct precinct, Boolean flush) {
        PrecinctRepository pr = getPr();
        String targetCName = precinct.getCanonName();
        Set<String> neighbors = precinct.getNeighborCNames();

        for (String neighbor : neighbors) {
            addNeighbor(neighbor, targetCName);
        }
        pr.save(precinct);
        if (flush) {
            pr.flush();
        }
    }

    public void insertMultiplePrecincts(List<Precinct> precincts) {
        PrecinctRepository pr = getPr();
        for (Precinct p : precincts) {
            insertPrecinct(p, false);
        }
        pr.flush();
    }
}
