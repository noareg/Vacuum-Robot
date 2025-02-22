package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.GurionRockRunner;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.GPSIMU;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.STATUS;

/**
 * PoseService is responsible for maintaining the robot's current pose (position and orientation)
 * and broadcasting PoseEvents at every tick.
 */
public class PoseService extends MicroService {

    /**
     * Constructor for PoseService.
     *
     * @param gpsimu The GPSIMU object that provides the robot's pose data.
     */

    private final GPSIMU gpsimu;

    public PoseService(GPSIMU gpsimu) {
        super("PoseService");
        this.gpsimu = gpsimu;
    }

    /**
     * Initializes the PoseService.
     * Subscribes to TickBroadcast and sends PoseEvents at every tick based on the current pose.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, tick -> {
            int currentTick = tick.getCurrentTick();
            Pose currentPose = gpsimu.getCurrentPoseAtTick(currentTick);
            if (currentPose != null) {
                PoseEvent poseEvent = new PoseEvent(currentPose);
                sendEvent(poseEvent);
            } else {
                gpsimu.setStatus(STATUS.DOWN);
                sendBroadcast(new TerminatedBroadcast("gpsimu"));
            }
        });

        // Handle TerminatedBroadcast (Graceful shutdown)
        this.subscribeBroadcast(TerminatedBroadcast.class, terminated -> {
            if(terminated.getSensorName().equals("TimeService"))
                terminate();
        });

        // Handle CrashedBroadcast for sensor errors
        this.subscribeBroadcast(CrashedBroadcast.class, crashed -> terminate());
        GurionRockRunner.latch.countDown();
    }


}
