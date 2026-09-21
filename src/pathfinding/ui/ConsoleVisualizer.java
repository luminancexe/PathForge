package pathfinding.ui;

import pathfinding.algorithms.PathResult;
import pathfinding.datastructures.CustomLinkedList;
import pathfinding.model.GridMap;
import pathfinding.model.Node;
import pathfinding.model.NodeType;

/**
 * Text-based console visualizer supporting colored ANSI output and benchmark tables.
 */
public class ConsoleVisualizer {

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";  // Start
    public static final String ANSI_RED = "\u001B[31m";    // Target
    public static final String ANSI_YELLOW = "\u001B[33m"; // Path
    public static final String ANSI_BLUE = "\u001B[34m";   // Visited
    public static final String ANSI_GRAY = "\u001B[90m";   // Wall
    public static final String ANSI_CYAN = "\u001B[36m";   // Mud

    public static void printGrid(GridMap map, PathResult result) {
        int rows = map.getRows();
        int cols = map.getCols();

        boolean[][] isPath = new boolean[rows][cols];
        boolean[][] isVisited = new boolean[rows][cols];

        if (result != null) {
            for (Node n : result.getPath()) {
                isPath[n.getX()][n.getY()] = true;
            }
            for (Node n : result.getVisitedOrder()) {
                isVisited[n.getX()][n.getY()] = true;
            }
        }

        System.out.println("+" + "---".repeat(cols) + "+");
        for (int r = 0; r < rows; r++) {
            System.out.print("|");
            for (int c = 0; c < cols; c++) {
                Node node = map.getNode(r, c);
                if (node == map.getStartNode()) {
                    System.out.print(ANSI_GREEN + " A " + ANSI_RESET);
                } else if (node == map.getTargetNode()) {
                    System.out.print(ANSI_RED + " B " + ANSI_RESET);
                } else if (isPath[r][c]) {
                    System.out.print(ANSI_YELLOW + " * " + ANSI_RESET);
                } else if (node.getBaseType() == NodeType.WALL) {
                    System.out.print(ANSI_GRAY + "###" + ANSI_RESET);
                } else if (node.getBaseType() == NodeType.MUD) {
                    System.out.print(ANSI_CYAN + " ~ " + ANSI_RESET);
                } else if (isVisited[r][c]) {
                    System.out.print(ANSI_BLUE + " . " + ANSI_RESET);
                } else {
                    System.out.print("   ");
                }
            }
            System.out.println("|");
        }
        System.out.println("+" + "---".repeat(cols) + "+");
    }

    public static void printMetricsTable(PathResult astar, PathResult dijkstra) {
        System.out.println("\n=======================================================");
        System.out.println("          ALGORITHM BENCHMARK PERFORMANCE COMPARISON    ");
        System.out.println("=======================================================");
        System.out.printf("%-22s | %-14s | %-14s%n", "Metric", "A* Search", "Dijkstra");
        System.out.println("-------------------------------------------------------");
        System.out.printf("%-22s | %-14s | %-14s%n", "Path Status",
                astar.isPathFound() ? "FOUND" : "BLOCKED",
                dijkstra.isPathFound() ? "FOUND" : "BLOCKED");
        System.out.printf("%-22s | %-14d | %-14d%n", "Nodes Explored",
                astar.getNodesExplored(), dijkstra.getNodesExplored());
        System.out.printf("%-22s | %-14d | %-14d%n", "Path Length (Steps)",
                astar.getPathLength(), dijkstra.getPathLength());
        System.out.printf("%-22s | %-14.1f | %-14.1f%n", "Total Movement Cost",
                astar.getTotalCost(), dijkstra.getTotalCost());
        System.out.printf("%-22s | %-14.2f | %-14.2f%n", "Execution Time (µs)",
                astar.getExecutionTimeMicro(), dijkstra.getExecutionTimeMicro());
        System.out.println("-------------------------------------------------------");

        if (dijkstra.getNodesExplored() > 0) {
            double reduction = (1.0 - ((double) astar.getNodesExplored() / dijkstra.getNodesExplored())) * 100.0;
            System.out.printf("Search Space Reduction by Heuristic: %.1f%%%n", Math.max(0.0, reduction));
        }
        System.out.println("=======================================================\n");
    }
}
