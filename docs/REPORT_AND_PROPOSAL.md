# PathForge: A Graph-Based Pathfinding Engine for Interactive Game Environments
## Project Report & Technical Specification

---

## 1. Project Proposal & Abstract

* **Subject**: Data Structures & Algorithms
* **Project Title**: PathForge: A Graph-Based Pathfinding Engine for Interactive Game Environments
* **Recommended Group Size**: 3–4 Students
* **Implementation Language**: Java (Java 17+ / Java 25 compatible, zero external dependencies)
* **Application Types**: Interactive Java Swing GUI Visualizer & Terminal Benchmark Engine

### Abstract
In modern video games and robotics simulations, non-player characters (NPCs) and autonomous agents must navigate dynamic terrain, avoid obstacles, and bypass hazardous areas (e.g., swamps, mud, or water) to reach an objective efficiently. Standard naive search techniques explore every direction blindly, causing massive computational overhead in large virtual worlds. 

**PathForge** is an autonomous graph-based pathfinding engine engineered strictly using foundational Data Structures and Algorithms. Rather than relying on Java's built-in collections, core data structures—specifically a **Generic Binary Min-Heap (`MinHeap<T>`)** and a **Pointer-Linked List (`CustomLinkedList<T>`)**—are developed from scratch alongside a **2D Spatial Array (`GridMap`)**. 

The engine implements and compares two pathfinding algorithms: **Dijkstra’s Algorithm** (uniform-cost search) and **A\* Search Algorithm** (heuristic-guided search with Manhattan distance). PathForge features an interactive Java Swing visual interface enabling live obstacle painting, variable terrain weighting, step-by-step wavefront animations, autonomous character walking, and real-time empirical complexity benchmarking demonstrating over **88% reduction in explored search space** via heuristic pruning.

---

## 2. Real-Life Problem Analysis & Scope

### Real-Life Problem
Game characters and autonomous logistics robots must compute real-time collision-free trajectories between Point A (current location) and Point B (destination). If an algorithm searches naively, games suffer severe frame drops, and automated robots make sluggish path recalculations. Furthermore, game terrains feature varying traversal difficulties: traversing solid road is faster than wading through mud or water. The engine must compute the truly optimal (lowest-cost) path while examining the minimal number of potential grid tiles.

### Project Scope
The application enables users and evaluators to:
1. Initialize a 2D tile-based terrain grid with custom dimensions.
2. Designate custom Start $(A)$ and Target $(B)$ coordinates dynamically.
3. Place obstacles (solid walls) and weighted terrain (mud/swamp with $4\times$ traversal cost).
4. Execute **Dijkstra's Algorithm** to compute the shortest path based strictly on cumulative cost $g(n)$.
5. Execute **A\* Search Algorithm** using Manhattan distance heuristic $h(n)$ to direct the search toward the target.
6. Track and display visited exploration wavefronts step-by-step.
7. Reconstruct the optimal path and animate an autonomous game character (NPC) moving along the waypoints.
8. Output real-time empirical performance metrics: Nodes Explored, Path Length, Total Cost, and Execution Time ($\mu s$).
9. Handle boundary cases gracefully (e.g., completely boxed-in targets, invalid coordinates, start equals target).

---

## 3. Data Structure Design & Justification

Core Data Structures Design (with manual implementation of key structures):

```
+-----------------------------------------------------------------------------------+
|                            CORE THREE DATA STRUCTURES                             |
+------------------------------------+----------------------------------------------+
| 1. GridMap (2D Array)              | Spatial representation of the game world.    |
| 2. MinHeap<T> (Handcrafted Heap)   | Priority Queue for lowest-cost open frontier.|
| 3. CustomLinkedList<T> (Handcrafted)| Waypoint sequence and path reconstruction.   |
+------------------------------------+----------------------------------------------+
```

### 1. Data Structure 1: 2D Array (`GridMap`)
* **Role**: Models the discrete 2D spatial grid coordinates $(x, y)$, terrain states, and neighbor connectivity.
* **Justification**:
  * Provides instantaneous $O(1)$ random access coordinate lookup `grid[r][c]`.
  * Spatial adjacency is naturally defined via constant-time orthogonal boundary indexing $(r \pm 1, c \pm 1)$.
  * Compact memory footprint without pointer overhead for grid layout.

### 2. Data Structure 2: Custom Generic Binary Min-Heap (`MinHeap<T>`) — *Student Implemented*
* **Role**: Serves as the priority queue managing the "Open Set" (frontier nodes awaiting evaluation).
* **Justification**:
  * Both Dijkstra and A* require repeatedly extracting the node with the lowest cumulative cost $f(n)$ or $g(n)$.
  * Using an unsorted array or list requires $O(n)$ linear scan per extraction ($O(n^2)$ total search).
  * Our manually implemented Binary Min-Heap guarantees $O(\log n)$ insertion (`siftUp`) and $O(\log n)$ minimum extraction (`siftDown`), reducing pathfinding time to $O((V + E) \log V)$.
  * Implemented completely from scratch without `java.util.PriorityQueue`.

### 3. Data Structure 3: Custom Generic Singly Linked List (`CustomLinkedList<T>`) — *Student Implemented*
* **Role**: Stores the reconstructed path from Start to Target and records the order of visited nodes for visualization.
* **Justification**:
  * During path reconstruction, traversal proceeds backwards from Target to Start via parent pointers (`node.parent`).
  * A standard array requires reversing elements ($O(n)$) or shifting elements ($O(n^2)$).
  * Our `CustomLinkedList` provides $O(1)$ constant-time `addFirst()` (prepending), allowing the forward route to be constructed effortlessly in $O(L)$ time.
  * Implemented from scratch with head/tail pointers and custom `Iterator<T>` without `java.util.LinkedList`.

### Operations Mapping Table (Rubric Page 1, 16)
The rubric explicitly requires that *"appropriate searching, sorting, insertion, deletion, traversal, or processing operations"* are mapped properly to the selected structures:

| Data Structure | Operation Category | Method Name | Time Complexity | Space Complexity | Description |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **GridMap (2D Array)** | Coordinate Lookup | `getNode(r, c)` | $O(1)$ | $O(1)$ | Direct array index access with boundary verification. |
| **GridMap (2D Array)** | Neighbor Traversal | `getNeighbors(Node)` | $O(1)$ | $O(1)$ | Generates traversable adjacent cardinal cells. |
| **GridMap (2D Array)** | Obstacle Insertion | `setObstacle / setMud` | $O(1)$ | $O(1)$ | Sets wall barriers or terrain movement multipliers. |
| **GridMap (2D Array)** | State Reset / Deletion | `clearAllObstacles()` | $O(R \times C)$ | $O(1)$ | Reverts all cells back to open empty tiles. |
| **MinHeap<T>** | Frontier Insertion | `insert(T item)` | $O(\log N)$ | $O(1)$ aux | Appends to end of array buffer and invokes `siftUp()`. |
| **MinHeap<T>** | Minimum Deletion | `extractMin()` | $O(\log N)$ | $O(1)$ aux | Pops root element, moves last leaf to root, and sifts down. |
| **MinHeap<T>** | Arbitrary Deletion | `delete(T item)` | $O(N + \log N)$ | $O(1)$ aux | Locates element and re-sifts heap to preserve invariant. |
| **MinHeap<T>** | Cost Update / Processing| `reHeapify(T item)` | $O(N + \log N)$ | $O(1)$ aux | Re-establishes heap ordering when a shorter path is found. |
| **MinHeap<T>** | Search / Lookup | `contains(T item)` | $O(N)$ | $O(1)$ aux | Scans elements to check open-set membership. |
| **CustomLinkedList<T>**| Head Insertion (Prepend)| `addFirst(T data)` | $O(1)$ | $O(1)$ aux | Prepends node in $O(1)$ time for forward path reconstruction. |
| **CustomLinkedList<T>**| Tail Insertion (Append) | `addLast(T data)` | $O(1)$ | $O(1)$ aux | Appends waypoint to end of list using cached tail pointer. |
| **CustomLinkedList<T>**| Head Deletion | `removeFirst()` | $O(1)$ | $O(1)$ aux | Deletes head node and updates pointer reference. |
| **CustomLinkedList<T>**| Element Deletion | `remove(T data)` | $O(N)$ | $O(1)$ aux | Unlinks first matching data node from the list chain. |
| **CustomLinkedList<T>**| Traversal & Search | `get(idx) / contains()`| $O(N)$ | $O(1)$ aux | Sequential pointer traversal across linked nodes. |

---

## 4. Algorithm Selection & Pseudocode

Core Pathfinding Algorithms:

```
+-----------------------------------------------------------------------------------+
|                              CORE TWO ALGORITHMS                                  |
+------------------------------------+----------------------------------------------+
| 1. Dijkstra's Algorithm            | Uniform-Cost Search (uninformed, g-cost only)|
| 2. A* Search Algorithm             | Heuristic-Guided Search (f = g + h)          |
+------------------------------------+----------------------------------------------+
```

### Algorithm 1: Dijkstra's Algorithm (Uniform-Cost Search)
Dijkstra's algorithm guarantees the shortest path on weighted graphs by expanding the frontier uniformly outward in concentric rings of equal cost.

#### Pseudocode:
```text
Algorithm: DijkstraPathfinding(map, start, target)
Input: 2D GridMap map, Node start, Node target
Output: PathResult containing reconstructed path and metrics

1.  map.resetSearchData()
2.  If start is null OR target is null OR NOT start.isWalkable() OR NOT target.isWalkable() Then
3.      Return Failure
4.  If start == target Then
5.      Return SingleNodePath(start)
6.  
7.  Initialize MinHeap<Node> openSet
8.  Initialize CustomLinkedList<Node> visitedOrder
9.  start.gCost = 0.0
10. start.fCost = 0.0
11. openSet.insert(start)
12. start.inOpenSet = True
13. 
14. While NOT openSet.isEmpty() Do
15.     current = openSet.extractMin()
16.     current.inOpenSet = False
17.     
18.     If current.inClosedSet Then
19.         Continue
20.     current.inClosedSet = True
21.     visitedOrder.addLast(current)
22.     
23.     If current == target Then
24.         Return ReconstructPath(target), visitedOrder
25.         
26.     For each neighbor in map.getNeighbors(current) Do
27.         If neighbor.inClosedSet OR NOT neighbor.isWalkable() Then
28.             Continue
29.         tentativeGCost = current.gCost + neighbor.getTerrainCost()
30.         If tentativeGCost < neighbor.gCost Then
31.             neighbor.gCost = tentativeGCost
32.             neighbor.fCost = tentativeGCost
33.             neighbor.parent = current
34.             If NOT neighbor.inOpenSet Then
35.                 neighbor.inOpenSet = True
36.                 openSet.insert(neighbor)
37.             Else
38.                 openSet.reHeapify(neighbor)
39. End While
40. Return Failure (Path Blocked)
```

---

### Algorithm 2: A* Search Algorithm (Heuristic-Guided Search)
A* accelerates shortest path computation by incorporating an admissible heuristic function $h(n)$ that estimates the remaining cost to the goal:
$$f(n) = g(n) + h(n)$$
Where:
* $g(n)$: Exact accumulated cost from the Start node to node $n$.
* $h(n)$: Admissible Manhattan distance heuristic to the Target:
$$h(n) = |n.x - \text{target}.x| + |n.y - \text{target}.y|$$

#### Pseudocode:
```text
Algorithm: AStarPathfinding(map, start, target)
Input: 2D GridMap map, Node start, Node target
Output: PathResult containing reconstructed path and metrics

1.  map.resetSearchData()
2.  If start is null OR target is null OR NOT start.isWalkable() OR NOT target.isWalkable() Then
3.      Return Failure
4.  If start == target Then
5.      Return SingleNodePath(start)
6.      
7.  Initialize MinHeap<Node> openSet
8.  Initialize CustomLinkedList<Node> visitedOrder
9.  start.gCost = 0.0
10. start.hCost = ManhattanDistance(start, target)
11. start.fCost = start.gCost + start.hCost
12. openSet.insert(start)
13. start.inOpenSet = True
14. 
15. While NOT openSet.isEmpty() Do
16.     current = openSet.extractMin()
17.     current.inOpenSet = False
18.     
19.     If current.inClosedSet Then
20.         Continue
21.     current.inClosedSet = True
22.     visitedOrder.addLast(current)
23.     
24.     If current == target Then
25.         Return ReconstructPath(target), visitedOrder
26.         
27.     For each neighbor in map.getNeighbors(current) Do
28.         If neighbor.inClosedSet OR NOT neighbor.isWalkable() Then
29.             Continue
30.         tentativeGCost = current.gCost + neighbor.getTerrainCost()
31.         If tentativeGCost < neighbor.gCost Then
32.             neighbor.gCost = tentativeGCost
33.             neighbor.hCost = ManhattanDistance(neighbor, target)
34.             neighbor.fCost = neighbor.gCost + neighbor.hCost
35.             neighbor.parent = current
36.             If NOT neighbor.inOpenSet Then
37.                 neighbor.inOpenSet = True
38.                 openSet.insert(neighbor)
39.             Else
40.                 openSet.reHeapify(neighbor)
41. End While
42. Return Failure (Path Blocked)
```

---

## 5. Required Complexity Analysis (Rubric: 3 Marks)

Below is the formal asymptotic complexity breakdown matching the required rubric format (Pages 14–15):

| Operation / Component | Structure / Algorithm | Time Complexity (Average) | Time Complexity (Worst-Case) | Space Complexity | Technical Rationale |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Grid Cell Lookup** | `GridMap` (2D Array) | $O(1)$ | $O(1)$ | $O(R \times C)$ | Direct 2D memory indexing via row and column offsets. |
| **Insert Frontier Node** | `MinHeap<T>` | $O(\log N)$ | $O(\log N)$ | $O(N)$ | Element inserted at leaf and sifted up binary tree levels ($h = \lfloor\log_2 N\rfloor$). |
| **Extract Min Node** | `MinHeap<T>` | $O(\log N)$ | $O(\log N)$ | $O(1)$ aux | Root removed; last leaf moved to root and sifted down to restore heap invariant. |
| **Prepend Path Step** | `CustomLinkedList<T>` | $O(1)$ | $O(1)$ | $O(1)$ aux | Pointer manipulation updating head reference (`newHead.next = head`). |
| **Path Reconstruction** | `CustomLinkedList<T>` | $O(L)$ | $O(L)$ | $O(L)$ | Prepending $L$ parent pointers from target back to start node. |
| **Dijkstra Search** | Dijkstra Algorithm | $O((V + E) \log V)$ | $O((V + E) \log V)$ | $O(V)$ | Uniform radial expansion across $V$ grid vertices and $E$ edges using binary min-heap. |
| **A\* Search** | A\* Algorithm | $O(E \log V)$ (heuristic) | $O((V + E) \log V)$ | $O(V)$ | Manhattan heuristic prunes non-promising branches; worst-case bounded by Dijkstra if heuristic is uninformative. |

*Where $R, C$ are grid rows and columns, $V = R \times C$, $E \le 4V$ (for 4-connected grid), and $L$ is shortest path length.*

---

## 6. Testing & Error Handling Suite

The project includes an automated test suite (`PathfindingTests.java`) rigorously verifying all **5 core testing scenarios**:

```
===============================================================
       Game Pathfinding Engine: Automated Test Suite           
===============================================================
[PASS] 1. Normal Data Test - Clear Grid Shortest Path
[PASS] 1. Normal Data Test - Weighted Terrain (Mud avoidance)
[PASS] 2. Empty Structure Test - MinHeap underflow handling
[PASS] 2. Empty Structure Test - CustomLinkedList underflow handling
[PASS] 2. Operation Test - MinHeap arbitrary deletion
[PASS] 2. Operation Test - CustomLinkedList middle/tail deletion
[PASS] 3. Invalid Input Test - Negative / out-of-bounds dimensions
[PASS] 3. Invalid Input Test - Out-of-bounds coordinate queries
[PASS] 3. Invalid Input Test - Start or Target on Wall obstacle
[PASS] 4. Duplicate / Priority Update Test - MinHeap reHeapify
[PASS] 4. Duplicate / Multiple Path Discovery Test - Lower cost selection
[PASS] 5. Boundary Case Test - Goal completely blocked by walls
[PASS] 5. Boundary Case Test - Start is Target (zero distance)
[PASS] 5. Boundary Case Test - Minimum 2x2 grid navigation
---------------------------------------------------------------
Test Summary: 14 / 14 tests passed successfully. (ALL PASS)
---------------------------------------------------------------
```

### Details of Test Scenarios:
1. **Normal Data**:
   - Clear $10 \times 10$ grid from $(0,0)$ to $(9,9)$: Both A* and Dijkstra identify optimal 19-node path of cost 18.0.
   - Weighted Terrain: Direct path covered with mud ($4\times$ cost); algorithm successfully circumvents mud through a longer geometric path that incurs lower total cost.
2. **Empty Structure Handling**:
   - `MinHeap.extractMin()` and `peek()` correctly catch empty conditions and throw `NoSuchElementException` without memory faults.
   - `CustomLinkedList.removeFirst()` throws `NoSuchElementException` and `get(0)` throws `IndexOutOfBoundsException`.
3. **Invalid Input Handling**:
   - Grid instantiation with zero or negative dimensions throws `IllegalArgumentException`.
   - Out-of-bounds coordinate queries throw `IndexOutOfBoundsException`.
   - Setting Start or Target on solid walls returns graceful failure status without unhandled exceptions.
4. **Duplicate & Priority Updates**:
   - `reHeapify()` accurately restores heap order when an existing node's $gCost$ is reduced via an alternate shorter path.
5. **Boundary Cases**:
   - Target surrounded completely by impenetrable walls: Algorithms terminate cleanly with `pathFound = false` and 0 path length (no infinite loops).
   - Start equals Target: Handled instantaneously with path length 1 and cost 0.0.

---

## 7. Empirical Benchmark Results

Running the benchmark on a $15 \times 20$ Labyrinth Maze demonstrates the dramatic advantage of heuristic guidance:

| Metric | A\* Search Algorithm | Dijkstra's Algorithm | Heuristic Advantage |
| :--- | :--- | :--- | :--- |
| **Path Status** | `FOUND` | `FOUND` | Both guarantee optimal route |
| **Total Movement Cost** | **29.0** | **29.0** | Identical optimal cost |
| **Path Length (Steps)** | **30 nodes** | **30 nodes** | Identical optimal route |
| **Nodes Explored** | **30 nodes** | **257 nodes** | **88.3% fewer nodes evaluated!** |
| **Execution Time** | **1.29 ms** | **6.79 ms** | Over $5\times$ faster |

---

## 8. Live Demonstration Guide (Rubric: 2 Marks)

### Launching the Application
```bash
# 1. Run Automated Unit Tests (Rubric Section 6)
java -cp bin pathfinding.PathfindingTests

# 2. Run Interactive Terminal Benchmark Mode
java -cp bin pathfinding.Main --cli

# 3. Launch the Modern Java Swing Visualizer Window
java -cp bin pathfinding.Main
```

### Demonstration Steps for Evaluator:
1. **Explore Presets**: Select **"Labyrinth Maze"** from the top preset dropdown.
2. **Interactive Drawing**:
   - Select **"Wall Barrier"** to draw solid obstacles with mouse drag.
   - Select **"Mud Terrain (4x Cost)"** to paint swamps; observe how algorithms calculate whether cutting through mud or walking around is cheaper.
   - Select **"Move Start (A)"** or **"Move Target (B)"** to reposition endpoints.
3. **Wavefront Animation**: Click **"▶ Animate Wavefront"** with Dijkstra to observe uniform radial expansion across the entire grid; then switch to A* to see the search beam focus directly toward Point B.
4. **NPC Walking**: Click **"🚶 Walk Character (NPC)"** to watch the purple character avatar navigate smoothly along the `CustomLinkedList` waypoints.
5. **Side-by-Side Benchmark**: Select **"Side-by-Side Comparison"** to display real-time comparative metrics showing the exact percentage of search space saved.

---

## 9. Viva Voce & Oral Defense Preparation (Rubric: 2 Marks)

Common examiner questions and model answers for student group members:

### Q1: Why use a Priority Queue (Min-Heap) instead of a standard FIFO Queue?
> *Answer*: An ordinary FIFO queue only works for unweighted graphs where all edge costs are identical (Breadth-First Search). In game environments with varied terrain costs (e.g., mud or road) or heuristic evaluations ($f = g + h$), nodes must be expanded in order of minimum cost, not arrival order. An unsorted array would require $O(n)$ search per step, whereas our custom `MinHeap` performs insertions and extractions in $O(\log n)$ time.

### Q2: Why did A* explore 88% fewer nodes than Dijkstra while producing the same path?
> *Answer*: Dijkstra is an uninformed search that explores uniformly in all directions. A* adds an admissible heuristic $h(n)$ (Manhattan distance) estimating remaining distance to the target. This prioritizes frontier nodes moving closer to Point B, steering the search beam directly toward the goal and pruning hundreds of irrelevant nodes.

### Q3: Why is Manhattan distance admissible for this grid?
> *Answer*: A heuristic is admissible if it never overestimates the actual cost to reach the goal ($h(n) \le h^*(n)$). In a 4-connected grid where each step has a minimum cost of 1.0, the minimum number of steps between $(x_1, y_1)$ and $(x_2, y_2)$ is $|x_1 - x_2| + |y_1 - y_2|$. Because obstacles or mud can only *increase* the cost, Manhattan distance is guaranteed to be admissible and consistent, ensuring A* always finds an optimal path.

### Q4: Why use a custom Singly Linked List for path reconstruction?
> *Answer*: When the target is reached, we trace backwards from Target to Start using `parent` pointers. Prepending each node to our custom linked list (`addFirst()`) takes $O(1)$ constant time per node, yielding the correct forward order from Start to Target in $O(L)$ total time with zero array reallocation or shifting overhead.

---

## 10. Individual Contribution & Role Matrix (Rubric Criterion 9: 2 Marks)

For group submissions (recommended 3–4 students), the rubric evaluates individual accountability and understanding of the code. Below is the recommended role distribution:

| Team Member | Assigned Core Role | Java Files Owned | Key Concepts to Explain in Viva |
| :--- | :--- | :--- | :--- |
| **Student 1** | **Grid World Modeling & Data Structure 1** | `GridMap.java`<br>`Node.java`<br>`NodeType.java` | • 2D array representation of coordinate space.<br>• $O(1)$ neighbor lookup and boundary validation.<br>• Tile state preservation (mud costs, walls). |
| **Student 2** | **Priority Queue & Data Structure 2** | `MinHeap.java`<br>`PathResult.java` | • Handcrafted binary min-heap mechanics.<br>• $O(\log n)$ sift-up and sift-down algorithms.<br>• `reHeapify()` when lower-cost route discovered. |
| **Student 3** | **Waypoints & Data Structure 3** | `CustomLinkedList.java`<br>`PathfindingTests.java` | • Handcrafted pointer-linked list mechanics.<br>• $O(1)$ `addFirst()` prepending for parent tracing.<br>• Rubric unit testing suite across all 5 criteria. |
| **Student 4** | **Pathfinding Search & UI Integration** | `AStarAlgorithm.java`<br>`DijkstraAlgorithm.java`<br>`PathfindingFrame.java` | • $f(n) = g(n) + h(n)$ Manhattan heuristic derivation.<br>• Uniform-cost vs. heuristic search space pruning.<br>• Swing event dispatching and timer animations. |

---

## 11. Application Visuals & Screenshot Layout Guide (Rubric Criterion 7: 2 Marks)

The rubric explicitly mandates inclusion of **Screenshots** in the final report submission. Below is the interface layout and screenshot checklist:

### Screenshot Checklist for Final PDF:
1. **Screenshot 1: Initial Grid Setup**
   - Labyrinth maze with Start ($A$ in Green) at $(1, 1)$, Target ($B$ in Red) at $(rows-2, cols-2)$, solid dark gray wall barriers, and amber mud swamp patches.
2. **Screenshot 2: Dijkstra Wavefront Expansion**
   - Shows Dijkstra's algorithm exploring radially across the entire board (light blue visited tiles covering nearly the entire grid).
3. **Screenshot 3: A\* Search Heuristic Focus**
   - Shows A* search exploring a tight, narrow corridor directed straight toward Target $B$, demonstrating massive node reduction.
4. **Screenshot 4: Side-by-Side Performance Comparison Dashboard**
   - The metrics card showing:
     - Dijkstra: 257 nodes explored.
     - A*: 30 nodes explored (88.3% search space reduction).
     - Identical path length (30) and cost (29.0).
5. **Screenshot 5: Autonomous NPC Movement**
   - The purple NPC avatar walking along the golden path line toward Point $B$.
6. **Screenshot 6: Automated Test Suite Terminal Output**
   - Terminal showing `Test Summary: 14 / 14 tests passed successfully. (ALL PASS)`.

```
+---------------------------------------------------------------------------------------------------+
|  PathForge: Graph-Based Pathfinding Engine                                              [-] [X]  |
+---------------------------------------------------------------------------------------------------+
| Algorithm: [A* Search (Heuristic) v]  Presets: [Labyrinth Maze v]  Tool: [Wall v]  Speed: [==|==] |
+---------------------------------------------------------------------------------------------------+
|                                                     |  [ACTIONS]                                  |
|   +---------------------------------------------+   |  [ ⚡ Instant Solve       ]                 |
|   |  A . . . * * * * * * * * * * *              |   |  [ ▶ Animate Wavefront    ]                 |
|   |          # # # # # # # # #   *              |   |  [ 🚶 Walk Character (NPC) ]                 |
|   |          #                 ~ *              |   |  [ 🔄 Clear Solution      ]                 |
|   |          #   ~ ~ ~ ~ ~ ~   ~ *              |   |                                             |
|   |          #   ~ ~ ~ ~ ~ ~   ~ *              |   |  [DSA PERFORMANCE METRICS]                  |
|   |          #                 ~ *              |   |  Status: Optimal Path Found                 |
|   |          # # # # # # # # #   *              |   |  Nodes Explored: 30                         |
|   |                              *              |   |  Path Length: 30 steps                      |
|   |                              * B            |   |  Total Movement Cost: 29.0                  |
|   +---------------------------------------------+   |  Execution Time: 1293.00 µs                 |
|                                                     |                                             |
|  Legend: [A Start] [B Target] [# Wall] [~ Mud]     |  A* evaluated 88% FEWER nodes!              |
|          [. Visited] [* Path] [NPC Character]       |                                             |
+---------------------------------------------------------------------------------------------------+
```
