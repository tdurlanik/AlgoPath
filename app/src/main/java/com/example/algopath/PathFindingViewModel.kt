package com.example.algopath

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.LinkedList
import java.util.PriorityQueue
import java.util.Queue
import kotlin.math.abs

class PathFindingViewModel : ViewModel() {
    private val ROWS = 20
    private val COLS = 12

    private val START_NODE = Pair(0, 0)
    private val END_NODE = Pair(ROWS - 1, COLS - 1)

    private val _gridState = MutableStateFlow(createInitialGrid())
    val gridState = _gridState.asStateFlow()

    private val _statsState = MutableStateFlow(SimulationStats())
    val statsState = _statsState.asStateFlow()

    var isRunning = false
    private var lastTouchedCell: Pair<Int, Int>? = null

    private val _selectedAlgorithm = MutableStateFlow(AlgorithmType.BFS)
    val selectedAlgorithm = _selectedAlgorithm.asStateFlow()

    fun onAlgorithmSelected(algo: AlgorithmType) {
        _selectedAlgorithm.value = algo
    }

    private fun createInitialGrid(): List<List<Cell>> {
        val newGrid = mutableListOf<List<Cell>>()
        for (i in 0 until ROWS) {
            val rowList = mutableListOf<Cell>()
            for (j in 0 until COLS) {
                val type = when {
                    i == START_NODE.first && j == START_NODE.second -> CellType.START
                    i == END_NODE.first && j == END_NODE.second -> CellType.END
                    else -> CellType.EMPTY
                }
                rowList.add(Cell(row = i, col = j, type = type))
            }
            newGrid.add(rowList)
        }
        return newGrid
    }

    fun onCellClicked(row: Int, col: Int) {
        if (isRunning) return
        if ((row == START_NODE.first && col == START_NODE.second) ||
            (row == END_NODE.first && col == END_NODE.second)) return

        val currentGrid = _gridState.value.map { it.toMutableList() }.toMutableList()
        val currentCell = currentGrid[row][col]

        if (currentCell.type == CellType.EMPTY || currentCell.type == CellType.WALL) {
            val newType = if (currentCell.type == CellType.WALL) CellType.EMPTY else CellType.WALL
            currentGrid[row][col] = currentCell.copy(type = newType)
            _gridState.value = currentGrid
        }
    }

    fun onDragMove(row: Int, col: Int) {
        if (lastTouchedCell == Pair(row, col)) return
        onCellClicked(row, col)
        lastTouchedCell = Pair(row, col)
    }

    fun onDragEnd() {
        lastTouchedCell = null
    }

    fun runAlgorithm() {
        if (isRunning) return
        clearVisuals()
        when (_selectedAlgorithm.value) {
            AlgorithmType.BFS -> runBFS()
            AlgorithmType.DIJKSTRA -> runDijkstra()
            AlgorithmType.ASTAR -> runAStar()
        }
    }

    fun runBFS() {
        if (isRunning) return
        isRunning = true

        val startTime = System.currentTimeMillis()
        var visitedCounter = 0

        viewModelScope.launch {
            val grid = _gridState.value.map { it.toMutableList() }.toMutableList()
            val queue: Queue<Pair<Int, Int>> = LinkedList()
            val visited = Array(ROWS) { BooleanArray(COLS) }
            val parentMap = mutableMapOf<Pair<Int, Int>, Pair<Int, Int>>()

            queue.add(START_NODE)
            visited[START_NODE.first][START_NODE.second] = true

            var found = false

            while (!queue.isEmpty()) {
                val current = queue.poll() ?: break
                val (r, c) = current

                if (r == END_NODE.first && c == END_NODE.second) {
                    found = true
                    break
                }

                if (grid[r][c].type != CellType.START) {
                    grid[r][c] = grid[r][c].copy(type = CellType.VISITED)
                    _gridState.value = grid.map { it.toList() }
                    visitedCounter++
                    delay(10)
                }

                val directions = listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)
                for ((dr, dc) in directions) {
                    val nr = r + dr
                    val nc = c + dc

                    if (nr in 0 until ROWS && nc in 0 until COLS &&
                        grid[nr][nc].type != CellType.WALL &&
                        !visited[nr][nc]) {
                        queue.add(nr to nc)
                        visited[nr][nc] = true
                        parentMap[nr to nc] = current
                    }
                }
            }
            drawPath(grid, parentMap, startTime, visitedCounter)
        }
    }

    private fun runDijkstra() {
        if (isRunning) return
        isRunning = true
        val startTime = System.currentTimeMillis()
        var visitedCounter = 0

        viewModelScope.launch {
            val grid = _gridState.value.map { it.toMutableList() }.toMutableList()
            val pq = PriorityQueue<Pair<Int, Pair<Int, Int>>> { o1, o2 -> o1.first - o2.first }
            val distances = Array(ROWS) { IntArray(COLS) { Int.MAX_VALUE } }
            val parentMap = mutableMapOf<Pair<Int, Int>, Pair<Int, Int>>()

            distances[START_NODE.first][START_NODE.second] = 0
            pq.add(0 to START_NODE)

            while (!pq.isEmpty()) {
                val (currentDist, currentPos) = pq.poll() ?: break
                val (r, c) = currentPos

                if (r == END_NODE.first && c == END_NODE.second) break
                if (currentDist > distances[r][c]) continue

                if (grid[r][c].type != CellType.START) {
                    grid[r][c] = grid[r][c].copy(type = CellType.VISITED)
                    _gridState.value = grid.map { it.toList() }
                    visitedCounter++
                    delay(5)
                }

                val directions = listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)
                for ((dr, dc) in directions) {
                    val nr = r + dr
                    val nc = c + dc

                    if (nr in 0 until ROWS && nc in 0 until COLS && grid[nr][nc].type != CellType.WALL) {
                        val newDist = currentDist + 1
                        if (newDist < distances[nr][nc]) {
                            distances[nr][nc] = newDist
                            parentMap[nr to nc] = currentPos
                            pq.add(newDist to (nr to nc))
                        }
                    }
                }
            }
            drawPath(grid, parentMap, startTime, visitedCounter)
        }
    }

    private fun runAStar() {
        if (isRunning) return
        isRunning = true
        val startTime = System.currentTimeMillis()
        var visitedCounter = 0

        viewModelScope.launch {
            val grid = _gridState.value.map { it.toMutableList() }.toMutableList()
            val pq = PriorityQueue<Triple<Int, Int, Int>> { o1, o2 -> o1.first - o2.first }
            val gScore = Array(ROWS) { IntArray(COLS) { Int.MAX_VALUE } }
            val fScore = Array(ROWS) { IntArray(COLS) { Int.MAX_VALUE } }
            val parentMap = mutableMapOf<Pair<Int, Int>, Pair<Int, Int>>()

            gScore[START_NODE.first][START_NODE.second] = 0
            fScore[START_NODE.first][START_NODE.second] = heuristic(START_NODE, END_NODE)
            pq.add(Triple(fScore[START_NODE.first][START_NODE.second], START_NODE.first, START_NODE.second))

            val openSetHash = mutableSetOf(START_NODE)

            while (!pq.isEmpty()) {
                val current = pq.poll() ?: break
                val r = current.second
                val c = current.third
                openSetHash.remove(r to c)

                if (r == END_NODE.first && c == END_NODE.second) break

                if (grid[r][c].type != CellType.START) {
                    grid[r][c] = grid[r][c].copy(type = CellType.VISITED)
                    _gridState.value = grid.map { it.toList() }
                    visitedCounter++
                    delay(10)
                }

                val directions = listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)
                for ((dr, dc) in directions) {
                    val nr = r + dr
                    val nc = c + dc

                    if (nr in 0 until ROWS && nc in 0 until COLS && grid[nr][nc].type != CellType.WALL) {
                        val tentativeGScore = gScore[r][c] + 1
                        if (tentativeGScore < gScore[nr][nc]) {
                            parentMap[nr to nc] = r to c
                            gScore[nr][nc] = tentativeGScore
                            fScore[nr][nc] = tentativeGScore + heuristic(nr to nc, END_NODE)

                            if (!openSetHash.contains(nr to nc)) {
                                pq.add(Triple(fScore[nr][nc], nr, nc))
                                openSetHash.add(nr to nc)
                            }
                        }
                    }
                }
            }
            drawPath(grid, parentMap, startTime, visitedCounter)
        }
    }

    private fun heuristic(a: Pair<Int, Int>, b: Pair<Int, Int>): Int {
        return abs(a.first - b.first) + abs(a.second - b.second)
    }

    private suspend fun drawPath(
        grid: MutableList<MutableList<Cell>>,
        parentMap: Map<Pair<Int, Int>, Pair<Int, Int>>,
        startTime: Long,
        visitedCounter: Int
    ) {
        var pathCounter = 0
        var curr = parentMap[END_NODE]

        if (curr != null || parentMap.containsKey(END_NODE)) {
            while (curr != null && curr != START_NODE) {
                grid[curr.first][curr.second] = grid[curr.first][curr.second].copy(type = CellType.PATH)
                _gridState.value = grid.map { it.toList() }
                pathCounter++
                delay(20)
                curr = parentMap[curr]
            }
        }

        val endTime = System.currentTimeMillis()
        _statsState.value = SimulationStats(
            durationMs = endTime - startTime,
            visitedCount = visitedCounter,
            pathLength = pathCounter
        )
        isRunning = false
    }

    private fun clearVisuals() {
        val newGrid = _gridState.value.map { row ->
            row.map { cell ->
                if (cell.type == CellType.VISITED || cell.type == CellType.PATH) {
                    cell.copy(type = CellType.EMPTY)
                } else {
                    cell
                }
            }
        }
        _gridState.value = newGrid
        _statsState.value = SimulationStats()
    }

    fun resetGrid() {
        if (isRunning) return
        _gridState.value = createInitialGrid()
        _statsState.value = SimulationStats()
        lastTouchedCell = null
    }
}