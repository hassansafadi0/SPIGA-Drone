package com.spiga.ui;

import com.spiga.core.*;
import com.spiga.mission.GestionnaireEssaim;
import com.spiga.mission.Mission;
import com.spiga.mission.MissionReconnaissance;
import com.spiga.env.ZoneOperation;
import java.util.Scanner;

/**
 * Console-based interface for the simulation.
 * Provides a text-based menu to interact with the fleet manager.
 */
public class ConsoleInterface {
    private GestionnaireEssaim gestionnaire;
    private Scanner scanner;
    private ZoneOperation zone;

    /**
     * Constructor for ConsoleInterface.
     * 
     * @param gestionnaire The fleet manager.
     */
    public ConsoleInterface(GestionnaireEssaim gestionnaire) {
        this.gestionnaire = gestionnaire;
        this.scanner = new Scanner(System.in);
        // Initialize persistent zone
        this.zone = new ZoneOperation(new Point3D(0, 0, -1000), new Point3D(1000, 1000, 1000));
    }

    /**
     * Starts the console interface loop.
     * Displays a menu and processes user input.
     */
    /**
     * Starts the console interface loop.
     * Displays a menu and processes user input.
     */
    public void demarrer() {
        boolean running = true;
        while (running) {
            try {
                System.out.println("\n--- SPIGA Simulator Console ---");
                System.out.println("1. Ajouter un actif");
                System.out.println("2. Lister la flotte");
                System.out.println("3. Créer une mission");
                System.out.println("4. Simuler un pas de temps");
                System.out.println("5. Quitter");
                System.out.print("Choix: ");

                String choix = scanner.nextLine();
                switch (choix) {
                    case "1":
                        ajouterActif();
                        break;
                    case "2":
                        listerFlotte();
                        break;
                    case "3":
                        creerMission();
                        break;
                    case "4":
                        simulerPasDeTemps();
                        break;
                    case "5":
                        running = false;
                        break;
                    default:
                        System.out.println("Choix invalide.");
                }
            } catch (Exception e) {
                System.out.println("Erreur lors du traitement de la commande: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Prompts the user to add a new asset.
     */
    private void ajouterActif() {
        try {
            System.out.print("ID: ");
            String id = scanner.nextLine();
            
            if (id == null || id.trim().isEmpty()) {
                System.out.println("Erreur: L'ID ne peut pas être vide.");
                return;
            }
            
            System.out.println("Type (1: Recon, 2: Logistique, 3: Surface, 4: Sous-marin, 5: Terrestre): ");
            String type = scanner.nextLine();

            ActifMobile actif = null;

            switch (type) {
                case "1":
                    actif = new DroneReconnaissance(id, new Point3D(0, 0, 50));
                    break;
                case "2":
                    actif = new DroneLogistique(id, new Point3D(0, 0, 10));
                    break;
                case "3":
                    actif = new VehiculeSurface(id, new Point3D(0, 0, 0));
                    break;
                case "4":
                    actif = new VehiculeSousMarin(id, new Point3D(0, 0, -50));
                    break;
                case "5":
                    actif = new VehiculeTerrestre(id, new Point3D(0, 0, 0));
                    break;
                default:
                    System.out.println("Type inconnu.");
                    return;
            }

            if (actif != null) {
                gestionnaire.ajouterActif(actif);
                zone.addCollidable(actif); // Register for collision detection
                System.out.println("Actif ajouté : " + actif.getId());
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de l'ajout de l'actif: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Lists all assets in the fleet with their status and position.
     */
    private void listerFlotte() {
        try {
            System.out.println("\n--- Flotte ---");
            if (gestionnaire.getFlotte() == null || gestionnaire.getFlotte().isEmpty()) {
                System.out.println("Aucun actif dans la flotte.");
                return;
            }
            
            for (ActifMobile actif : gestionnaire.getFlotte()) {
                System.out.printf("%s [%s] - %s - Pos: %s%n",
                        actif.getId(),
                        actif.getClass().getSimpleName(),
                        actif.getEtat(),
                        actif.getPosition());
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de l'affichage de la flotte: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Creates and assigns a mission to an available asset.
     */
    private void creerMission() {
        try {
            System.out.print("ID Mission: ");
            String id = scanner.nextLine();
            
            if (id == null || id.trim().isEmpty()) {
                System.out.println("Erreur: L'ID de mission ne peut pas être vide.");
                return;
            }
            
            Mission mission = new MissionReconnaissance(id);

            // Auto assign first available asset
            ActifMobile asset = gestionnaire.suggererActif(10);
            if (asset != null) {
                mission.assignerActif(asset);
                mission.demarrer();
                System.out.println("Mission " + id + " assignée à " + asset.getId() + " et démarrée.");
            } else {
                System.out.println("Aucun actif disponible pour cette mission.");
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la création de la mission: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Simulates a time step by moving assets.
     */
    private void simulerPasDeTemps() {
        try {
            System.out.println("Simulation d'un pas de temps...");
            // Simple simulation: move all assets towards a dummy target
            Point3D target = new Point3D(100, 100, 50);

            if (gestionnaire.getFlotte() == null || gestionnaire.getFlotte().isEmpty()) {
                System.out.println("Aucun actif à déplacer.");
                return;
            }

            for (ActifMobile actif : gestionnaire.getFlotte()) {
                try {
                    if (actif.getEtat() == EtatOperationnel.EN_MISSION || actif.getEtat() == EtatOperationnel.AU_SOL) {
                        actif.deplacer(target, zone);
                        System.out.println(actif.getId() + " déplacé vers " + actif.getPosition());
                    }
                } catch (Exception e) {
                    System.out.println("Erreur lors du déplacement de " + actif.getId() + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("Erreur lors de la simulation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Main entry point for the console interface.
     * 
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        GestionnaireEssaim gestionnaire = new GestionnaireEssaim();
        ConsoleInterface console = new ConsoleInterface(gestionnaire);
        console.demarrer();
    }
}
