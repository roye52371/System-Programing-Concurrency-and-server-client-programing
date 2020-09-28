package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.Callback;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.messages.GadgetAvailableEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.Inventory;
import javafx.util.Pair;

/**
 * Q is the only Subscriber\Publisher that has access to the {@link bgu.spl.mics.application.passiveObjects.Inventory}.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Q extends Subscriber {

	private int currentTime;

	public Q() {
		super("Q");
		this.currentTime = 0;
	}

	@Override
	protected void initialize() {
		Callback<GadgetAvailableEvent> callbackGadget = c -> {
			Boolean available = Inventory.getInstance().getItem(c.getGadget());
			Pair<Boolean, Integer> result = new Pair<>(available, currentTime);
			complete(c, result);
		};
		this.subscribeEvent(GadgetAvailableEvent.class, callbackGadget);

		Callback<TickBroadcast> callbackTick = c -> currentTime = c.getCurrentTick();
		this.subscribeBroadcast(TickBroadcast.class, callbackTick);

		Callback<TerminateBroadcast> callbackTerminate = c -> terminate();
		this.subscribeBroadcast(TerminateBroadcast.class, callbackTerminate);
	}

}
