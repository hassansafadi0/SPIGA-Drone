package com.spiga.core;

import com.spiga.env.ZoneOperation;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class representing a marine mobile asset.
 * Extends ActifMobile to provide specific behavior for water-based vehicles.
 */
public abstract class ActifMarin extends ActifMobile {

    /**
     * Constructor for ActifMarin.
     * 
     * @param id           Unique identifier of the asset.
     * @param position     Initial 3D position.
     * @param vitesseMax   Maximum speed of the asset.
     * @param autonomieMax Maximum autonomy (fuel/battery).
     */
    public ActifMarin(String id, Point3D position, double vitesseMax, double autonomieMax) {
        super(id, position, vitesseMax, autonomieMax);
    }

    /**
     * Moves the marine asset towards a target coordinate.
     * Considers ocean currents for movement and energy consumption.
     * Uses pathfinding to navigate around land obstacles.
     * 
     * @param cible The target 3D point.
     * @param zone  The simulation zone containing environmental data and obstacles.
     */
    @Override
    public void deplacer(Point3D cible, ZoneOperation zone) {
        if (getEtat() == EtatOperationnel.EN_PANNE || getAutonomieActuelle() <= 0) {
            return;
        }

        if (getEtat() == EtatOperationnel.AU_SOL) {
            setEtat(EtatOperationnel.EN_MISSION);
        }

        // Pathfinding Logic
        if (getCurrentPath().isEmpty()) {
            // Calculate path if empty
            List<Point3D> path = zone.findPath(getPosition(), cible, true); // true = isMarine
            setCurrentPath(path);
        }

        // Get next waypoint
        Point3D nextPoint = cible;
        if (!getCurrentPath().isEmpty()) {
            nextPoint = getCurrentPath().get(0);
        }

        // Similar logic but with currents
        Point3D courant = zone.getCourantMarin();

        double dx = nextPoint.getX() - getPosition().getX();
        double dy = nextPoint.getY() - getPosition().getY();
        double dz = nextPoint.getZ() - getPosition().getZ();
        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

        if (distance < 5.0) { // Reached waypoint
            if (!getCurrentPath().isEmpty()) {
                getCurrentPath().remove(0); // Remove reached point
                if (getCurrentPath().isEmpty())
                    return; // Reached final target
                // Recurse or wait for next update to move to next point
                return;
            }
        }

        double nx = dx / distance;
        double ny = dy / distance;
        double nz = dz / distance;

        double speed = getVitesseMax();

        if (speed > distance) {
            speed = distance;
        }

        double moveX = nx * speed;
        double moveY = ny * speed;
        double moveZ = nz * speed;

        // Apply current
        moveX += courant.getX();
        moveY += courant.getY();
        moveZ += courant.getZ();

        Point3D newPos = new Point3D(
                getPosition().getX() + moveX,
                getPosition().getY() + moveY,
                getPosition().getZ() + moveZ);

        if (!zone.isInside(newPos)) {
            // System.out.println(getId() + " : Sortie de zone !");
            return;
        }
        if (zone.isCollision(newPos, this)) {
            // System.out.println(getId() + " : Collision !");
            notifierEtatCritique(TypeAlerte.COLLISION_IMMINENTE);
            return;
        }

        // Check Land Collision (for Marine)
        if (zone.isLand(newPos)) {
            // System.out.println("Marine vehicle hit land!");
            return;
        }

        setPosition(newPos);

        double consumption = 1.0;
        // Current opposition
        // Simplified
        if (courant.getX() != 0 || courant.getY() != 0 || courant.getZ() != 0) {
            consumption += 0.5;
        }

        setAutonomieActuelle(getAutonomieActuelle() - consumption);
        if (getAutonomieActuelle() <= 0) {
            setAutonomieActuelle(0);
            setEtat(EtatOperationnel.EN_PANNE);
            notifierEtatCritique(TypeAlerte.BATTERIE_FAIBLE);
        }

        // System.out.println(getId() + " moved to " + getPosition() + ". Autonomy: " +
        // getAutonomieActuelle());
    }

    @Override
    public List<Point3D> calculerTrajet(Point3D cible) {
        List<Point3D> path = new ArrayList<>();
        path.add(getPosition());
        path.add(cible);
        return path;
    }
}
