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
    private List<Island> islands;
    private static final int GRID_SIZE = 50; // 50x50 grid for 1000x1000 world (20 units per cell)

    private List<Collidable> collidables;

    public ZoneOperation(Point3D minCoord, Point3D maxCoord) {
        this.minCoord = minCoord;
        this.maxCoord = maxCoord;
        this.vent = new Point3D(0, 0, 0);
        this.precipitations = 0;
        this.courantMarin = new Point3D(0, 0, 0);
        this.obstacles = new ArrayList<>();
        this.islands = new ArrayList<>();
        this.collidables = new ArrayList<>();

        // Define Islands
        // Island 1: Circle at (300, 300), Radius 150
        islands.add(new Island(300, 300, 150, 150, true));
        // Island 2: Rectangle at (700, 700), 200x300
        islands.add(new Island(700, 700, 200, 300, false));
    }

    /**
     * Nested class representing an island obstacle.
     */
    public static class Island {
        /** X coordinate of the island center/top-left. */
        public double x;
        /** Y coordinate of the island center/top-left. */
        public double y;
        /** Width or radius of the island. */
        public double w;
        /** Height of the island (if not circular). */
        public double h;
        /** True if the island is circular, false if rectangular. */
        public boolean isCircle;

        /**
         * Constructor for Island.
         * 
         * @param x        X coordinate.
         * @param y        Y coordinate.
         * @param w        Width or radius.
         * @param h        Height.
         * @param isCircle Shape type.
         */
        public Island(double x, double y, double w, double h, boolean isCircle) {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
            this.isCircle = isCircle;
        }

        public boolean contains(double px, double py) {
            if (isCircle) {
                return Math.sqrt(Math.pow(px - x, 2) + Math.pow(py - y, 2)) <= w;
            } else {
                // Rectangle centered at x,y
                return px >= x - w / 2 && px <= x + w / 2 && py >= y - h / 2 && py <= y + h / 2;
            }
        }
    }

    /**
     * Gets the list of islands in the zone.
     * 
     * @return List of islands.
     */
    public List<Island> getIslands() {
        return islands;
    }

    public boolean isLand(Point3D p) {
        for (Island island : islands) {
            if (island.contains(p.getX(), p.getY()))
                return true;
        }
        return false;
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

    /**
     * Adds a collidable object to the zone.
     * 
     * @param c The collidable object.
     */
    public void addCollidable(Collidable c) {
        this.collidables.add(c);
    }

    /**
     * Removes a collidable object from the zone.
     * 
     * @param c The collidable object to remove.
     */
    public void removeCollidable(Collidable c) {
        this.collidables.remove(c);
    }

    /**
     * Checks if a point collides with any obstacle or collidable object.
     * 
     * @param point The point to check.
     * @return True if a collision is detected, false otherwise.
     */
    public boolean isCollision(Point3D point) {
        return isCollision(point, null);
    }

    /**
     * Checks if a point collides with any obstacle or collidable object, ignoring a
     * specific object.
     * 
     * @param point    The point to check.
     * @param ignoreMe The collidable object to ignore (usually self).
     * @return True if a collision is detected, false otherwise.
     */
    public boolean isCollision(Point3D point, Collidable ignoreMe) {
        // Check static obstacles
        for (Obstacle obs : obstacles) {
            if (obs.contains(point)) {
                return true;
            }
        }

        // Check dynamic collidables (vehicles)
        for (Collidable c : collidables) {
            if (c == ignoreMe)
                continue;

            // Check if at same Z level (with small tolerance)
            if (Math.abs(c.getPosition().getZ() - point.getZ()) < 0.1) {
                double dist = Math.sqrt(
                        Math.pow(c.getPosition().getX() - point.getX(), 2) +
                                Math.pow(c.getPosition().getY() - point.getY(), 2));

                // Assume default radius for the point being checked if not provided
                double myRadius = (ignoreMe != null) ? ignoreMe.getRadius() : 5.0;

                if (dist < (c.getRadius() + myRadius)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if a point is inside the zone boundaries.
     * 
     * @param point The point to check.
     * @return True if inside, false otherwise.
     */
    public boolean isInside(Point3D point) {
        return point.getX() >= minCoord.getX() && point.getX() <= maxCoord.getX() &&
                point.getY() >= minCoord.getY() && point.getY() <= maxCoord.getY() &&
                point.getZ() >= minCoord.getZ() && point.getZ() <= maxCoord.getZ();
    }

    // A* Pathfinding
    /**
     * Finds a path from start to end using A* algorithm.
     * 
     * @param start    Starting point.
     * @param end      Ending point.
     * @param isMarine True if the vehicle is marine (avoids land), false if land
     *                 vehicle (avoids water).
     * @return A list of points representing the path.
     */
    public List<Point3D> findPath(Point3D start, Point3D end, boolean isMarine) {
        // Simple grid-based A*
        Node startNode = new Node((int) (start.getX() / 20), (int) (start.getY() / 20));
        Node endNode = new Node((int) (end.getX() / 20), (int) (end.getY() / 20));

        java.util.PriorityQueue<Node> openSet = new java.util.PriorityQueue<>(
                java.util.Comparator.comparingDouble(n -> n.f));
        java.util.Set<String> closedSet = new java.util.HashSet<>();
        java.util.Map<String, Node> allNodes = new java.util.HashMap<>();

        startNode.g = 0;
        startNode.h = heuristic(startNode, endNode);
        startNode.f = startNode.g + startNode.h;
        openSet.add(startNode);
        allNodes.put(startNode.key(), startNode);

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            if (current.key().equals(endNode.key())) {
                return reconstructPath(current);
            }

            closedSet.add(current.key());

            for (Node neighbor : getNeighbors(current)) {
                if (closedSet.contains(neighbor.key()))
                    continue;

                // Check terrain constraints
                Point3D p = new Point3D(neighbor.x * 20 + 10, neighbor.y * 20 + 10, 0);
                boolean land = isLand(p);
                if (isMarine && land)
                    continue; // Marine cannot go on land
                if (!isMarine && !land)
                    continue; // Land vehicle cannot go on water (assuming isMarine=false means Land vehicle)
                // Note: Aerial vehicles shouldn't use this pathfinder or should have
                // isMarine=null logic?
                // For now, let's assume this method is only called for Surface/Land vehicles.

                double tentativeG = current.g + 1; // Cost 1 per step

                Node existing = allNodes.get(neighbor.key());
                if (existing == null || tentativeG < existing.g) {
                    neighbor.parent = current;
                    neighbor.g = tentativeG;
                    neighbor.h = heuristic(neighbor, endNode);
                    neighbor.f = neighbor.g + neighbor.h;

                    if (existing == null) {
                        allNodes.put(neighbor.key(), neighbor);
                        openSet.add(neighbor);
                    } else {
                        // Update existing (re-add to sort)
                        openSet.remove(existing);
                        existing.g = neighbor.g;
                        existing.f = neighbor.f;
                        existing.parent = current;
                        openSet.add(existing);
                    }
                }
            }
        }

        // No path found, return direct line (fallback)
        List<Point3D> fallback = new ArrayList<>();
        fallback.add(end);
        return fallback;
    }

    private double heuristic(Node a, Node b) {
        return Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
    }

    private List<Node> getNeighbors(Node n) {
        List<Node> neighbors = new ArrayList<>();
        int[] dx = { 0, 0, 1, -1, 1, 1, -1, -1 };
        int[] dy = { 1, -1, 0, 0, 1, -1, 1, -1 };

        for (int i = 0; i < 8; i++) {
            int nx = n.x + dx[i];
            int ny = n.y + dy[i];
            if (nx >= 0 && nx < GRID_SIZE && ny >= 0 && ny < GRID_SIZE) { // Use GRID_SIZE here
                neighbors.add(new Node(nx, ny));
            }
        }
        return neighbors;
    }

    private List<Point3D> reconstructPath(Node current) {
        List<Point3D> path = new ArrayList<>();
        while (current != null) {
            path.add(0, new Point3D(current.x * 20 + 10, current.y * 20 + 10, 0));
            current = current.parent;
        }
        return path;
    }

    private static class Node {
        int x, y;
        double g, h, f;
        Node parent;

        public Node(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public String key() {
            return x + "," + y;
        }
    }
}
