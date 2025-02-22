package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;


public class CrashedBroadcast implements Broadcast {
    private final String sensorName;   // The name of the sensor that caused the crash
    private final String errorDescription;  // Description of the error

    public CrashedBroadcast(String sensorName, String errorDescription) {
        this.sensorName = sensorName;
        this.errorDescription = errorDescription;
    }

    public String getSensorName() {
        return sensorName;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

}
