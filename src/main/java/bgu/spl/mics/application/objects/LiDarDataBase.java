package bgu.spl.mics.application.objects;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * LiDarDataBase is a singleton class responsible for managing LiDAR data.
 * It provides access to cloud point data and other relevant information for tracked objects.
 */
public class LiDarDataBase {

    /**
     * Returns the singleton instance of LiDarDataBase.
     *
     * @param filePath The path to the LiDAR data file.
     * @return The singleton instance of LiDarDataBase.
     */


    private static LiDarDataBase instance=null;
    private final List<StampedCloudPoints> cloudPointsList=new ArrayList<>();

    private LiDarDataBase(String filePath) {
        loadDataFromFile(filePath);
    }
    public static LiDarDataBase getInstance(String filePath) {
        if(instance==null){
            synchronized(LiDarDataBase.class){
                if(instance==null){
                    instance=new LiDarDataBase(filePath);
                }
            }
        }
        return instance;
    }

    public static LiDarDataBase getInstance() {
        if (instance == null) {
            System.err.println("LiDarDataBase is not initialized yet. Please provide a file path initially.");
        }
        return instance;
    }

    public List<StampedCloudPoints> getCloudPointsList() {
        return cloudPointsList;
    }

    private void loadDataFromFile(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            Gson gson = new Gson();
            Type listType = new TypeToken<List<LidarEntry>>() {}.getType();
            List<LidarEntry> lidarEntries = gson.fromJson(reader, listType);
            for (LidarEntry entry : lidarEntries) {
                StampedCloudPoints stampedCloudPoints = new StampedCloudPoints(entry.getId(), entry.getTime());
                stampedCloudPoints.setCloudPoints(entry.getCloudPoints()); // Set the cloud points
                cloudPointsList.add(stampedCloudPoints);
            }
        } catch (IOException e) {
            System.err.println("Error reading LiDAR data file: " + e.getMessage());
        }
    }

    private static class LidarEntry {
        private int time;
        private String id;
        private List<List<Double>> cloudPoints;

        public int getTime() {
            return time;
        }

        public String getId() {
            return id;
        }

        public List<List<Double>> getCloudPoints() {
            return cloudPoints;
        }
    }
}
