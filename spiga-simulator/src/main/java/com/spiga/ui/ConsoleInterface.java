package com.spiga.ui;

import com.spiga.core.*;
import com.spiga.env.ZoneOperation;
import com.spiga.mission.*;
import java.util.Scanner;

public class ConsoleInterface {
    private GestionnaireEssaim gestionnaire;
    private ZoneOperation zone;
    private Scanner scanner;

    public ConsoleInterface() {
        this.gestionnaire = new GestionnaireEssaim();
        // Default zone 1000x1000x1000
        this.zone = new ZoneOperation(new Point3D(0, 0, 0), new Point3D(1000, 1000, 1000));
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n--- SPIGA CLI ---");
            System.out.println("1. Créer un actif");
            System.out.println("2. Lister les actifs");
            System.out.println("3. Créer une mission");
            System.out.println("4. Simuler un pas de temps");
            System.out.println("5. Quitter");
            System.out.print("Choix: ");

            String choice = scanner.nextLine();
            switch (choice) {
                case "1":
                    createAsset();
                    break;
                case "2":
                    listAssets();
                    break;
                case "3":
                    createMission();
                    break;
                case "4":
                    simulateStep();
                    break;
                case "5":
                    running = false;
                    break;
                default:
                    System.out.println("Choix invalide.");
            }
        }
    }

    private void createAsset() {
        System.out.println(
                "Type d'actif (1: DroneReconnaissance, 2: DroneLogistique, 3: VehiculeSurface, 4: VehiculeSousMarin): ");
        String type = scanner.nextLine();
        System.out.print("ID: ");
        String id = scanner.nextLine();

        ActifMobile actif = null;
        Point3D startPos = new Point3D(0, 0, 0); // Default start

        switch (type) {
            case "1":
                actif = new DroneReconnaissance(id, startPos);
                break;
            case "2":
                actif = new DroneLogistique(id, startPos);
                break;
            case "3":
                actif = new VehiculeSurface(id, startPos);
                break;
            case "4":
                actif = new VehiculeSousMarin(id, startPos);
                break;
            default:
                System.out.println("Type inconnu.");
                return;
        }

        gestionnaire.ajouterActif(actif);
        System.out.println("Actif créé.");
    }

    private void listAssets() {
        for (ActifMobile a : gestionnaire.getFlotte()) {
            System.out.println(a.getId() + " [" + a.getClass().getSimpleName() + "] - " + a.getEtat() + " - Pos: "
                    + a.getPosition());
        }
    }

    private void createMission() {
        System.out.print("ID Mission: ");
        String id = scanner.nextLine();
        Mission mission = new MissionReconnaissance(id);

        // Auto assign first available asset for demo
        ActifMobile asset = gestionnaire.suggererActif(10);
        if (asset != null) {
            mission.assignerActif(asset);
            mission.demarrer(); // Start immediately for demo
        } else {
            System.out.println("Aucun actif disponible.");
        }
    }

    private void simulateStep() {
        // Move all assets in mission
        // For simplicity, move all assets towards a dummy target (100, 100, 100)
        Point3D target = new Point3D(100, 100, 100);
        for (ActifMobile a : gestionnaire.getFlotte()) {
            if (a.getEtat() == EtatOperationnel.EN_MISSION || a.getEtat() == EtatOperationnel.AU_SOL) { // Allow AU_SOL
                                                                                                        // to move for
                                                                                                        // test
                a.deplacer(target, zone);
            }
        }
    }

    public static void main(String[] args) {
        new ConsoleInterface().start();
    }
}
