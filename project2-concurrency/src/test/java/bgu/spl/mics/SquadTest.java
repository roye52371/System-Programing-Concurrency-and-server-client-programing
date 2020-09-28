package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Agent;
import bgu.spl.mics.application.passiveObjects.Squad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SquadTest {
    private Squad squad;

    @BeforeEach
    public void setUp() {
        squad = Squad.getInstance();
    }

    @Test
    public void testGetInstance() {
        assertNotEquals(squad,null);
    }

    @Test
    public void testLoad(){
        Agent a1 = new Agent();
        a1.setName("James Bond");
        a1.setSerialNumber("007");
        a1.release();
        Agent a2 = new Agent();
        a2.setName("Daniel Krage");
        a2.setSerialNumber("002");
        a2.release();
        Agent[] agents = {a1, a2};
        List<String> serials = Arrays.asList("007", "002");
        squad.load(agents);
        assertTrue(squad.getAgents(serials));
    }

    @Test
    public void testReleaseAgents() {
        Agent a1 = new Agent();
        a1.setName("James Bond");
        a1.setSerialNumber("007");
        a1.release();
        Agent a2 = new Agent();
        a2.setName("Daniel Krage");
        a2.setSerialNumber("002");
        a2.release();
        Agent[] agents = {a1, a2};
        squad.load(agents);
        List<String> serials = Arrays.asList("002");
        squad.releaseAgents(serials);
        assertFalse(squad.getAgents(serials));
    }

    @Test
    public void testGetAgents() {
        Agent a1 = new Agent();
        a1.setName("James Bond");
        a1.setSerialNumber("007");
        a1.release();
        Agent a2 = new Agent();
        a2.setName("Daniel Krage");
        a2.setSerialNumber("002");
        a2.release();
        Agent[] agents = {a1, a2};
        squad.load(agents);
        List<String> serials = Arrays.asList("002");
        squad.releaseAgents(serials);
        assertFalse(squad.getAgents(serials));

        serials = Arrays.asList("007");
        assertTrue(squad.getAgents(serials));
    }

    @Test
    public void testGetAgentsNames() {
        Agent a1 = new Agent();
        a1.setName("James Bond");
        a1.setSerialNumber("007");
        a1.release();
        Agent a2 = new Agent();
        a2.setName("Daniel Krage");
        a2.setSerialNumber("002");
        a2.release();
        Agent[] agents = {a1, a2};
        squad.load(agents);
        List<String> serials = Arrays.asList("007");
        List<String> names = squad.getAgentsNames(serials);
        assertEquals(names.get(0), "James Bond");
    }
}
