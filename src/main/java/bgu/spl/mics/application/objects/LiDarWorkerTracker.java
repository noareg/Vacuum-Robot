package bgu.spl.mics.application.objects;


import java.util.*;

/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */
public class LiDarWorkerTracker {
    private final int id;
    private final int frequency;
    private STATUS status;
    private final List<TrackedObject> lastTrackedObjects;
    private final LiDarDataBase database;
    private final Map<Integer, List<TrackedObject>> mapOfDetectedObjects = new HashMap<>();


    public LiDarWorkerTracker(int Id, int frequency) {
        this.id = Id;
        this.frequency = frequency;
        this.status = STATUS.UP;
        this.lastTrackedObjects = new ArrayList<>();
        this.database = LiDarDataBase.getInstance();
    }

    public int getId() {
        return id;
    }

    public STATUS getStatus() {
        return status;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public List<TrackedObject> handlesDetectedObjects(StampedDetectedObjects stampedDetectedObjects) {
        List<TrackedObject> trackedObjects = new ArrayList<>();
        int timeDetection = stampedDetectedObjects.getTime();
        for (StampedCloudPoints stampedCloudPoints : database.getCloudPointsList()) {
            if (stampedCloudPoints.getTime() == timeDetection) {
                for (DetectedObject detectedObject : stampedDetectedObjects.getDetectedObjects()) {
                    if (stampedCloudPoints.getId().equals(detectedObject.getId())) {
                        String id = detectedObject.getId();
                        String description = detectedObject.getDescription();
                        List<CloudPoint> coordinates = stampedCloudPoints.convertToCloudPoints();
                        TrackedObject trackedObject = new TrackedObject(id, timeDetection, description, coordinates);
                        trackedObjects.add(trackedObject);
                    }
                }
            }
        }
        if (!trackedObjects.isEmpty()) LastFrameTracker.getInstance().updateLiDARFrame("LiDarTrackerWorker"+id,trackedObjects);
        mapOfDetectedObjects.merge(timeDetection, trackedObjects, (existingList, newList) -> {
            existingList.addAll(newList); // Merge both lists
            return existingList; // Return the merged list
        });
        return trackedObjects;
    }

    public boolean foundError(int time) {
        for (StampedCloudPoints stampedCloudPoints : database.getCloudPointsList()) {
            if (stampedCloudPoints.getTime() == time) {
                if (stampedCloudPoints.getId().equals("ERROR")) {
                    status = STATUS.ERROR;
                    return true;
                }
            }
        }
        return false;
    }

    public List<List<TrackedObject>> sendingEvent (int tickTime){
        List<List<TrackedObject>> listsOfDetectedObjects = new ArrayList<>();
        Iterator<Map.Entry<Integer, List<TrackedObject>>> iterator = mapOfDetectedObjects.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, List<TrackedObject>> entry = iterator.next();
            List<TrackedObject> list = entry.getValue();
            if (list == null) {
                setStatus(STATUS.DOWN);
            }
            if (list!=null && entry.getKey() <= tickTime - frequency) {
                listsOfDetectedObjects.add(list);
                iterator.remove();
            }
        }
        return listsOfDetectedObjects;
    }
}