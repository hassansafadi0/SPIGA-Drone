package com.spiga.core;

import com.spiga.env.ZoneOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DroneLogistiqueTest {

    private DroneLogistique drone;
    private ZoneOperation zone;

    @BeforeEach
    public void setUp() {
        drone = new DroneLogistique("L1", new Point3D(0, 0, 10));
        zone = new ZoneOperation(new Point3D(0, 0, 0), new Point3D(1000, 1000, 1000));
        zone.addCollidable(drone);
    }

    @Test
    public void testMovement() {
        Point3D target = new Point3D(100, 100, 20);
        drone.deplacer(target, zone);

        assertTrue(drone.getPosition().getX() > 0);
        assertTrue(drone.getPosition().getY() > 0);
    }

    @Test
    public void testCapacity() {
        assertEquals(10.0, drone.getCapaciteEmport(), 0.1);

        DroneLogistique heavyDrone = new DroneLogistique("L2", new Point3D(0, 0, 0), 50.0);
        assertEquals(50.0, heavyDrone.getCapaciteEmport(), 0.1);
    }

    @Test
    public void testOperations() {
        drone.demarrer();
        // Just verify no exception

        drone.envoyerMessage("Base", "Colis livré");
        // Verify output manually or assume success if no crash

        drone.arreter();

        drone.notifierEtatCritique(TypeAlerte.BATTERIE_FAIBLE);
    }

    @Test
    public void testRecharge() {
        double max = drone.getAutonomieMax();
        drone.setAutonomieActuelle(10.0);

        drone.recharger(20.0);
        assertEquals(30.0, drone.getAutonomieActuelle(), 0.1);

        drone.ravitailler();
        assertEquals(max, drone.getAutonomieActuelle(), 0.1);
    }
}
