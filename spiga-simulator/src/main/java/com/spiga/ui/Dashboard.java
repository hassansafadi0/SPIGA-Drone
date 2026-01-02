package com.spiga.ui;

import com.spiga.core.ActifMobile;
import com.spiga.core.Point3D;
import com.spiga.mission.GestionnaireEssaim;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class Dashboard extends VBox {
    private GestionnaireEssaim gestionnaire;
    private ListView<String> assetList;

    private ComboBox<String> typeSelect;
    private TextField idInput;
    private TextField xInput, yInput, zInput;
    private AssetCreator assetCreator;
    private Consumer<Point3D> onSetTarget;

    /**
     * Functional interface for creating assets.
     */
    public interface AssetCreator {
        void create(String type, String id, Point3D position);
    }

    /**
     * Constructor for Dashboard.
     * 
     * @param gestionnaire The fleet manager.
     */
    public Dashboard(GestionnaireEssaim gestionnaire) {
        this(gestionnaire, null);
    }

    /**
     * Constructor for Dashboard with asset creator callback.
     * 
     * @param gestionnaire The fleet manager.
     * @param assetCreator Callback for creating assets.
     */
    public Dashboard(GestionnaireEssaim gestionnaire, AssetCreator assetCreator) {
        this.gestionnaire = gestionnaire;
        this.assetCreator = assetCreator;
        this.setPadding(new Insets(10));
        this.setSpacing(10);
        this.setStyle("-fx-background-color: #f0f0f0; -fx-border-color: #ccc;");
        this.setPrefWidth(300);

        // Title
        Label title = new Label("Dashboard");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        this.getChildren().add(title);

        // Asset Creation Section
        VBox creationBox = new VBox(5);
        creationBox.setStyle("-fx-border-color: #aaa; -fx-padding: 5;");
        creationBox.getChildren().add(new Label("Add New Asset:"));

        typeSelect = new ComboBox<>();
        typeSelect.getItems().addAll(
                "DroneReconnaissance",
                "DroneLogistique",
                "VehiculeSurface",
                "VehiculeSousMarin",
                "VehiculeTerrestre");
        typeSelect.getSelectionModel().selectFirst();

        idInput = new TextField();
        idInput.setPromptText("ID (e.g. D1)");

        HBox posBox = new HBox(5);
        xInput = new TextField("100");
        xInput.setPrefWidth(60);
        yInput = new TextField("100");
        yInput.setPrefWidth(60);
        zInput = new TextField("50");
        zInput.setPrefWidth(60);
        posBox.getChildren().addAll(new Label("X:"), xInput, new Label("Y:"), yInput, new Label("Z:"), zInput);

        Button addButton = new Button("Add Asset");
        addButton.setOnAction(e -> handleAddAsset());

        creationBox.getChildren().addAll(typeSelect, idInput, posBox, addButton);
        this.getChildren().add(creationBox);

        // Asset List
        this.getChildren().add(new Label("Current Assets:"));
        assetList = new ListView<>();
        update(); // Initial population
        this.getChildren().add(assetList);

        // Refresh Button
        Button refreshBtn = new Button("Refresh List");
        refreshBtn.setOnAction(e -> update());
        this.getChildren().add(refreshBtn);

        // Target Control
        VBox targetBox = new VBox(5);
        targetBox.setStyle("-fx-border-color: #aaa; -fx-padding: 5;");
        targetBox.getChildren().add(new Label("Set Target (All):"));

        HBox targetPosBox = new HBox(5);
        TextField txInput = new TextField("500");
        txInput.setPrefWidth(60);
        TextField tyInput = new TextField("500");
        tyInput.setPrefWidth(60);
        TextField tzInput = new TextField("0");
        tzInput.setPrefWidth(60);
        targetPosBox.getChildren().addAll(new Label("X:"), txInput, new Label("Y:"), tyInput, new Label("Z:"), tzInput);

        Button setTargetBtn = new Button("Set Target");
        setTargetBtn.setOnAction(e -> {
            try {
                double x = Double.parseDouble(txInput.getText());
                double y = Double.parseDouble(tyInput.getText());
                double z = Double.parseDouble(tzInput.getText());
                if (onSetTarget != null) {
                    onSetTarget.accept(new Point3D(x, y, z));
                }
            } catch (NumberFormatException ex) {
                showAlert("Invalid target coordinates!");
            }
        });

        targetBox.getChildren().addAll(targetPosBox, setTargetBtn);
        this.getChildren().add(targetBox);
    }

    /**
     * Sets the asset creator callback.
     * 
     * @param creator The callback.
     */
    public void setAssetCreator(AssetCreator creator) {
        this.assetCreator = creator;
    }

    /**
     * Sets the callback for setting a target.
     * 
     * @param onSetTarget The callback.
     */
    public void setOnSetTarget(Consumer<Point3D> onSetTarget) {
        this.onSetTarget = onSetTarget;
    }

    /**
     * Handles the "Add Asset" button click.
     */
    private void handleAddAsset() {
        if (assetCreator != null) {
            try {
                String type = typeSelect.getValue();
                String id = idInput.getText();
                double x = Double.parseDouble(xInput.getText());
                double y = Double.parseDouble(yInput.getText());
                double z = Double.parseDouble(zInput.getText());

                if (id.isEmpty()) {
                    showAlert("ID is required!");
                    return;
                }

                assetCreator.create(type, id, new Point3D(x, y, z));
                idInput.clear(); // Reset ID for next
            } catch (NumberFormatException ex) {
                showAlert("Invalid coordinates!");
            }
        }
    }

    /**
     * Updates the asset list view.
     */
    public void update() {
        assetList.getItems().clear();
        for (ActifMobile actif : gestionnaire.getFlotte()) {
            String status = String.format("%s [%s] - %s (Autonomie: %.1f%%)",
                    actif.getId(),
                    actif.getClass().getSimpleName(),
                    actif.getEtat(),
                    (actif.getAutonomieActuelle() / actif.getAutonomieMax()) * 100);
            assetList.getItems().add(status);
        }
    }

    /**
     * Shows an alert dialog.
     * 
     * @param message The message to display.
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
