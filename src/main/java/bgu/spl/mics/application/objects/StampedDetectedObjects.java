package bgu.spl.mics.application.objects;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Represents objects detected by the camera at a specific timestamp.
 * Includes the time of detection and a list of detected objects.
 */
public class StampedDetectedObjects {
    private int time;
    @SerializedName("detectedObjects")
    private List<DetectedObject> DetectedObjects;

    public StampedDetectedObjects (int time, List<DetectedObject> DetectedObjects){
        this.time = time;
        this.DetectedObjects = DetectedObjects;
    }


    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public List<DetectedObject> getDetectedObjects() {
        return DetectedObjects;
    }

    public void setDetectedObjects(List<DetectedObject> detectedObjects) {
        this.DetectedObjects = detectedObjects;
    }
}