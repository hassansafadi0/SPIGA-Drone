package com.spiga.core;

import com.spiga.env.ZoneOperation;
import java.util.ArrayList;
import java.util.List;

public class VehiculeTerrestre extends ActifMobile {

    public VehiculeTerrestre(String id, Point3D position) {
        super(id, position, 50.0, 120.0); // Medium speed, good autonomy
        // Force Z to be 0 (ground)
        position.setZ(0);
    }

    @Override
    public void setPosition(Point3D position) {
        // Ensure Z is always 0
        super.setPosition(new Point3D(position.getX(), position.getY(), 0));
    }

    @Override
    public void deplacer(Point3D cible, ZoneOperation zone) {
        if (getEtat() == EtatOperationnel.EN_PANNE || getAutonomieActuelle() <= 0) {
            return;
        }

        if (getEtat() == EtatOperationnel.AU_SOL) {
            setEtat(EtatOperationnel.EN_MISSION);
        }

        // Simple movement logic on ground (2D)
        double dx = cible.getX() - getPosition().getX();
        double dy = cible.getY() - getPosition().getY();
        // Ignore Z difference for movement direction, we stay on ground

        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance == 0)
            return;

        double nx = dx / distance;
        double ny = dy / distance;

        double speed = getVitesseMax();
        if (speed > distance) {
            speed = distance;
        }

        double moveX = nx * speed;
        double moveY = ny * speed;

        Point3D newPos = new Point3D(
                getPosition().getX() + moveX,
                getPosition().getY() + moveY,
                0 // Stay on ground
        );

        if (!zone.isInside(newPos)) {
            return;
        }
        if (zone.isCollision(newPos)) {
            notifierEtatCritique(TypeAlerte.COLLISION_IMMINENTE);
            return;
        }

        setPosition(newPos);

        // Consumption
        double consumption = 1.0;
        setAutonomieActuelle(getAutonomieActuelle() - consumption);
        if (getAutonomieActuelle() <= 0) {
            setAutonomieActuelle(0);
            setEtat(EtatOperationnel.EN_PANNE);
            notifierEtatCritique(TypeAlerte.BATTERIE_FAIBLE);
        }
    }

    @Override
    public List<Point3D> calculerTrajet(Point3D cible) {
        List<Point3D> path = new ArrayList<>();
        path.add(getPosition());
        path.add(cible);
        return path;
    }
}
