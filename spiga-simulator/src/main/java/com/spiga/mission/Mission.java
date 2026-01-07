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
        try {
            if (actif == null) {
                System.err.println("Cannot assign null asset to mission " + id);
                return;
            }
            this.actifsAssignes.add(actif);
        } catch (Exception e) {
            System.err.println("Error assigning asset to mission " + id + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Starts the mission.
     */
    public void demarrer() {
        try {
            this.statut = StatutMission.EN_COURS;
            for (ActifMobile actif : actifsAssignes) {
                try {
                    actif.demarrer();
                } catch (Exception e) {
                    System.err.println(
                            "Error starting asset " + actif.getId() + " for mission " + id + ": " + e.getMessage());
                }
            }
            System.out.println("Mission " + id + " démarrée.");
        } catch (Exception e) {
            System.err.println("Error starting mission " + id + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Terminates the mission.
     */
    public void terminer() {
        try {
            this.statut = StatutMission.TERMINEE;
            for (ActifMobile actif : actifsAssignes) {
                try {
                    actif.arreter(); // Or return to base
                } catch (Exception e) {
                    System.err.println(
                            "Error stopping asset " + actif.getId() + " for mission " + id + ": " + e.getMessage());
                }
            }
            System.out.println("Mission " + id + " terminée.");
        } catch (Exception e) {
            System.err.println("Error terminating mission " + id + ": " + e.getMessage());
            e.printStackTrace();
        }
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
