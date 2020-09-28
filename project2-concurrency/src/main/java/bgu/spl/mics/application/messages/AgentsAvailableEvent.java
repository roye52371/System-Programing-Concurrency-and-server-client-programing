package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import bgu.spl.mics.Triplet;

import java.util.List;

public class AgentsAvailableEvent implements Event<Triplet<Integer, Boolean, List<String>>> {

    private List<String> agentsNumber;

    public AgentsAvailableEvent(List<String> agentsNumber) {
        this.agentsNumber = agentsNumber;
    }

    public List<String> getAgentsNumber() { return this.agentsNumber; }

}
