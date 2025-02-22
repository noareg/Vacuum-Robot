package bgu.spl.mics.application.objects;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {

    private final int ID;
    private final int frequency;
    private STATUS status;
    private List<StampedDetectedObjects> DetectedObjects;
    private StampedDetectedObjects lastDetectedObjects;

    public Camera(int Id, int frequency) {
        this.ID = Id;
        this.frequency = frequency;
        this.status = STATUS.UP;
        this.DetectedObjects = new ArrayList<>();
    }


    public int getID() {
        return ID;
    }

    public int getFrequency() {
        return frequency;
    }

    public STATUS getStatus() {
        return status;
    }

    public StampedDetectedObjects getDetectedObjectsAt(int time) {
        if (DetectedObjects != null) {
            for (StampedDetectedObjects obj : DetectedObjects) {
                if (obj.getTime() == time) {
                    return obj;
                }
            }
        }
        return null;
    }

    public List<StampedDetectedObjects> getDetectedObjects() {
        return DetectedObjects;
    }


    public void setStatus(STATUS status) {
        this.status = status;
    }

    public void setDetectedObjects(List<StampedDetectedObjects> detectedObjects) {
        this.DetectedObjects = detectedObjects;
    }

    public void updateLastDetectedObjects(int time) {
        for (StampedDetectedObjects obj : DetectedObjects) {
            if (obj.getTime() == time) {
                lastDetectedObjects = obj;
            }
        }
        if(lastDetectedObjects!=null) LastFrameTracker.getInstance().updateCameraFrame("Camera "+ID,lastDetectedObjects);

    }

    public String errorChecking (int time) {
        StampedDetectedObjects detectedObjectAtTime = getDetectedObjectsAt(time);
        if (detectedObjectAtTime != null && detectedObjectAtTime.getDetectedObjects() != null) {
            for (DetectedObject detectedObject : detectedObjectAtTime.getDetectedObjects()) {
                if ("ERROR".equals(detectedObject.getId())) {
                    setStatus(STATUS.ERROR);
                    return detectedObject.getDescription();
                }
            }
        }
        return null;
    }
}