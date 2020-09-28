package bgu.spl.net.srv;

import javafx.util.Pair;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


public class ConnectionsImpl implements Connections<Frame> {

    private ConcurrentMap<Integer, DynamicPair<ConnectionHandler<Frame>, Boolean>> activeUsers;
    private ConcurrentMap<Integer, String> activeUsersNames;
    private ConcurrentMap<String, List<Pair<Integer, Integer>>> subscribersTopicsMap;  // pair key is connectionID, pair value is subscriptionID
    private ConcurrentMap<String, String> users;

    public ConnectionsImpl() {
        this.activeUsers = new ConcurrentHashMap<>();
        this.activeUsersNames = new ConcurrentHashMap<>();
        this.subscribersTopicsMap = new ConcurrentHashMap<>();
        this.users = new ConcurrentHashMap<>();
    }

    @Override
    public boolean send(int connectionId, Frame msg) {
        if(activeUsers.containsKey(connectionId)) {
            activeUsers.get(connectionId).getKey().send(msg);
            return true;
        }
        return false;
    }

    @Override
    public void send(String channel, Frame msg) {
        if(subscribersTopicsMap.containsKey(channel)) {
            List<Pair<Integer, Integer>> subscribersChannel = this.subscribersTopicsMap.get(channel);
            for (int i = 0; i < subscribersChannel.size(); i++) {
                msg.getHeaders().remove("Message-id");
                msg.getHeaders().put("Message-id", Frame.getFrameId().toString());
                msg.getHeaders().remove("subscription");
                msg.getHeaders().put("subscription", subscribersChannel.get(i).getValue().toString());
                send(subscribersChannel.get(i).getKey(), msg);
            }
        }
        else {
            List<Pair<Integer, Integer>> subscribers = new LinkedList<>();
            this.subscribersTopicsMap.put(channel, subscribers);
        }
    }

    @Override
    public void disconnect(int connectionId) {
        this.activeUsers.remove(connectionId);
        this.activeUsersNames.remove(connectionId);

        for (Map.Entry<String, List<Pair<Integer, Integer>>> topic : subscribersTopicsMap.entrySet()) {  // unsubscribe for all topics
            List<Pair<Integer, Integer>> subscribers = topic.getValue();
            for (int i = 0; i < subscribers.size(); i++)
                if (subscribers.get(i).getKey() == connectionId)
                    subscribers.remove(i);
        }
    }

    public boolean isConnected(int connectionId) { return this.activeUsers.containsKey(connectionId); }

    public boolean isAlreadyConected(int connectionId, String userName) {
        boolean output = this.activeUsers.get(connectionId).getValue();
        for (Map.Entry<Integer, String> user : activeUsersNames.entrySet()) {
            if(user.getValue().equals(userName))
                return true;
        }
        return output;
    }

    public void userConnect(int connectionId, String userName) {
        this.activeUsersNames.put(connectionId, userName);
        this.activeUsers.get(connectionId).setValue(true);
    }

    public boolean validNamePass(String userName, String password) {
        if(!this.users.containsKey(userName)) {
            this.users.put(userName, password);
            return true;
        }
        else if(this.users.get(userName).equals(password))
            return true;
        return false;
    }

    public void connect(int connectionId, ConnectionHandler<Frame> connectionHandler) {
        this.activeUsers.put(connectionId, new DynamicPair<>(connectionHandler, false));
    }

    public void disconnectUserName(int connectionId) {
        this.activeUsersNames.remove(connectionId);
    }

    public void subscribeTopic(String topic, int connectionId, int subscriptionId) {
        if (this.subscribersTopicsMap.containsKey(topic))
            this.subscribersTopicsMap.get(topic).add(new Pair<>(connectionId, subscriptionId));
        else {
            List<Pair<Integer, Integer>> subscribers = new LinkedList<>();
            subscribers.add(new Pair<>(connectionId, subscriptionId));
            this.subscribersTopicsMap.put(topic, subscribers);
        }
    }

    public boolean unsubscribeTopic(int connectionId, int subscriptionId) {
        for (Map.Entry<String, List<Pair<Integer, Integer>>> topic : subscribersTopicsMap.entrySet()) {
            List<Pair<Integer, Integer>> subscribers = topic.getValue();
            for (int i = 0; i < subscribers.size(); i++)
                if (subscribers.get(i).getKey() == connectionId & subscribers.get(i).getValue() == subscriptionId) {
                    subscribers.remove(i);
                    return true;
                }
        }
        return false;
    }

}
