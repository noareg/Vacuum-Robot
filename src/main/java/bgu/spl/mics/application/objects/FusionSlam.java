package bgu.spl.mics.application.objects;

import java.util.*;

/** Manages the fusion of sensor data for simultaneous localization and mapping (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam exists.
 */
public class FusionSlam {
    private static class FusionSlamHolderHolder{
        private static FusionSlam instance = new FusionSlam();
    }
    private final ArrayList<LandMark> landmarks; // List of Landmarks
    private Pose currentPose; // The robot's current pose
    private final List<Pose> poseHistory; // History of poses for reference
    private final List<List<TrackedObject>> trackedObjectsWaiting = new ArrayList<>();

    // Private constructor to prevent direct instantiation
    private FusionSlam() {
        this.landmarks = new ArrayList<>();
        this.poseHistory = new ArrayList<>();
        this.currentPose = null;
    }

    /**
     * Retrieves the single instance of FusionSlam.
     *
     * @return The FusionSlam instance.
     */
    public static FusionSlam getInstance() {
        return FusionSlamHolderHolder.instance;
    }

    public int getSizeOfLandmarks(){
        return this.landmarks.size();
    }

    public ArrayList<LandMark> getLandmarks(){
        return this.landmarks;
    }


    /**
     * Updates the global map with tracked objects.
     *
     * @param trackedObjects List of objects tracked by sensors.
     */
    public void updateLandmarks(List<TrackedObject> trackedObjects) {
        trackedObjectsWaiting.add(trackedObjects);

        Iterator<List<TrackedObject>> iterator = trackedObjectsWaiting.iterator(); // Use an explicit iterator
        while (iterator.hasNext()) {
            List<TrackedObject> objList = iterator.next();
            if (objList.get(0).getTime() <= currentPose.getTime()) {
                for (TrackedObject obj : trackedObjects) {
                    String objectId = obj.getId();
                    ArrayList<CloudPoint> globalCoordinates = transformToGlobal(obj.getCoordinates(), obj.getTime());

                    // Search for the Landmark by ID
                    LandMark currLM = findLandmarkById(objectId);

                    if (currLM != null) {
                        // Update existing landmark with averaged coordinates
                        ArrayList<CloudPoint> updatedCoordinates = averageCoordinates(currLM.getCoordinates(), globalCoordinates);
                        currLM.setCoordinates(updatedCoordinates);
                    } else {
                        // Add a new Landmark
                        LandMark newLandmark = new LandMark(objectId, obj.getDescription());
                        newLandmark.setCoordinates(globalCoordinates);
                        landmarks.add(newLandmark); // Add new Landmark to the list
                    }
                }
                iterator.remove(); // Safely remove the current list using the iterator
            }
        }
    }

    /**
     * Averages the coordinates of two lists of CloudPoints.
     *
     * @param existing The existing global coordinates of the landmark.
     * @param newPoints The new coordinates to be merged.
     * @return The averaged coordinates.
     */
    //check calc!!!
    private ArrayList<CloudPoint> averageCoordinates(List<CloudPoint> existing, List<CloudPoint> newPoints) {
        ArrayList<CloudPoint> averaged = new ArrayList<>();

        for (int i = 0; i < Math.min(existing.size(), newPoints.size()); i++) {
            double avgX = ((existing.get(i).getX() ) + (newPoints.get(i).getX())) / 2;
            double avgY = ((existing.get(i).getY() ) + (newPoints.get(i).getY())) / 2;
            averaged.add(new CloudPoint(avgX, avgY));
        }

        // If one list is larger than the other
        for (int i = Math.min(existing.size(), newPoints.size()); i < existing.size(); i++) {
            averaged.add(existing.get(i));
        }
        for (int i = Math.min(existing.size(), newPoints.size()); i < newPoints.size(); i++) {
            averaged.add(newPoints.get(i));
        }

        return averaged;
    }



    /**
     * Updates the robot's current pose.
     *
     * @param pose The new pose of the robot.
     */
    public void updatePose(Pose pose) {
        this.currentPose = pose;
        this.poseHistory.add(pose); // Keep track of pose history
    }

    /**
     * Converts local coordinates to global coordinates using the robot's pose at the given timestamp.
     *
     * @param localCoordinates The list of local coordinates.
     * @param timestamp        The timestamp of the data.
     * @return List of global coordinates.
     */
    public ArrayList<CloudPoint> transformToGlobal(List<CloudPoint> localCoordinates, int timestamp) {
        Pose poseAtTime = getPoseAtTime(timestamp);
        ArrayList<CloudPoint> globalCoordinates = new ArrayList<>();

        for (CloudPoint point : localCoordinates) {
            double radians = Math.toRadians(poseAtTime.getYaw());
            double cosTheta = Math.cos(radians);
            double sinTheta = Math.sin(radians);

            // Apply rotation first
            double rotatedX = cosTheta * point.getX() - sinTheta * point.getY();
            double rotatedY = sinTheta * point.getX() + cosTheta * point.getY();

            // Then apply translation
            double globalX = rotatedX + poseAtTime.getX();
            double globalY = rotatedY + poseAtTime.getY();


            globalCoordinates.add(new CloudPoint(globalX, globalY));
        }

        return globalCoordinates;
    }

    /**
     * Retrieves the robot's pose at a specific time.
     *
     * @param timestamp The timestamp to look up.
     * @return The robot's pose at the specified timestamp.
     */
    private Pose getPoseAtTime(int timestamp) {
        // Find the closest pose at or before the timestamp (or return default pose)
        for (int i = poseHistory.size() - 1; i >= 0; i--) {
            Pose pose = poseHistory.get(i);
            if (pose.getTime() <= timestamp) {
                return pose;
            }
        }
        return currentPose; // If no pose found, return the most recent pose
    }
    private LandMark findLandmarkById(String id) {
        for (LandMark landmark : landmarks) {
            if (landmark.getId().equals(id)) {
                return landmark;
            }
        }
        return null;
    }
}