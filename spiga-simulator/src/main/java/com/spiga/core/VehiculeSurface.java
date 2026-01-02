package com.spiga.core;

/**
 * Class representing a surface vessel (boat).
 * Restricted to water surface (Z=0).
 */
public class VehiculeSurface extends ActifMarin {

    /**
     * Constructor for VehiculeSurface.
     * 
     * @param id       Unique identifier.
     * @param position Initial position.
     */
    public VehiculeSurface(String id, Point3D position) {
        super(id, position, 60.0, 150.0); // Medium speed, high autonomy
    }

    @Override
    public void envoyerMessage(String destinataire, String message) {
        System.out.println(getId() + " (Surface) envoie à " + destinataire + ": " + message);
    }

    @Override
    public void demarrer() {
        System.out.println(getId() + " : Moteurs surface activés.");
    }

    @Override
    public void arreter() {
        System.out.println(getId() + " : Moteurs surface arrêtés.");
    }

    @Override
    public void recharger(double quantite) {
        setAutonomieActuelle(Math.min(getAutonomieMax(), getAutonomieActuelle() + quantite));
        System.out.println(getId() + " : Carburant ravitaillé. Niveau: " + getAutonomieActuelle());
    }

    @Override
    public void ravitailler() {
        recharger(100);
    }

    @Override
    public void notifierEtatCritique(TypeAlerte type) {
        System.out.println("ALERTE SURFACE " + getId() + ": " + type);
    }

    @Override
    public void setPosition(Point3D position) {
        // Ensure Z is always 0
        super.setPosition(new Point3D(position.getX(), position.getY(), 0));
    }
}
