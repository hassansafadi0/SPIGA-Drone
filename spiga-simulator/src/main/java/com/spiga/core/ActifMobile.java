package com.spiga.core;

import com.spiga.env.ZoneOperation;

/**
 * Abstract base class for all mobile assets in the simulation.
 * Implements core interfaces for movement, energy management, communication,
 * and piloting.
 */
import com.spiga.env.Collidable;

/**
 * Abstract base class for all mobile assets in the simulation.
 * Implements core interfaces for movement, energy management, communication,
 * and piloting.
 */
public abstract class ActifMobile implements Deplacable, Rechargeable, Communicable, Pilotable, Alertable, Collidable {
    private String id;
    private Point3D position;
    private double vitesseMax;
    private double autonomieMax;
    private double autonomieActuelle;
    private EtatOperationnel etat;
    private Point3D target; // Individual target for movement
    private java.util.List<Point3D> currentPath; // Path to follow
    private double radius = 5.0; // Default collision radius

    /**
     * Constructor for ActifMobile.
     * 
     * @param id           Unique identifier.
     * @param position     Initial position.
     * @param vitesseMax   Maximum speed.
     * @param autonomieMax Maximum autonomy.
     */
    public ActifMobile(String id, Point3D position, double vitesseMax, double autonomieMax) {
        this.id = id;
        this.position = position;
        this.vitesseMax = vitesseMax;
        this.autonomieMax = autonomieMax;
        this.autonomieActuelle = autonomieMax;
        this.etat = EtatOperationnel.AU_SOL; // Default state
        this.target = null; // No target initially
        this.currentPath = new java.util.ArrayList<>();
    }

    @Override
    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    // ... (rest of the class)

    public Point3D getTarget() {
        return target;
    }

    public void setTarget(Point3D target) {
        this.target = target;
        // Path calculation will be triggered by subclasses or manually
        // For now, clear path so it recalculates
        this.currentPath.clear();
    }

    public java.util.List<Point3D> getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(java.util.List<Point3D> path) {
        this.currentPath = path;
    }

    public String getId() {
        return id;
    }

    public Point3D getPosition() {
        return position;
    }

    public void setPosition(Point3D position) {
        this.position = position;
    }

    public double getVitesseMax() {
        return vitesseMax;
    }

    public double getAutonomieMax() {
        return autonomieMax;
    }

    public double getAutonomieActuelle() {
        return autonomieActuelle;
    }

    public void setAutonomieActuelle(double autonomieActuelle) {
        this.autonomieActuelle = autonomieActuelle;
    }

    public EtatOperationnel getEtat() {
        return etat;
    }

    public void setEtat(EtatOperationnel etat) {
        this.etat = etat;
    }

    // Implementations of interfaces can be abstract or default here

    @Override
    public void demarrer() {
        if (etat == EtatOperationnel.EN_PANNE || etat == EtatOperationnel.EN_MAINTENANCE) {
            System.out.println("Impossible de démarrer : " + id + " est en " + etat);
            return;
        }
        System.out.println(id + " démarre.");
    }

    @Override
    public void arreter() {
        System.out.println(id + " s'arrête.");
        this.etat = EtatOperationnel.AU_SOL;
    }

    @Override
    public void notifierEtatCritique(TypeAlerte type) {
        System.out.println("ALERTE [" + id + "]: " + type);
    }

    @Override
    public void envoyerMessage(String destinataire, String message) {
        System.out.println("Transmission de " + id + " à " + destinataire + ": " + message);
    }

    @Override
    public void recharger(double quantite) {
        this.autonomieActuelle = Math.min(this.autonomieMax, this.autonomieActuelle + quantite);
        System.out.println(id + " rechargé de " + quantite + ". Niveau: " + this.autonomieActuelle);
    }

    @Override
    public void ravitailler() {
        this.autonomieActuelle = this.autonomieMax;
        System.out.println(id + " ravitaillé complètement.");
    }

    // Abstract methods for movement to be implemented by subclasses
    @Override
    public abstract void deplacer(Point3D cible, ZoneOperation zone);

    @Override
    public abstract java.util.List<Point3D> calculerTrajet(Point3D cible);
}
