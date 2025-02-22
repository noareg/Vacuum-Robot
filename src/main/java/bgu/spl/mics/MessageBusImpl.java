package bgu.spl.mics;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only one public method (in addition to getters which can be public solely for unit testing) may be added to this class
 * All other methods and members you add the class must be private.
 */
public class MessageBusImpl implements MessageBus {
	private static class SingletonHolder {
		private static final MessageBusImpl instance = new MessageBusImpl();
	}

	public static MessageBusImpl getInstance() {
		return SingletonHolder.instance;
	}

	private final ConcurrentHashMap<Class<? extends Event<?>>, BlockingQueue<MicroService>> eventSubscribers; // events map
	private final ConcurrentHashMap<Class<? extends Broadcast>, BlockingQueue<MicroService>> broadcastSubscribers ; // broadcast map
	private final ConcurrentHashMap<MicroService, BlockingQueue<Message>> microServiceQueues; // micro service map
	private final ConcurrentHashMap<Event<?>, Future<?>> eventFutures; //futures map

	private MessageBusImpl() {
		eventSubscribers= new ConcurrentHashMap<>();
		broadcastSubscribers= new ConcurrentHashMap<>();
		microServiceQueues= new ConcurrentHashMap<>();
		eventFutures= new ConcurrentHashMap<>();
	}


	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		eventSubscribers.computeIfAbsent(type, k -> new LinkedBlockingQueue<>()).add(m); //if event is already exist, add the microService to it list, else create new event type in map and add the microService to it list
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		broadcastSubscribers.computeIfAbsent(type, k -> new LinkedBlockingQueue<>()).add(m); //if broadcast is already exist, add the microService to it list, else create new broadcast type in map and add the microService to it list
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> void complete(Event<T> e, T result) {
		Future<T> future = (Future<T>) eventFutures.get(e);
		if (future != null) {
			future.resolve(result);
		}
	}

	@Override
	public void sendBroadcast(Broadcast b) {
		BlockingQueue<MicroService> subscribers = broadcastSubscribers.get(b.getClass());
		if (subscribers != null) {
			synchronized (subscribers) {
				for (MicroService ms : subscribers) {
					microServiceQueues.computeIfAbsent(ms, k -> new LinkedBlockingQueue<>()).add(b);
				}
			}
		}
	}


	@Override
	public <T> Future<T> sendEvent(Event<T> e) {
		BlockingQueue<MicroService> subscribers = this.eventSubscribers.get(e.getClass());
		if (subscribers == null || subscribers.isEmpty()) {
			return null;
		}
		synchronized (subscribers) {
			MicroService ms = subscribers.poll();
			if (ms != null) {
				try {
					subscribers.put(ms);  // Reinsert at the end for round-robin fairness
					microServiceQueues.computeIfAbsent(ms, k -> new LinkedBlockingQueue<>()).put(e);
				} catch (InterruptedException ex) {
					Thread.currentThread().interrupt();
					return null;
				}
			}
		}
		Future<T> future = new Future<>();
		eventFutures.putIfAbsent(e, future);
		return future;
	}

	@Override
	public void register(MicroService m) {
		microServiceQueues.putIfAbsent(m, new LinkedBlockingQueue<>());
	}

	@Override
	public void unregister(MicroService m) {
		microServiceQueues.remove(m);
		synchronized (eventSubscribers) {
			eventSubscribers.values().forEach(subscribers -> subscribers.remove(m));
		}
		synchronized (broadcastSubscribers) {
			broadcastSubscribers.values().forEach(subscribers -> subscribers.remove(m));
		}
	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		BlockingQueue<Message> queue = microServiceQueues.get(m);
		return queue.take();
	}
}