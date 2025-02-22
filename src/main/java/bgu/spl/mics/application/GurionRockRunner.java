package bgu.spl.mics.application;

import bgu.spl.mics.application.Parser.*;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.services.*;
import com.google.gson.Gson;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * The main entry point for the GurionRock Pro Max Ultra Over 9000 simulation.
 */
public class GurionRockRunner {
    public static CountDownLatch latch;

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("invalid path");
            return;
        }
        try {
            String configFilePath = args[0];
            System.out.println("Loading config file: " + configFilePath);

            Gson gson = new Gson();
            JsonParse parser = new JsonParse(gson);
            Configuration configuration = parser.parseConfiguration(configFilePath);
            Path path= Paths.get(configFilePath).getParent();

            if (configuration == null) {
                return;
            }

            StatisticalFolder stats = new StatisticalFolder();
            int numOfServices = 0;
            List<Thread> threads = new ArrayList<>();


            // init cameras
            for (CamerasConfigurations cameraConfig : configuration.getCameras().getCamerasConfigurations()) {
                List<StampedDetectedObjects> cameraData = parser.parseCameraFile(path.toString() + configuration.getCameras().getCamera_datas_path().substring(1), cameraConfig.getCamera_key());
                Camera camera = new Camera(cameraConfig.getId(), cameraConfig.getFrequency());
                camera.setDetectedObjects(cameraData);

                CameraService cameraService = new CameraService(camera, stats);
                numOfServices++;
                Thread cameraThread = new Thread(() -> {
                    try {
                        cameraService.run();
                    } catch (Exception e) {
                        System.err.println("CameraService error: " + e.getMessage());
                    }
                });
                threads.add(cameraThread);
            }

            // init lidars
            for (LidarConfigurations liDARConfigurations : configuration.getLiDarWorkers().getLiDarWorkersConfigurations()) {
                LiDarDataBase.getInstance(path.toString() + configuration.getLiDarWorkers().getLidars_data_path().substring(1));
                LiDarWorkerTracker liDarWorkerTracker = new LiDarWorkerTracker(liDARConfigurations.getId(), liDARConfigurations.getFrequency());
                LiDarService lidarService = new LiDarService(liDarWorkerTracker, stats);
                numOfServices++;
                Thread lidarThread = new Thread(lidarService::run);
                threads.add(lidarThread);
            }

            // init poses
            List<Pose> poses = parser.parsePoseFile(path.toString() + configuration.getPoseJsonFile().substring(1));
            GPSIMU gpsimu = new GPSIMU(poses);
            PoseService poseService = new PoseService(gpsimu);
            numOfServices++;
            Thread poseThread = new Thread(poseService::run);
            threads.add(poseThread);

            stats.setNumOfSensors(numOfServices);
            LastFrameTracker.getInstance().setPoseList(poses);

            // Init FusionSLAM service
            FusionSlamService fusionSlamService = new FusionSlamService(stats, path);
            Thread fusionSlamThread = new Thread(fusionSlamService::run);
            threads.add(fusionSlamThread);
            latch = new CountDownLatch(threads.size());  // Latch for TimeService start

            // Start threads
            for (Thread thread : threads) {
                thread.start();
            }

            // Wait for all services to initialize
            latch.await();

            // Start TimeService after all services are ready
            TimeService timeService = new TimeService(configuration.getTickTime(), configuration.getDuration(), stats);
            Thread timeServiceThread = new Thread(timeService);
            timeServiceThread.start();
            //threads.add(timeServiceThread);

            // Join threads
            for (Thread thread : threads) {
                thread.join();
            }
            timeServiceThread.join();

            System.out.println("All services have completed.");
        } catch (InterruptedException e) {
            System.err.println("Error while waiting for threads to finish: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("Error initializing components: " + e.getMessage());
            e.printStackTrace();
        }
    }
}