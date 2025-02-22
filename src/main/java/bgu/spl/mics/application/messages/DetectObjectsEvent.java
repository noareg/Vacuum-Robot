package bgu.spl.mics.application.messages;
import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.StampedDetectedObjects;


public class DetectObjectsEvent implements  Event<Boolean> {
    private final StampedDetectedObjects detectedObjects;


    public DetectObjectsEvent(StampedDetectedObjects detectedObjects) {
        this.detectedObjects = detectedObjects;
    }

    public StampedDetectedObjects getStampedDetectedObjects() {
        return detectedObjects;
    }

}
