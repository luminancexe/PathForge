package pathfinding.model;

import java.util.Objects;

/**
 * Represents a single coordinate node on the 2D grid graph.
 * Implements Comparable<Node> so it can be ordered inside custom MinHeap.
 */
public class Node implements Comparable<Node> {

    private final int x;
    private final int y;

    private NodeType type;
    private NodeType baseType; // Preserves background terrain (e.g. MUD, EMPTY)

    private double gCost; // Exact cost from Start to this node
    private double hCost; // Heuristic estimated cost from this node to Target
    private double fCost; // Total evaluation cost (gCost + hCost)

    private Node parent;
    private boolean inOpenSet;
    private boolean inClosedSet;

    public Node(int x, int y, NodeType type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.baseType = type;
        resetSearchState();
    }

    public void resetSearchState() {
        this.gCost = Double.POSITIVE_INFINITY;
        this.hCost = 0.0;
        this.fCost = Double.POSITIVE_INFINITY;
        this.parent = null;
        this.inOpenSet = false;
        this.inClosedSet = false;

        // Restore visual type if it was temporarily set to VISITED, FRONTIER, or PATH
        if (type == NodeType.VISITED || type == NodeType.FRONTIER || type == NodeType.PATH || type == NodeType.AGENT) {
            this.type = this.baseType;
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public NodeType getType() {
        return type;
    }

    public void setType(NodeType type) {
        this.type = type;
        if (type == NodeType.EMPTY || type == NodeType.WALL || type == NodeType.MUD ||
            type == NodeType.START || type == NodeType.TARGET) {
            this.baseType = type;
        }
    }

    public NodeType getBaseType() {
        return baseType;
    }

    public void setBaseType(NodeType baseType) {
        this.baseType = baseType;
        this.type = baseType;
    }

    public double getGCost() {
        return gCost;
    }

    public void setGCost(double gCost) {
        this.gCost = gCost;
        this.fCost = this.gCost + this.hCost;
    }

    public double getHCost() {
        return hCost;
    }

    public void setHCost(double hCost) {
        this.hCost = hCost;
        this.fCost = this.gCost + this.hCost;
    }

    public double getFCost() {
        return fCost;
    }

    public void setFCost(double fCost) {
        this.fCost = fCost;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public boolean isInOpenSet() {
        return inOpenSet;
    }

    public void setInOpenSet(boolean inOpenSet) {
        this.inOpenSet = inOpenSet;
    }

    public boolean isInClosedSet() {
        return inClosedSet;
    }

    public void setInClosedSet(boolean inClosedSet) {
        this.inClosedSet = inClosedSet;
    }

    public double getTerrainCost() {
        return baseType.getMovementCost();
    }

    public boolean isWalkable() {
        return baseType.isWalkable();
    }

    /**
     * Min-Heap comparison:
     * Compares by fCost first.
     * Tie-breaker: compares by hCost (favors nodes closer to target).
     */
    @Override
    public int compareTo(Node other) {
        if (other == null) return 1;
        int cmp = Double.compare(this.fCost, other.fCost);
        if (cmp != 0) {
            return cmp;
        }
        return Double.compare(this.hCost, other.hCost);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return x == node.x && y == node.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return String.format("Node(%d, %d)[type=%s, g=%.1f, h=%.1f, f=%.1f]",
                x, y, type, gCost, hCost, fCost);
    }
}
