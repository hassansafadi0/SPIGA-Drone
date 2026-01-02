package com.spiga.core;

/**
 * Enum representing the operational states of an asset.
 */
public enum EtatOperationnel {
    /** Asset is on the ground or idle. */
    AU_SOL,
    /** Asset is currently executing a mission. */
    EN_MISSION,
    /** Asset has broken down or run out of energy. */
    EN_PANNE,
    /** Asset is undergoing maintenance. */
    EN_MAINTENANCE
}
