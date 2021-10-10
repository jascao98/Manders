package com.teammander.salamander.transaction;

import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import org.hibernate.annotations.CreationTimestamp;

@Entity(name = "ERROR_TRANSACTIONS")
public class Transaction {

    int tid;
    TransactionType transType;
    String before;
    String after;
    String whoCanon;
    String whoDisplay;
    String what;
    List<Comment> comments;
    Date timeCreated;

    @Id
    @GeneratedValue
    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    @Column(name = "who_canon")
    public String getWhoCanon() {
        return this.whoCanon;
    }

    public void setWhoCanon(String whoCanon) {
        this.whoCanon = whoCanon;
    }

    @Column(name = "who_display")
    public String getWhoDisplay() {
        return this.whoDisplay;
    }

    public void setWhoDisplay(String whoDisplay) {
        this.whoDisplay = whoDisplay;
    }

    @Enumerated(EnumType.STRING)
    @Column(name = "trans_type")
    public TransactionType getTransType() {
        return this.transType;
    }

    public void setTransType(TransactionType transType) {
        this.transType = transType;
    }

    @Column(name = "what")
    public String getWhat() {
        return this.what;
    }

    public void setWhat(String what) {
        this.what = what;
    }

    @Lob
    @Column(name = "before_val")
    public String getBefore() {
        return before;
    }

    public void setBefore(String before) {
        this.before = before;
    }

    @Lob
    @Column(name = "after_val")
    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }

    @OneToMany(mappedBy = "ownerTransaction", cascade = CascadeType.ALL)
    @JsonManagedReference
    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comment) {
        this.comments = comment;
    }

    @Basic
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "time_created")
    public Date getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(Date timeCreated) {
        this.timeCreated = timeCreated;
    }
}
