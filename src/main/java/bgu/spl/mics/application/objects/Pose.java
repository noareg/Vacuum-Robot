package bgu.spl.mics.application.objects;

/**
 * Represents the robot's pose (position and orientation) in the environment.
 * Includes x, y coordinates and the yaw angle relative to a global coordinate system.
 */
public class Pose {
    private float x;
    private float y;
    private final float yaw;
    private int time;

    /**
     * Constructor for Pose.
     *
     * @param x    The x-coordinate in the global system.
     * @param y    The y-coordinate in the global system.
     * @param yaw  The orientation (in degrees) relative to the global system.
     * @param time The timestamp of the pose.
     */
    public Pose(float x, float y, float yaw, int time) {
        if (time < 0) {
            throw new IllegalArgumentException("Time cannot be negative.");
        }
        this.x = x;
        this.y = y;
        this.yaw = yaw;
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public double getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public double getYaw() {
        return yaw;
    }



}