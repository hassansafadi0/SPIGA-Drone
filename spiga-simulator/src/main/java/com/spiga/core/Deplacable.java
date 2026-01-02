package com.spiga.core;

import com.spiga.env.ZoneOperation;
import java.util.List;

/**
 * Interface for entities that can move within a zone.
 */
public interface Deplacable {
    /**
     * Moves the entity towards a target within a specific zone.
     * 
     * @param cible The target 3D point.
     * @param zone  The zone where the movement occurs.
     */
    void deplacer(Point3D cible, ZoneOperation zone);

    List<Point3D> calculerTrajet(Point3D cible);
}
