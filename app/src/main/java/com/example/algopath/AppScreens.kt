package com.example.algopath

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun AppNavigation(vm: PathFindingViewModel = viewModel()) {
    var showStatsScreen by remember { mutableStateOf(false) }

    if (showStatsScreen) {
        StatsScreen(vm = vm, onBack = { showStatsScreen = false })
    } else {
        PathFinderScreen(vm = vm, onShowStats = { showStatsScreen = true })
    }
}

@Composable
fun PathFinderScreen(
    vm: PathFindingViewModel,
    onShowStats: () -> Unit
) {
    val grid by vm.gridState.collectAsState()
    val selectedAlgo by vm.selectedAlgorithm.collectAsState()
    val density = LocalDensity.current
    val cellSizeDp = 30.dp
    val cellSizePx = with(density) { cellSizeDp.toPx() }

    Column(
        modifier = Modifier.fillMaxSize().padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AlgorithmSelector(
            selectedAlgorithm = selectedAlgo,
            onAlgorithmSelected = { vm.onAlgorithmSelected(it) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        Box(
            modifier = Modifier
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { offset ->
                        val col = (offset.x / cellSizePx).toInt()
                        val row = (offset.y / cellSizePx).toInt()
                        if (col in 0 until 12 && row in 0 until 20) vm.onCellClicked(row, col)
                    })
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val col = (offset.x / cellSizePx).toInt()
                            val row = (offset.y / cellSizePx).toInt()
                            if (col in 0 until 12 && row in 0 until 20) vm.onDragMove(row, col)
                        },
                        onDrag = { change, _ ->
                            val col = (change.position.x / cellSizePx).toInt()
                            val row = (change.position.y / cellSizePx).toInt()
                            if (col in 0 until 12 && row in 0 until 20) vm.onDragMove(row, col)
                        },
                        onDragEnd = { vm.onDragEnd() }
                    )
                }
        ) {
            Column {
                grid.forEach { row ->
                    Row { row.forEach { cell -> CellView(cell) } }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(
                onClick = { vm.runAlgorithm() },
                modifier = Modifier.weight(1f).padding(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009688))
            ) {
                Text("START (${selectedAlgo.name})")
            }
            Button(
                onClick = { vm.resetGrid() },
                modifier = Modifier.weight(1f).padding(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
            ) {
                Text("RESET")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onShowStats,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5))
        ) {
            Text("STATS")
        }
    }
}

@Composable
fun StatsScreen(vm: PathFindingViewModel, onBack: () -> Unit) {
    val stats by vm.statsState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("SIMULATION REPORT", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))

        StatCard("TOTAL TIME", "${stats.durationMs} ms", Color(0xFFE3F2FD))
        StatCard("SCANNED SQUARE", "${stats.visitedCount} squares", Color(0xFFE8F5E9))
        StatCard("THE SHORTEST PATH", "${stats.pathLength} steps", Color(0xFFFFF3E0))

        Spacer(modifier = Modifier.height(48.dp))
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth().height(50.dp)) {
            Text("BACK TO PATHFINDER")
        }
    }
}