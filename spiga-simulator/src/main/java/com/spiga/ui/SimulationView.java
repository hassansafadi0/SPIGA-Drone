package com.spiga.ui;

import com.spiga.core.ActifMobile;
import com.spiga.core.Point3D;
import com.spiga.env.ZoneOperation;
import com.spiga.mission.GestionnaireEssaim;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class SimulationView extends Pane {
    private ZoneOperation zone;
    private GestionnaireEssaim gestionnaire;
    private Canvas canvas;
    private AnimationTimer timer;

    private ActifMobile selectedAsset = null;
    private final double SCALE = 0.9; // 1000 world -> 900 canvas

    public SimulationView(ZoneOperation zone, GestionnaireEssaim gestionnaire) {
        this.zone = zone;
        this.gestionnaire = gestionnaire;
        this.canvas = new Canvas(900, 900); // Square canvas
        getChildren().add(canvas);

        // Redraw on resize
        widthProperty().addListener(evt -> draw());
        heightProperty().addListener(evt -> draw());

        // Handle Mouse Events
        canvas.setOnMouseClicked(e -> {
            double mx = e.getX();
            double my = e.getY();

            if (e.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
                // Try to select an asset
                boolean clickedAsset = handleSelection(mx, my);

                // If we didn't click an asset, but have one selected, move it
                if (!clickedAsset && selectedAsset != null) {
                    handleMoveCommand(mx, my);
                }
            } else if (e.getButton() == javafx.scene.input.MouseButton.SECONDARY) {
                // Right click always moves if selected
                handleMoveCommand(mx, my);
            }
        });
    }

    private boolean handleSelection(double mx, double my) {
        for (ActifMobile actif : gestionnaire.getFlotte()) {
            // Map world to canvas
            double x = actif.getPosition().getX() * SCALE;
            double y = actif.getPosition().getY() * SCALE;

            // Simple hit detection (radius 20 for easier clicking)
            if (Math.abs(mx - x) < 20 && Math.abs(my - y) < 20) {
                selectedAsset = actif;
                System.out.println("Selected: " + actif.getId());
                draw();
                return true;
            }
        }
        // If clicked empty space, deselect? Or keep selected?
        // Let's keep selected to allow moving. To deselect, maybe click outside or
        // specific key.
        // For now, return false so we can trigger move.
        return false;
    }

    private void handleMoveCommand(double mx, double my) {
        if (selectedAsset == null)
            return;

        // Map canvas back to world
        double wx = mx / SCALE;
        double wy = my / SCALE;
        double wz = selectedAsset.getPosition().getZ(); // Keep current altitude/depth by default

        // Constraints
        String type = selectedAsset.getClass().getSimpleName();
        boolean isLand = wx < 500; // World coordinate boundary

        if (type.contains("VehiculeTerrestre") && !isLand) {
            System.out.println("Cannot move Car to Sea!");
            return;
        }
        if ((type.contains("Surface") || type.contains("SousMarin")) && isLand) {
            System.out.println("Cannot move Marine asset to Land!");
            return;
        }

        selectedAsset.setTarget(new Point3D(wx, wy, wz));
        System.out.println("Moving " + selectedAsset.getId() + " to " + wx + ", " + wy);
    }

    private Runnable onUpdate;

    public void setOnUpdate(Runnable onUpdate) {
        this.onUpdate = onUpdate;
    }

    private long lastUpdate = 0;

    public void startSimulation() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 100_000_000) { // Update every 100ms (10 FPS)
                    update();
                    draw();
                    if (onUpdate != null) {
                        onUpdate.run();
                    }
                    lastUpdate = now;
                }
            }
        };
        timer.start();
    }

    private void update() {
        for (ActifMobile actif : gestionnaire.getFlotte()) {
            if (actif.getTarget() != null) {
                actif.deplacer(actif.getTarget(), zone);

                // Stop if reached (simple check)
                double dx = actif.getTarget().getX() - actif.getPosition().getX();
                double dy = actif.getTarget().getY() - actif.getPosition().getY();
                if (Math.sqrt(dx * dx + dy * dy) < 2.0) { // Increased tolerance
                    actif.setTarget(null); // Stop
                    actif.setEtat(com.spiga.core.EtatOperationnel.AU_SOL); // Reset state
                }
            }
        }
    }

    private void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Draw Terrain (Split view: Left Land, Right Sea)
        // Land (0 to 500 world -> 0 to 450 canvas)
        double splitX = 500 * SCALE;

        gc.setFill(Color.LIGHTGREEN);
        gc.fillRect(0, 0, splitX, 900);
        // Sea
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(splitX, 0, 900 - splitX, 900);

        // Grid lines
        gc.setStroke(Color.GRAY);
        gc.setLineWidth(0.5);
        for (int i = 0; i <= 1000; i += 100) {
            double pos = i * SCALE;
            gc.strokeLine(pos, 0, pos, 900);
            gc.strokeLine(0, pos, 900, pos);
        }

        // Draw Zone Boundary
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(2.0);
        gc.strokeRect(0, 0, 1000 * SCALE, 1000 * SCALE);

        // Draw Assets
        for (ActifMobile actif : gestionnaire.getFlotte()) {
            drawAsset(gc, actif);
        }
    }

    private void drawAsset(GraphicsContext gc, ActifMobile actif) {
        // Map world coordinates to canvas coordinates
        double x = actif.getPosition().getX() * SCALE;
        double y = actif.getPosition().getY() * SCALE;

        String type = actif.getClass().getSimpleName();
        gc.setFill(getColorForType(actif));

        // Selection Highlight
        if (actif == selectedAsset) {
            gc.setStroke(Color.RED);
            gc.setLineWidth(2);
            gc.strokeOval(x - 15, y - 15, 30, 30);
        }

        if (type.contains("Reconnaissance")) {
            // Triangle for plane
            gc.fillPolygon(new double[] { x, x - 10, x + 10 }, new double[] { y - 10, y + 10, y + 10 }, 3);
        } else if (type.contains("Logistique")) {
            // Square/Quad for drone
            gc.fillRect(x - 8, y - 8, 16, 16);
            // Rotors
            gc.setStroke(Color.BLACK);
            gc.strokeOval(x - 12, y - 12, 10, 10);
            gc.strokeOval(x + 2, y - 12, 10, 10);
            gc.strokeOval(x - 12, y + 2, 10, 10);
            gc.strokeOval(x + 2, y + 2, 10, 10);
        } else if (type.contains("Surface")) {
            // Boat shape
            gc.fillPolygon(new double[] { x - 12, x + 12, x + 6, x - 6 }, new double[] { y - 6, y - 6, y + 6, y + 6 },
                    4);
        } else if (type.contains("SousMarin")) {
            // Ellipse for sub
            gc.fillOval(x - 14, y - 7, 28, 14);
            // Periscope
            gc.strokeLine(x, y - 7, x, y - 14);
        } else if (type.contains("Terrestre")) {
            // Car shape (Rectangle)
            gc.fillRect(x - 10, y - 6, 20, 12);
            // Wheels
            gc.setFill(Color.BLACK);
            gc.fillOval(x - 8, y + 6, 4, 4);
            gc.fillOval(x + 4, y + 6, 4, 4);
            gc.fillOval(x - 8, y - 8, 4, 4);
            gc.fillOval(x + 4, y - 8, 4, 4);
        } else {
            gc.fillOval(x - 5, y - 5, 10, 10);
        }

        gc.setFill(Color.BLACK);
        gc.fillText(actif.getId(), x + 12, y);
    }

    private Color getColorForType(ActifMobile actif) {
        String type = actif.getClass().getSimpleName();
        if (type.contains("Reconnaissance"))
            return Color.BLUE;
        if (type.contains("Logistique"))
            return Color.GREEN;
        if (type.contains("Surface"))
            return Color.ORANGE;
        if (type.contains("SousMarin"))
            return Color.DARKBLUE;
        return Color.RED;
    }
}
