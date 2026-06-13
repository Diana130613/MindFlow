package ru.mindflow.app.presentation.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.mindflow.app.control.MoodViewModel
import ru.mindflow.app.entity.MoodEntry
import ru.mindflow.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    viewModel: MoodViewModel,
    onNavigateBack: () -> Unit
) {
    val state   by viewModel.uiState.collectAsState()
    val history by viewModel.history.collectAsState()

    Scaffold(
        containerColor = NavyBase,
        topBar = {
            TopAppBar(
                title = {
                    Text("Аналитика", color = NavyWhite, fontWeight = FontWeight.SemiBold)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад", tint = NavyWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NavyDeep)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Average stat card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyCard)
            ) {
                Column(
                    Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Среднее настроение",
                        style = MaterialTheme.typography.titleSmall,
                        color = NavyGhost
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "%.1f".format(state.average),
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Bold,
                        color = SkyBlue
                    )
                    Text(
                        "из 10 за последние 30 дней",
                        style = MaterialTheme.typography.bodySmall,
                        color = NavyGhost
                    )
                }
            }

            // Chart card
            if (history.size >= 2) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = NavyCard)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text(
                            "График настроения",
                            style = MaterialTheme.typography.titleSmall,
                            color = NavyGhost
                        )
                        Spacer(Modifier.height(12.dp))
                        MoodLineChart(entries = history.take(14).reversed())
                    }
                }
            }

            // Distribution card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavyCard)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        "Распределение",
                        style = MaterialTheme.typography.titleSmall,
                        color = NavyGhost
                    )
                    Spacer(Modifier.height(12.dp))
                    listOf(
                        Triple("😄 Отлично (9–10)",    history.count { it.score >= 9 },    SkyBlue),
                        Triple("🙂 Хорошо (7–8)",      history.count { it.score in 7..8 }, NavyGhost),
                        Triple("😐 Нейтрально (5–6)",  history.count { it.score in 5..6 }, NavyGhost),
                        Triple("😢 Плохо (1–4)",        history.count { it.score <= 4 },    ErrorRed)
                    ).forEach { (label, count, tint) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(label, style = MaterialTheme.typography.bodySmall, color = NavyWhite)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = NavyBright
                            ) {
                                Text(
                                    count.toString(),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = tint,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        if (label != "😢 Плохо (1–4)") {
                            HorizontalDivider(color = NavyBright.copy(alpha = 0.4f))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MoodLineChart(entries: List<MoodEntry>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (entries.size < 2) return@Canvas
            val w = size.width
            val h = size.height
            val stepX = w / (entries.size - 1).toFloat()
            val paddingV = h * 0.1f

            val points = entries.mapIndexed { i, entry ->
                Offset(
                    x = i * stepX,
                    y = paddingV + (1f - entry.score / 10f) * (h - 2 * paddingV)
                )
            }

            // Grid lines
            for (score in listOf(2, 5, 8)) {
                val y = paddingV + (1f - score / 10f) * (h - 2 * paddingV)
                drawLine(
                    color = Color(0xFF243A8C),
                    start = Offset(0f, y),
                    end   = Offset(w, y),
                    strokeWidth = 1f
                )
            }

            // Fill area
            val fillPath = Path().apply {
                moveTo(points.first().x, h)
                points.forEach { lineTo(it.x, it.y) }
                lineTo(points.last().x, h)
                close()
            }
            drawPath(
                fillPath,
                color = Color(0xFF3B5ABD).copy(alpha = 0.25f)
            )

            // Line
            val linePath = Path().apply {
                moveTo(points.first().x, points.first().y)
                points.drop(1).forEach { lineTo(it.x, it.y) }
            }
            drawPath(linePath, color = Color(0xFF5B7BDA), style = Stroke(width = 3f))

            // Dots
            points.forEach { p ->
                drawCircle(color = Color(0xFF5B7BDA), radius = 6f, center = p)
                drawCircle(color = Color(0xFF1B2A68), radius = 3f, center = p)
            }
        }
    }
}
