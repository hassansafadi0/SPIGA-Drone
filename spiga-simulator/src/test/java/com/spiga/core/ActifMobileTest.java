package com.spiga.core;

import com.spiga.env.ZoneOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ActifMobileTest {

    private ActifMobile drone;
    private ZoneOperation zone;

    @BeforeEach
    public void setUp() {
        drone = new DroneReconnaissance("D1", new Point3D(0, 0, 50));
        zone = new ZoneOperation(new Point3D(0, 0, 0), new Point3D(1000, 1000, 1000));
        zone.addCollidable(drone);
    }

    @Test
    public void testMovement() {
        Point3D target = new Point3D(100, 100, 50);
        drone.deplacer(target, zone);

        // Drone should have moved towards target
        assertTrue(drone.getPosition().getX() > 0);
        assertTrue(drone.getPosition().getY() > 0);
        assertEquals(EtatOperationnel.EN_MISSION, drone.getEtat());
    }

    @Test
    public void testEnergyConsumption() {
        double initialAutonomy = drone.getAutonomieActuelle();
        Point3D target = new Point3D(100, 100, 50);

        drone.deplacer(target, zone);

        assertTrue(drone.getAutonomieActuelle() < initialAutonomy, "Energy should decrease after movement");
    }

    @Test
    public void testStateTransition() {
        assertEquals(EtatOperationnel.AU_SOL, drone.getEtat());

        drone.demarrer();
        // Depending on implementation, demarrer might not change state immediately if
        // not moving
        // But deplacer should

        Point3D target = new Point3D(100, 100, 50);
        drone.deplacer(target, zone);
        assertEquals(EtatOperationnel.EN_MISSION, drone.getEtat());

        drone.arreter();
        // Arreter might print but state change logic depends on implementation
    }

    @Test
    public void testBatteryDepletion() {
        drone.setAutonomieActuelle(1.0);
        Point3D target = new Point3D(1000, 1000, 50); // Far target

        // Move until depletion
        for (int i = 0; i < 100; i++) {
            drone.deplacer(target, zone);
            if (drone.getAutonomieActuelle() <= 0)
                break;
        }

        assertEquals(0.0, drone.getAutonomieActuelle(), 0.1);
        assertEquals(EtatOperationnel.EN_PANNE, drone.getEtat());
    }
}
