package com.teammander.salamander.data;

public enum ElectionType{
    PRESIDENTIAL("PRESIDENTIAL"), 
    CONGRESSIONAL("CONGRESSIONAL");
    private final String text;

    private ElectionType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return this.text;
    }
}