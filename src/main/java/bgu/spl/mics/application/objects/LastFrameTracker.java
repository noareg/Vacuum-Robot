package bgu.spl.mics.application.objects;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class LastFrameTracker {
    private static class LastFrameTrackHolder {
        private static LastFrameTracker instance= new LastFrameTracker();
    }

    private final Map<String, StampedDetectedObjects> lastCameraFrames = new ConcurrentHashMap<>();
    private final Map<String, List<TrackedObject>> lastLiDARFrames = new ConcurrentHashMap<>();

    private final Map<String, StampedDetectedObjects> currLastCameraFrames = new ConcurrentHashMap<>();
    private final Map<String, List<TrackedObject>> currLastLiDARFrames = new ConcurrentHashMap<>();

    private List<Pose> PoseList= new ArrayList<>();


    private LastFrameTracker() {}

    public static LastFrameTracker getInstance() {
        return LastFrameTrackHolder.instance;
    }

    public Map<String, StampedDetectedObjects> getLastCameraFrames() {
        return lastCameraFrames;
    }

    public Map<String, List<TrackedObject>> getLastLiDARFrames() {
        return lastLiDARFrames;
    }

    public Map<String, List<TrackedObject>> getCurrLiDARFrames() {
        return currLastLiDARFrames;
    }

    public List<Map<String, Object>> getPoses() {
        List<Map<String, Object>> poses = new ArrayList<>();
        for (Pose pose : PoseList) {
            Map<String, Object> poseData = new LinkedHashMap<>();
            poseData.put("time", pose.getTime());
            poseData.put("x", pose.getX());
            poseData.put("y", pose.getY());
            poseData.put("yaw", pose.getYaw());
            poses.add(poseData);
        }
        return poses;
    }

    public void setPoseList(List<Pose> PoseList) {
        this.PoseList = PoseList;
    }

    public void updateCameraFrame(String cameraId, StampedDetectedObjects stampedDetectedObjects) {
        synchronized (currLastCameraFrames) {
            synchronized (lastCameraFrames) {
                // If the current frame is being updated for the first time, transfer it to the last frame
                if (currLastCameraFrames.containsKey(cameraId)) {
                    lastCameraFrames.put(cameraId, currLastCameraFrames.get(cameraId));  // Save previous frame
                }
                // Update the current frame with the latest detected object
                currLastCameraFrames.put(cameraId, stampedDetectedObjects);
            }
        }
    }

    public void updateLiDARFrame(String lidarId, List<TrackedObject> trackedObjects) {
        synchronized (currLastLiDARFrames) {
            synchronized (lastLiDARFrames) {
                if (currLastLiDARFrames.containsKey(lidarId)) {
                    lastLiDARFrames.put(lidarId, currLastLiDARFrames.get(lidarId));
                }
                List<TrackedObject> list = new ArrayList<>(trackedObjects);
                currLastLiDARFrames.put(lidarId, list);
            }
        }
    }





}
