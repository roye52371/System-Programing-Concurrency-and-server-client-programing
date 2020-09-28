package bgu.spl.mics;

import bgu.spl.mics.application.messages.*;
import java.util.*;

/**
 * The {@link MessageBrokerImpl class is the implementation of the MessageBroker interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBrokerImpl implements MessageBroker {

	private static MessageBrokerImpl messageBrokerInstance = new MessageBrokerImpl();

	private Map<Subscriber, Queue<Message>> messagesQueues;
	private Map<Class<? extends Event>, DynamicPair<List<Subscriber>, Integer>> subscribersEvents;
	private Map<Class<? extends Broadcast>, List<Subscriber>> subscribersBroadcast;
	private Map<Event, Future> eventFutureMap;

	private MessageBrokerImpl() {
		this.messagesQueues = new HashMap<>();
		this.subscribersEvents = new HashMap<>();
		List<Subscriber> missionSubscribers = new LinkedList<>();
		DynamicPair<List<Subscriber>, Integer> missionSubscribersPair = new DynamicPair<>(missionSubscribers, 0);
		subscribersEvents.put(MissionReceivedEvent.class, missionSubscribersPair);
		List<Subscriber> agentsSubscribers = new LinkedList<>();
		DynamicPair<List<Subscriber>, Integer> agentsSubscribersPair = new DynamicPair<>(agentsSubscribers, 0);
		subscribersEvents.put(AgentsAvailableEvent.class, agentsSubscribersPair);
		List<Subscriber> gadgetSubscribers = new LinkedList<>();
		DynamicPair<List<Subscriber>, Integer> gadgetSubscribersPair = new DynamicPair<>(gadgetSubscribers, 0);
		subscribersEvents.put(GadgetAvailableEvent.class, gadgetSubscribersPair);
		List<Subscriber> simulationSubscribers = new LinkedList<>();
		DynamicPair<List<Subscriber>, Integer> simulationSubscribersPair = new DynamicPair<>(simulationSubscribers, 0);
		subscribersEvents.put(MissionSimulationEvent.class, simulationSubscribersPair);
		List<Subscriber> releaseSubscribers = new LinkedList<>();
		DynamicPair<List<Subscriber>, Integer> releaseSubscribersPair = new DynamicPair<>(releaseSubscribers, 0);
		subscribersEvents.put(ReleaseAgentsEvent.class, releaseSubscribersPair);
		subscribersBroadcast = new HashMap<>();
		List<Subscriber> tickSubscribers = new LinkedList<>();
		subscribersBroadcast.put(TickBroadcast.class, tickSubscribers);
		List<Subscriber> terminateSubscribers = new LinkedList<>();
		subscribersBroadcast.put(TerminateBroadcast.class, terminateSubscribers);
		eventFutureMap = new HashMap<>();
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static MessageBroker getInstance() {
		return messageBrokerInstance;
	}

	@Override
	public <T> void subscribeEvent(Class<? extends Event<T>> type, Subscriber m) {
		synchronized (subscribersEvents) {
			this.subscribersEvents.get(type).getFirst().add(m);
		}
	}

	@Override
	public void subscribeBroadcast(Class<? extends Broadcast> type, Subscriber m) {
		synchronized (subscribersBroadcast) {
			this.subscribersBroadcast.get(type).add(m);
		}
	}

	@Override
	public <T> void complete(Event<T> e, T result) {
		if(eventFutureMap.containsKey(e))
			eventFutureMap.get(e).resolve(result);
	}

	@Override
	public synchronized void sendBroadcast(Broadcast b) {
		List<Subscriber> subscribers = subscribersBroadcast.get(b.getClass());
		if(b.getClass() == TerminateBroadcast.class) {
			for (int i = 0; i < subscribers.size(); i++)
				subscribers.get(i).shouldTerminate();
		}
		for (int i = 0; i < subscribers.size(); i++) {
			Subscriber subscriber = subscribers.get(i);
			if(messagesQueues.containsKey(subscriber))
				messagesQueues.get(subscriber).add(b);
		}
		notifyAll();
	}

	@Override
	public synchronized <T> Future<T> sendEvent(Event<T> e) {
		DynamicPair<List<Subscriber>, Integer> subscribersPair = subscribersEvents.get(e.getClass());
		List<Subscriber> subscribers = subscribersPair.getFirst();
		Integer roundRobinCounter = subscribersPair.getSecond();
		if(subscribers.size() > 0) {
			if (subscribers.size() > roundRobinCounter) {
				Subscriber subscriber = subscribers.get(roundRobinCounter);
				if(messagesQueues.containsKey(subscriber)) {
					messagesQueues.get(subscriber).add(e);
					subscribersEvents.get(e.getClass()).setSecond(roundRobinCounter + 1);
				}
				else
					return null;
			} else {
				Subscriber subscriber = subscribers.get(0);
				if(messagesQueues.containsKey(subscriber)) {
					messagesQueues.get(subscriber).add(e);
					subscribersEvents.get(e.getClass()).setSecond(1);
				}
				else
					return null;
			}
			notifyAll();
			Future<T> eFuture = new Future<>();
			eventFutureMap.put(e, eFuture);
			return eFuture;
		}
		else
			return null;
	}

	@Override
	public void register(Subscriber m) {
		synchronized (messagesQueues) {
			Queue<Message> newQueue = new LinkedList<>();
			messagesQueues.put(m, newQueue);
		}
	}

	@Override
	public synchronized void unregister(Subscriber m) {
		if (messagesQueues.containsKey(m))
			messagesQueues.remove(m);
		for (Map.Entry<Class<? extends Event>, DynamicPair<List<Subscriber>, Integer>> subscribersForEvent : subscribersEvents.entrySet())
			if (subscribersForEvent.getValue().getFirst().contains(m)) {
				subscribersForEvent.getValue().getFirst().remove(m);
				for(Map.Entry<Event, Future> eventsFutures : eventFutureMap.entrySet()){
					if(eventsFutures.getKey().getClass() == subscribersForEvent.getKey() & eventsFutures.getKey().getClass() == AgentsAvailableEvent.class)
						if(!eventsFutures.getValue().isDone()) {
							Triplet<Integer, Boolean, List<String>> future = new Triplet<>(null, false, null);
							eventsFutures.getValue().resolve(future);
						}
				}
			}
		for (Map.Entry<Class<? extends Broadcast>, List<Subscriber>> subscribersForBroadcast : subscribersBroadcast.entrySet())
			if (subscribersForBroadcast.getValue().contains(m))
				subscribersForBroadcast.getValue().remove(m);
	}

	@Override
	public synchronized Message awaitMessage(Subscriber m) throws InterruptedException {
		if(messagesQueues.containsKey(m)) {
			while (messagesQueues.get(m).isEmpty())
				this.wait();
			return messagesQueues.get(m).remove();
		}
		return null;
	}

}
