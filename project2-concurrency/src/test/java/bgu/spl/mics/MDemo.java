package bgu.spl.mics;

import bgu.spl.mics.application.messages.MissionReceivedEvent;
import bgu.spl.mics.example.messages.ExampleEvent;

import java.util.LinkedList;
import java.util.List;

public class MDemo extends Subscriber {

    private List<Message> messages;

    public MDemo(String name) {
        super(name);
        messages = new LinkedList<>();
    }

    public void Register() {
        messages = new LinkedList<Message>();
    }

    public void Unregister() {
        for (int i = 0; i < messages.size(); i++)
            messages.remove(i);
    }

    @Override
    protected void initialize() {

    }

    public boolean IsMessageExist(Message message) {
        for (int i = 0; i < messages.size(); i++)
            if(messages.get(i).equals(message))
                return true;
        return false;
    }

    public boolean IsMessagesEmpty() { return messages.isEmpty(); }

}
