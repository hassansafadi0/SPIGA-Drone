package com.spiga.mission;

import com.spiga.core.ActifMobile;
import com.spiga.core.EtatOperationnel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GestionnaireEssaim {
    private List<ActifMobile> flotte;

    public GestionnaireEssaim() {
        this.flotte = new ArrayList<>();
    }

    public void ajouterActif(ActifMobile actif) {
        this.flotte.add(actif);
    }

    public List<ActifMobile> getFlotte() {
        return flotte;
    }

    public List<ActifMobile> getActifsDisponibles() {
        return flotte.stream()
                .filter(a -> a.getEtat() == EtatOperationnel.AU_SOL || a.getEtat() == EtatOperationnel.EN_MISSION) // Assuming
                                                                                                                   // EN_MISSION
                                                                                                                   // can
                                                                                                                   // be
                                                                                                                   // reassigned
                                                                                                                   // or
                                                                                                                   // available
                                                                                                                   // means
                                                                                                                   // ready
                // Actually available usually means AU_SOL (idle).
                .filter(a -> a.getEtat() == EtatOperationnel.AU_SOL)
                .collect(Collectors.toList());
    }

    public ActifMobile suggererActif(double minAutonomie) {
        return getActifsDisponibles().stream()
                .filter(a -> a.getAutonomieActuelle() >= minAutonomie)
                .findFirst()
                .orElse(null);
    }
}
