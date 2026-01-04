package com.spiga.ui;

import com.spiga.core.Point3D;
import com.spiga.env.ZoneOperation;
import com.spiga.mission.GestionnaireEssaim;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainApp extends Application {

    private GestionnaireEssaim gestionnaire;
    private ZoneOperation zone;
    private SimulationView simulationView;
    private Dashboard dashboard;

    @Override
    public void start(Stage primaryStage) {
        // Initialize Gestionnaire and Zone
        gestionnaire = new GestionnaireEssaim();
        // Min Z is -1000 to allow submarines (underwater)
        zone = new ZoneOperation(new Point3D(0, 0, -1000), new Point3D(1000, 1000, 1000));

        // Add sample assets
        // Add sample assets
        // Drones (Air)
        com.spiga.core.ActifMobile d1 = new com.spiga.core.DroneReconnaissance("D1", new Point3D(100, 100, 50));
        gestionnaire.ajouterActif(d1);
        zone.addCollidable(d1);

        com.spiga.core.ActifMobile d2 = new com.spiga.core.DroneLogistique("D2", new Point3D(200, 200, 50));
        gestionnaire.ajouterActif(d2);
        zone.addCollidable(d2);

        // Marine (Water) - Avoid Islands (300,300) and (700,700)
        com.spiga.core.ActifMobile s1 = new com.spiga.core.VehiculeSurface("S1", new Point3D(100, 800, 0));
        gestionnaire.ajouterActif(s1);
        zone.addCollidable(s1);

        com.spiga.core.ActifMobile u1 = new com.spiga.core.VehiculeSousMarin("U1", new Point3D(800, 100, -50));
        gestionnaire.ajouterActif(u1);
        zone.addCollidable(u1);

        // Land (Islands) - Island 1 is at (300,300)
        com.spiga.core.ActifMobile c1 = new com.spiga.core.VehiculeTerrestre("C1", new Point3D(300, 300, 0));
        gestionnaire.ajouterActif(c1);
        zone.addCollidable(c1);

        // Initialize UI Components
        simulationView = new SimulationView(zone, gestionnaire);
        dashboard = new Dashboard(gestionnaire);

        simulationView.setOnUpdate(() -> dashboard.update());

        // Handle Target Setting (Global target removed, now per-asset)
        dashboard.setOnSetTarget((target, type) -> {
            // Optional: Set target for ALL assets or currently selected?
            // For now, let's make "Set Target" button set target for ALL assets for
            // convenience
            for (com.spiga.core.ActifMobile actif : gestionnaire.getFlotte()) {
                actif.setTarget(target);
            }
            System.out.println("Global target set to: " + target);
        });

        // Handle Group Mission Creation
        dashboard.setOnCreateGroupMission((ids, target) -> {
            com.spiga.mission.Mission mission = new com.spiga.mission.MissionReconnaissance(
                    "Mission-" + System.currentTimeMillis());
            mission.setObjectif(com.spiga.mission.ObjectifMission.RECONNAISSANCE); // Default for now

            System.out.println("Creating group mission for assets: " + ids);

            for (String id : ids) {
                // Find asset by ID
                com.spiga.core.ActifMobile asset = gestionnaire.getFlotte().stream()
                        .filter(a -> a.getId().equals(id))
                        .findFirst()
                        .orElse(null);

                if (asset != null) {
                    mission.assignerActif(asset);
                    asset.setTarget(target); // Set individual target for movement
                    System.out.println("Assigned " + id + " to mission.");
                }
            }

            mission.demarrer();
        });

        // Handle Asset Creation with Validation
        dashboard.setAssetCreator((type, id, pos) -> {
            // Validation Logic
            boolean isMarine = type.contains("Surface") || type.contains("SousMarin");
            boolean isLandVehicle = type.contains("VehiculeTerrestre");
            boolean isLand = zone.isLand(pos);

            if (isMarine && isLand) {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                        javafx.scene.control.Alert.AlertType.ERROR);
                alert.setContentText("Marine assets cannot spawn on land!");
                alert.showAndWait();
                return;
            }
            if (isLandVehicle && !isLand) {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                        javafx.scene.control.Alert.AlertType.ERROR);
                alert.setContentText("Land vehicles cannot spawn in sea!");
                alert.showAndWait();
                return;
            }

            com.spiga.core.ActifMobile asset = null;
            switch (type) {
                case "DroneReconnaissance":
                    asset = new com.spiga.core.DroneReconnaissance(id, pos);
                    break;
                case "DroneLogistique":
                    asset = new com.spiga.core.DroneLogistique(id, pos);
                    break;
                case "VehiculeSurface":
                    asset = new com.spiga.core.VehiculeSurface(id, pos);
                    break;
                case "VehiculeSousMarin":
                    asset = new com.spiga.core.VehiculeSousMarin(id, pos);
                    break;
                case "VehiculeTerrestre":
                    asset = new com.spiga.core.VehiculeTerrestre(id, pos);
                    break;
            }

            if (asset != null) {
                gestionnaire.ajouterActif(asset);
                zone.addCollidable(asset); // Register for collision detection
                dashboard.update(); // Immediate update
                System.out.println("Added asset: " + id);
            }
        });

        BorderPane root = new BorderPane();
        root.setCenter(simulationView);
        root.setRight(dashboard);

        Scene scene = new Scene(root, 1400, 900); // Increased window size
        primaryStage.setTitle("SPIGA Simulator - JavaFX");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Start Simulation Loop
        simulationView.startSimulation();
    }

    /**
     * Main entry point for the application.
     * 
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
