package com.teammander.salamander.map;

public enum ErrorType {
    MULTI_POLYGON("MULTIPOLYGON"),
    ENCLOSED("ENCLOSED"),
    OVERLAP("OVERLAP"),
    UNCLOSED("UNCLOSED"),
    ZERO_POPULATION("ZERO_POP"),
    UNPROPORTIONAL_ELEC("UNPROPORTIONAL_ELEC"),
    GHOST("GHOST");
    private final String text;

    private ErrorType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return this.text;
    }
}