package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.GurionRockRunner;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;

import java.util.*;


/**
 * LiDarService is responsible for processing data from the LiDAR sensor and
 * sending TrackedObjectsEvents to the FusionSLAM service.
 * <p>
 * This service interacts with the LiDarWorkerTracker object to retrieve and process
 * cloud point data and updates the system's StatisticalFolder upon sending its
 * observations.
 */
public class LiDarService extends MicroService {

    private final LiDarWorkerTracker workerTracker;
    private final StatisticalFolder stats;
    /**
     * Constructor for LiDarService.
     *
     * @param LiDarWorkerTracker A LiDAR Tracker worker object that this service will use to process data.
     */
    public LiDarService(LiDarWorkerTracker LiDarWorkerTracker, StatisticalFolder stats) {
        super("LiDarTrackerWorker");
        this.workerTracker = LiDarWorkerTracker;
        this.stats = stats;
    }

    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callbacks for processing data.
     */
    @Override
    protected void initialize() {

        subscribeBroadcast(TerminatedBroadcast.class, terminated -> {
            if (terminated.getSensorName().equals("TimeService"))
                terminate();
        });

        subscribeBroadcast(CrashedBroadcast.class, crashed -> {
            terminate();
        });

        subscribeEvent(DetectObjectsEvent.class, detectObjectsEvent -> {
            List<TrackedObject> trackedObjects = workerTracker.handlesDetectedObjects(detectObjectsEvent.getStampedDetectedObjects());
            if (trackedObjects != null) {
                stats.incrementNumTrackedObjects(trackedObjects.size());
                complete(detectObjectsEvent, Boolean.TRUE);
            }
        });

        subscribeBroadcast(TickBroadcast.class, tick -> {
            int currentTick = tick.getCurrentTick();
            if(workerTracker.foundError(currentTick)){
                sendBroadcast(new CrashedBroadcast("LiDarWorkerTracker " + workerTracker.getId(), "Sensor error or invalid data"));
            }

            List<List<TrackedObject>> listsOfDetectedObjects = workerTracker.sendingEvent(currentTick);
            if (workerTracker.getStatus().equals(STATUS.DOWN)) {
                sendBroadcast(new TerminatedBroadcast("LiDarWorkerTracker" + workerTracker.getId()));
            }
            else {
                for (List<TrackedObject> listOfTrackedObjects : listsOfDetectedObjects) {
                    TrackedObjectsEvent trackedObjectsEvent = new TrackedObjectsEvent(listOfTrackedObjects);
                    sendEvent(trackedObjectsEvent);
                }
            }
        });
        GurionRockRunner.latch.countDown();
    }

}
