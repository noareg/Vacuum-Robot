package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents the robot's GPS and IMU system.
 * Provides information about the robot's position and movement.
 */
public class GPSIMU {
    int currentTick;
    STATUS status;
    List<Pose> PoseList;

   public GPSIMU(List<Pose> PoseList) {
        currentTick = 0;
        this.status =STATUS.UP;
        this.PoseList = PoseList;
    }

    public void setStatus(STATUS status) {
        this.status = status;
    }

    public Pose getCurrentPoseAtTick(int tick) {
       if(tick>=PoseList.size()) return null;
       return PoseList.get(tick);
    }
}