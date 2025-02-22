package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a landmark in the environment map.
 * Landmarks are identified and updated by the FusionSlam service.
 */
public class LandMark {
    private final String id;
    private final String Description;
    private ArrayList<CloudPoint> Coordinates;


    LandMark(String id, String Description) {
        this.id = id;
        this.Description = Description;
        this.Coordinates = new ArrayList<>();
    }
    public String getId() {
        return id;
    }

    public String getDescription() {
        return Description;
    }

    public List<Map<String, Double>> getCoordinatesMap() {
        List<Map<String, Double>> coordinatesList = new ArrayList<>();
        // Convert each CloudPoint to a Map with "x" and "y" keys
        for (CloudPoint cloudPoint : Coordinates) {
            Map<String, Double> pointMap = new HashMap<>();
            pointMap.put("x", cloudPoint.getX());
            pointMap.put("y", cloudPoint.getY());
            coordinatesList.add(pointMap);
        }
        return coordinatesList;
    }


    public ArrayList<CloudPoint> getCoordinates() {
        return Coordinates;
    }
    public void setCoordinates(ArrayList<CloudPoint> coordinates) {
        this.Coordinates = coordinates;
    }


}