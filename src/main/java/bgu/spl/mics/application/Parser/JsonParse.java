
package bgu.spl.mics.application.Parser;
import bgu.spl.mics.application.objects.*;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;


import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class JsonParse {
    private final Gson gson;

    public JsonParse(Gson gson) {
        this.gson = gson;
    }

    public Configuration parseConfiguration(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            Type configType = new TypeToken<Configuration>(){}.getType();
            Configuration config = gson.fromJson(reader, configType);
            if (config == null) {
                System.out.println("Configuration is null.");
            } else {
                System.out.println("Configuration parsed successfully.");
            }
            return config;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public List<StampedDetectedObjects> parseCameraFile(String filePath, String cameraKey) {
        try (FileReader reader = new FileReader(filePath)) {
            // Parse the whole file as a JsonObject
            JsonObject cameraDataObject = gson.fromJson(reader, JsonObject.class);

            // Get the array by the camera key (e.g., "camera1")
            JsonArray cameraDataArray = cameraDataObject.getAsJsonArray(cameraKey);

            List<StampedDetectedObjects> cameraList = new ArrayList<>();

            // Iterate over the JSON array of camera data
            for (JsonElement cameraElement : cameraDataArray) {
                JsonObject cameraInfo = cameraElement.getAsJsonObject();  // Each entry is a JSON object
                int time = cameraInfo.get("time").getAsInt();  // Get the time value
                JsonArray detectedObjectsArray = cameraInfo.getAsJsonArray("detectedObjects");  // Get the detectedObjects array

                List<DetectedObject> detectedObjects = new ArrayList<>();
                for (JsonElement obj : detectedObjectsArray) {
                    String id = obj.getAsJsonObject().get("id").getAsString();  // Get id
                    String description = obj.getAsJsonObject().get("description").getAsString();  // Get description
                    detectedObjects.add(new DetectedObject(id, description));  // Add to the list of detected objects
                }

                // Add the StampedDetectedObjects to the list
                cameraList.add(new StampedDetectedObjects(time, detectedObjects));
            }

            return cameraList;
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }



    public List<StampedCloudPoints> parseLidarFile(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            Type lidarDataType = new TypeToken<List<StampedCloudPoints>>(){}.getType();
            return gson.fromJson(reader, lidarDataType);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Pose> parsePoseFile(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            Type poseListType = new TypeToken<List<Pose>>() {}.getType();
            return gson.fromJson(reader, poseListType);
        } catch (IOException e) {
            System.err.println("Error reading pose data file: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}