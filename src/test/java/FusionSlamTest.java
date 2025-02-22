import bgu.spl.mics.application.objects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class FusionSlamTest {
    private FusionSlam fusionSlam;
    private List<CloudPoint> localPoints;
    private final Pose pose=new Pose(2, 3, 45, 10);  // x = 2, y = 3, angle = 45 degrees;

    @BeforeEach
    public void setUp() {
        fusionSlam = FusionSlam.getInstance();
        fusionSlam.updatePose(pose);

        localPoints = new ArrayList<>();
        localPoints.add(new CloudPoint(1.0, 1.0));
        localPoints.add(new CloudPoint(2.0, 0.0));
    }
    /**
     * @pre localPoints.size() > 0
     * @post globalPoints.size() == 2
     * @post Math.abs(globalPoints.get(0).getX() - expectedX) <= 0.001
     * @post Math.abs(globalPoints.get(0).getY() - expectedY) <= 0.001
     * @inv pose != null
     * @inv pose.yaw < 360
     */
    @Test
    public void testTransformToGlobalPositiveCoordinates() {
        List<CloudPoint> globalPoints = fusionSlam.transformToGlobal(localPoints, pose.getTime());



        //actual given calc
        double expectedX = 2 + (Math.cos(Math.toRadians(45)) * 1.0 - Math.sin(Math.toRadians(45)) * 1.0);
        double expectedY = 3 + (Math.sin(Math.toRadians(45)) * 1.0 + Math.cos(Math.toRadians(45)) * 1.0);

        assertEquals(2, globalPoints.size());
        assertEquals(expectedX, globalPoints.get(0).getX(), 0.001); //checks accuracy of calc
        assertEquals(expectedY, globalPoints.get(0).getY(), 0.001); //checks accuracy of calc
    }
    /**
     * @pre localPoints.size() > 0
     * @post globalPoints.size() == 1
     * @post Math.abs(globalPoints.get(0).getX() - expectedX) <= 0.001
     * @post Math.abs(globalPoints.get(0).getY() - expectedY) <= 0.001
     * * @inv pose != null
     * * @inv pose.yaw < 360
     */
    @Test
    public void testTransformToGlobalNegativeCoordinates() {
        localPoints.clear();
        localPoints.add(new CloudPoint(-1.0, -1.0));

        List<CloudPoint> globalPoints = fusionSlam.transformToGlobal(localPoints, pose.getTime());

        //actual given calc
        double expectedX = 2 + (Math.cos(Math.toRadians(45)) * -1.0 - Math.sin(Math.toRadians(45)) * -1.0);
        double expectedY = 3 + (Math.sin(Math.toRadians(45)) * -1.0 + Math.cos(Math.toRadians(45)) * -1.0);


        assertEquals(1, globalPoints.size());
        assertEquals(expectedX, globalPoints.get(0).getX(), 0.001);  // x unchanged
        assertEquals(expectedY, globalPoints.get(0).getY(), 0.001);  // Approx. 1.58
    }
}