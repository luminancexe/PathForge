package pathfinding.algorithms;

import pathfinding.datastructures.CustomLinkedList;
import pathfinding.datastructures.MinHeap;
import pathfinding.model.GridMap;
import pathfinding.model.Node;

/**
 * Algorithm 1: Dijkstra's Algorithm (Uniform-Cost Search).
 * Explores grid nodes strictly in order of cumulative path distance g(n).
 * Guarantees shortest path across weighted and unweighted terrain.
 */
public class DijkstraAlgorithm implements PathfindingAlgorithm {

    @Override
    public String getName() {
        return "Dijkstra's Algorithm";
    }

    @Override
    public PathResult findPath(GridMap map, Node start, Node target) {
        long startTime = System.nanoTime();

        map.resetSearchData();
        CustomLinkedList<Node> visitedOrder = new CustomLinkedList<>();

        if (start == null || target == null || !start.isWalkable() || !target.isWalkable()) {
            long elapsed = System.nanoTime() - startTime;
            return PathResult.failure(getName(), visitedOrder, 0, elapsed);
        }

        // Boundary case: Start is already Target
        if (start.equals(target)) {
            CustomLinkedList<Node> singleNodePath = new CustomLinkedList<>();
            singleNodePath.addLast(start);
            visitedOrder.addLast(start);
            long elapsed = System.nanoTime() - startTime;
            return new PathResult(getName(), true, singleNodePath, visitedOrder, 1, 0.0, elapsed);
        }

        MinHeap<Node> openSet = new MinHeap<>();

        start.setGCost(0.0);
        start.setFCost(0.0);
        openSet.insert(start);
        start.setInOpenSet(true);

        while (!openSet.isEmpty()) {
            Node current = openSet.extractMin();
            current.setInOpenSet(false);

            if (current.isInClosedSet()) {
                continue;
            }
            current.setInClosedSet(true);
            visitedOrder.addLast(current);

            // Target reached
            if (current.equals(target)) {
                CustomLinkedList<Node> path = reconstructPath(target);
                long elapsed = System.nanoTime() - startTime;
                return new PathResult(getName(), true, path, visitedOrder, visitedOrder.size(), target.getGCost(), elapsed);
            }

            // Expand adjacent neighbors
            for (Node neighbor : map.getNeighbors(current)) {
                if (neighbor.isInClosedSet() || !neighbor.isWalkable()) {
                    continue;
                }

                double tentativeGCost = current.getGCost() + neighbor.getTerrainCost();

                if (tentativeGCost < neighbor.getGCost()) {
                    neighbor.setGCost(tentativeGCost);
                    neighbor.setFCost(tentativeGCost); // In Dijkstra, fCost = gCost (no heuristic)
                    neighbor.setParent(current);

                    if (!neighbor.isInOpenSet()) {
                        neighbor.setInOpenSet(true);
                        openSet.insert(neighbor);
                    } else {
                        openSet.reHeapify(neighbor);
                    }
                }
            }
        }

        // No path exists
        long elapsed = System.nanoTime() - startTime;
        return PathResult.failure(getName(), visitedOrder, visitedOrder.size(), elapsed);
    }

    private CustomLinkedList<Node> reconstructPath(Node target) {
        CustomLinkedList<Node> path = new CustomLinkedList<>();
        Node curr = target;
        while (curr != null) {
            path.addFirst(curr);
            curr = curr.getParent();
        }
        return path;
    }
}
