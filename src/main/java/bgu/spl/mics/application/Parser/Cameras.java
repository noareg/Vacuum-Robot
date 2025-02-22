package bgu.spl.mics.application.Parser;

import java.util.List;

public class Cameras {
    private List<CamerasConfigurations> CamerasConfigurations; // Array of cameras
    private String camera_datas_path;  // Path to the camera data file

    // Getters and Setters
    public List<CamerasConfigurations> getCamerasConfigurations() {
        return CamerasConfigurations;
    }

    public String getCamera_datas_path() {
        return camera_datas_path;
    }

}

