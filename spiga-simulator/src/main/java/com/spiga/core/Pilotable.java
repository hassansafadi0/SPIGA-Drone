package com.spiga.core;

/**
 * Interface for entities that can be piloted or controlled.
 */
public interface Pilotable {
    /**
     * Starts the entity's operation.
     */
    void demarrer();

    /**
     * Stops the entity's operation.
     */
    void arreter();
}
