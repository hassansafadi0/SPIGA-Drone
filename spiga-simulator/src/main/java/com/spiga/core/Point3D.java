package com.spiga.core;

/**
 * Class representing a point in 3D space.
 */
public class Point3D {
    private double x;
    private double y;
    private double z;

    /**
     * Constructor for Point3D.
     * 
     * @param x X coordinate.
     * @param y Y coordinate.
     * @param z Z coordinate.
     */
    public Point3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Gets the X coordinate.
     * 
     * @return The X coordinate.
     */
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    @Override
    public String toString() {
        return "Point3D{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
