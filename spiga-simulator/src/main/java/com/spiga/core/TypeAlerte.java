package com.spiga.core;

/**
 * Enum representing types of alerts that can be triggered.
 */
public enum TypeAlerte {
    /** Battery level is critically low. */
    BATTERIE_FAIBLE,
    /** A collision is imminent. */
    COLLISION_IMMINENTE,
    /** Asset has exited the operational zone. */
    SORTIE_ZONE,
    /** A general failure or breakdown. */
    PANNE,
    /** Extreme weather conditions. */
    CONDITIONS_METEO_EXTREMES,
    /** Other type of alert. */
    AUTRE
}
