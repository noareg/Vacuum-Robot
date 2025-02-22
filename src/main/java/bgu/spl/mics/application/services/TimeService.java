package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.StatisticalFolder;

/**
 * TimeService acts as the global timer for the system, broadcasting TickBroadcast messages
 * at regular intervals and controlling the simulation's duration.
 */
public class TimeService extends MicroService {
    private final int TickTime;
    private final int Duration;
    private int TickCount;
    private final StatisticalFolder stats;

    /**
     * Constructor for TimeService.
     *
     * @param TickTime  The duration of each tick in milliseconds.
     * @param Duration  The total number of ticks before the service terminates.
     */
    public TimeService(int TickTime, int Duration, StatisticalFolder stats) {
        super("TimeService");
        this.TickTime = TickTime;
        this.Duration = Duration;
        this.TickCount = 0;
        this.stats = stats;
    }

    /**
     * Initializes the TimeService.
     * Starts broadcasting TickBroadcast messages and terminates after the specified duration.
     */
    @Override
    protected void initialize() {

        try {
                while (TickCount < Duration && stats.getNumOfSensors()>0) {
                    TickCount++;
                    stats.incrementSystemRuntime();
                    sendBroadcast(new TickBroadcast(TickCount));
                    Thread.sleep(TickTime* 1000L);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        finally {
            terminate();
            sendBroadcast(new TerminatedBroadcast(getName()));
        }
    }

}
