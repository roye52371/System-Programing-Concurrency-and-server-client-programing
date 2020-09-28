package bgu.spl.mics.application.messages;

import bgu.spl.mics.Event;
import java.util.List;

public class ReleaseAgentsEvent implements Event<Boolean> {

    private List<String> serialsAgentsNumbers;

    public ReleaseAgentsEvent(List<String> serialsAgentsNumbers) {
        this.serialsAgentsNumbers = serialsAgentsNumbers;
    }

    public List<String> getSerialsAgentsNumbers() { return this.serialsAgentsNumbers; }

}
