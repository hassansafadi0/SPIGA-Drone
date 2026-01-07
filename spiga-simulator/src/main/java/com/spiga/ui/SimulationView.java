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

    private java.util.List<ActifMobile> selectedAssets = new java.util.ArrayList<>();
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
            try {
                double mx = e.getX();
                double my = e.getY();

                if (e.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
                    // Try to select an asset
                    boolean clickedAsset = handleSelection(mx, my);

                    // If we didn't click an asset, but have one selected, move it
                    if (!clickedAsset && !selectedAssets.isEmpty()) {
                        handleMoveCommand(mx, my);
                    }
                } else if (e.getButton() == javafx.scene.input.MouseButton.SECONDARY) {
                    // Right click always moves if selected
                    handleMoveCommand(mx, my);
                }
            } catch (Exception ex) {
                System.err.println("Error handling mouse click: " + ex.getMessage());
                ex.printStackTrace();
            }
        });
    }

    private Runnable onUpdate;

    /**
     * Sets the callback to be run on every simulation update.
     * 
     * @param onUpdate The runnable to execute.
     */
    public void setOnUpdate(Runnable onUpdate) {
        this.onUpdate = onUpdate;
    }

    private long lastUpdate = 0;

    /**
     * Starts the simulation animation timer.
     */
    public void startSimulation() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                try {
                    if (now - lastUpdate >= 100_000_000) { // Update every 100ms (10 FPS)
                        update();
                        draw();
                        if (onUpdate != null) {
                            onUpdate.run();
                        }
                        lastUpdate = now;
                    }
                } catch (Exception e) {
                    System.err.println("Error in animation timer: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };
        timer.start();
    }

    private boolean handleSelection(double mx, double my) {
        try {
            boolean clickedOnAsset = false;
            // If CTRL is not held, clear selection (simplified logic: assume always
            // multi-select or toggle)
            // For now, let's implement simple toggle behavior

            if (gestionnaire.getFlotte() == null) {
                return false;
            }

            for (ActifMobile actif : gestionnaire.getFlotte()) {
                // Map world to canvas
                double x = actif.getPosition().getX() * SCALE;
                double y = actif.getPosition().getY() * SCALE;

                // Simple hit detection (radius 20 for easier clicking)
                if (Math.abs(mx - x) < 20 && Math.abs(my - y) < 20) {
                    if (selectedAssets.contains(actif)) {
                        selectedAssets.remove(actif);
                        System.out.println("Deselected: " + actif.getId());
                    } else {
                        selectedAssets.add(actif);
                        System.out.println("Selected: " + actif.getId());
                    }
                    draw();
                    clickedOnAsset = true;
                    break; // Only select one at a time per click
                }
            }

            // If clicked on empty space, clear selection?
            if (!clickedOnAsset) {
                // Optional: Clear selection if clicking on empty space
                // selectedAssets.clear();
                // draw();
            }

            return clickedOnAsset;
        } catch (Exception e) {
            System.err.println("Error handling selection: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Handles the movement command for the selected asset.
     * Validates the target position based on terrain constraints.
     * 
     * @param mx Mouse X coordinate.
     * @param my Mouse Y coordinate.
     */
    private void handleMoveCommand(double mx, double my) {
        try {
            if (selectedAssets.isEmpty())
                return;

            // Map canvas back to world
            double wx = mx / SCALE;
            double wy = my / SCALE;

            for (ActifMobile asset : selectedAssets) {
                try {
                    double wz = asset.getPosition().getZ(); // Keep current altitude/depth

                    // Constraints
                    // 1. Marine assets cannot go on land
                    if (asset instanceof com.spiga.core.ActifMarin) {
                        if (zone.isLand(new Point3D(wx, wy, 0))) {
                            System.out.println("Cannot move marine asset " + asset.getId() + " to land!");
                            continue;
                        }
                    }
                    // 2. Land vehicles cannot go into water
                    if (asset instanceof com.spiga.core.VehiculeTerrestre) {
                        if (!zone.isLand(new Point3D(wx, wy, 0))) {
                            System.out.println("Cannot move land vehicle " + asset.getId() + " to water!");
                            continue;
                        }
                    }

                    asset.setTarget(new Point3D(wx, wy, wz));
                    System.out.println("Moving " + asset.getId() + " to " + wx + ", " + wy);
                } catch (Exception e) {
                    System.err.println("Error moving asset " + asset.getId() + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error handling move command: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Updates the state of all assets.
     * Moves assets towards their targets and checks for arrival.
     */
    private void update() {
        try {
            if (gestionnaire.getFlotte() == null) {
                return;
            }

            for (ActifMobile actif : gestionnaire.getFlotte()) {
                try {
                    if (actif.getTarget() != null) {
                        actif.deplacer(actif.getTarget(), zone);

                        // Stop if reached (simple check)
                        double dx = actif.getTarget().getX() - actif.getPosition().getX();
                        double dy = actif.getTarget().getY() - actif.getPosition().getY();
                        if (Math.sqrt(dx * dx + dy * dy) < 5.0) { // Tolerance
                            actif.setTarget(null); // Stop
                            actif.setEtat(com.spiga.core.EtatOperationnel.AU_SOL); // Reset state
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error updating asset " + actif.getId() + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Error in update loop: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Draws the simulation state on the canvas.
     * Renders the map (water, islands) and assets.
     */
    private void draw() {
        try {
            GraphicsContext gc = canvas.getGraphicsContext2D();

            // Clear background (Water)
            gc.setFill(Color.LIGHTBLUE);
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

            // Draw Islands
            gc.setFill(Color.LIGHTGREEN);
            if (zone.getIslands() != null) {
                for (ZoneOperation.Island island : zone.getIslands()) {
                    try {
                        if (island.isCircle()) {
                            gc.fillOval(island.getX() * SCALE - (island.getW() * SCALE),
                                    island.getY() * SCALE - (island.getW() * SCALE),
                                    island.getW() * SCALE * 2, island.getW() * SCALE * 2);
                        } else {
                            gc.fillRect(island.getX() * SCALE, island.getY() * SCALE, island.getW() * SCALE,
                                    island.getH() * SCALE);
                        }
                    } catch (Exception e) {
                        System.err.println("Error drawing island: " + e.getMessage());
                    }
                }
            }

            // Draw Assets
            if (gestionnaire.getFlotte() != null) {
                for (ActifMobile actif : gestionnaire.getFlotte()) {
                    try {
                        drawAsset(gc, actif);
                    } catch (Exception e) {
                        System.err.println("Error drawing asset " + actif.getId() + ": " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error in draw method: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void drawAsset(GraphicsContext gc, ActifMobile actif) {
        double x = actif.getPosition().getX() * SCALE;
        double y = actif.getPosition().getY() * SCALE;
        String type = actif.getClass().getSimpleName();

        gc.setFill(getColorForType(actif));

        if (selectedAssets.contains(actif)) {
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
