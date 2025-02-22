package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.GurionRockRunner;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.*;

/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 * This service interacts with the Camera object to detect objects and updates
 * the system's StatisticalFolder upon sending its observations.
 */
public class CameraService extends MicroService {
    private final Camera camera;
    private final StatisticalFolder stats;

    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera, StatisticalFolder stats) {
        super("CameraService");
        this.camera = camera;
        this.stats = stats;
    }

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for sending
     * DetectObjectsEvents.
     */
    @Override
    protected void initialize() {
        this.subscribeBroadcast(TickBroadcast.class, tick -> {
            int currentTick = tick.getCurrentTick();
            camera.updateLastDetectedObjects(currentTick);
            String errorDescription= camera.errorChecking(currentTick);
            if (camera.getStatus()==STATUS.ERROR && errorDescription!=null) {
                sendBroadcast(new CrashedBroadcast("Camera " + camera.getID(), errorDescription));
            }

            int frequency = camera.getFrequency();
            StampedDetectedObjects objects = camera.getDetectedObjectsAt(currentTick - frequency);
            if (objects != null ) {
                // Send DetectObjectsEvent if no error is detected
                DetectObjectsEvent detectObjectsEvent = new DetectObjectsEvent(objects);
                sendEvent(detectObjectsEvent);
                stats.incrementNumDetectedObjects(objects.getDetectedObjects().size());
            } else {
                if (camera.getDetectedObjects() == null || camera.getDetectedObjects().isEmpty()) {
                    camera.setStatus(STATUS.DOWN);
                    sendBroadcast(new TerminatedBroadcast("Camera" + camera.getID()));
                }
            }
        });
        // Handle TerminatedBroadcast (Graceful shutdown)
        this.subscribeBroadcast(TerminatedBroadcast.class, terminated -> {
            if(terminated.getSensorName().equals("TimeService"))
                terminate();
        });

        // Handle CrashedBroadcast (System-wide failure)
        this.subscribeBroadcast(CrashedBroadcast.class, crashed -> {
            terminate(); // Terminate when a crash occurs
        });

        GurionRockRunner.latch.countDown();

    }
}
