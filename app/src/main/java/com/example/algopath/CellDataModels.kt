package com.example.algopath

enum class CellType {
    EMPTY,
    WALL,
    START,
    END,
    PATH,
    VISITED
}

data class Cell(
    val row: Int,
    val col: Int,
    var type: CellType = CellType.EMPTY
)

data class SimulationStats(
    val durationMs : Long = 0,
    val visitedCount : Int = 0,
    val pathLength : Int = 0
)

enum class AlgorithmType(val title: String) {
    BFS("Breadth-First Search"),
    DIJKSTRA("Dijkstra's Algorithm"),
    ASTAR("A* (A-Star) Search")
}