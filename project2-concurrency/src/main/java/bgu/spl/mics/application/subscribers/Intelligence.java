package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.MissionReceivedEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.passiveObjects.MissionInfo;
import java.util.List;

/**
 * A Publisher only.
 * Holds a list of Info objects and sends them
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Intelligence extends Subscriber {

	private SimplePublisher publisher;
	private List<MissionInfo> missionInfo;
	private int currentTick;

	public Intelligence(List<MissionInfo> missionInfo) {
		super("Intelligence");
		this.publisher = new SimplePublisher();
		this.missionInfo = missionInfo;
		this.currentTick = 0;
	}

	@Override
	protected void initialize() {
		Callback<TickBroadcast> callbackTick = c -> {
			currentTick = c.getCurrentTick();
			for (int i = 0; i < missionInfo.size(); i++) {
				if (missionInfo.get(i).getTimeIssued() == currentTick) {
					MissionReceivedEvent newMission = new MissionReceivedEvent(missionInfo.get(i));
					publisher.sendEvent(newMission);
				}
			}
		};
		this.subscribeBroadcast(TickBroadcast.class, callbackTick);

		Callback<TerminateBroadcast> callbackTerminate = c -> terminate();
		this.subscribeBroadcast(TerminateBroadcast.class, callbackTerminate);
	}

}
