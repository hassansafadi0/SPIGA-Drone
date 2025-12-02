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
        // Drones (Air)
        gestionnaire.ajouterActif(new com.spiga.core.DroneReconnaissance("D1", new Point3D(100, 100, 50)));
        gestionnaire.ajouterActif(new com.spiga.core.DroneLogistique("D2", new Point3D(200, 200, 50)));
        // Marine (Water) - Avoid Islands (300,300) and (700,700)
        gestionnaire.ajouterActif(new com.spiga.core.VehiculeSurface("S1", new Point3D(100, 800, 0)));
        gestionnaire.ajouterActif(new com.spiga.core.VehiculeSousMarin("U1", new Point3D(800, 100, -50)));
        // Land (Islands) - Island 1 is at (300,300)
        gestionnaire.ajouterActif(new com.spiga.core.VehiculeTerrestre("C1", new Point3D(300, 300, 0)));

        // Initialize UI Components
        simulationView = new SimulationView(zone, gestionnaire);
        dashboard = new Dashboard(gestionnaire);

        simulationView.setOnUpdate(() -> dashboard.update());

        // Handle Target Setting (Global target removed, now per-asset)
        dashboard.setOnSetTarget(target -> {
            // Optional: Set target for ALL assets or currently selected?
            // For now, let's make "Set Target" button set target for ALL assets for
            // convenience
            for (com.spiga.core.ActifMobile actif : gestionnaire.getFlotte()) {
                actif.setTarget(target);
            }
            System.out.println("Global target set to: " + target);
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

    public static void main(String[] args) {
        launch(args);
    }
}
