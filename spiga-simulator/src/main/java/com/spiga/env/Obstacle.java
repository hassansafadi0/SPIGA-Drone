package com.spiga.env;

import com.spiga.core.Point3D;

public class Obstacle {
    private Point3D position;
    private double radius; // Simple spherical obstacle for now

    public Obstacle(Point3D position, double radius) {
        this.position = position;
        this.radius = radius;
    }

    public boolean contains(Point3D point) {
        double dx = point.getX() - position.getX();
        double dy = point.getY() - position.getY();
        double dz = point.getZ() - position.getZ();
        return (dx * dx + dy * dy + dz * dz) <= (radius * radius);
    }
}
