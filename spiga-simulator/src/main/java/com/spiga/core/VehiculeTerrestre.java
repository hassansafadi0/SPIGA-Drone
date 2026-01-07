package com.spiga.core;

import com.spiga.env.ZoneOperation;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a land-based vehicle.
 * Restricted to ground movement (Z=0) and land terrain.
 */
public class VehiculeTerrestre extends ActifMobile {

    /**
     * Constructor for VehiculeTerrestre.
     * 
     * @param id       Unique identifier.
     * @param position Initial position (Z will be forced to 0).
     */
    public VehiculeTerrestre(String id, Point3D position) {
        super(id, position, 50.0, 120.0); // Medium speed, good autonomy
        // Force Z to be 0 (ground)
        position.setZ(0);
    }

    @Override
    public void setPosition(Point3D position) {
        // Ensure Z is always 0
        super.setPosition(new Point3D(position.getX(), position.getY(), 0));
    }

    @Override
    public void envoyerMessage(String destinataire, String message) {
        System.out.println(getId() + " (Terrestre) envoie à " + destinataire + ": " + message);
    }

    @Override
    public void demarrer() {
        System.out.println(getId() + " : Moteur démarré.");
    }

    @Override
    public void arreter() {
        System.out.println(getId() + " : Moteur arrêté.");
    }

    @Override
    public void recharger(double quantite) {
        setAutonomieActuelle(Math.min(getAutonomieMax(), getAutonomieActuelle() + quantite));
        System.out.println(getId() + " : Plein fait. Niveau: " + getAutonomieActuelle());
    }

    @Override
    public void ravitailler() {
        recharger(100);
    }

    @Override
    public void notifierEtatCritique(TypeAlerte type) {
        System.out.println("ALERTE TERRESTRE " + getId() + ": " + type);
    }

    /**
     * Moves the land vehicle towards a target.
     * Enforces land-only movement and uses pathfinding.
     * 
     * @param cible The target point.
     * @param zone  The simulation zone.
     */
    @Override
    public void deplacer(Point3D cible, ZoneOperation zone) {
        try {
            if (getEtat() == EtatOperationnel.EN_PANNE || getAutonomieActuelle() <= 0) {
                return;
            }

            if (getEtat() == EtatOperationnel.AU_SOL) {
                setEtat(EtatOperationnel.EN_MISSION);
            }

            // Pathfinding Logic
            if (getCurrentPath().isEmpty()) {
                // Calculate path if empty
                List<Point3D> path = zone.findPath(getPosition(), cible, false); // false = isLand (not Marine)
                setCurrentPath(path);
            }

            // Get next waypoint
            Point3D nextPoint = cible;
            if (!getCurrentPath().isEmpty()) {
                nextPoint = getCurrentPath().get(0);
            }

            // Simple movement logic on ground (2D)
            double dx = nextPoint.getX() - getPosition().getX();
            double dy = nextPoint.getY() - getPosition().getY();
            // Ignore Z difference for movement direction, we stay on ground

            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance < 5.0) { // Reached waypoint
                if (!getCurrentPath().isEmpty()) {
                    getCurrentPath().remove(0); // Remove reached point
                    if (getCurrentPath().isEmpty())
                        return; // Reached final target
                    return;
                }
            }

            double nx = dx / distance;
            double ny = dy / distance;

            double speed = getVitesseMax();
            if (speed > distance) {
                speed = distance;
            }

            double moveX = nx * speed;
            double moveY = ny * speed;

            Point3D newPos = new Point3D(
                    getPosition().getX() + moveX,
                    getPosition().getY() + moveY,
                    0 // Stay on ground
            );

            if (!zone.isInside(newPos)) {
                return;
            }
            if (zone.isCollision(newPos, this)) {
                notifierEtatCritique(TypeAlerte.COLLISION_IMMINENTE);
                return;
            }

            // Check Water Collision (for Land Vehicle)
            if (!zone.isLand(newPos)) {
                // System.out.println("Car hit water!");
                return;
            }

            setPosition(newPos);

            // Consumption
            double consumption = 1.0;
            setAutonomieActuelle(getAutonomieActuelle() - consumption);
            if (getAutonomieActuelle() <= 0) {
                setAutonomieActuelle(0);
                setEtat(EtatOperationnel.EN_PANNE);
                notifierEtatCritique(TypeAlerte.BATTERIE_FAIBLE);
            }
        } catch (Exception e) {
            System.err.println("Error moving land vehicle " + getId() + ": " + e.getMessage());
            e.printStackTrace();
            setEtat(EtatOperationnel.EN_PANNE);
        }
    }

    @Override
    public List<Point3D> calculerTrajet(Point3D cible) {
        try {
            List<Point3D> path = new ArrayList<>();
            path.add(getPosition());
            path.add(cible);
            return path;
        } catch (Exception e) {
            System.err.println("Error calculating path for " + getId() + ": " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
