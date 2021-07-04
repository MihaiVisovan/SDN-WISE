package com.github.sdnwiselab.sdnwise.controller;

public class DataManagerConstants {

    // measurement types
    public static final String TEMPERATURE = "TEMPERATURE", HUMIDITY = "HUMIDITY", LIGHT = "LIGHT", VOLTAGE = "VOLTAGE";

    // operation types
    public static final String AVERAGE = "AVERAGE", SUM = "SUM", MAXIMUM = "MAXIMUM", MINIMUM = "MINIMUM";
    
    // format permitted: DD/MM/YYY
    public static final String DATE_REGEX = "\\d{4}\\-(0?[1-9]|1[012])\\-(3[01]|[0-2]?[1-9])";
    
    // any number between 0-49 with space before and after the number
    public static final String MOTES_NUMBER_REGEX = "([0-4]?[0-9])";
}
