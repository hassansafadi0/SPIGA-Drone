package com.spiga.mission;

import com.spiga.core.ActifMobile;
import java.util.ArrayList;
import java.util.List;

public abstract class Mission {
    private String id;
    private ObjectifMission objectif;
    private StatutMission statut;
    private List<ActifMobile> actifsAssignes;

    public Mission(String id, ObjectifMission objectif) {
        this.id = id;
        this.objectif = objectif;
        this.statut = StatutMission.PLANIFIEE;
        this.actifsAssignes = new ArrayList<>();
    }

    /**
     * Assigns an asset to the mission.
     * 
     * @param actif The mobile asset to assign.
     */
    public void assignerActif(ActifMobile actif) {
        this.actifsAssignes.add(actif);
    }

    /**
     * Starts the mission.
     */
    public void demarrer() {
        this.statut = StatutMission.EN_COURS;
        for (ActifMobile actif : actifsAssignes) {
            actif.demarrer();
        }
        System.out.println("Mission " + id + " démarrée.");
    }

    /**
     * Terminates the mission.
     */
    public void terminer() {
        this.statut = StatutMission.TERMINEE;
        for (ActifMobile actif : actifsAssignes) {
            actif.arreter(); // Or return to base
        }
        System.out.println("Mission " + id + " terminée.");
    }

    /**
     * Returns the current status of the mission.
     * 
     * @return The status of the mission.
     */
    public StatutMission getStatut() {
        return statut;
    }

    /**
     * Gets the mission objective.
     * 
     * @return The objective of the mission.
     */
    public ObjectifMission getObjectif() {
        return objectif;
    }

    /**
     * Sets the mission objective.
     * 
     * @param objectif The new objective.
     */
    public void setObjectif(ObjectifMission objectif) {
        this.objectif = objectif;
    }
}
