package com.github.sdnwiselab.sdnwise.mote.standalone;

import java.io.Serializable;
import java.time.LocalDateTime;

public class SensorData implements Serializable{
    
    public final LocalDateTime dateTime;
    public final String type;
    public final double value;
    public final int epoch;
    
     /**
     * Sensor data object
     *
     * @param type measured type, this can be light, voltage, humidity or temperature
     * @param value measured value
     * @param date the day when the measurement was made
     * @param time the hour when the measurement was made
     * @param epoch epoch when the measurement was made
     */

    public SensorData(
        final String type,
        final double value,
        final LocalDateTime dateTime,
        final int epoch
        )
    {
       this.type = type;
       this.value = value;
       this.dateTime = dateTime;
       this.epoch = epoch;
    }

    @Override
    public String toString() {
        return '\n' + "Value: " 
                + value 
                + "; DateTime: " 
                + dateTime 
                + "; Epoch: " 
                + epoch;
    }

    public String getType() {
        return type;
    }

    public double getValue() {
        return value;
    }

    public int getEpoch() {
        return epoch;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }
}
