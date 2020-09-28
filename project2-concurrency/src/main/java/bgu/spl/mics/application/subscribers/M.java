package bgu.spl.mics.application.subscribers;

import bgu.spl.mics.*;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.passiveObjects.Diary;
import bgu.spl.mics.application.passiveObjects.Report;
import javafx.util.Pair;
import java.util.List;

/**
 * M handles ReadyEvent - fills a report and sends agents to mission.
 *
 * You can add private fields and public methods to this class.
 * You MAY change constructor signatures and even add new public constructors.
 */
public class M extends Subscriber {

	private int idM;
	private int currentTime;

	public M(int idM) {
		super("M");
		this.idM = idM;
		this.currentTime = 0;
	}

	@Override
	protected void initialize() {
		Callback<MissionReceivedEvent> callbackMission = c -> {
			Diary.getInstance().increment();

			if(!getShouldTerminate()) {
				List<String> agentNumbers = c.getMissionInfo().getSerialAgentsNumbers();
				AgentsAvailableEvent agentsEvent = new AgentsAvailableEvent(agentNumbers);
				Future<Triplet<Integer, Boolean, List<String>>> futureAgent = getSimplePublisher().sendEvent(agentsEvent);
				Integer idMoneypenny = null;
				Boolean isAgentsAvailable = false;
				List<String> agentsNames = null;
				if (futureAgent != null) {
					Triplet<Integer, Boolean, List<String>> resultAgentAvailable = futureAgent.get();
					idMoneypenny = resultAgentAvailable.getFirst();
					isAgentsAvailable = resultAgentAvailable.getSecond();
					agentsNames = resultAgentAvailable.getThird();
				}

				Boolean isGadgetAvailable = false;
				Integer qTime = null;
				String gadget = null;
				if (isAgentsAvailable) {
					gadget = c.getMissionInfo().getGadget();
					GadgetAvailableEvent gadgetEvent = new GadgetAvailableEvent(gadget);
					Future<Pair<Boolean, Integer>> futureGadget = getSimplePublisher().sendEvent(gadgetEvent);
					if (futureGadget != null) {
						Pair<Boolean, Integer> resultGadgetAvailable = futureGadget.get();
						isGadgetAvailable = resultGadgetAvailable.getKey();
						qTime = resultGadgetAvailable.getValue();
					}
				}

				if (isAgentsAvailable & isGadgetAvailable & currentTime < c.getMissionInfo().getTimeExpired()) {
					MissionSimulationEvent simulationEvent = new MissionSimulationEvent(agentNumbers, c.getMissionInfo().getDuration());
					Future<Boolean> futureMission = getSimplePublisher().sendEvent(simulationEvent);
					if (futureMission != null) {
						Report report = new Report(c.getMissionInfo().getMissionName(), idM, idMoneypenny, agentNumbers, agentsNames, gadget, c.getMissionInfo().getTimeIssued(), qTime, currentTime);
						Diary.getInstance().addReport(report);
					}
				}
				else if (isAgentsAvailable & (!isGadgetAvailable | currentTime >= c.getMissionInfo().getTimeExpired()) & !getShouldTerminate()) {
					ReleaseAgentsEvent releaseEvent = new ReleaseAgentsEvent(agentNumbers);
					getSimplePublisher().sendEvent(releaseEvent);
				}
			}
		};
		this.subscribeEvent(MissionReceivedEvent.class, callbackMission);

		Callback<TickBroadcast> callbackTick = c -> currentTime = c.getCurrentTick();
		this.subscribeBroadcast(TickBroadcast.class, callbackTick);

		Callback<TerminateBroadcast> callbackTerminate = c -> terminate();
		this.subscribeBroadcast(TerminateBroadcast.class, callbackTerminate);
	}

}
