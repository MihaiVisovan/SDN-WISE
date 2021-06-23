package com.github.sdnwiselab.sdnwise.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.sdnwiselab.sdnwise.mote.standalone.Mote;

// borasc cu importurile astea
import static com.github.sdnwiselab.sdnwise.controller.DataManagerConstants.TEMPERATURE;
import static com.github.sdnwiselab.sdnwise.controller.DataManagerConstants.HUMIDITY;
import static com.github.sdnwiselab.sdnwise.controller.DataManagerConstants.LIGHT;
import static com.github.sdnwiselab.sdnwise.controller.DataManagerConstants.VOLTAGE;
import static com.github.sdnwiselab.sdnwise.controller.DataManagerConstants.AVERAGE;
import static com.github.sdnwiselab.sdnwise.controller.DataManagerConstants.MAXIMUM;
import static com.github.sdnwiselab.sdnwise.controller.DataManagerConstants.MINIMUM;
import static com.github.sdnwiselab.sdnwise.controller.DataManagerConstants.SUM;
import static com.github.sdnwiselab.sdnwise.controller.DataManagerConstants.DATE_REGEX;
import static com.github.sdnwiselab.sdnwise.controller.DataManagerConstants.MOTES_NUMBER_REGEX;

public class DataManager {

    /**
     * All motes used within the app
     */    
    private List<Mote> motes;

    public HashMap<List<String>, List<Object>> motesData;

    /**
     * The Controller managed by this ui.
     */
    private final AbstractController controller;
    

    public DataManager(AbstractController ctrl, List<Mote> mts) {
        motes = mts;
        controller = ctrl;
    }

    public List<String> processQuery(String query) {
        List<String> processedQuery = new ArrayList<String>();
        // check measurement type
        if(query.contains(TEMPERATURE.toLowerCase())) {
            processedQuery.add(0, TEMPERATURE);
        }
        if(query.contains(HUMIDITY.toLowerCase())) {
            processedQuery.add(0, HUMIDITY);
        }
        if(query.contains(LIGHT.toLowerCase())) {
            processedQuery.add(0, LIGHT);
        }
        if(query.contains(VOLTAGE.toLowerCase())) {
            processedQuery.add(0, VOLTAGE);
        }

        // check operation type
        if(query.contains(SUM.toLowerCase())) {
            processedQuery.add(1, SUM);
        }
        if(query.contains(AVERAGE.toLowerCase())) {
            processedQuery.add(1, AVERAGE);
        }
        if(query.contains(MAXIMUM.toLowerCase())) {
            processedQuery.add(1, MAXIMUM);
        }
        if(query.contains(MINIMUM.toLowerCase())) {
            processedQuery.add(1, MINIMUM);
        }

        // check dates range
        Pattern datePattern = Pattern.compile(DATE_REGEX);
        Matcher dateMatcher = datePattern.matcher(query);
        processedQuery.add(2, dateMatcher.group(0));
        processedQuery.add(3, dateMatcher.group(1));

        
        return processedQuery;
    }

    public List<String> getRequestedMotes (String query) {
        Pattern motesPattern = Pattern.compile(MOTES_NUMBER_REGEX);
        Matcher motesMatcher = motesPattern.matcher(query);

        List<String> requestedMotes = new ArrayList<String>();
        
        int i = 0;
        while(motesMatcher.find()) {
            requestedMotes.add("0." + motesMatcher.group(i));
            i++;
        }
        return requestedMotes;
    }

    public HashMap<List<String>, List<Object>> getMotesData(List<String> processedQuery, List<String> requestedMotes) {
        // what measures are requested
        String measurementType = processedQuery.get(0);
        // LocalDateTime dateTimeOne = LocalDateTime.parse("2004-01-13T23:45:24.22222");

        LocalDateTime dateTimeOne = LocalDateTime.parse(processedQuery.get(1));
        LocalDateTime dateTimeTwo = LocalDateTime.parse(processedQuery.get(2));

        // TO DO: implement these math operations
        String operationType = processedQuery.get(3);

        for(Mote mote: motes) {
            String moteAddress = mote.getCore().getMyAddress().toString();

            for(String str: requestedMotes) {

                // check if user requested current mote
                if (str.compareTo(moteAddress) == 0) {
                    // get the data between specified dates
                    List<Object> dataWithinDates = mote.getCore().getDataWithinDates(measurementType, dateTimeOne, dateTimeTwo);

                    List<String> moteIdMeasurementType = new ArrayList<String>();
                    moteIdMeasurementType.add(mote.getCore().getMyAddress().toString());
                    moteIdMeasurementType.add(measurementType);

                    // MoteId + MeasurementType + Data Requested        
                    motesData.put(moteIdMeasurementType, dataWithinDates);
                }   
            }
        }

        return motesData;
    }
}
