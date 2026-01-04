package com.spiga.env;

import com.spiga.core.Point3D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ZoneOperationTest {

    private ZoneOperation zone;

    @BeforeEach
    public void setUp() {
        zone = new ZoneOperation(new Point3D(0, 0, -1000), new Point3D(1000, 1000, 1000));
    }

    @Test
    public void testIsLand() {
        // Island 1 at (300, 300) radius 150
        assertTrue(zone.isLand(new Point3D(300, 300, 0)));
        assertTrue(zone.isLand(new Point3D(400, 300, 0))); // Inside
        assertFalse(zone.isLand(new Point3D(500, 500, 0))); // Outside
    }

    @Test
    public void testFindPathMarine() {
        // Start at (100, 100) [Water], End at (500, 500) [Water]
        // Island at (300, 300) blocks direct path
        Point3D start = new Point3D(100, 100, 0);
        Point3D end = new Point3D(500, 500, 0);

        List<Point3D> path = zone.findPath(start, end, true); // isMarine=true

        assertFalse(path.isEmpty());
        // Verify no point in path is on land
        for (Point3D p : path) {
            assertFalse(zone.isLand(p), "Marine path should not cross land at " + p);
        }
    }

    @Test
    public void testFindPathLand() {
        // Start at (300, 300) [Island 1], End at (350, 350) [Island 1]
        Point3D start = new Point3D(300, 300, 0);
        Point3D end = new Point3D(350, 350, 0);

        List<Point3D> path = zone.findPath(start, end, false); // isMarine=false (Land)

        assertFalse(path.isEmpty());
        // Verify all points in path are on land
        for (Point3D p : path) {
            assertTrue(zone.isLand(p), "Land path should not cross water at " + p);
        }
    }

    @Test
    public void testIsCollision() {
        Obstacle obs = new Obstacle(new Point3D(500, 500, 50), 10);
        zone.addObstacle(obs);

        assertTrue(zone.isCollision(new Point3D(500, 500, 50)));
        assertFalse(zone.isCollision(new Point3D(600, 600, 50)));
    }
}
