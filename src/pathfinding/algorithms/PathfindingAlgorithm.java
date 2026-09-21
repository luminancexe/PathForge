package pathfinding.algorithms;

import pathfinding.model.GridMap;
import pathfinding.model.Node;

/**
 * Common interface for pathfinding algorithms.
 */
public interface PathfindingAlgorithm {

    /**
     * Returns the human-readable name of the algorithm.
     */
    String getName();

    /**
     * Executes the pathfinding search from start to target on the given grid.
     */
    PathResult findPath(GridMap map, Node start, Node target);
}
