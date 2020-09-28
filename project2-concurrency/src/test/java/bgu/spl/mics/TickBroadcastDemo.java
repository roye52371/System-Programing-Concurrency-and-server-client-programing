package bgu.spl.mics;

import java.util.LinkedList;
import java.util.List;

public class TickBroadcastDemo implements Broadcast {

    private List<Subscriber> subscribers;

    public TickBroadcastDemo() {
        subscribers = new LinkedList<Subscriber>();
    }

    public boolean IsSubscribe(Subscriber subscriber) {
        for (int i = 0; i < subscribers.size(); i++)
            if(subscribers.get(i) == subscriber)
                return true;
        return false;
    }

}
