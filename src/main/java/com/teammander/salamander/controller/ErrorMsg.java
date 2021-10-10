package com.teammander.salamander.controller;

public class ErrorMsg {
    private static String unableToFindFormat = "Unable to find %s";
    private static String badQueryFormat = "Bad request %s for %s";

    public static String unableToFindMsg(String query) {
        String formattedMsg = String.format(unableToFindFormat);
        return formattedMsg;
    }

    public static String badQueryMsg(String param, String query) {
        String formattedMsg = String.format(badQueryFormat);
        return formattedMsg;
    }

}