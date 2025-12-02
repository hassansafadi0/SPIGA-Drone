package com.spiga.core;

public class VehiculeSurface extends ActifMarin {
    public VehiculeSurface(String id, Point3D position) {
        super(id, position, 40.0, 200.0);
        // Force Z to be 0 (surface)
        position.setZ(0);
    }

    @Override
    public void setPosition(Point3D position) {
        // Ensure Z is always 0
        super.setPosition(new Point3D(position.getX(), position.getY(), 0));
    }
}
