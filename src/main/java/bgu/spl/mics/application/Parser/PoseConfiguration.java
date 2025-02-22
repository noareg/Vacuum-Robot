package bgu.spl.mics.application.Parser;

public class PoseConfiguration {
    private float x;
    private float y;
    private float yaw;
    private String poseJsonFile;
    private int time;

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getTime() {
        return time;
    }

}
