package bgu.spl.mics.application.Parser;

import java.util.List;

public class LiDarWorkers {
    private List<LidarConfigurations> LidarConfigurations;
    private String lidars_data_path;

    public List<LidarConfigurations> getLiDarWorkersConfigurations() {
        return LidarConfigurations;
    }

    public String getLidars_data_path() {
        return lidars_data_path;
    }


}

