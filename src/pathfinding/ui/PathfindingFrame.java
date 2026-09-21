package pathfinding.ui;

import pathfinding.algorithms.AStarAlgorithm;
import pathfinding.algorithms.DijkstraAlgorithm;
import pathfinding.algorithms.PathResult;
import pathfinding.algorithms.PathfindingAlgorithm;
import pathfinding.datastructures.CustomLinkedList;
import pathfinding.model.GridMap;
import pathfinding.model.Node;
import pathfinding.model.NodeType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.Iterator;
import java.util.Random;

/**
 * Main Swing GUI Window for Live Demonstration of the Game Pathfinding Engine.
 */
public class PathfindingFrame extends JFrame {

    private final GridMap gridMap;
    private final GridPanel gridPanel;

    private final PathfindingAlgorithm aStar = new AStarAlgorithm();
    private final PathfindingAlgorithm dijkstra = new DijkstraAlgorithm();

    private JComboBox<String> algoSelector;
    private JSlider speedSlider;
    private JLabel statusLabel;
    private JLabel nodesExploredLabel;
    private JLabel pathLengthLabel;
    private JLabel pathCostLabel;
    private JLabel execTimeLabel;
    private JTextArea comparisonArea;

    private Timer animationTimer;
    private PathResult lastResult;

    public PathfindingFrame(int rows, int cols) {
        super("PathForge: Graph-Based Pathfinding Engine");

        this.gridMap = new GridMap(rows, cols);
        this.gridPanel = new GridPanel(gridMap);

        initializeUI();
    }

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(8, 8));

        // Top Controls Panel
        JPanel topPanel = createTopPanel();
        add(topPanel, BorderLayout.NORTH);

        // Center Grid Canvas
        JPanel centerContainer = new JPanel(new BorderLayout());
        centerContainer.setBorder(new EmptyBorder(5, 10, 5, 5));
        centerContainer.add(gridPanel, BorderLayout.CENTER);
        add(centerContainer, BorderLayout.CENTER);

        // East Metrics & Control Dashboard
        JPanel eastPanel = createMetricsPanel();
        add(eastPanel, BorderLayout.EAST);

        // Bottom Legend / Status
        JPanel bottomPanel = createLegendPanel();
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        panel.setBackground(new Color(238, 242, 246));

        // Algorithm selection
        panel.add(new JLabel("Algorithm:"));
        algoSelector = new JComboBox<>(new String[]{"A* Search (Heuristic)", "Dijkstra (Uniform-Cost)", "Side-by-Side Comparison"});
        panel.add(algoSelector);

        // Preset Maps
        panel.add(new JLabel("Presets:"));
        JComboBox<String> presetBox = new JComboBox<>(new String[]{"Select Preset...", "Clear Grid", "Labyrinth Maze", "Mud Swamp", "Random Walls"});
        presetBox.addActionListener(e -> {
            stopAnimation();
            String selected = (String) presetBox.getSelectedItem();
            if ("Clear Grid".equals(selected)) {
                gridMap.clearAllObstacles();
                gridPanel.clearVisualOverlays();
            } else if ("Labyrinth Maze".equals(selected)) {
                gridMap.loadMazePreset();
                gridPanel.clearVisualOverlays();
            } else if ("Mud Swamp".equals(selected)) {
                gridMap.clearAllObstacles();
                for (int r = 5; r < gridMap.getRows() - 5; r++) {
                    for (int c = 5; c < gridMap.getCols() - 5; c++) {
                        gridMap.getNode(r, c).setBaseType(NodeType.MUD);
                    }
                }
                gridPanel.clearVisualOverlays();
            } else if ("Random Walls".equals(selected)) {
                gridMap.clearAllObstacles();
                Random rand = new Random();
                for (int r = 0; r < gridMap.getRows(); r++) {
                    for (int c = 0; c < gridMap.getCols(); c++) {
                        if (rand.nextDouble() < 0.25 && gridMap.getNode(r, c) != gridMap.getStartNode()
                                && gridMap.getNode(r, c) != gridMap.getTargetNode()) {
                            gridMap.getNode(r, c).setBaseType(NodeType.WALL);
                        }
                    }
                }
                gridPanel.clearVisualOverlays();
            }
        });
        panel.add(presetBox);

        // Brush tool selector
        panel.add(new JLabel("Tool:"));
        JComboBox<String> toolBox = new JComboBox<>(new String[]{"Wall Barrier", "Mud Terrain (4x Cost)", "Eraser", "Move Start (A)", "Move Target (B)"});
        toolBox.addActionListener(e -> {
            int idx = toolBox.getSelectedIndex();
            switch (idx) {
                case 0 -> gridPanel.setToolMode(GridPanel.ToolMode.WALL);
                case 1 -> gridPanel.setToolMode(GridPanel.ToolMode.MUD);
                case 2 -> gridPanel.setToolMode(GridPanel.ToolMode.ERASE);
                case 3 -> gridPanel.setToolMode(GridPanel.ToolMode.SET_START);
                case 4 -> gridPanel.setToolMode(GridPanel.ToolMode.SET_TARGET);
            }
        });
        panel.add(toolBox);

        // Speed Slider
        panel.add(new JLabel("Speed:"));
        speedSlider = new JSlider(2, 60, 15);
        speedSlider.setPreferredSize(new Dimension(85, 24));
        panel.add(speedSlider);

        // Architecture & System Info button
        JButton infoBtn = createStyledButton("ℹ Architecture", new Color(30, 110, 180), Color.WHITE);
        infoBtn.setToolTipText("View System Architecture & Algorithm Details");
        infoBtn.addActionListener(e -> showArchitectureInfoDialog());
        panel.add(infoBtn);

        return panel;
    }

    private void showArchitectureInfoDialog() {
        String info = """
            <html>
            <body style='width: 440px; font-family: sans-serif;'>
            <h2 style='color: #2980b9; margin-bottom: 2px;'>PathForge</h2>
            <p style='color: #555; margin-top: 0;'><b>A Graph-Based Pathfinding Engine for Interactive Game Environments</b></p>
            <hr>
            <p><b>1. Core Data Structures:</b><br>
            • <b>GridMap (2D Array):</b> Spatial layout & O(1) coordinate lookup.<br>
            • <b>MinHeap&lt;Node&gt; (Manual):</b> Binary min-heap for priority queue frontier (O(log n) insert/extract).<br>
            • <b>CustomLinkedList&lt;Node&gt; (Manual):</b> Singly linked list for O(1) prepending path reconstruction.
            </p>
            <p><b>2. Pathfinding Search Algorithms:</b><br>
            • <b>Dijkstra's Algorithm:</b> Uniform-cost search exploring outward by cumulative g-cost.<br>
            • <b>A* Search Algorithm:</b> Heuristic-guided search (f = g + h) with Manhattan distance.
            </p>
            <p><b>3. Benchmark Insight:</b><br>
            A* evaluates <b>60%–90% fewer nodes</b> than Dijkstra while guaranteeing the exact same optimal path!
            </p>
            </body>
            </html>
            """;
        JOptionPane.showMessageDialog(this, info, "PathForge - Architecture & Algorithms", JOptionPane.INFORMATION_MESSAGE);
    }

    private JPanel createMetricsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(5, 5, 5, 10));
        panel.setPreferredSize(new Dimension(280, 0));

        // Action Buttons Box
        JPanel actionBox = new JPanel(new GridLayout(4, 1, 6, 6));
        actionBox.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(BorderFactory.createLineBorder(new Color(200, 205, 215)), "Actions"),
                new EmptyBorder(6, 6, 6, 6)
        ));

        JButton runInstantBtn = createStyledButton("⚡ Instant Solve", new Color(31, 110, 185), Color.WHITE);
        runInstantBtn.addActionListener(e -> runInstant());
        actionBox.add(runInstantBtn);

        JButton runAnimateBtn = createStyledButton("▶ Animate Wavefront", new Color(35, 140, 75), Color.WHITE);
        runAnimateBtn.addActionListener(e -> runAnimated());
        actionBox.add(runAnimateBtn);

        JButton moveNpcBtn = createStyledButton("🚶 Walk Character (NPC)", new Color(125, 55, 160), Color.WHITE);
        moveNpcBtn.addActionListener(e -> animateCharacter());
        actionBox.add(moveNpcBtn);

        JButton clearBtn = createStyledButton("🔄 Clear Solution", new Color(240, 243, 246), new Color(40, 45, 55));
        clearBtn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 205, 215), 1, true),
                BorderFactory.createEmptyBorder(7, 10, 7, 10)
        ));
        clearBtn.addActionListener(e -> {
            stopAnimation();
            gridMap.resetSearchData();
            gridPanel.clearVisualOverlays();
            updateMetrics(null);
        });
        actionBox.add(clearBtn);

        panel.add(actionBox);
        panel.add(Box.createVerticalStrut(10));

        // Metrics Dashboard Box
        JPanel statsBox = new JPanel(new GridLayout(6, 1, 5, 5));
        statsBox.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder(BorderFactory.createLineBorder(new Color(200, 205, 215)), "DSA Performance Metrics"),
                new EmptyBorder(6, 8, 6, 8)
        ));

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 12);
        Color labelColor = new Color(40, 45, 55);

        statusLabel = new JLabel("Status: Ready");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        statusLabel.setForeground(new Color(30, 110, 180));
        statsBox.add(statusLabel);

        nodesExploredLabel = new JLabel("Nodes Explored: -");
        nodesExploredLabel.setFont(labelFont);
        nodesExploredLabel.setForeground(labelColor);
        statsBox.add(nodesExploredLabel);

        pathLengthLabel = new JLabel("Path Length (Steps): -");
        pathLengthLabel.setFont(labelFont);
        pathLengthLabel.setForeground(labelColor);
        statsBox.add(pathLengthLabel);

        pathCostLabel = new JLabel("Total Movement Cost: -");
        pathCostLabel.setFont(labelFont);
        pathCostLabel.setForeground(labelColor);
        statsBox.add(pathCostLabel);

        execTimeLabel = new JLabel("Execution Time: -");
        execTimeLabel.setFont(labelFont);
        execTimeLabel.setForeground(labelColor);
        statsBox.add(execTimeLabel);

        panel.add(statsBox);
        panel.add(Box.createVerticalStrut(10));

        // Comparison Output
        JPanel compBox = new JPanel(new BorderLayout());
        compBox.setBorder(new TitledBorder("A* vs Dijkstra Benchmark"));
        comparisonArea = new JTextArea(8, 20);
        comparisonArea.setEditable(false);
        comparisonArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
        comparisonArea.setText("Select 'Side-by-Side Comparison'\nand click Instant Solve to\nbenchmark search efficiency.");
        compBox.add(new JScrollPane(comparisonArea), BorderLayout.CENTER);

        panel.add(compBox);

        return panel;
    }

    private JPanel createLegendPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 6));
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(220, 224, 230)));

        panel.add(createLegendItem("Start (A)", new Color(46, 204, 113)));
        panel.add(createLegendItem("Target (B)", new Color(231, 76, 60)));
        panel.add(createLegendItem("Wall", new Color(44, 62, 80)));
        panel.add(createLegendItem("Mud (4x Cost)", new Color(211, 140, 68)));
        panel.add(createLegendItem("Explored Wavefront", new Color(160, 218, 255)));
        panel.add(createLegendItem("Optimal Path", new Color(241, 196, 15)));
        panel.add(createLegendItem("NPC Character", new Color(142, 68, 173)));

        return panel;
    }

    private JPanel createLegendItem(String text, Color color) {
        JPanel item = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        item.setOpaque(false);
        JPanel box = new JPanel();
        box.setPreferredSize(new Dimension(14, 14));
        box.setBackground(color);
        box.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
        item.add(box);
        item.add(new JLabel(text));
        return item;
    }

    private void runInstant() {
        stopAnimation();
        gridPanel.clearVisualOverlays();

        int selectedIdx = algoSelector.getSelectedIndex();
        if (selectedIdx == 2) {
            // Side-by-Side Comparison
            runComparisonBenchmark();
            return;
        }

        PathfindingAlgorithm algorithm = (selectedIdx == 0) ? aStar : dijkstra;
        lastResult = algorithm.findPath(gridMap, gridMap.getStartNode(), gridMap.getTargetNode());

        // Overlay visited nodes
        for (Node node : lastResult.getVisitedOrder()) {
            gridPanel.setVisited(node.getX(), node.getY(), true);
        }

        // Overlay shortest path
        for (Node node : lastResult.getPath()) {
            gridPanel.setOnPath(node.getX(), node.getY(), true);
        }

        updateMetrics(lastResult);
    }

    private void runAnimated() {
        stopAnimation();
        gridPanel.clearVisualOverlays();

        int selectedIdx = algoSelector.getSelectedIndex();
        PathfindingAlgorithm algorithm = (selectedIdx == 1) ? dijkstra : aStar;

        lastResult = algorithm.findPath(gridMap, gridMap.getStartNode(), gridMap.getTargetNode());
        updateMetrics(lastResult);

        Iterator<Node> visitedIterator = lastResult.getVisitedOrder().iterator();
        Iterator<Node> pathIterator = lastResult.getPath().iterator();

        int delay = speedSlider.getValue();
        animationTimer = new Timer(delay, null);
        animationTimer.addActionListener(e -> {
            if (visitedIterator.hasNext()) {
                Node n = visitedIterator.next();
                gridPanel.setVisited(n.getX(), n.getY(), true);
            } else if (pathIterator.hasNext()) {
                Node p = pathIterator.next();
                gridPanel.setOnPath(p.getX(), p.getY(), true);
            } else {
                animationTimer.stop();
            }
        });
        animationTimer.start();
    }

    private void animateCharacter() {
        if (lastResult == null || !lastResult.isPathFound() || lastResult.getPath().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please find an optimal path first!", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        stopAnimation();
        Iterator<Node> pathIterator = lastResult.getPath().iterator();

        animationTimer = new Timer(Math.max(40, speedSlider.getValue() * 3), null);
        animationTimer.addActionListener(e -> {
            if (pathIterator.hasNext()) {
                Node step = pathIterator.next();
                gridPanel.setCharacterPosition(step);
            } else {
                animationTimer.stop();
            }
        });
        animationTimer.start();
    }

    private void runComparisonBenchmark() {
        PathResult resAStar = aStar.findPath(gridMap, gridMap.getStartNode(), gridMap.getTargetNode());
        PathResult resDijkstra = dijkstra.findPath(gridMap, gridMap.getStartNode(), gridMap.getTargetNode());

        // Overlay A* result visually
        for (Node node : resAStar.getVisitedOrder()) {
            gridPanel.setVisited(node.getX(), node.getY(), true);
        }
        for (Node node : resAStar.getPath()) {
            gridPanel.setOnPath(node.getX(), node.getY(), true);
        }

        this.lastResult = resAStar;
        updateMetrics(resAStar);

        // Format benchmark comparison table
        int savings = 0;
        if (resDijkstra.getNodesExplored() > 0) {
            savings = (int) Math.round((1.0 - ((double) resAStar.getNodesExplored() / resDijkstra.getNodesExplored())) * 100.0);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== BENCHMARK RESULTS ===\n");
        sb.append(String.format("%-10s | %-6s | %-6s\n", "Metric", "A*", "Dijkstra"));
        sb.append("----------------------------\n");
        sb.append(String.format("Explored   | %-6d | %-6d\n", resAStar.getNodesExplored(), resDijkstra.getNodesExplored()));
        sb.append(String.format("Steps      | %-6d | %-6d\n", resAStar.getPathLength(), resDijkstra.getPathLength()));
        sb.append(String.format("Cost       | %-6.1f | %-6.1f\n", resAStar.getTotalCost(), resDijkstra.getTotalCost()));
        sb.append(String.format("Time (µs)  | %-6.0f | %-6.0f\n", resAStar.getExecutionTimeMicro(), resDijkstra.getExecutionTimeMicro()));
        sb.append("----------------------------\n");
        sb.append(String.format("A* evaluated %d%% FEWER\nnodes via heuristic guidance!\n", Math.max(0, savings)));

        comparisonArea.setText(sb.toString());
    }

    private void updateMetrics(PathResult result) {
        if (result == null) {
            statusLabel.setText("Status: Cleared");
            nodesExploredLabel.setText("Nodes Explored: -");
            pathLengthLabel.setText("Path Length (Steps): -");
            pathCostLabel.setText("Total Movement Cost: -");
            execTimeLabel.setText("Execution Time: -");
            return;
        }

        if (result.isPathFound()) {
            statusLabel.setText("Status: Optimal Path Found! (" + result.getAlgorithmName() + ")");
            statusLabel.setForeground(new Color(39, 174, 96));
        } else {
            statusLabel.setText("Status: Target Unreachable (Blocked)");
            statusLabel.setForeground(new Color(192, 57, 43));
        }

        nodesExploredLabel.setText(String.format("Nodes Explored: %d", result.getNodesExplored()));
        pathLengthLabel.setText(String.format("Path Length: %d steps", result.getPathLength()));
        pathCostLabel.setText(String.format("Total Movement Cost: %.1f", result.getTotalCost()));
        execTimeLabel.setText(String.format("Execution Time: %.2f µs (%.4f ms)",
                result.getExecutionTimeMicro(), result.getExecutionTimeMs()));
    }

    private void stopAnimation() {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(bgColor);
        btn.setForeground(fgColor);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 1, true),
                BorderFactory.createEmptyBorder(7, 12, 7, 12)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(blend(bgColor, Color.WHITE, 0.18f));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(bgColor);
            }
        });
        return btn;
    }

    private Color blend(Color c1, Color c2, float ratio) {
        float r = (c1.getRed() * (1 - ratio)) + (c2.getRed() * ratio);
        float g = (c1.getGreen() * (1 - ratio)) + (c2.getGreen() * ratio);
        float b = (c1.getBlue() * (1 - ratio)) + (c2.getBlue() * ratio);
        return new Color(Math.round(r), Math.round(g), Math.round(b));
    }
}
