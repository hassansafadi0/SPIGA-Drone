package com.spiga.core;

import com.spiga.env.ZoneOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VehiculeSurfaceTest {

    private VehiculeSurface ship;
    private ZoneOperation zone;

    @BeforeEach
    public void setUp() {
        ship = new VehiculeSurface("S1", new Point3D(500, 500, 0));
        zone = new ZoneOperation(new Point3D(0, 0, 0), new Point3D(1000, 1000, 1000));
        zone.addCollidable(ship);
    }

    @Test
    public void testSurfaceMovement() {
        Point3D target = new Point3D(600, 600, 0);
        ship.deplacer(target, zone);

        assertTrue(ship.getPosition().getX() > 500);
        assertTrue(ship.getPosition().getY() > 500);
        assertEquals(0, ship.getPosition().getZ(), 0.1);
    }
}
