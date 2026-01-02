package com.spiga.core;

/**
 * Interface for entities that can be recharged.
 */
public interface Rechargeable {
    /**
     * Recharges the entity by a specified amount.
     * 
     * @param quantite The amount of energy to add.
     */
    void recharger(double quantite);

    void ravitailler();
}
