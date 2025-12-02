package com.spiga.core;

import com.spiga.env.ZoneOperation;
import java.util.List;

public interface Deplacable {
    void deplacer(Point3D cible, ZoneOperation zone);

    List<Point3D> calculerTrajet(Point3D cible);
}
