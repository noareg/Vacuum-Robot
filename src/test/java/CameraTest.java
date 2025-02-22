import bgu.spl.mics.application.objects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class CameraTest {
    private Camera camera;

    @BeforeEach
    public void setUp() {
        camera = new Camera(1, 5);  // Camera ID = 1, Frequency = 5

        // Create DetectedObject instances
        DetectedObject car = new DetectedObject("car", "car" );
        DetectedObject person = new DetectedObject("person", "person");
        DetectedObject tree = new DetectedObject("tree", "tree");

        // Create lists of DetectedObjects for each timestamp
        List<DetectedObject> detectedAt10 = new ArrayList<>();
        detectedAt10.add(car);
        List<DetectedObject> detectedAt15 = new ArrayList<>();
        detectedAt15.add(person);
        List<DetectedObject> detectedAt20 = new ArrayList<>();
        detectedAt20.add(tree);

        // Create StampedDetectedObjects for each time frame
        StampedDetectedObjects stampedAt10 = new StampedDetectedObjects(10, detectedAt10);
        StampedDetectedObjects stampedAt15 = new StampedDetectedObjects(15, detectedAt15);
        StampedDetectedObjects stampedAt20 = new StampedDetectedObjects(20, detectedAt20);

        // Add StampedDetectedObjects to Camera
        List<StampedDetectedObjects> detectedObjects = new ArrayList<>();
        detectedObjects.add(stampedAt10);
        detectedObjects.add(stampedAt15);
        detectedObjects.add(stampedAt20);

        camera.setDetectedObjects(detectedObjects);  // Set to camera
    }
    /**
     * @pre camera.getDetectedObjects().size() > 0
     * @post obj.getTime() == 15
     * @post obj.getDetectedObjects().size() == 1
     * @post obj.getDetectedObjects().get(0).getDescription().equals("person")
     * @inv camera.getDetectedObjects != null
     */
    @Test
    public void testGetDetectedObjectsAtExactTime() {
        StampedDetectedObjects obj = camera.getDetectedObjectsAt(15);
        assertNotNull(obj);
        assertEquals(15, obj.getTime());
        assertEquals(1, obj.getDetectedObjects().size());
        assertEquals("person", obj.getDetectedObjects().get(0).getDescription());
    }

    /**
     * @pre camera.getDetectedObjects().size() > 0
     * @post obj == null
     * @inv camera.detDetectedObjects != null
     */
    @Test
    public void testGetDetectedObjectsAtNonExistingTime() {
        StampedDetectedObjects obj = camera.getDetectedObjectsAt(12);
        assertNull(obj);  // No object exists exactly at time 12
    }
}