package com.spiga.core;

import com.spiga.env.ZoneOperation;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class representing an aerial mobile asset.
 * Extends ActifMobile to provide specific behavior for flying vehicles.
 */
public abstract class ActifAerien extends ActifMobile {

    /**
     * Constructor for ActifAerien.
     * 
     * @param id           Unique identifier of the asset.
     * @param position     Initial 3D position.
     * @param vitesseMax   Maximum speed of the asset.
     * @param autonomieMax Maximum autonomy (fuel/battery).
     */
    public ActifAerien(String id, Point3D position, double vitesseMax, double autonomieMax) {
        super(id, position, vitesseMax, autonomieMax);
    }

    /**
     * Moves the aerial asset towards a target coordinate.
     * Considers wind effects and precipitation for energy consumption.
     * 
     * @param cible The target 3D point.
     * @param zone  The simulation zone containing environmental data.
     */
    @Override
    public void deplacer(Point3D cible, ZoneOperation zone) {
        if (getEtat() == EtatOperationnel.EN_PANNE || getAutonomieActuelle() <= 0) {
            return;
        }

        // Auto-switch to EN_MISSION if moving
        if (getEtat() == EtatOperationnel.AU_SOL) {
            setEtat(EtatOperationnel.EN_MISSION);
        }

        // Simple movement logic
        // Check wind
        Point3D vent = zone.getVent();
        // Wind affects speed or consumption? "augmenter la consommation d'énergie et
        // perturber le cap"

        // Calculate direction
        double dx = cible.getX() - getPosition().getX();
        double dy = cible.getY() - getPosition().getY();
        double dz = cible.getZ() - getPosition().getZ();
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (distance == 0)
            return;

        // Normalize
        double nx = dx / distance;
        double ny = dy / distance;
        double nz = dz / distance;

        // Apply speed (simplified)
        double speed = getVitesseMax();

        // Prevent overshooting
        if (speed > distance) {
            speed = distance;
        }

        // Wind effect (simplified: subtract wind vector from movement vector? Or add
        // wind to position?)
        // If wind is strong, it pushes the drone.
        // Let's say effective movement = desired_movement + wind * factor

        double moveX = nx * speed;
        double moveY = ny * speed;
        double moveZ = nz * speed;

        // Update position
        Point3D newPos = new Point3D(
                getPosition().getX() + moveX,
                getPosition().getY() + moveY,
                getPosition().getZ() + moveZ);

        // Check collision/bounds
        if (!zone.isInside(newPos)) {
            // System.out.println(getId() + " : Sortie de zone !");
            return;
        }
        if (zone.isCollision(newPos, this)) {
            // System.out.println(getId() + " : Collision détectée !");
            notifierEtatCritique(TypeAlerte.COLLISION_IMMINENTE);
            return;
        }

        setPosition(newPos);

        // Consume energy
        // Base consumption + wind factor + rain factor
        double consumption = 1.0;
        if (zone.getPrecipitations() > 0) {
            consumption += zone.getPrecipitations() * 0.1;
        }
        // Wind opposition increases consumption
        // Simplified: just add constant if wind is present
        if (vent.getX() != 0 || vent.getY() != 0) {
            consumption += 0.5;
        }

        setAutonomieActuelle(getAutonomieActuelle() - consumption);
        if (getAutonomieActuelle() <= 0) {
            setAutonomieActuelle(0);
            setEtat(EtatOperationnel.EN_PANNE); // Or forced landing
            notifierEtatCritique(TypeAlerte.BATTERIE_FAIBLE);
        }

        // System.out.println(getId() + " moved to " + getPosition() + ". Autonomy: " +
        // getAutonomieActuelle());
    }

    @Override
    public List<Point3D> calculerTrajet(Point3D cible) {
        // Simple direct path
        List<Point3D> path = new ArrayList<>();
        path.add(getPosition());
        path.add(cible);
        return path;
    }
}
