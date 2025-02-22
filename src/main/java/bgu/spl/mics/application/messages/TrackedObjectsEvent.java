package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.application.objects.TrackedObject;
import java.util.List;

public class TrackedObjectsEvent implements Event<Boolean> {
    private final List<TrackedObject> detectedObjects;

    public TrackedObjectsEvent(List<TrackedObject> detectedObjects) {
        this.detectedObjects = detectedObjects;
    }
    public List<TrackedObject> getDetectedObjects() {
        return detectedObjects;
    }
}
