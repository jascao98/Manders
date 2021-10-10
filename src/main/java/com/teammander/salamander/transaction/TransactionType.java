package com.teammander.salamander.transaction;

public enum TransactionType {
    NEW_PRECINCT("NEW_PRECINCT"),
    MERGE_PRECINCT("MERGE_PRECINCT"),
    RENAME_PRECINCT("RENAME_PRECINCT"),
    CHANGE_NEIGHBOR("CHANGE_NEIGHBOR"),
    CHANGE_DEMODATA("CHANGE_DEMODATA"),
    CHANGE_ELECDATA("CHANGE_ELECDATA"),
    CHANGE_BOUNDARY("CHANGE_BOUNDARY"),
    ERROR_RESOLUTION("ERROR_STATUS_CHANGE"),
    INIT_GHOST("INITIALIZE_GHOST");
    private final String text;

    private TransactionType(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return this.text;
    }
}