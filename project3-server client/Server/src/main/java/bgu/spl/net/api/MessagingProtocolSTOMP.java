package bgu.spl.net.api;

import bgu.spl.net.srv.ConnectionsImpl;
import bgu.spl.net.srv.Frame;

import java.util.HashMap;
import java.util.Map;

public class MessagingProtocolSTOMP implements StompMessagingProtocol {

    private boolean shouldTerminate = false;
    private int connectionId;
    private ConnectionsImpl connections;

    @Override
    public void start(int connectionId, ConnectionsImpl connections) {
        this.connectionId = connectionId;
        this.connections = connections;
    }

    @Override
    public void process(Frame message) {

        if(message.getStompCommand().equals("CONNECT")) {
            Map<String, String> messageHeaders = message.getHeaders();
            String version = messageHeaders.get("accept-version");
            String userName = messageHeaders.get("login");
            String password = messageHeaders.get("passcode");
            if(connections.isConnected(this.connectionId)) {
                if(!connections.isAlreadyConected(this.connectionId, userName)) {
                    if(connections.validNamePass(userName, password)) {
                        connections.userConnect(this.connectionId, userName);
                        Map<String, String> headers = new HashMap<>();
                        headers.put("version", version);
                        Frame frame = new Frame("CONNECTED", headers, "");
                        this.connections.send(this.connectionId, frame);
                    }
                    else {
                        Map<String, String> headers = new HashMap<>();
                        headers.put("message", "Wrong password");
                        Frame frame = new Frame("ERROR", headers, "");
                        this.connections.send(this.connectionId, frame);
                        this.connections.disconnectUserName(connectionId);
                        this.connections.disconnect(connectionId);
                        shouldTerminate = true;
                    }
                }
                else {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("message", "User already logged in");
                    Frame frame = new Frame("ERROR", headers, "");
                    this.connections.send(this.connectionId, frame);
                    this.connections.disconnectUserName(connectionId);
                    this.connections.disconnect(connectionId);
                    shouldTerminate = true;
                }
            }
            else
                System.out.println("Could not connect to server");
        }

        if(message.getStompCommand().equals("SUBSCRIBE")) {
            Map<String, String> messageHeaders = message.getHeaders();
            String topic = messageHeaders.get("destination");
            int id = Integer.parseInt(messageHeaders.get("id"));
            this.connections.subscribeTopic(topic, this.connectionId, id);
            if(messageHeaders.containsKey("receipt")) {
                Map<String, String> headers = new HashMap<>();
                String rId = message.getHeaders().get("receipt");
                headers.put("receipt-id", rId);
                Frame frame = new Frame("RECEIPT", headers, "");
                connections.send(this.connectionId, frame);
            }
        }

        if(message.getStompCommand().equals("UNSUBSCRIBE")) {
            Map<String, String> messageHeaders = message.getHeaders();
            int id = Integer.parseInt(messageHeaders.get("id"));
            boolean b = this.connections.unsubscribeTopic(this.connectionId, id);
            if(messageHeaders.containsKey("receipt") & b) {
                Map<String, String> headers = new HashMap<>();
                String rId = message.getHeaders().get("receipt");
                headers.put("receipt-id", rId);
                Frame frame = new Frame("RECEIPT", headers, "");
                connections.send(this.connectionId, frame);
            }
        }

        if(message.getStompCommand().equals("SEND")) {
            Map<String, String> messageHeaders = message.getHeaders();
            String topic = messageHeaders.get("destination");
            Map<String, String> headers = new HashMap<>();
            headers.put("destination", topic);
            headers.put("Message-id", "");
            headers.put("subscription", "");
            String frameBody = message.getFrameBody();
            Frame frame = new Frame("MESSAGE", headers, frameBody);
            this.connections.send(topic, frame);
            if (messageHeaders.containsKey("receipt")) {
                Map<String, String> reciptHeaders = new HashMap<>();
                String rId = message.getHeaders().get("receipt");
                reciptHeaders.put("receipt-id", rId);
                Frame reciptFrame = new Frame("RECEIPT", reciptHeaders, "");
                connections.send(this.connectionId, reciptFrame);
            }
        }

        if(message.getStompCommand().equals("DISCONNECT")) {
            Map<String, String> headers = new HashMap<>();
            String id = message.getHeaders().get("receipt");
            headers.put("receipt-id", id);
            Frame frame = new Frame("RECEIPT", headers, "");
            connections.send(this.connectionId, frame);

            this.connections.disconnectUserName(connectionId);
            this.connections.disconnect(connectionId);
            shouldTerminate = true;
        }

    }

    @Override
    public boolean shouldTerminate() { return shouldTerminate; }

}
