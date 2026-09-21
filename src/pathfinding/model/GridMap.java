package pathfinding.model;

import pathfinding.datastructures.CustomLinkedList;

/**
 * Data Structure 1: 2D Array Representation of the Game Map.
 * Manages grid layout, terrain costs, boundary checks, and neighborhood connectivity.
 */
public class GridMap {

    private final int rows;
    private final int cols;
    private final Node[][] grid;

    private Node startNode;
    private Node targetNode;

    public GridMap(int rows, int cols) {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Grid dimensions must be positive.");
        }
        this.rows = rows;
        this.cols = cols;
        this.grid = new Node[rows][cols];

        initializeGrid();
    }

    private void initializeGrid() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = new Node(r, c, NodeType.EMPTY);
            }
        }
        // Set default start and target
        setStart(1, 1);
        setTarget(rows - 2, cols - 2);
    }

    public boolean isValid(int r, int c) {
        return r >= 0 && r < rows && c >= 0 && c < cols;
    }

    public Node getNode(int r, int c) {
        if (!isValid(r, c)) {
            throw new IndexOutOfBoundsException(String.format("Coordinates (%d, %d) out of grid bounds (%d, %d)", r, c, rows, cols));
        }
        return grid[r][c];
    }

    public void setStart(int r, int c) {
        if (!isValid(r, c)) {
            throw new IllegalArgumentException("Start coordinates out of bounds: (" + r + ", " + c + ")");
        }
        if (startNode != null) {
            startNode.setBaseType(NodeType.EMPTY);
        }
        startNode = grid[r][c];
        startNode.setBaseType(NodeType.START);
    }

    public void setTarget(int r, int c) {
        if (!isValid(r, c)) {
            throw new IllegalArgumentException("Target coordinates out of bounds: (" + r + ", " + c + ")");
        }
        if (targetNode != null) {
            targetNode.setBaseType(NodeType.EMPTY);
        }
        targetNode = grid[r][c];
        targetNode.setBaseType(NodeType.TARGET);
    }

    public Node getStartNode() {
        return startNode;
    }

    public Node getTargetNode() {
        return targetNode;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    /**
     * Retrieves valid traversable 4-directional cardinal neighbors.
     * Time Complexity: O(1)
     */
    public CustomLinkedList<Node> getNeighbors(Node node) {
        CustomLinkedList<Node> neighbors = new CustomLinkedList<>();
        if (node == null) return neighbors;

        int r = node.getX();
        int c = node.getY();

        // 4 Cardinal Directions: Up, Right, Down, Left
        int[][] directions = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};

        for (int[] dir : directions) {
            int nr = r + dir[0];
            int nc = c + dir[1];
            if (isValid(nr, nc)) {
                Node neighbor = grid[nr][nc];
                if (neighbor.isWalkable()) {
                    neighbors.addLast(neighbor);
                }
            }
        }
        return neighbors;
    }

    /**
     * Resets algorithm search data (gCost, hCost, visited markers, parent pointers)
     * while preserving wall barriers and terrain weights.
     */
    public void resetSearchData() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c].resetSearchState();
            }
        }
    }

    /**
     * Clears all obstacles and mud, reverting to empty grid.
     */
    public void clearAllObstacles() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (grid[r][c] != startNode && grid[r][c] != targetNode) {
                    grid[r][c].setBaseType(NodeType.EMPTY);
                }
            }
        }
        resetSearchData();
    }

    /**
     * Generates a sample labyrinth obstacle course for testing and live demonstrations.
     */
    public void loadMazePreset() {
        clearAllObstacles();

        // Add interesting wall maze corridors
        for (int c = 3; c < cols - 3; c++) {
            if (c != cols / 2) {
                grid[rows / 3][c].setBaseType(NodeType.WALL);
                grid[2 * rows / 3][c].setBaseType(NodeType.WALL);
            }
        }
        for (int r = 2; r < rows - 2; r++) {
            if (r != rows / 2) {
                grid[r][cols / 2].setBaseType(NodeType.WALL);
            }
        }

        // Add mud patches to demonstrate weighted cost avoidance
        for (int r = rows / 3 + 1; r < 2 * rows / 3; r++) {
            for (int c = cols / 2 + 2; c < cols / 2 + 5 && c < cols - 1; c++) {
                if (grid[r][c].getBaseType() == NodeType.EMPTY) {
                    grid[r][c].setBaseType(NodeType.MUD);
                }
            }
        }

        resetSearchData();
    }
}
