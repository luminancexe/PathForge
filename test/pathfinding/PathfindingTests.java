package pathfinding;

import pathfinding.algorithms.AStarAlgorithm;
import pathfinding.algorithms.DijkstraAlgorithm;
import pathfinding.algorithms.PathResult;
import pathfinding.datastructures.CustomLinkedList;
import pathfinding.datastructures.MinHeap;
import pathfinding.model.GridMap;
import pathfinding.model.Node;
import pathfinding.model.NodeType;

import java.util.NoSuchElementException;

/**
 * Automated Test Suite for PathForge:
 * A Graph-Based Pathfinding Engine for Interactive Game Environments.
 *
 * Comprehensive test coverage for algorithms, data structures, and edge cases:
 * 1. Normal Data
 * 2. Empty Structure Handling
 * 3. Invalid Input Handling
 * 4. Duplicate / Frontier Update Values
 * 5. Boundary Cases
 */
public class PathfindingTests {

    private static int testsRun = 0;
    private static int testsPassed = 0;

    public static void main(String[] args) {
        System.out.println("==========================================================================");
        System.out.println("            PathForge: Automated Pathfinding Test Suite                   ");
        System.out.println("==========================================================================");

        runTest("1. Normal Data Test - Clear Grid Shortest Path", PathfindingTests::testNormalDataClearGrid);
        runTest("1. Normal Data Test - Weighted Terrain (Mud avoidance)", PathfindingTests::testNormalDataWeightedTerrain);
        runTest("2. Empty Structure Test - MinHeap underflow handling", PathfindingTests::testEmptyMinHeap);
        runTest("2. Empty Structure Test - CustomLinkedList underflow handling", PathfindingTests::testEmptyLinkedList);
        runTest("2. Operation Test - MinHeap arbitrary deletion", PathfindingTests::testMinHeapDeletion);
        runTest("2. Operation Test - CustomLinkedList middle/tail deletion", PathfindingTests::testLinkedListDeletion);
        runTest("3. Invalid Input Test - Negative / out-of-bounds dimensions", PathfindingTests::testInvalidDimensions);
        runTest("3. Invalid Input Test - Out-of-bounds coordinate queries", PathfindingTests::testOutOfBoundsCoordinates);
        runTest("3. Invalid Input Test - Start or Target on Wall obstacle", PathfindingTests::testEndpointsOnObstacles);
        runTest("4. Duplicate / Priority Update Test - MinHeap reHeapify", PathfindingTests::testMinHeapReHeapify);
        runTest("4. Duplicate / Multiple Path Discovery Test - Lower cost selection", PathfindingTests::testAlternativeLowerCostPath);
        runTest("5. Boundary Case Test - Goal completely blocked by walls", PathfindingTests::testUnreachableTarget);
        runTest("5. Boundary Case Test - Start is Target (zero distance)", PathfindingTests::testStartIsTarget);
        runTest("5. Boundary Case Test - Minimum 2x2 grid navigation", PathfindingTests::testMinimalGrid);

        System.out.println("\n---------------------------------------------------------------");
        System.out.printf("Test Summary: %d / %d tests passed successfully. (%s)%n",
                testsPassed, testsRun, (testsPassed == testsRun ? "ALL PASS" : "FAILURES DETECTED"));
        System.out.println("---------------------------------------------------------------");

        if (testsPassed != testsRun) {
            System.exit(1);
        }
    }

    private static void runTest(String testName, Runnable test) {
        testsRun++;
        try {
            test.run();
            testsPassed++;
            System.out.printf("[PASS] %s%n", testName);
        } catch (Throwable t) {
            System.out.printf("[FAIL] %s: %s%n", testName, t.getMessage());
            t.printStackTrace(System.out);
        }
    }

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (expected == null && actual == null) return;
        if (expected == null || !expected.equals(actual)) {
            throw new AssertionError(message + " (Expected: " + expected + ", Actual: " + actual + ")");
        }
    }

    // --- Category 1: Normal Data ---

    private static void testNormalDataClearGrid() {
        GridMap map = new GridMap(10, 10);
        map.setStart(0, 0);
        map.setTarget(9, 9);

        AStarAlgorithm aStar = new AStarAlgorithm();
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm();

        PathResult resA = aStar.findPath(map, map.getStartNode(), map.getTargetNode());
        PathResult resD = dijkstra.findPath(map, map.getStartNode(), map.getTargetNode());

        assertTrue(resA.isPathFound(), "A* should find path on clear grid");
        assertTrue(resD.isPathFound(), "Dijkstra should find path on clear grid");

        // Manhattan distance from (0,0) to (9,9) is 18. Path has 19 nodes (18 steps).
        assertEquals(19, resA.getPathLength(), "Path length should be 19 nodes");
        assertEquals(19, resD.getPathLength(), "Dijkstra path length should be 19 nodes");
        assertEquals(18.0, resA.getTotalCost(), "Cost should be 18.0");
        assertEquals(18.0, resD.getTotalCost(), "Dijkstra cost should match 18.0");

        // A* should explore fewer nodes than Dijkstra on an open grid
        assertTrue(resA.getNodesExplored() <= resD.getNodesExplored(),
                "A* should explore equal or fewer nodes than Dijkstra");
    }

    private static void testNormalDataWeightedTerrain() {
        // Create 5x5 grid with mud on direct path
        GridMap map = new GridMap(5, 5);
        map.setStart(2, 0);
        map.setTarget(2, 4);

        // Put expensive mud in column 2, rows 1, 2, 3
        map.getNode(2, 1).setBaseType(NodeType.MUD);
        map.getNode(2, 2).setBaseType(NodeType.MUD);
        map.getNode(2, 3).setBaseType(NodeType.MUD);

        AStarAlgorithm aStar = new AStarAlgorithm();
        PathResult res = aStar.findPath(map, map.getStartNode(), map.getTargetNode());

        assertTrue(res.isPathFound(), "Path should be found around mud");
        // Walking around the mud (e.g. via row 0 or 4) costs 6 * 1.0 = 6.0,
        // whereas walking through 3 mud cells costs 3 * 4.0 + 1 = 13.0.
        assertTrue(res.getTotalCost() < 10.0, "Path should circumvent expensive mud terrain");
    }

    // --- Category 2: Empty Structure Handling ---

    private static void testEmptyMinHeap() {
        MinHeap<Integer> heap = new MinHeap<>();
        assertTrue(heap.isEmpty(), "Heap should initially be empty");
        assertEquals(0, heap.size(), "Heap size should be 0");

        boolean threwOnPeek = false;
        try {
            heap.peek();
        } catch (NoSuchElementException e) {
            threwOnPeek = true;
        }
        assertTrue(threwOnPeek, "Peeking empty heap must throw NoSuchElementException");

        boolean threwOnExtract = false;
        try {
            heap.extractMin();
        } catch (NoSuchElementException e) {
            threwOnExtract = true;
        }
        assertTrue(threwOnExtract, "Extracting from empty heap must throw NoSuchElementException");

        // Insert and clear
        heap.insert(42);
        heap.insert(10);
        assertEquals(2, heap.size(), "Heap size should be 2");
        heap.clear();
        assertTrue(heap.isEmpty(), "Heap should be empty after clear()");
    }

    private static void testEmptyLinkedList() {
        CustomLinkedList<String> list = new CustomLinkedList<>();
        assertTrue(list.isEmpty(), "List should initially be empty");
        assertEquals(0, list.size(), "List size should be 0");

        boolean threwOnRemove = false;
        try {
            list.removeFirst();
        } catch (NoSuchElementException e) {
            threwOnRemove = true;
        }
        assertTrue(threwOnRemove, "removeFirst on empty list must throw NoSuchElementException");

        boolean threwOnGet = false;
        try {
            list.get(0);
        } catch (IndexOutOfBoundsException e) {
            threwOnGet = true;
        }
        assertTrue(threwOnGet, "get(0) on empty list must throw IndexOutOfBoundsException");
    }

    private static void testMinHeapDeletion() {
        MinHeap<Integer> heap = new MinHeap<>();
        heap.insert(10);
        heap.insert(20);
        heap.insert(5);
        heap.insert(30);
        heap.insert(15);

        assertEquals(5, heap.size(), "Heap size should be 5");
        assertEquals(5, heap.peek(), "Min should be 5");

        // Delete middle element (20)
        boolean deleted20 = heap.delete(20);
        assertTrue(deleted20, "Should successfully delete 20");
        assertEquals(4, heap.size(), "Heap size should be 4");
        assertTrue(!heap.contains(20), "Heap should no longer contain 20");

        // Delete root (5)
        boolean deleted5 = heap.delete(5);
        assertTrue(deleted5, "Should successfully delete root 5");
        assertEquals(10, heap.peek(), "New minimum after deleting 5 should be 10");

        // Attempt non-existent delete
        boolean deleted99 = heap.delete(99);
        assertTrue(!deleted99, "Deleting non-existent element should return false");
    }

    private static void testLinkedListDeletion() {
        CustomLinkedList<String> list = new CustomLinkedList<>();
        list.addLast("A");
        list.addLast("B");
        list.addLast("C");
        list.addLast("D");

        assertEquals(4, list.size(), "List should have 4 items");

        // Delete middle element "B"
        boolean removedB = list.remove("B");
        assertTrue(removedB, "Should successfully remove 'B'");
        assertEquals(3, list.size(), "List should have 3 items");
        assertTrue(!list.contains("B"), "List should not contain 'B'");

        // Remove last element using removeLast()
        String last = list.removeLast();
        assertEquals("D", last, "removeLast should return 'D'");
        assertEquals(2, list.size(), "List should have 2 items");

        // Remove head using remove()
        boolean removedA = list.remove("A");
        assertTrue(removedA, "Should remove head 'A'");
        assertEquals("C", list.getFirst(), "Remaining head should be 'C'");
    }

    // --- Category 3: Invalid Input Handling ---

    private static void testInvalidDimensions() {
        boolean threwZero = false;
        try {
            new GridMap(0, 10);
        } catch (IllegalArgumentException e) {
            threwZero = true;
        }
        assertTrue(threwZero, "0 row dimension must throw IllegalArgumentException");

        boolean threwNegative = false;
        try {
            new GridMap(-5, 10);
        } catch (IllegalArgumentException e) {
            threwNegative = true;
        }
        assertTrue(threwNegative, "Negative dimension must throw IllegalArgumentException");
    }

    private static void testOutOfBoundsCoordinates() {
        GridMap map = new GridMap(5, 5);

        boolean threwNegativeCoord = false;
        try {
            map.getNode(-1, 2);
        } catch (IndexOutOfBoundsException e) {
            threwNegativeCoord = true;
        }
        assertTrue(threwNegativeCoord, "Negative coordinates must throw IndexOutOfBoundsException");

        boolean threwOverflowCoord = false;
        try {
            map.getNode(5, 5);
        } catch (IndexOutOfBoundsException e) {
            threwOverflowCoord = true;
        }
        assertTrue(threwOverflowCoord, "Coordinate equal to dimension must throw IndexOutOfBoundsException");
    }

    private static void testEndpointsOnObstacles() {
        GridMap map = new GridMap(5, 5);
        map.setStart(0, 0);
        map.setTarget(4, 4);

        // Turn target into a wall
        map.getTargetNode().setBaseType(NodeType.WALL);

        AStarAlgorithm aStar = new AStarAlgorithm();
        PathResult res = aStar.findPath(map, map.getStartNode(), map.getTargetNode());

        // Must fail gracefully without throwing null pointer or crashing
        assertTrue(!res.isPathFound(), "Must return failure when target is a wall");
        assertEquals(0, res.getPathLength(), "Path length must be 0 for invalid target");
    }

    // --- Category 4: Duplicate & Priority Update Values ---

    private static void testMinHeapReHeapify() {
        MinHeap<Node> heap = new MinHeap<>();
        Node n1 = new Node(0, 0, NodeType.EMPTY);
        n1.setFCost(50.0);

        Node n2 = new Node(1, 1, NodeType.EMPTY);
        n2.setFCost(30.0);

        Node n3 = new Node(2, 2, NodeType.EMPTY);
        n3.setFCost(80.0);

        heap.insert(n1);
        heap.insert(n2);
        heap.insert(n3);

        assertEquals(n2, heap.peek(), "Minimum should be n2 with fCost 30.0");

        // Decrease cost of n3 to 10.0 (lower than n2) and reHeapify
        n3.setFCost(10.0);
        heap.reHeapify(n3);

        assertEquals(n3, heap.peek(), "Minimum should now be n3 after reHeapify");
        assertEquals(n3, heap.extractMin(), "extractMin should return n3");
        assertEquals(n2, heap.extractMin(), "extractMin should next return n2");
        assertEquals(n1, heap.extractMin(), "extractMin should finally return n1");
    }

    private static void testAlternativeLowerCostPath() {
        // Grid where initial straight line path is longer than a shortcut
        GridMap map = new GridMap(3, 4);
        map.setStart(0, 0);
        map.setTarget(0, 3);

        // Put high mud in the middle direct route
        map.getNode(0, 1).setBaseType(NodeType.MUD); // cost 4.0
        map.getNode(0, 2).setBaseType(NodeType.MUD); // cost 4.0

        // Clear bypass route through row 1
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm();
        PathResult res = dijkstra.findPath(map, map.getStartNode(), map.getTargetNode());

        assertTrue(res.isPathFound(), "Should find optimal detour");
        // Detour: (0,0)->(1,0)->(1,1)->(1,2)->(1,3)->(0,3) = 5 steps * 1.0 = 5.0 cost
        // Direct route: 4 + 4 + 1 = 9.0 cost
        assertEquals(5.0, res.getTotalCost(), "Algorithm must update frontier to choose the 5.0 cost bypass");
    }

    // --- Category 5: Boundary Cases ---

    private static void testUnreachableTarget() {
        GridMap map = new GridMap(5, 5);
        map.setStart(0, 0);
        map.setTarget(4, 4);

        // Completely enclose the target (4,4) with walls: (3,4) and (4,3)
        map.getNode(3, 4).setBaseType(NodeType.WALL);
        map.getNode(4, 3).setBaseType(NodeType.WALL);

        AStarAlgorithm aStar = new AStarAlgorithm();
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm();

        PathResult resA = aStar.findPath(map, map.getStartNode(), map.getTargetNode());
        PathResult resD = dijkstra.findPath(map, map.getStartNode(), map.getTargetNode());

        assertTrue(!resA.isPathFound(), "A* must report unreachable when surrounded by walls");
        assertTrue(!resD.isPathFound(), "Dijkstra must report unreachable when surrounded by walls");
        assertEquals(0, resA.getPathLength(), "A* path length should be 0");
        assertEquals(0, resD.getPathLength(), "Dijkstra path length should be 0");
    }

    private static void testStartIsTarget() {
        GridMap map = new GridMap(5, 5);
        map.setStart(2, 2);
        map.setTarget(2, 2);

        AStarAlgorithm aStar = new AStarAlgorithm();
        PathResult res = aStar.findPath(map, map.getStartNode(), map.getTargetNode());

        assertTrue(res.isPathFound(), "Path should be found when start == target");
        assertEquals(1, res.getPathLength(), "Path length should be 1 (the start node itself)");
        assertEquals(0.0, res.getTotalCost(), "Total movement cost should be 0.0");
    }

    private static void testMinimalGrid() {
        GridMap map = new GridMap(2, 2);
        map.setStart(0, 0);
        map.setTarget(1, 1);

        AStarAlgorithm aStar = new AStarAlgorithm();
        PathResult res = aStar.findPath(map, map.getStartNode(), map.getTargetNode());

        assertTrue(res.isPathFound(), "Should solve 2x2 grid");
        assertEquals(3, res.getPathLength(), "2x2 diagonal navigation takes 2 steps (3 nodes)");
        assertEquals(2.0, res.getTotalCost(), "Cost should be 2.0");
    }
}
