package bgu.spl.mics.application.services;

import bgu.spl.mics.application.GurionRockRunner;
import bgu.spl.mics.application.Parser.*;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.StatisticalFolder;

import java.nio.file.Path;

/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {
    private final FusionSlam fusionSlam;
    private final StatisticalFolder stats;
    private Path parentPath;

    /**
     * Constructor for FusionSlamService.
     */
    public FusionSlamService(StatisticalFolder stats, Path parentPath) {
        super("FusionSlamService");
        this.fusionSlam = FusionSlam.getInstance();
        this.stats = stats;
        this.parentPath = parentPath;
    }

    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {

        // Subscribing to TerminatedBroadcast
        subscribeBroadcast(TerminatedBroadcast.class, terminated -> {
       //     System.out.println("Termination received.");
            stats.decrementNumOfSensors();
    //        System.out.println("Number of sensors: " + stats.getNumOfSensors());
            if (terminated.getSensorName().equals("TimeService") || stats.getNumOfSensors() == 0) {
                generateOutputFile(false, null, null);
                terminate();
                // sendBroadcast(new TerminatedBroadcast(getName()));
      //          System.out.println(getName() + " sent TerminatedBroadcast.");
            }
        });

        // Subscribe to CrashedBroadcast
        subscribeBroadcast(CrashedBroadcast.class, crashed -> {
            stats.setNumOfSensors(0);
  //          System.out.println("Crash signal received: " + crashed.getSensorName());
            generateOutputFile(true, crashed.getErrorDescription(), crashed.getSensorName());
            terminate();
        });

        // Subscribing to TrackedObjectsEvent
        subscribeEvent(TrackedObjectsEvent.class, e -> {
 //           System.out.println(" new Tracked object received: " + e.getDetectedObjects());
            fusionSlam.updateLandmarks(e.getDetectedObjects());
            stats.incrementNumLandmarks(fusionSlam.getSizeOfLandmarks());
            complete(e, true); // Indicate event completion
        });

        // Subscribing to PoseEvent
        subscribeEvent(PoseEvent.class,e -> {
//            System.out.println("new pose received: " + e.getPose());
            fusionSlam.updatePose(e.getPose());
            complete(e, true); // Indicate event completion
        });


//        System.out.println("FusionSlamService initialized and ready");
        GurionRockRunner.latch.countDown();
    }

    private void generateOutputFile(boolean errorOccurred, String errorDescription, String faultySensor) {
        // Generate the output file when termination or crash occurs
        OutputFileGenerator.generateOutputFile(stats, fusionSlam, errorOccurred, errorDescription, faultySensor, parentPath);
    }
}