package com.spiga.core;

/**
 * Class representing a reconnaissance drone.
 * Specialized for surveillance with high speed and autonomy.
 */
public class DroneReconnaissance extends ActifAerien {

    private double rayonCapteur; // Sensor radius in meters

    /**
     * Constructor for DroneReconnaissance.
     * 
     * @param id           Unique identifier.
     * @param position     Initial position.
     * @param rayonCapteur Sensor radius in meters.
     */
    public DroneReconnaissance(String id, Point3D position, double rayonCapteur) {
        super(id, position, 120.0, 60.0); // Fast, good autonomy
        this.rayonCapteur = rayonCapteur;
    }

    /**
     * Constructor for DroneReconnaissance with default sensor radius.
     * 
     * @param id       Unique identifier.
     * @param position Initial position.
     */
    public DroneReconnaissance(String id, Point3D position) {
        this(id, position, 500.0);
    }

    @Override
    public void envoyerMessage(String destinataire, String message) {
        System.out.println(getId() + " (Recon) envoie à " + destinataire + ": " + message);
    }

    @Override
    public void demarrer() {
        System.out.println(getId() + " : Décollage immédiat.");
    }

    @Override
    public void arreter() {
        System.out.println(getId() + " : Atterrissage.");
    }

    @Override
    public void recharger(double quantite) {
        setAutonomieActuelle(Math.min(getAutonomieMax(), getAutonomieActuelle() + quantite));
        System.out.println(getId() + " : Recharge rapide terminée. Niveau: " + getAutonomieActuelle());
    }

    @Override
    public void ravitailler() {
        recharger(100);
    }

    @Override
    public void notifierEtatCritique(TypeAlerte type) {
        System.out.println("ALERTE RECONNAISSANCE " + getId() + ": " + type);
    }
}
