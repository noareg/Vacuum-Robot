package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a group of cloud points corresponding to a specific timestamp.
 * Used by the LiDAR system to store and process point cloud data for tracked objects.
 */
public class StampedCloudPoints {
    private String id;
    private int time;
    private List<List<Double>> cloudPoints;

    public StampedCloudPoints(String Id, int time) {
        this.id = Id;
        this.time = time;
        this.cloudPoints = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public int getTime() {
        return time;
    }

    public List<CloudPoint> convertToCloudPoints() {
        List<CloudPoint> convertCloudPoints = new ArrayList<>();
        for (List<Double> list : cloudPoints) {
            convertCloudPoints.add(new CloudPoint(list.get(0),list.get(1)));
        }
        return convertCloudPoints;
    }


    public void setId(String id) {
        this.id = id;
    }


    public void setTime(int time) {
        this.time = time;
    }


    public void setCloudPoints(List<List<Double>> cloudPoints) {
        this.cloudPoints = cloudPoints;
    }


}