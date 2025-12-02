package com.spiga.core;

public class DroneLogistique extends ActifAerien {
    public DroneLogistique(String id, Point3D position) {
        super(id, position, 60.0, 100.0); // Lower speed, high autonomy
    }
}
