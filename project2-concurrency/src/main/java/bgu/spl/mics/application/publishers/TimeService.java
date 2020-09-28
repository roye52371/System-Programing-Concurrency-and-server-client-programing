package bgu.spl.mics.application.publishers;

import bgu.spl.mics.Publisher;
import bgu.spl.mics.SimplePublisher;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import java.util.concurrent.TimeUnit;

/**
 * TimeService is the global system timer There is only one instance of this Publisher.
 * It keeps track of the amount of ticks passed since initialization and notifies
 * all other subscribers about the current time tick using.
 * This class may not hold references for objects which it is not responsible for.
 * 
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class TimeService extends Publisher {

	private SimplePublisher publisher;
	private int currentTick;
	private int duration;

	public TimeService(int duration) {
		super("TimeService");
		this.publisher = new SimplePublisher();
		this.currentTick = 0;
		this.duration = duration;
	}

	@Override
	protected void initialize() { }

	@Override
	public void run() {
		initialize();
		while (currentTick < duration) {
			try {
				TimeUnit.MILLISECONDS.sleep(100);
				currentTick++;
				TickBroadcast tickBroadcast = new TickBroadcast(currentTick);
				publisher.sendBroadcast(tickBroadcast);
			} catch (InterruptedException e) { }
		}
		TerminateBroadcast terminateBroadcast = new TerminateBroadcast();
		publisher.sendBroadcast(terminateBroadcast);
	}

	public int getCurrentTick() { return this.currentTick; }

}
