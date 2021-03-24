package com.github.sdnwiselab.sdnwise.mote.standalone;

public class SensorData {
    
    public final String type, value, date, time, epoch;
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
        final String value,
        final String date,
        final String time,
        final String epoch
        )
    {
       this.type = type;
       this.value = value;
       this.date = date;
       this.time = time;
       this.epoch = epoch;
    }

    @Override
    public String toString() {
        return "Measurement: " + type + " value: " + value + "date: " + date + " time: " + time + " epoch: " + epoch + '\n';
    }
}
