package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

public class TerminatedBroadcast implements Broadcast {
    private final String sensorName;

    public TerminatedBroadcast(String sensorName) {
        this.sensorName = sensorName;

    }
    public String getSensorName() {
        return sensorName;
    }
}
