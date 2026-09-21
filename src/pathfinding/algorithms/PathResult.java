package pathfinding.algorithms;

import pathfinding.datastructures.CustomLinkedList;
import pathfinding.model.Node;

/**
 * Encapsulates the output and benchmark statistics of a pathfinding run.
 */
public class PathResult {

    private final String algorithmName;
    private final boolean pathFound;
    private final CustomLinkedList<Node> path;
    private final CustomLinkedList<Node> visitedOrder;
    private final int nodesExplored;
    private final double totalCost;
    private final long executionTimeNano;

    public PathResult(String algorithmName, boolean pathFound,
                      CustomLinkedList<Node> path,
                      CustomLinkedList<Node> visitedOrder,
                      int nodesExplored, double totalCost,
                      long executionTimeNano) {
        this.algorithmName = algorithmName;
        this.pathFound = pathFound;
        this.path = path != null ? path : new CustomLinkedList<>();
        this.visitedOrder = visitedOrder != null ? visitedOrder : new CustomLinkedList<>();
        this.nodesExplored = nodesExplored;
        this.totalCost = totalCost;
        this.executionTimeNano = executionTimeNano;
    }

    public static PathResult failure(String algorithmName,
                                    CustomLinkedList<Node> visitedOrder,
                                    int nodesExplored,
                                    long executionTimeNano) {
        return new PathResult(algorithmName, false, new CustomLinkedList<>(), visitedOrder, nodesExplored, 0.0, executionTimeNano);
    }

    public String getAlgorithmName() {
        return algorithmName;
    }

    public boolean isPathFound() {
        return pathFound;
    }

    public CustomLinkedList<Node> getPath() {
        return path;
    }

    public CustomLinkedList<Node> getVisitedOrder() {
        return visitedOrder;
    }

    public int getNodesExplored() {
        return nodesExplored;
    }

    public double getTotalCost() {
        return totalCost;
    }

    public long getExecutionTimeNano() {
        return executionTimeNano;
    }

    public double getExecutionTimeMicro() {
        return executionTimeNano / 1_000.0;
    }

    public double getExecutionTimeMs() {
        return executionTimeNano / 1_000_000.0;
    }

    public int getPathLength() {
        return path.size();
    }
}
