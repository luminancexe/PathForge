package pathfinding.ui;

import pathfinding.datastructures.CustomLinkedList;
import pathfinding.model.GridMap;
import pathfinding.model.Node;
import pathfinding.model.NodeType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

/**
 * Interactive Swing JPanel for visualizing the grid, obstacles, visited search tree,
 * shortest path, and game character NPC animation.
 */
public class GridPanel extends JPanel {

    public enum ToolMode {
        WALL, MUD, ERASE, SET_START, SET_TARGET
    }

    private final GridMap gridMap;
    private ToolMode currentTool = ToolMode.WALL;

    // Visual overlays
    private boolean[][] visitedOverlay;
    private boolean[][] pathOverlay;
    private Node characterPosition;

    private final Color COLOR_EMPTY = new Color(245, 247, 250);
    private final Color COLOR_GRID_LINE = new Color(220, 224, 230);
    private final Color COLOR_WALL = new Color(44, 62, 80);
    private final Color COLOR_MUD = new Color(211, 140, 68);
    private final Color COLOR_START = new Color(46, 204, 113);
    private final Color COLOR_TARGET = new Color(231, 76, 60);
    private final Color COLOR_VISITED = new Color(160, 218, 255, 180);
    private final Color COLOR_PATH = new Color(241, 196, 15);
    private final Color COLOR_CHARACTER = new Color(142, 68, 173);

    public GridPanel(GridMap gridMap) {
        this.gridMap = gridMap;
        this.visitedOverlay = new boolean[gridMap.getRows()][gridMap.getCols()];
        this.pathOverlay = new boolean[gridMap.getRows()][gridMap.getCols()];
        this.characterPosition = gridMap.getStartNode();

        setBackground(Color.WHITE);

        MouseAdapter mouseHandler = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                handleMouse(e);
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                handleMouse(e);
            }
        };

        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);
    }

    public void setToolMode(ToolMode tool) {
        this.currentTool = tool;
    }

    public ToolMode getToolMode() {
        return currentTool;
    }

    public void setVisited(int r, int c, boolean visited) {
        if (gridMap.isValid(r, c)) {
            visitedOverlay[r][c] = visited;
            repaint();
        }
    }

    public void setOnPath(int r, int c, boolean onPath) {
        if (gridMap.isValid(r, c)) {
            pathOverlay[r][c] = onPath;
            repaint();
        }
    }

    public void setCharacterPosition(Node node) {
        this.characterPosition = node;
        repaint();
    }

    public void clearVisualOverlays() {
        int rows = gridMap.getRows();
        int cols = gridMap.getCols();
        this.visitedOverlay = new boolean[rows][cols];
        this.pathOverlay = new boolean[rows][cols];
        this.characterPosition = gridMap.getStartNode();
        repaint();
    }

    private void handleMouse(MouseEvent e) {
        int cellWidth = getWidth() / gridMap.getCols();
        int cellHeight = getHeight() / gridMap.getRows();
        if (cellWidth <= 0 || cellHeight <= 0) return;

        int c = e.getX() / cellWidth;
        int r = e.getY() / cellHeight;

        if (!gridMap.isValid(r, c)) return;

        Node node = gridMap.getNode(r, c);

        switch (currentTool) {
            case WALL -> {
                if (node != gridMap.getStartNode() && node != gridMap.getTargetNode()) {
                    node.setBaseType(NodeType.WALL);
                }
            }
            case MUD -> {
                if (node != gridMap.getStartNode() && node != gridMap.getTargetNode()) {
                    node.setBaseType(NodeType.MUD);
                }
            }
            case ERASE -> {
                if (node != gridMap.getStartNode() && node != gridMap.getTargetNode()) {
                    node.setBaseType(NodeType.EMPTY);
                }
            }
            case SET_START -> {
                if (node.getBaseType() != NodeType.WALL && node != gridMap.getTargetNode()) {
                    gridMap.setStart(r, c);
                    characterPosition = gridMap.getStartNode();
                }
            }
            case SET_TARGET -> {
                if (node.getBaseType() != NodeType.WALL && node != gridMap.getStartNode()) {
                    gridMap.setTarget(r, c);
                }
            }
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int rows = gridMap.getRows();
        int cols = gridMap.getCols();

        int cellWidth = getWidth() / cols;
        int cellHeight = getHeight() / rows;

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                Node node = gridMap.getNode(r, c);
                int x = c * cellWidth;
                int y = r * cellHeight;

                // Determine base background
                Color cellColor = COLOR_EMPTY;
                if (node.getBaseType() == NodeType.WALL) {
                    cellColor = COLOR_WALL;
                } else if (node.getBaseType() == NodeType.MUD) {
                    cellColor = COLOR_MUD;
                }

                g2d.setColor(cellColor);
                g2d.fillRect(x, y, cellWidth, cellHeight);

                // Paint Visited Exploration Overlay
                if (visitedOverlay[r][c] && node.getBaseType() != NodeType.WALL) {
                    g2d.setColor(COLOR_VISITED);
                    g2d.fillRect(x + 1, y + 1, cellWidth - 2, cellHeight - 2);
                }

                // Paint Shortest Path Overlay
                if (pathOverlay[r][c] && node.getBaseType() != NodeType.WALL) {
                    g2d.setColor(COLOR_PATH);
                    g2d.fillRoundRect(x + 2, y + 2, cellWidth - 4, cellHeight - 4, 6, 6);
                }

                // Paint Start / Target Nodes
                if (node == gridMap.getStartNode()) {
                    g2d.setColor(COLOR_START);
                    g2d.fillOval(x + 2, y + 2, cellWidth - 4, cellHeight - 4);
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("SansSerif", Font.BOLD, Math.max(10, cellHeight / 2)));
                    drawCenteredString(g2d, "A", x, y, cellWidth, cellHeight);
                } else if (node == gridMap.getTargetNode()) {
                    g2d.setColor(COLOR_TARGET);
                    g2d.fillOval(x + 2, y + 2, cellWidth - 4, cellHeight - 4);
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("SansSerif", Font.BOLD, Math.max(10, cellHeight / 2)));
                    drawCenteredString(g2d, "B", x, y, cellWidth, cellHeight);
                }

                // Paint Character NPC
                if (characterPosition != null && characterPosition.getX() == r && characterPosition.getY() == c) {
                    g2d.setColor(COLOR_CHARACTER);
                    int pad = Math.max(3, cellWidth / 4);
                    g2d.fillOval(x + pad, y + pad, cellWidth - 2 * pad, cellHeight - 2 * pad);
                    g2d.setColor(Color.WHITE);
                    g2d.setFont(new Font("SansSerif", Font.BOLD, Math.max(9, cellHeight / 3)));
                    drawCenteredString(g2d, "NPC", x, y, cellWidth, cellHeight);
                }

                // Grid boundary
                g2d.setColor(COLOR_GRID_LINE);
                g2d.drawRect(x, y, cellWidth, cellHeight);
            }
        }
    }

    private void drawCenteredString(Graphics2D g, String text, int x, int y, int width, int height) {
        FontMetrics metrics = g.getFontMetrics();
        int sx = x + (width - metrics.stringWidth(text)) / 2;
        int sy = y + ((height - metrics.getHeight()) / 2) + metrics.getAscent();
        g.drawString(text, sx, sy);
    }
}
