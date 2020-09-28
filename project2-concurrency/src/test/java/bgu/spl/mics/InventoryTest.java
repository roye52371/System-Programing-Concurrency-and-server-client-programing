package bgu.spl.mics;

import bgu.spl.mics.application.passiveObjects.Inventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryTest {
    private Inventory inventory;

    @BeforeEach
    public void setUp(){
        inventory = Inventory.getInstance();
    }

    @Test
    public void testGetInstance(){
        assertNotEquals(inventory,null);
    }

    @Test
    public void testLoad(){
        String[] gadgets = {"KillingPen", "FlyingCar", "Grenade"};
        inventory.load(gadgets);
        for (int i =0; i < gadgets.length; i++)
            assertTrue(inventory.getItem(gadgets[i]));
    }

    @Test
    public void testGetItem(){
        String[] gadgets = {"KillingPen", "FlyingCar", "Grenade"};
        inventory.load(gadgets);
        String gadget = "FlyingCar";
        assertTrue(inventory.getItem(gadget));

        gadget = "ShootGun";
        assertFalse(inventory.getItem(gadget));
    }
}
