package com.spiga.env;

import com.spiga.core.Point3D;

/**
 * Interface for objects that can be involved in collisions.
 */
public interface Collidable {
    /**
     * Gets the current position of the object.
     * 
     * @return The position.
     */
    Point3D getPosition();

    /**
     * Gets the collision radius of the object.
     * 
     * @return The radius in units.
     */
    double getRadius();

    /**
     * Gets the unique identifier of the object.
     * 
     * @return The ID.
     */
    String getId();
}
