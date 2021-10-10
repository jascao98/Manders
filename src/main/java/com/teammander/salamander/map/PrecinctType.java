package com.teammander.salamander.map;

public enum PrecinctType {
    GHOST("GHOST"), 
    NORMAL("NORMAL"), 
    GAP("GAP");
    private final String text;

    private PrecinctType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return this.text;
    }
}