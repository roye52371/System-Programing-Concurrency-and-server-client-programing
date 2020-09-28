package bgu.spl.mics;

import bgu.spl.mics.application.messages.TickBroadcast;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MessageBrokerTest {

    private MessageBroker messageBroker;

    @BeforeEach
    public void setUp(){
        messageBroker = MessageBrokerImpl.getInstance();
    }

    @Test
    public void testGetInstance(){
        assertNotEquals(messageBroker,null);
    }

    @Test
    public void testSubscribeEvent(){
        MDemo m = new MDemo("M");
        m.Register();
        MissionReceivedEventDemo missionReceivedEvent = new MissionReceivedEventDemo("M");
        assertFalse(missionReceivedEvent.IsSubscribe(m));
        messageBroker.subscribeEvent(MissionReceivedEventDemo.class, m);
        assertTrue(missionReceivedEvent.IsSubscribe(m));
    }

    @Test
    public void testSubscribeBroadcast(){
        MDemo m = new MDemo("M");
        m.Register();
        TickBroadcastDemo tickBroadcast = new TickBroadcastDemo();
        assertFalse(tickBroadcast.IsSubscribe(m));
        messageBroker.subscribeBroadcast(TickBroadcastDemo.class, m);
        assertTrue(tickBroadcast.IsSubscribe(m));
    }

    @Test
    public void testComplete(){
        MissionReceivedEventDemo missionReceivedEvent = new MissionReceivedEventDemo("Trevelyan");
        String result = "Trevelyan took part in the execution of the mission Thunderball";
        Future<String> future = new Future<>();
        future.resolve(result);
        assertFalse(missionReceivedEvent.IsResult(future));
        messageBroker.complete(missionReceivedEvent, result);
        assertTrue(missionReceivedEvent.IsResult(future));
    }

    @Test
    public void testSendBroadcast(){
        TickBroadcastDemo broadcast = new TickBroadcastDemo();
        MDemo m = new MDemo("M");
        m.Register();
        TickBroadcastCallbackDemo callback = new TickBroadcastCallbackDemo();
        m.subscribeBroadcast(TickBroadcast.class, callback);
        assertFalse(m.IsMessageExist(broadcast));
        messageBroker.sendBroadcast(broadcast);
        assertTrue(m.IsMessageExist(broadcast));
    }

    @Test
    public void testSendEvent(){
        MDemo m = new MDemo("M");
        m.Register();
        MissionReceivedEventDemo event = new MissionReceivedEventDemo("M");
        MissionReceivedCallbackDemo callback = new MissionReceivedCallbackDemo();
        m.subscribeEvent(MissionReceivedEventDemo.class, callback);
        assertFalse(m.IsMessageExist(event));
        Future future = messageBroker.sendEvent(event);
        assertTrue(m.IsMessageExist(event));
        assertNotEquals(future, null);
    }

    @Test
    public void testRegister(){
        MDemo m = new MDemo("M");
        messageBroker.register(m);
        assertTrue(m.IsMessagesEmpty());
    }

    @Test
    public void testUnregister(){
        MDemo m = new MDemo("M");
        m.Register();
        MissionReceivedCallbackDemo callback = new MissionReceivedCallbackDemo();
        m.subscribeEvent(MissionReceivedEventDemo.class, callback);
        assertFalse(m.IsMessagesEmpty());
        messageBroker.unregister(m);
        assertTrue(m.IsMessagesEmpty());
    }

    @Test
    public void testAwaitMessage(){
        MDemo m = new MDemo("M");
        try {
            messageBroker.awaitMessage(m);
            fail();
        }
        catch (Exception e) {}
        m.Register();
        MissionReceivedCallbackDemo callback = new MissionReceivedCallbackDemo();
        m.subscribeEvent(MissionReceivedEventDemo.class, callback);
        try {
            Message message = messageBroker.awaitMessage(m);
            assertEquals(message, MissionReceivedEventDemo.class);
        }
        catch (Exception e){}
    }
}
