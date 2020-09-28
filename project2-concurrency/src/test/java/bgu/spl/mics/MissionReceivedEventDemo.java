package bgu.spl.mics;

import java.util.LinkedList;
import java.util.List;

public class MissionReceivedEventDemo implements Event<String> {

    private String senderName;
    private Future<String> result;
    private List<Subscriber> subscribers;

    public MissionReceivedEventDemo(String senderName) {
        this.senderName = senderName;
        result = new Future<String>();
        subscribers = new LinkedList<>();
    }

    public String getSenderName() {
        return senderName;
    }

    public boolean IsResult(Future<String> future) {
        return result.get() == future.get();
    }

    public boolean IsSubscribe(Subscriber subscriber) {
        for (int i = 0; i < subscribers.size(); i++)
            if(subscribers.get(i) == subscriber)
                return true;
        return false;
    }

}
