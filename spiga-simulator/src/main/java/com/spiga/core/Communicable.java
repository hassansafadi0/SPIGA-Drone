package com.spiga.core;

/**
 * Interface for entities that can communicate.
 */
public interface Communicable {
    /**
     * Sends a message to a recipient.
     * 
     * @param destinataire The ID of the recipient.
     * @param message      The message content.
     */
    void envoyerMessage(String destinataire, String message);
}
