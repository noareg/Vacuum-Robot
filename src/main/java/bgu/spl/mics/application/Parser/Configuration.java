package bgu.spl.mics.application.Parser;

public class Configuration {
    private Cameras Cameras;
    private LiDarWorkers LiDarWorkers;
    private String poseJsonFile;
    private int TickTime;
    private int Duration;

    public Cameras getCameras() {
        return Cameras;
    }

    public LiDarWorkers getLiDarWorkers() {
        return LiDarWorkers;
    }

    public String getPoseJsonFile() {
        return poseJsonFile;
    }

    public int getTickTime() {
        return TickTime;
    }

    public int getDuration() {
        return Duration;
    }

}