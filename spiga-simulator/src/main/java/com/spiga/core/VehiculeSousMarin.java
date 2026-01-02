package com.spiga.core;

/**
 * Class representing a submarine.
 * Capable of underwater movement (Z < 0).
 */
public class VehiculeSousMarin extends ActifMarin {

    /**
     * Constructor for VehiculeSousMarin.
     * 
     * @param id       Unique identifier.
     * @param position Initial position (Z should be < 0).
     */
    public VehiculeSousMarin(String id, Point3D position) {
        super(id, position, 40.0, 200.0); // Slow, very high autonomy
    }

    @Override
    public void envoyerMessage(String destinataire, String message) {
        System.out.println(getId() + " (Sous-marin) envoie à " + destinataire + ": " + message);
    }

    @Override
    public void demarrer() {
        System.out.println(getId() + " : Moteurs sous-marins activés.");
    }

    @Override
    public void arreter() {
        System.out.println(getId() + " : Moteurs sous-marins arrêtés.");
    }

    @Override
    public void recharger(double quantite) {
        setAutonomieActuelle(Math.min(getAutonomieMax(), getAutonomieActuelle() + quantite));
        System.out.println(getId() + " : Batteries rechargées. Niveau: " + getAutonomieActuelle());
    }

    @Override
    public void ravitailler() {
        // Not applicable for battery, but maybe for oxygen/supplies
    }

    @Override
    public void notifierEtatCritique(TypeAlerte type) {
        System.out.println("ALERTE SOUS-MARIN " + getId() + ": " + type);
    }
}
