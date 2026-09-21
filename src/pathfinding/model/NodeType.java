package pathfinding.model;

/**
 * Enumeration representing different cell states in the pathfinding grid.
 */
public enum NodeType {
    EMPTY(1.0, "Open Ground"),
    WALL(Double.POSITIVE_INFINITY, "Impassable Wall"),
    MUD(4.0, "Difficult Terrain (Mud/Water)"),
    START(1.0, "Start Point (A)"),
    TARGET(1.0, "Target Goal (B)"),
    VISITED(1.0, "Explored Node"),
    FRONTIER(1.0, "Frontier (In MinHeap)"),
    PATH(1.0, "Optimal Path Node"),
    AGENT(1.0, "Game Character / NPC");

    private final double movementCost;
    private final String description;

    NodeType(double movementCost, String description) {
        this.movementCost = movementCost;
        this.description = description;
    }

    public double getMovementCost() {
        return movementCost;
    }

    public String getDescription() {
        return description;
    }

    public boolean isWalkable() {
        return this != WALL;
    }
}
