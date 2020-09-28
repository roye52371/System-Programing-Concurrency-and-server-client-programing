package bgu.spl.mics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class FutureTest {

    private Future<String> future;

    @BeforeEach
    public void setUp() {
        future = new Future();
    }

    @Test
    public void testGet1() {
        assertNotEquals(future.get(),null);
    }

    @Test
    public void testResolve(){
        String result = "Trevelyan took part in the execution of the mission Thunderball";
        future.resolve(result);
        String get = future.get();
        assertEquals(result, get);
    }

    @Test
    public void testIsDone(){
        assertFalse(future.isDone());
        future.resolve("Trevelyan took part in the execution of the mission Thunderball");
        assertTrue(future.isDone());
    }

    @Test
    public void testGet2(){
        String get = future.get(1, TimeUnit.SECONDS);
        assertEquals(get, null);
        String result = "Trevelyan took part in the execution of the mission Thunderball";
        future.resolve(result);
        get = future.get(1, TimeUnit.SECONDS);
        assertEquals(result, get);
    }
}
