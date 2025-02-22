import bgu.spl.mics.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.*;

public class MessageBusTest {
    private MessageBusImpl messageBus;
    private MicroService microService1;
    private MicroService microService2;

    @BeforeEach
    public void setUp() {
        messageBus = MessageBusImpl.getInstance();
        microService1 = new MicroService("Service1") {
            @Override
            protected void initialize() {}
        };
        microService2 = new MicroService("Service2") {
            @Override
            protected void initialize() {}
        };
        messageBus.register(microService1);
        messageBus.register(microService2);
    }
    /**
     * @pre microservice1 != null
     * @post future != null
     * @post messageBus.awaitMessage(microService1).equals(testEvent)
     * @inv microServiceQueues != null
     * @inv subscribersMap != null
     */
    @Test
    public void testSubscribeEventAndSendEvent() throws InterruptedException {
        class TestEvent implements Event<String> {}

        TestEvent testEvent = new TestEvent();
        messageBus.subscribeEvent(TestEvent.class, microService1);

        Future<String> future = messageBus.sendEvent(testEvent);

        assertNotNull(future);
        assertEquals(testEvent, messageBus.awaitMessage(microService1));
    }
    /**
     * @pre microService1 != null
     * @pre microService2 != null
     * @post messageBus.awaitMessage(microService1).equals(testBroadcast)
     * @post messageBus.awaitMessage(microService2).equals(testBroadcast)
     * @inv microServiceQueues != null
     * @inv subscribersMap != null
     */
    @Test
    public void testSubscribeBroadcastAndSendBroadcast() throws InterruptedException {
        class TestBroadcast implements Broadcast {}

        TestBroadcast testBroadcast = new TestBroadcast();
        messageBus.subscribeBroadcast(TestBroadcast.class, microService1);
        messageBus.subscribeBroadcast(TestBroadcast.class, microService2);

        messageBus.sendBroadcast(testBroadcast);

        assertEquals(testBroadcast, messageBus.awaitMessage(microService1));
        assertEquals(testBroadcast, messageBus.awaitMessage(microService2));
    }

    /**
     * @pre microService1 != null
     * @pre future != null
     * @post future.isDone()
     * @post future.get(1, SECONDS) == ("Success")
     */
    @Test
    public void testComplete() throws InterruptedException {
        class TestEvent implements Event<String> {}

        TestEvent testEvent = new TestEvent();
        messageBus.subscribeEvent(TestEvent.class, microService1); //filling microservices queue
        Future<String> future = messageBus.sendEvent(testEvent);

        messageBus.complete(testEvent, "Success");

        assertTrue(future.isDone());
        assertEquals("Success", future.get(1, TimeUnit.SECONDS));
    }

}