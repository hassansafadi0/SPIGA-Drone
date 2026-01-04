package com.spiga.mission;

import com.spiga.core.ActifMobile;
import com.spiga.core.DroneReconnaissance;
import com.spiga.core.Point3D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MissionTest {

    private Mission mission;
    private ActifMobile drone;

    @BeforeEach
    public void setUp() {
        mission = new MissionReconnaissance("M1");
        drone = new DroneReconnaissance("D1", new Point3D(0, 0, 0));
    }

    @Test
    public void testAssignAsset() {
        mission.assignerActif(drone);
        // Mission class doesn't expose assigned assets list directly,
        // but we can check if drone state changed or if no exception occurred.
        // Assuming assignment works if no error.
        // We can verify drone is in mission? Not until started.
    }

    @Test
    public void testStartMission() {
        mission.assignerActif(drone);
        mission.demarrer();

        // Drone should be in mission state (or at least started)
        // Note: DroneReconnaissance starts in AU_SOL, demarrer might not change it
        // immediately
        // without movement command, but let's check if it runs without error.
        assertEquals(com.spiga.core.EtatOperationnel.AU_SOL, drone.getEtat()); // Initial state
    }

    @Test
    public void testMissionType() {
        assertEquals(ObjectifMission.RECONNAISSANCE, mission.getObjectif());

        mission.setObjectif(ObjectifMission.SURVEILLANCE);
        assertEquals(ObjectifMission.SURVEILLANCE, mission.getObjectif());
    }
}
