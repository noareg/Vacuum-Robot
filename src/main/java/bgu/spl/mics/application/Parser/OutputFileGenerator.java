package bgu.spl.mics.application.Parser;


import bgu.spl.mics.application.objects.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OutputFileGenerator {
    public static void generateOutputFile(StatisticalFolder stats, FusionSlam fusionSlam, boolean errorOccurred, String errorDescription, String faultySensor, Path parentPath) {

        // If an error occurred, fill in the error details
        if (errorOccurred) {
            Map<String, StampedDetectedObjects> cameraFrames = LastFrameTracker.getInstance().getLastCameraFrames();
            Map<String, List<TrackedObject>> lidarFrames = LastFrameTracker.getInstance().getLastLiDARFrames();

            if(faultySensor.substring(0,6).equals("Camera"))
                lidarFrames = LastFrameTracker.getInstance().getCurrLiDARFrames();


            OutputDataError outputDataError = new OutputDataError();
            outputDataError.setError(errorDescription);
            outputDataError.setFaultySensor(faultySensor);
            outputDataError.setCameraLastFrames(cameraFrames);
            outputDataError.setLidarLastFrames(lidarFrames);
            outputDataError.setPoses(LastFrameTracker.getInstance().getPoses());
            Map<String, Object> statistics = new LinkedHashMap<>();
            statistics.put("systemRuntime", stats.getSystemRuntime());
            statistics.put("numDetectedObjects", stats.getNumDetectedObjects());
            statistics.put("numTrackedObjects", stats.getNumTrackedObjects());
            statistics.put("numLandmarks", stats.getNumLandmarks());

            Map<String, LandmarkData> landMarks = new HashMap<>();
            for (LandMark landmark : fusionSlam.getLandmarks()) {
                landMarks.put(landmark.getId(), new LandmarkData(landmark.getId(), landmark.getDescription(), landmark.getCoordinatesMap()));
            }
            statistics.put("landMarks", landMarks);

            outputDataError.setStatistics(statistics);
            writeOutputFile(outputDataError, parentPath);
        }
        else {
            OutputData outputData = new OutputData();
            outputData.setSystemRuntime(stats.getSystemRuntime());
            outputData.setNumDetectedObjects(stats.getNumDetectedObjects());
            outputData.setNumTrackedObjects(stats.getNumTrackedObjects());
            outputData.setNumLandmarks(stats.getNumLandmarks());


            // Fill in the landmarks
            Map<String, LandmarkData> landMarks = new HashMap<>();
            for (LandMark landmark : fusionSlam.getLandmarks()) {
                landMarks.put(landmark.getId(), new LandmarkData(landmark.getId(), landmark.getDescription(), landmark.getCoordinatesMap()));
            }
            outputData.setLandMarks(landMarks);
            writeOutputFile(outputData, parentPath);
        }

    }

    private static void writeOutputFile(OutputData outputData, Path parentPath) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String outputPath = parentPath.toString() + "/output_file.json";
        try (FileWriter writer = new FileWriter(outputPath)) {
            gson.toJson(outputData, writer);
            System.out.println("Output file written to " + "output_file.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeOutputFile(OutputDataError outputDataError, Path parentPath) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String outputPath = parentPath.toString() + "/error_output_file.json";
        try (FileWriter writer = new FileWriter(outputPath)) {
            gson.toJson(outputDataError, writer);
            System.out.println("Output file written to " + "error_output_file.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static class OutputData {
        private int systemRuntime;
        private int numDetectedObjects;
        private int numTrackedObjects;
        private int numLandmarks;
        private Map<String, LandmarkData> landMarks;


        public void setSystemRuntime(int systemRuntime) {
            this.systemRuntime = systemRuntime;
        }

        public void setNumDetectedObjects(int numDetectedObjects) {
            this.numDetectedObjects = numDetectedObjects;
        }

        public void setNumTrackedObjects(int numTrackedObjects) {
            this.numTrackedObjects = numTrackedObjects;
        }

        public void setNumLandmarks(int numLandmarks) {
            this.numLandmarks = numLandmarks;
        }

        public void setLandMarks(Map<String, LandmarkData> landMarks) {
            this.landMarks = landMarks;
        }



    }

    public static class OutputDataError {
        private String error;
        private String faultySensor;
        private Map<String, StampedDetectedObjects> lastCamerasFrame;
        private Map<String, List<TrackedObject>> lastLiDarWorkerTrackersFrame;
        private List<Map<String, Object>> poses;
        private Map<String, Object> statistics;



        public void setError(String error) {
            this.error = error;
        }

        public void setFaultySensor(String faultySensor) {
            this.faultySensor = faultySensor;
        }

        public void setCameraLastFrames(Map<String, StampedDetectedObjects> lastCameraFrames) {
            this.lastCamerasFrame = lastCameraFrames;
        }
        public void setLidarLastFrames(Map<String, List<TrackedObject>> lastLidarFrames) {
            this.lastLiDarWorkerTrackersFrame = lastLidarFrames;
        }
        public void setPoses(List<Map<String, Object>> poses) {
            this.poses = poses;
        }
        public void setStatistics(Map<String, Object> statistics) {
            this.statistics = statistics;
        }

    }

    // Data structure to hold individual landmark information
    public static class LandmarkData {
        private String id;
        private String description;
        private List<Map<String, Double>> coordinates;

        public LandmarkData(String id, String description, List<Map<String, Double>> coordinates) {
            this.id = id;
            this.description = description;
            this.coordinates = coordinates;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<Map<String, Double>> getCoordinates() {
            return coordinates;
        }

        public void setCoordinates(List<Map<String, Double>> coordinates) {
            this.coordinates = coordinates;
        }
    }
}