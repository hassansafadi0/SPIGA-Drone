package com.spiga.env;

import com.spiga.core.Point3D;
import java.util.ArrayList;
import java.util.List;

public class ZoneOperation {
    private Point3D minCoord;
    private Point3D maxCoord;
    private Point3D vent; // Direction and intensity
    private double precipitations; // Intensity
    private Point3D courantMarin; // 3D vector
    private List<Obstacle> obstacles;

    public ZoneOperation(Point3D minCoord, Point3D maxCoord) {
        this.minCoord = minCoord;
        this.maxCoord = maxCoord;
        this.vent = new Point3D(0, 0, 0);
        this.precipitations = 0;
        this.courantMarin = new Point3D(0, 0, 0);
        this.obstacles = new ArrayList<>();
    }

    public Point3D getVent() {
        return vent;
    }

    public void setVent(Point3D vent) {
        this.vent = vent;
    }

    public double getPrecipitations() {
        return precipitations;
    }

    public void setPrecipitations(double precipitations) {
        this.precipitations = precipitations;
    }

    public Point3D getCourantMarin() {
        return courantMarin;
    }

    public void setCourantMarin(Point3D courantMarin) {
        this.courantMarin = courantMarin;
    }

    public void addObstacle(Obstacle obstacle) {
        this.obstacles.add(obstacle);
    }

    public boolean isCollision(Point3D point) {
        for (Obstacle obs : obstacles) {
            if (obs.contains(point)) {
                return true;
            }
        }
        return false;
    }

    public boolean isInside(Point3D point) {
        return point.getX() >= minCoord.getX() && point.getX() <= maxCoord.getX() &&
                point.getY() >= minCoord.getY() && point.getY() <= maxCoord.getY() &&
                point.getZ() >= minCoord.getZ() && point.getZ() <= maxCoord.getZ();
    }
}
