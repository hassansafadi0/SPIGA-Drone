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

    public ZoneOperation(Point3D minCoord, Point3D maxCoord) {
        this.minCoord = minCoord;
        this.maxCoord = maxCoord;
        this.vent = new Point3D(0, 0, 0);
        this.precipitations = 0;
        this.courantMarin = new Point3D(0, 0, 0);
        this.obstacles = new ArrayList<>();
        this.islands = new ArrayList<>();

        // Define Islands
        // Island 1: Circle at (300, 300), Radius 150
        islands.add(new Island(300, 300, 150, 150, true));
        // Island 2: Rectangle at (700, 700), 200x300
        islands.add(new Island(700, 700, 200, 300, false));
    }

    public static class Island {
        public double x, y, w, h;
        public boolean isCircle; // true = circle (w=radius), false = rectangle

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

    // A* Pathfinding
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
