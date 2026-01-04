package com.spiga.core;

import com.spiga.env.ZoneOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VehiculeSousMarinTest {

    private VehiculeSousMarin sub;
    private ZoneOperation zone;

    @BeforeEach
    public void setUp() {
        sub = new VehiculeSousMarin("U1", new Point3D(500, 500, -50));
        zone = new ZoneOperation(new Point3D(0, 0, -1000), new Point3D(1000, 1000, 1000));
        zone.addCollidable(sub);
    }

    @Test
    public void testUnderwaterMovement() {
        Point3D target = new Point3D(600, 600, -50);
        sub.deplacer(target, zone);

        assertTrue(sub.getPosition().getX() > 500);
        assertTrue(sub.getPosition().getY() > 500);
        assertEquals(-50, sub.getPosition().getZ(), 0.1);
    }

    @Test
    public void testDepthConstraint() {
        // Submarines shouldn't fly
        Point3D target = new Point3D(500, 500, 100);
        sub.deplacer(target, zone);

        // Assuming implementation prevents going above water or clamps it
        // If not explicitly handled, it might just move.
        // But let's check if it stays underwater or at surface (0).
        assertTrue(sub.getPosition().getZ() <= 0);
    }
}
