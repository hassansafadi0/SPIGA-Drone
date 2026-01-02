package com.spiga.core;

/**
 * Class representing a logistics drone.
 * Specialized for carrying cargo with moderate speed and autonomy.
 */
public class DroneLogistique extends ActifAerien {

    private double capaciteEmport; // Cargo capacity in kg

    /**
     * Constructor for DroneLogistique.
     * 
     * @param id             Unique identifier.
     * @param position       Initial position.
     * @param capaciteEmport Cargo capacity in kg.
     */
    public DroneLogistique(String id, Point3D position, double capaciteEmport) {
        super(id, position, 80.0, 45.0); // Slower, less autonomy than reconnaissance
        this.capaciteEmport = capaciteEmport;
    }

    /**
     * Constructor for DroneLogistique with default capacity.
     * 
     * @param id       Unique identifier.
     * @param position Initial position.
     */
    public DroneLogistique(String id, Point3D position) {
        this(id, position, 10.0);
    }

    /**
     * Gets the cargo capacity.
     * 
     * @return The capacity in kg.
     */
    public double getCapaciteEmport() {
        return capaciteEmport;
    }

    @Override
    public void envoyerMessage(String destinataire, String message) {
        System.out.println(getId() + " (Logistique) envoie à " + destinataire + ": " + message);
    }

    @Override
    public void demarrer() {
        System.out.println(getId() + " : Rotors activés.");
    }

    @Override
    public void arreter() {
        System.out.println(getId() + " : Rotors arrêtés.");
    }

    @Override
    public void recharger(double quantite) {
        setAutonomieActuelle(Math.min(getAutonomieMax(), getAutonomieActuelle() + quantite));
        System.out.println(getId() + " : Batterie remplacée. Niveau: " + getAutonomieActuelle());
    }

    @Override
    public void ravitailler() {
        recharger(100);
    }

    @Override
    public void notifierEtatCritique(TypeAlerte type) {
        System.out.println("ALERTE LOGISTIQUE " + getId() + ": " + type);
    }
}
