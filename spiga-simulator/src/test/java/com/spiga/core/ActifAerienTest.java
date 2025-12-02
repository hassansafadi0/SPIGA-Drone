package com.spiga.core;

import com.spiga.env.ZoneOperation;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ActifAerienTest {

    @Test
    public void testDeplacement() {
        ZoneOperation zone = new ZoneOperation(new Point3D(0, 0, 0), new Point3D(1000, 1000, 1000));
        ActifAerien drone = new DroneReconnaissance("D1", new Point3D(0, 0, 0));

        // Start drone
        drone.demarrer();
        drone.setEtat(EtatOperationnel.EN_MISSION); // Force state for test if needed, though demarrer might not set it
                                                    // to EN_MISSION automatically in my impl

        Point3D target = new Point3D(100, 0, 0);
        drone.deplacer(target, zone);

        // Speed is 100. Distance is 100. Should reach target in 1 step?
        // My impl: moveX = nx * speed. nx=1. moveX=100.
        // New pos should be 100, 0, 0.

        assertEquals(100.0, drone.getPosition().getX(), 0.01);
        assertEquals(0.0, drone.getPosition().getY(), 0.01);
        assertEquals(0.0, drone.getPosition().getZ(), 0.01);
    }

    @Test
    public void testConsumption() {
        ZoneOperation zone = new ZoneOperation(new Point3D(0, 0, 0), new Point3D(1000, 1000, 1000));
        ActifAerien drone = new DroneReconnaissance("D1", new Point3D(0, 0, 0));
        double initialAutonomy = drone.getAutonomieMax();

        drone.deplacer(new Point3D(100, 0, 0), zone);

        assertTrue(drone.getAutonomieActuelle() < initialAutonomy);
    }
}
