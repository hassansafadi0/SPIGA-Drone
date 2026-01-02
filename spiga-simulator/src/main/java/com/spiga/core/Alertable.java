package com.spiga.core;

/**
 * Interface for entities that can send alerts.
 */
public interface Alertable {
    /**
     * Notifies a critical state with a specific alert type.
     * 
     * @param type The type of alert to send.
     */
    void notifierEtatCritique(TypeAlerte type);
}
