package com.teammander.salamander.service;

import com.teammander.salamander.data.Election;
import com.teammander.salamander.map.DataError;
import com.teammander.salamander.map.Precinct;
import com.teammander.salamander.map.PrecinctType;
import com.teammander.salamander.repository.CommentRepository;
import com.teammander.salamander.repository.TransactionRepository;
import com.teammander.salamander.transaction.Comment;
import com.teammander.salamander.transaction.Transaction;
import com.teammander.salamander.transaction.TransactionType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {
    TransactionRepository tr;
    CommentRepository cr;

    @Autowired
    public TransactionService(TransactionRepository tr, CommentRepository cr) {
        this.tr = tr;
    }

    public TransactionRepository getTr() {
        return this.tr;
    }

    public CommentRepository getCr() {
        return this.cr;
    }

    public void addTransaction(Transaction trans) {
        tr.saveAndFlush(trans);
    }

    public List<Transaction> getAllTransactions() {
        return tr.findAll();
    }

    public void addComment(int tid, String comment) {
        TransactionRepository tr = getTr();
        Transaction targetTrans = tr.findById(tid).orElseThrow();
        Comment newComment = new Comment();
        newComment.setOwnerTransaction(targetTrans);
        newComment.setComment(comment);
        targetTrans.getComments().add(newComment);
        tr.flush();
    }

    public void updateComment(int cid, String update) {
        CommentRepository cr = getCr();
        Comment targetComment = cr.findById(cid).orElseThrow();
        targetComment.setComment(update);
        cr.flush();
    }

    public void logNewPrecinct(Precinct targetPrecinct) {
        Transaction nTrans = new Transaction();
        nTrans.setTransType(TransactionType.NEW_PRECINCT);
        nTrans.setWhoCanon(targetPrecinct.getCanonName());
        nTrans.setWhoDisplay(targetPrecinct.getDisplayName());
        nTrans.setWhat("New Precinct");
        addTransaction(nTrans);
    }

    public void logMergePrecinct(Precinct targetPrecinct, String mergedString) {
        Transaction nTrans = new Transaction();
        nTrans.setTransType(TransactionType.MERGE_PRECINCT);
        nTrans.setWhat("Merge Precinct");
        nTrans.setWhoCanon(targetPrecinct.getCanonName());
        nTrans.setWhoDisplay(targetPrecinct.getDisplayName());
        nTrans.setBefore(mergedString);
        nTrans.setAfter(targetPrecinct.getCanonName());
        addTransaction(nTrans);
    }

    public void logRenamePrecinct(Precinct targetPrecinct, String beforeName, String afterName) {
        Transaction nTrans = new Transaction();
        nTrans.setTransType(TransactionType.RENAME_PRECINCT);
        nTrans.setWhat("Display Name Change");
        nTrans.setWhoCanon(targetPrecinct.getCanonName());
        nTrans.setWhoDisplay(beforeName);
        nTrans.setBefore(beforeName);
        nTrans.setAfter(afterName);
        addTransaction(nTrans);
    }

    public void logChangeNeighbor(Precinct p1, Precinct p2, boolean add) {
        String edgePresent = " <--> ";
        String edgeAbsent = " <-/-> ";
        String beforeEdge;
        String afterEdge;
        if (add) {
            beforeEdge = edgeAbsent;
            afterEdge = edgePresent;
        } else {
            beforeEdge = edgePresent;
            afterEdge = edgeAbsent;
        }

        Transaction nTrans = new Transaction();
        String p1Display = p1.getDisplayName();
        String p2Display = p2.getDisplayName();
        String precinctName1 = p1.getCanonName();
        String precinctName2 = p2.getCanonName();
        nTrans.setTransType(TransactionType.CHANGE_NEIGHBOR);
        nTrans.setBefore(p1Display + beforeEdge + p2Display);
        nTrans.setAfter(p1Display + afterEdge + p2Display);
        nTrans.setWhoCanon(precinctName1 + ", " + precinctName2);
        nTrans.setWhoDisplay(p1Display + ", " + p2Display);
        nTrans.setWhat("Neighborship");
        addTransaction(nTrans);
    }

    public void logDemoDataChange(Precinct targetPrecinct, String field, String beforeVal, String afterVal) {
        Transaction nTrans = new Transaction();
        nTrans.setTransType(TransactionType.CHANGE_DEMODATA);
        nTrans.setWhoCanon(targetPrecinct.getCanonName());
        nTrans.setWhoDisplay(targetPrecinct.getDisplayName());
        nTrans.setWhat(field);
        nTrans.setBefore(beforeVal);
        nTrans.setAfter(afterVal);
        addTransaction(nTrans);
    }

    public void logElecDataChange(Precinct targetPrecinct, Election elec, String field, String beforeVal, String afterVal) {
        Transaction nTrans = new Transaction();
        String whatString = String.format("%s %s %s", elec.getYear(), elec.getType(), field);
        nTrans.setTransType(TransactionType.CHANGE_ELECDATA);
        nTrans.setWhoCanon(targetPrecinct.getCanonName());
        nTrans.setWhoDisplay(targetPrecinct.getDisplayName());
        nTrans.setWhat(whatString);
        nTrans.setBefore(beforeVal);
        nTrans.setAfter(afterVal);
        addTransaction(nTrans);
    }

    public void logBoundaryChange(Precinct targetPrecinct, String oldGeom, String newGeom) {
        Transaction nTrans = new Transaction();
        nTrans.setTransType(TransactionType.CHANGE_BOUNDARY);
        nTrans.setWhoCanon(targetPrecinct.getCanonName());
        nTrans.setWhoDisplay(targetPrecinct.getDisplayName());
        nTrans.setWhat("Boundary Data");
        nTrans.setBefore(oldGeom);
        nTrans.setAfter(newGeom);
        addTransaction(nTrans);
    }

    public void logInitializeGhost(Precinct targetPrecinct, PrecinctType beforeType) {
        Transaction nTrans = new Transaction();
        nTrans.setTransType(TransactionType.INIT_GHOST);
        nTrans.setWhoCanon(targetPrecinct.getCanonName());
        nTrans.setWhoDisplay(targetPrecinct.getDisplayName());
        nTrans.setWhat("Initialize Ghost Precinct");
        nTrans.setBefore(beforeType.toString());
        nTrans.setAfter(targetPrecinct.getType().toString());
        addTransaction(nTrans);
    }

    public void logErrorStatusChange(DataError targetError, boolean oldStatus, boolean newStatus) {
        Transaction nTrans = new Transaction();
        nTrans.setTransType(TransactionType.ERROR_RESOLUTION);
        nTrans.setWhoCanon(targetError.getAffectedPrct());
        nTrans.setWhoDisplay(targetError.getPrecinctDisplayName());
        String errType = targetError.getEType().toString();
        String whatString = String.format("%s Error Status Change", errType);
        nTrans.setWhat(whatString);
        String beforeString = Boolean.toString(oldStatus);
        String afterString = Boolean.toString(newStatus);
        nTrans.setBefore(beforeString);
        nTrans.setAfter(afterString);
        addTransaction(nTrans);
    }
}
