package bgu.spl.net.srv;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class Frame {

    private static AtomicInteger frameId = new AtomicInteger(0);

    private String stompCommand;
    private Map<String, String> headers;
    private String frameBody;

    public Frame(String stompCommand, Map<String, String> headers, String frameBody)
    {
        this.stompCommand = stompCommand;
        this.headers = headers;
        this.frameBody = frameBody;
    }

    public String getStompCommand() { return this.stompCommand; }
    public Map<String, String> getHeaders() { return this.headers; }
    public String getFrameBody() { return this.frameBody; }
    public static Integer getFrameId() {
        frameId.incrementAndGet();
        return frameId.get();
    }

}
