package com.teammander.salamander.data;

public enum Year {
    SIXTEEN(2016), 
    EIGHTEEN(2018);
    private int value;

    private Year(int val) {
        this.value = val;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        String enumStr = Integer.toString(this.value);
        return enumStr;
    }
}