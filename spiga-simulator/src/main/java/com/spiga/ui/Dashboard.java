package com.spiga.ui;

import com.spiga.core.ActifMobile;
import com.spiga.core.Point3D;
import com.spiga.mission.GestionnaireEssaim;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class Dashboard extends VBox {
    private GestionnaireEssaim gestionnaire;
    private ListView<String> assetList;

    private ComboBox<String> typeSelect;
    private TextField idInput;
    private TextField xInput, yInput, zInput;
    private Button addButton;

    private TextField targetXInput, targetYInput, targetZInput;
    private Button setTargetButton;

    private BiConsumer<String, Point3D> onAddAsset; // Type, Position (ID is handled internally or passed?) Let's pass
                                                    // ID too.
    // Actually better: Consumer<AssetRequest>

    // Let's use a simple interface callback
    public interface AssetCreator {
        void create(String type, String id, Point3D pos);
    }

    private AssetCreator assetCreator;
    private Consumer<Point3D> onSetTarget;

    public Dashboard(GestionnaireEssaim gestionnaire) {
        this.gestionnaire = gestionnaire;
        setPadding(new Insets(10));
        setSpacing(10);
        setPrefWidth(300);
        setStyle("-fx-background-color: #f0f0f0;");

        Label title = new Label("Dashboard");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Asset Creation Section
        Label addLabel = new Label("Add Asset:");
        addLabel.setStyle("-fx-font-weight: bold;");

        typeSelect = new ComboBox<>();
        typeSelect.getItems().addAll("DroneReconnaissance", "DroneLogistique", "VehiculeSurface", "VehiculeSousMarin",
                "VehiculeTerrestre");
        typeSelect.getSelectionModel().selectFirst();

        idInput = new TextField();
        idInput.setPromptText("ID (e.g. D3)");

        xInput = new TextField("0");
        xInput.setPromptText("X");
        yInput = new TextField("0");
        yInput.setPromptText("Y");
        zInput = new TextField("0");
        zInput.setPromptText("Z");

        addButton = new Button("Add Asset");
        addButton.setOnAction(e -> handleAddAsset());

        // Target Section
        Label targetLabel = new Label("Set Target:");
        targetLabel.setStyle("-fx-font-weight: bold;");

        targetXInput = new TextField("500");
        targetXInput.setPromptText("X");
        targetYInput = new TextField("500");
        targetYInput.setPromptText("Y");
        targetZInput = new TextField("50");
        targetZInput.setPromptText("Z");

        setTargetButton = new Button("Set Target");
        setTargetButton.setOnAction(e -> handleSetTarget());

        assetList = new ListView<>();

        getChildren().addAll(
                title,
                addLabel, typeSelect, idInput, xInput, yInput, zInput, addButton,
                new Separator(),
                targetLabel, targetXInput, targetYInput, targetZInput, setTargetButton,
                new Separator(),
                new Label("Flotte:"), assetList);

        // Initial update
        update();
    }

    public void setAssetCreator(AssetCreator creator) {
        this.assetCreator = creator;
    }

    public void setOnSetTarget(Consumer<Point3D> onSetTarget) {
        this.onSetTarget = onSetTarget;
    }

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

    private void handleSetTarget() {
        if (onSetTarget != null) {
            try {
                double x = Double.parseDouble(targetXInput.getText());
                double y = Double.parseDouble(targetYInput.getText());
                double z = Double.parseDouble(targetZInput.getText());
                onSetTarget.accept(new Point3D(x, y, z));
            } catch (NumberFormatException ex) {
                showAlert("Invalid target coordinates!");
            }
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void update() {
        assetList.getItems().clear();
        for (ActifMobile actif : gestionnaire.getFlotte()) {
            String status = String.format("%s [%s] - %s\nAutonomie: %.1f%%",
                    actif.getId(),
                    actif.getClass().getSimpleName(),
                    actif.getEtat(),
                    (actif.getAutonomieActuelle() / actif.getAutonomieMax()) * 100);
            assetList.getItems().add(status);
        }
    }
}
