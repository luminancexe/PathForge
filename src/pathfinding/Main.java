package pathfinding;

import pathfinding.algorithms.AStarAlgorithm;
import pathfinding.algorithms.DijkstraAlgorithm;
import pathfinding.algorithms.PathResult;
import pathfinding.model.GridMap;
import pathfinding.ui.ConsoleVisualizer;
import pathfinding.ui.PathfindingFrame;

import javax.swing.*;
import java.awt.*;

/**
 * Main application launcher for PathForge:
 * A Graph-Based Pathfinding Engine for Interactive Game Environments.
 */
public class Main {

    public static void main(String[] args) {
        boolean forceCli = false;
        for (String arg : args) {
            if ("--cli".equalsIgnoreCase(arg) || "-c".equalsIgnoreCase(arg)) {
                forceCli = true;
                break;
            }
        }

        if (forceCli || GraphicsEnvironment.isHeadless()) {
            runConsoleDemo();
        } else {
            SwingUtilities.invokeLater(() -> {
                try {
                    // Use platform native Look and Feel for modern aesthetic
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ignored) {}

                PathfindingFrame frame = new PathfindingFrame(22, 32);
                frame.setVisible(true);
            });
        }
    }

    public static void runConsoleDemo() {
        System.out.println("==========================================================================");
        System.out.println("  PathForge: Graph-Based Pathfinding Engine for Game Environments         ");
        System.out.println("==========================================================================");

        GridMap map = new GridMap(15, 20);
        map.loadMazePreset();

        System.out.println("\n[1] Initializing Labyrinth Map with Obstacles and Mud:");
        ConsoleVisualizer.printGrid(map, null);

        AStarAlgorithm aStar = new AStarAlgorithm();
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm();

        System.out.println("\n[2] Executing Dijkstra's Algorithm (Uniform Cost Search)...");
        PathResult dijkstraResult = dijkstra.findPath(map, map.getStartNode(), map.getTargetNode());
        ConsoleVisualizer.printGrid(map, dijkstraResult);

        System.out.println("\n[3] Executing A* Search Algorithm (Heuristic Search)...");
        PathResult astarResult = aStar.findPath(map, map.getStartNode(), map.getTargetNode());
        ConsoleVisualizer.printGrid(map, astarResult);

        System.out.println("\n[4] Comparative Performance Benchmark:");
        ConsoleVisualizer.printMetricsTable(astarResult, dijkstraResult);

        System.out.println("Note: Launch without '--cli' on desktop to open the interactive Swing GUI.");
    }
}
