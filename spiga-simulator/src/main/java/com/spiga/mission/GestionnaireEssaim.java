package com.spiga.mission;

import com.spiga.core.ActifMobile;
import com.spiga.core.EtatOperationnel;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages the fleet of mobile assets.
 * Responsible for adding assets, retrieving the fleet, and suggesting available
 * assets for missions.
 */
public class GestionnaireEssaim {
    private List<ActifMobile> flotte;

    /**
     * Constructor for GestionnaireEssaim.
     * Initializes an empty fleet.
     */
    public GestionnaireEssaim() {
        this.flotte = new ArrayList<>();
    }

    /**
     * Adds a mobile asset to the fleet.
     * 
     * @param actif The asset to add.
     */
    public void ajouterActif(ActifMobile actif) {
        this.flotte.add(actif);
    }

    /**
     * Retrieves the entire fleet of assets.
     * 
     * @return A list of all assets in the fleet.
     */
    public List<ActifMobile> getFlotte() {
        return flotte;
    }

    /**
     * Retrieves a list of available assets (those currently on the ground/idle).
     * 
     * @return A list of available assets.
     */
    public List<ActifMobile> getActifsDisponibles() {
        return flotte.stream()
                .filter(a -> a.getEtat() == EtatOperationnel.AU_SOL)
                .collect(Collectors.toList());
    }

    /**
     * Suggests an available asset that meets a minimum autonomy requirement.
     * 
     * @param minAutonomie The minimum autonomy required.
     * @return An available asset meeting the criteria, or null if none found.
     */
    public ActifMobile suggererActif(double minAutonomie) {
        return getActifsDisponibles().stream()
                .filter(a -> a.getAutonomieActuelle() >= minAutonomie)
                .findFirst()
                .orElse(null);
    }
}
