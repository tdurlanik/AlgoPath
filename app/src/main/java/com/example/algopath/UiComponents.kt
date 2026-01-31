package com.example.algopath

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CellView(cell: Cell) {
    val color = when (cell.type) {
        CellType.EMPTY -> Color.LightGray
        CellType.WALL -> Color.Black
        CellType.START -> Color.Green
        CellType.END -> Color.Red
        CellType.VISITED -> Color(0xFFADD8E6)
        CellType.PATH -> Color.Yellow
    }

    Box(
        modifier = Modifier
            .size(30.dp)
            .padding(1.dp)
            .background(color)
            .border(0.5.dp, Color.Gray)
    )
}

@Composable
fun StatCard(title: String, value: String, bgColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontSize = 16.sp, color = Color.Black)
            Text(text = value, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AlgorithmSelector(
    selectedAlgorithm: AlgorithmType,
    onAlgorithmSelected: (AlgorithmType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp)) {
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = selectedAlgorithm.title)
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Default.ArrowDropDown, contentDescription = "Select")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            AlgorithmType.values().forEach { algo ->
                DropdownMenuItem(
                    text = { Text(algo.title) },
                    onClick = {
                        onAlgorithmSelected(algo)
                        expanded = false
                    }
                )
            }
        }
    }
}