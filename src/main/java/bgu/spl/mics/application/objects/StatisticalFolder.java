package bgu.spl.mics.application.objects;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Holds statistical information about the system's operation.
 * This class aggregates metrics such as the runtime of the system,
 * the number of objects detected and tracked, and the number of landmarks identified.
 */
public class StatisticalFolder {
    private final AtomicInteger systemRuntime = new AtomicInteger(0);
    private final AtomicInteger numDetectedObjects = new AtomicInteger(0);
    private final AtomicInteger numTrackedObjects = new AtomicInteger(0);
    private final AtomicInteger numLandmarks = new AtomicInteger(0);
    private final AtomicInteger numOfSensors = new AtomicInteger(0);

    public StatisticalFolder() {}

    public void incrementSystemRuntime() {
        int oldVal;
        int newVal;
        do{
            oldVal = systemRuntime.get();
            newVal = oldVal + 1;
        }
        while(!systemRuntime.compareAndSet(oldVal, newVal));
    }
    public void incrementNumDetectedObjects(int num) {
        int oldVal;
        int newVal;
        do{
            oldVal = numDetectedObjects.get();
            newVal = oldVal + num;
        }
        while(!numDetectedObjects.compareAndSet(oldVal, newVal));
    }
    public void incrementNumTrackedObjects(int num) {
        int oldVal;
        int newVal;
        do{
            oldVal = numTrackedObjects.get();
            newVal = oldVal + num;
        }
        while(!numTrackedObjects.compareAndSet(oldVal, newVal));
    }
    public void incrementNumLandmarks(int num) {
        int oldVal;
        int newVal;
        do{
            oldVal = numLandmarks.get();
            newVal = num;
        }
        while(!numLandmarks.compareAndSet(oldVal, newVal));
    }

    public void decrementNumOfSensors() {
        int oldVal;
        int newVal;
        do{
            oldVal = numOfSensors.get();
            newVal = oldVal -1;
        }
        while(!numOfSensors.compareAndSet(oldVal, newVal));
    }

    public void setNumOfSensors(int num) {
        int oldVal;
        int newVal;
        do{
            oldVal = numOfSensors.get();
            newVal = num;
        }
        while(!numOfSensors.compareAndSet(oldVal, newVal));
    }

    public int getSystemRuntime() {
        return systemRuntime.get();
    }
    public int getNumDetectedObjects() {
        return numDetectedObjects.get();
    }
    public int getNumTrackedObjects() {
        return numTrackedObjects.get();
    }
    public int getNumLandmarks() {
        return numLandmarks.get();
    }
    public int getNumOfSensors() {
        return numOfSensors.get();
    }



}