package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.AgentsAvailableEvent;
import bgu.spl.mics.application.messages.MissionSimulationEvent;
import bgu.spl.mics.application.messages.ReleaseAgentsEvent;
import bgu.spl.mics.application.messages.TerminateBroadcast;
import bgu.spl.mics.application.passiveObjects.Squad;

import java.util.LinkedList;
import java.util.List;

/**
 * Only this type of Subscriber can access the squad.
 * Three are several Moneypenny-instances - each of them holds a unique serial number that will later be printed on the report.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class Moneypenny extends Subscriber {

	private int idMoneypenny;
	private static boolean oneMoneypennyTerminated = false;

	public Moneypenny(int idMoneypenny) {
		super("Moneypenny");
		this.idMoneypenny = idMoneypenny;
	}

	public static boolean isOneMoneypennyTerminated() {
		return oneMoneypennyTerminated;
	}

	@Override
	protected void initialize() {
		if(idMoneypenny > 1) {
			Callback<AgentsAvailableEvent> callbackAgents = c -> {
				Boolean available = Squad.getInstance().getAgents(c.getAgentsNumber());
				List<String> agentsNames = Squad.getInstance().getAgentsNames(c.getAgentsNumber());
				Triplet<Integer, Boolean, List<String>> result = new Triplet<>(idMoneypenny, available, agentsNames);
				complete(c, result);
			};
			this.subscribeEvent(AgentsAvailableEvent.class, callbackAgents);
		}
		else {
			Callback<MissionSimulationEvent> callbackSimulation = c -> {
				Squad.getInstance().sendAgents(c.getSerials(), c.getDuration());
				complete(c, true);
			};
			this.subscribeEvent(MissionSimulationEvent.class, callbackSimulation);

			Callback<ReleaseAgentsEvent> callbackRelease = c -> {
				Squad.getInstance().releaseAgents(c.getSerialsAgentsNumbers());
				complete(c, true);
			};
			this.subscribeEvent(ReleaseAgentsEvent.class, callbackRelease);
		}

		Callback<TerminateBroadcast> callbackTerminate = new Callback<TerminateBroadcast>() {
			@Override
			public synchronized void call(TerminateBroadcast c) {
				if(idMoneypenny == 1) {
					oneMoneypennyTerminated = true;
					Squad.getInstance().releaseAgents(new LinkedList<>());
				}
				terminate();
			}
		};
		this.subscribeBroadcast(TerminateBroadcast.class, callbackTerminate);
	}
}
