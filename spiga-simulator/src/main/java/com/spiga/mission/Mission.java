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

    public void assignerActif(ActifMobile actif) {
        this.actifsAssignes.add(actif);
    }

    public void demarrer() {
        this.statut = StatutMission.EN_COURS;
        for (ActifMobile actif : actifsAssignes) {
            actif.demarrer();
        }
        System.out.println("Mission " + id + " démarrée.");
    }

    public void terminer() {
        this.statut = StatutMission.TERMINEE;
        for (ActifMobile actif : actifsAssignes) {
            actif.eteindre(); // Or return to base
        }
        System.out.println("Mission " + id + " terminée.");
    }

    public StatutMission getStatut() {
        return statut;
    }
}
