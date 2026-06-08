package ru.mindflow.app.presentation.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import ru.mindflow.app.control.MoodViewModel
import ru.mindflow.app.entity.MoodEntry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: MoodViewModel,
    onNavigateBack: () -> Unit
) {
    val state   by viewModel.uiState.collectAsState()
    val history by viewModel.history.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Аналитика") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Average stat
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Среднее настроение (30 дней)",
                        style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(8.dp))
                    Text("%.1f".format(state.average),
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.primary)
                    Text("из 10", style = MaterialTheme.typography.bodySmall)
                }
            }

            // Mood distribution
            if (history.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("График настроения (последние записи)",
                            style = MaterialTheme.typography.titleSmall)
                        Spacer(Modifier.height(12.dp))
                        MoodLineChart(entries = history.takeLast(14).reversed())
                    }
                }
            }

            // Distribution
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Распределение", style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(8.dp))
                    listOf(
                        "Отлично (9-10)" to history.count { it.score >= 9 },
                        "Хорошо (7-8)"  to history.count { it.score in 7..8 },
                        "Нейтрально (5-6)" to history.count { it.score in 5..6 },
                        "Плохо (1-4)"   to history.count { it.score <= 4 }
                    ).forEach { (label, count) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(label, style = MaterialTheme.typography.bodySmall)
                            Text("$count", style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MoodLineChart(entries: List<MoodEntry>) {
    val primary = MaterialTheme.colorScheme.primary
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (entries.size < 2) return@Canvas
            val w = size.width
            val h = size.height
            val stepX = w / (entries.size - 1).toFloat()

            val points = entries.mapIndexed { i, entry ->
                Offset(x = i * stepX, y = h - (entry.score / 10f) * h)
            }

            val path = Path().apply {
                moveTo(points.first().x, points.first().y)
                points.drop(1).forEach { lineTo(it.x, it.y) }
            }
            drawPath(path, color = primary, style = Stroke(width = 3f))
            points.forEach { p ->
                drawCircle(color = primary, radius = 6f, center = p)
                drawCircle(color = Color.White, radius = 3f, center = p)
            }
        }
    }
}
