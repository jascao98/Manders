package com.teammander.salamander.data;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity(name = "DEMOGRAPHIC_DATA")
public class DemographicData {
    int demographicDataID;
    int whitePop;
    int blackPop;
    int asianPop;
    int otherPop;

    @Id
    @GeneratedValue
    @Column(name = "ID")
    public int getDemographicDataID() {
        return demographicDataID;
    }

    public void setDemographicDataID(int demographicDataID) {
        this.demographicDataID = demographicDataID;
    }

    @Column(name = "white_pop")
    public int getWhitePop() {
        return whitePop;
    }

    public void setWhitePop(int whitePop) {
        if (whitePop < 0) {
            throw new IllegalArgumentException("Negative number");
        }
        this.whitePop = whitePop;
    }

    @Column(name = "black_pop")
    public int getBlackPop() {
        return blackPop;
    }

    public void setBlackPop(int blackPop) {
        if (blackPop < 0) {
            throw new IllegalArgumentException("Negative number");
        }
        this.blackPop = blackPop;
    }

    @Column(name = "asian_pop")
    public int getAsianPop() {
        return asianPop;
    }

    public void setAsianPop(int asianPop) {
        if (asianPop < 0) {
            throw new IllegalArgumentException("Negative number");
        }
        this.asianPop = asianPop;
    }

    @Column(name = "other_pop")
    public int getOtherPop() {
        return otherPop;
    }

    public void setOtherPop(int otherPop) {
        if (otherPop < 0) {
            throw new IllegalArgumentException("Negative number");
        }
        this.otherPop = otherPop;
    }

    static public DemographicData mergeDemoData(List<DemographicData> demoDatas) {
        DemographicData mergedData = new DemographicData();
        for (DemographicData data : demoDatas) {
            mergedData.setWhitePop(mergedData.getWhitePop() + data.getWhitePop());
            mergedData.setBlackPop(mergedData.getBlackPop() + data.getBlackPop());
            mergedData.setAsianPop(mergedData.getAsianPop() + data.getAsianPop());
            mergedData.setOtherPop(mergedData.getOtherPop() + data.getOtherPop());
        }
        return mergedData;
    }
}
