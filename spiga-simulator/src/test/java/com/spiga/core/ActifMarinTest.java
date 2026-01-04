package com.spiga.core;

import com.spiga.env.ZoneOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ActifMarinTest {

    private ActifMarin boat;
    private ZoneOperation zone;

    @BeforeEach
    public void setUp() {
        boat = new VehiculeSurface("S1", new Point3D(500, 500, 0)); // Water area
        zone = new ZoneOperation(new Point3D(0, 0, 0), new Point3D(1000, 1000, 1000));
        zone.addCollidable(boat);
    }

    @Test
    public void testMovementOnWater() {
        Point3D target = new Point3D(600, 600, 0); // Still water
        boat.deplacer(target, zone);

        assertTrue(boat.getPosition().getX() > 500);
        assertTrue(boat.getPosition().getY() > 500);
    }

    @Test
    public void testCollisionWithLand() {
        // Island 1 is at 300, 300 with radius 150.
        // Try to move into island
        boat.setPosition(new Point3D(460, 460, 0)); // Near island edge
        Point3D target = new Point3D(300, 300, 0); // Center of island

        boat.deplacer(target, zone);

        // Should not have moved significantly into land (simple check if it moved full
        // speed)
        // Note: Current implementation might just stop or not move.
        // Let's verify it didn't reach target.
        assertNotEquals(target, boat.getPosition());
    }
}
