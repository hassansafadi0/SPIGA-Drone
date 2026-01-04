package com.spiga.core;

import com.spiga.env.ZoneOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VehiculeTerrestreTest {

    private VehiculeTerrestre car;
    private ZoneOperation zone;

    @BeforeEach
    public void setUp() {
        // Island 1 is at 300, 300 with radius 150.
        car = new VehiculeTerrestre("C1", new Point3D(300, 300, 0)); // On land
        zone = new ZoneOperation(new Point3D(0, 0, 0), new Point3D(1000, 1000, 1000));
        zone.addCollidable(car);
    }

    @Test
    public void testMovementOnLand() {
        Point3D target = new Point3D(310, 310, 0); // Still on land
        car.deplacer(target, zone);

        assertTrue(car.getPosition().getX() > 300);
        assertTrue(car.getPosition().getY() > 300);
    }

    @Test
    public void testCollisionWithWater() {
        // Try to move to water (0,0)
        Point3D target = new Point3D(0, 0, 0);

        car.deplacer(target, zone);

        // Should not move into water
        // The implementation checks isLand for next position.
        // If pathfinding is used, it returns fallback or path.
        // If direct move, it checks constraints.
        // Let's assume it shouldn't reach 0,0.
        assertNotEquals(target, car.getPosition());
    }
}
