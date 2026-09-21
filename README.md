# PathForge: A Graph-Based Pathfinding Engine for Interactive Game Environments

[![Java](https://img.shields.io/badge/Java-17%2B%20%7C%2025-blue.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](#)
[![Tests](https://img.shields.io/badge/Tests-14%2F14%20Passing-brightgreen.svg)](#)

**PathForge** is an interactive, graph-based pathfinding engine in Java designed for autonomous non-player character (NPC) and robot navigation across 2D terrain. 

Rather than relying on Java's built-in collections, the core data structures—specifically a **Generic Binary Min-Heap (`MinHeap<T>`)** and a **Pointer-Linked List (`CustomLinkedList<T>`)**—are developed completely from scratch alongside a **2D Spatial Array (`GridMap`)**.

---

## 🌟 Key Features

* **Interactive Swing GUI Visualizer**:
  * **Dynamic Terrain Painting**: Click and drag to draw solid walls, place high-friction mud swamps ($4\times$ movement cost), or erase cells.
  * **Draggable Endpoints**: Move Start ($A$) and Target ($B$) dynamically across the map.
  * **Wavefront Animation**: Step through search frontier expansions with an adjustable animation speed slider.
  * **Autonomous NPC Avatar**: Watch an animated character walk step-by-step along the reconstructed linked list path.
  * **Real-Time Performance Dashboard**: Live metrics for nodes explored, path length, total movement cost, and execution time ($\mu s$).
* **Heuristic Search Pruning**:
  * Side-by-side benchmark demonstrates over **88% reduction in explored search space** using Manhattan distance A* compared to uninformed Dijkstra search.
* **Handcrafted Data Structures**:
  * Zero reliance on `java.util.PriorityQueue` or `java.util.LinkedList`.
* **Automated Test Suite**:
  * 14 automated unit tests covering normal data, underflow handling, arbitrary element deletion, input bounds validation, priority updates, and boundary conditions.

---

## 🏛 Architecture & Data Structures

| Component | Class | Complexity | Description |
| :--- | :--- | :--- | :--- |
| **Data Structure 1** | [`GridMap`](src/pathfinding/model/GridMap.java) | Lookup: $O(1)$ | 2D array representation of the spatial coordinate grid and terrain weights. |
| **Data Structure 2** | [`MinHeap<T>`](src/pathfinding/datastructures/MinHeap.java) | Insert/Extract: $O(\log N)$ | Custom binary min-heap serving as the priority queue for lowest-cost open frontier nodes. |
| **Data Structure 3** | [`CustomLinkedList<T>`](src/pathfinding/datastructures/CustomLinkedList.java) | AddFirst: $O(1)$, Traversal: $O(L)$ | Custom singly linked list with head/tail pointers for forward waypoint reconstruction. |
| **Algorithm 1** | [`DijkstraAlgorithm`](src/pathfinding/algorithms/DijkstraAlgorithm.java) | $O((V + E) \log V)$ | Uniform-cost search exploring outward radially by cumulative cost $g(n)$. |
| **Algorithm 2** | [`AStarAlgorithm`](src/pathfinding/algorithms/AStarAlgorithm.java) | $O(E \log V)$ (heuristic) | Heuristic search evaluated via $f(n) = g(n) + h(n)$ with admissible Manhattan distance. |

---

## 🚀 Quickstart & How to Run

Compile all source and test files into the `bin/` directory:

```powershell
$files = Get-ChildItem -Path src, test -Recurse -Filter *.java | ForEach-Object { $_.FullName }; javac -d bin $files
```

### 1. Launch the Interactive Swing GUI Window
```powershell
java -cp bin pathfinding.Main
```

### 2. Run the Automated Unit Test Suite
```powershell
java -cp bin pathfinding.PathfindingTests
```

### 3. Run the Terminal / Headless CLI Benchmark Mode
```powershell
java -cp bin pathfinding.Main --cli
```

---

## 📊 Empirical Benchmark Results

Benchmarked on a $15 \times 20$ Labyrinth maze:

```
=======================================================
          ALGORITHM BENCHMARK PERFORMANCE COMPARISON    
=======================================================
Metric                 | A* Search      | Dijkstra      
-------------------------------------------------------
Path Status            | FOUND          | FOUND         
Nodes Explored         | 30             | 257           
Path Length (Steps)    | 30             | 30            
Total Movement Cost    | 29.0           | 29.0          
Execution Time (µs)    | 1293.00        | 6794.70       
-------------------------------------------------------
Search Space Reduction by Heuristic: 88.3%
=======================================================
```

---

## 📁 Repository Structure

```
PathForge/
├── src/pathfinding/
│   ├── Main.java                          // Application launcher
│   ├── datastructures/
│   │   ├── MinHeap.java                   // Handcrafted generic binary min-heap
│   │   └── CustomLinkedList.java          // Handcrafted generic singly linked list
│   ├── model/
│   │   ├── GridMap.java                   // 2D spatial array and neighbor traversal
│   │   ├── Node.java                      // Coordinate node entity with f, g, h costs
│   │   └── NodeType.java                  // Cell terrain enum (WALL, MUD, START, TARGET)
│   ├── algorithms/
│   │   ├── PathfindingAlgorithm.java      // Strategy interface
│   │   ├── DijkstraAlgorithm.java         // Dijkstra's uniform-cost search
│   │   ├── AStarAlgorithm.java            // A* heuristic search (Manhattan)
│   │   └── PathResult.java                // Encapsulated benchmark results
│   └── ui/
│       ├── PathfindingFrame.java          // Main Swing GUI window & controls
│       ├── GridPanel.java                 // Interactive drawing canvas & NPC renderer
│       └── ConsoleVisualizer.java         // Terminal ANSI renderer & tables
├── test/pathfinding/
│   └── PathfindingTests.java              // 14 automated unit tests
└── docs/
    └── REPORT_AND_PROPOSAL.md             // Technical report and specification
```
