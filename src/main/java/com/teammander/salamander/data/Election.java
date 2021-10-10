package com.teammander.salamander.data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "ELECTIONS")
public class Election {

    int electionId;
    Year year;
    ElectionType type;
    int democraticVotes;
    int republicanVotes;
    int libertarianVotes;
    int greenVotes;
    int otherVotes;

    @Id
    @GeneratedValue
    @Column(name = "ID")
    public int getElectionId() {
        return electionId;
    }

    public void setElectionId(int electionId) {
        this.electionId = electionId;
    }

    @Enumerated(EnumType.STRING)
    public Year getYear() {
        return year;
    }

    public void setYear(Year year) {
        this.year = year;
    }

    @Enumerated(EnumType.STRING)
    public ElectionType getType() {
        return type;
    }

    public void setType(ElectionType type) {
        this.type = type;
    }

    @Column(name = "demo_vote")
    public int getDemocraticVotes() {
        return democraticVotes;
    }

    public void setDemocraticVotes(int democraticVotes) {
        if (democraticVotes < 0) {
            throw new IllegalArgumentException("Negative number");
        }
        this.democraticVotes = democraticVotes;
    }

    @Column(name = "repub_vote")
    public int getRepublicanVotes() {
        return republicanVotes;
    }

    public void setRepublicanVotes(int republicanVotes) {
        if (republicanVotes < 0) {
            throw new IllegalArgumentException("Negative number");
        }
        this.republicanVotes = republicanVotes;
    }

    @Column(name = "lib_vote")
    public int getLibertarianVotes() {
        return libertarianVotes;
    }

    public void setLibertarianVotes(int libertarianVotes) {
        if (libertarianVotes < 0) {
            throw new IllegalArgumentException("Negative number");
        }
        this.libertarianVotes = libertarianVotes;
    }

    @Column(name = "green_vote")
    public int getGreenVotes() {
        return greenVotes;
    }

    public void setGreenVotes(int greenVotes) {
        if (greenVotes < 0) {
            throw new IllegalArgumentException("Negative number");
        }
        this.greenVotes = greenVotes;
    }

    @Column(name = "other_vote")
    public int getOtherVotes() {
        return otherVotes;
    }

    public void setOtherVotes(int otherVotes) {
        if (otherVotes < 0) {
            throw new IllegalArgumentException("Negative number");
        }
        this.otherVotes = otherVotes;
    }

}
