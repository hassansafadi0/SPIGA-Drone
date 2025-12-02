package com.spiga.core;

public class DroneReconnaissance extends ActifAerien {
    public DroneReconnaissance(String id, Point3D position) {
        super(id, position, 100.0, 50.0); // High speed, medium autonomy
    }
}
