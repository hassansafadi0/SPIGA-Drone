package com.spiga.mission;

import com.spiga.core.ActifMobile;
import com.spiga.core.DroneReconnaissance;
import com.spiga.core.EtatOperationnel;
import com.spiga.core.Point3D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GestionnaireEssaimTest {

    private GestionnaireEssaim gestionnaire;

    @BeforeEach
    public void setUp() {
        gestionnaire = new GestionnaireEssaim();
    }

    @Test
    public void testAjouterActif() {
        ActifMobile drone = new DroneReconnaissance("D1", new Point3D(0, 0, 0));
        gestionnaire.ajouterActif(drone);
        assertEquals(1, gestionnaire.getFlotte().size());
        assertEquals("D1", gestionnaire.getFlotte().get(0).getId());
    }

    @Test
    public void testGetActifsDisponibles() {
        ActifMobile d1 = new DroneReconnaissance("D1", new Point3D(0, 0, 0));
        d1.setEtat(EtatOperationnel.AU_SOL);

        ActifMobile d2 = new DroneReconnaissance("D2", new Point3D(0, 0, 0));
        d2.setEtat(EtatOperationnel.EN_MISSION);

        gestionnaire.ajouterActif(d1);
        gestionnaire.ajouterActif(d2);

        assertEquals(1, gestionnaire.getActifsDisponibles().size());
        assertEquals("D1", gestionnaire.getActifsDisponibles().get(0).getId());
    }

    @Test
    public void testSuggererActif() {
        ActifMobile d1 = new DroneReconnaissance("D1", new Point3D(0, 0, 0));
        d1.setAutonomieActuelle(50);

        ActifMobile d2 = new DroneReconnaissance("D2", new Point3D(0, 0, 0));
        d2.setAutonomieActuelle(100);

        gestionnaire.ajouterActif(d1);
        gestionnaire.ajouterActif(d2);

        // Should find d2 (autonomy >= 80)
        ActifMobile suggested = gestionnaire.suggererActif(80);
        assertNotNull(suggested);
        assertEquals("D2", suggested.getId());

        // Should find d1 or d2 (autonomy >= 40)
        suggested = gestionnaire.suggererActif(40);
        assertNotNull(suggested);

        // Should find nothing (autonomy >= 120)
        suggested = gestionnaire.suggererActif(120);
        assertNull(suggested);
    }
}
