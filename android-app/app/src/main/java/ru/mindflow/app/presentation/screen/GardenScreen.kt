package ru.mindflow.app.presentation.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.mindflow.app.control.GardenViewModel
import ru.mindflow.app.garden.GardenProgress
import ru.mindflow.app.ui.theme.*
import kotlin.math.*

@Composable
fun GardenScreen(
    viewModel: GardenViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NavyBase)
    ) {
        // ── Header ──────────────────────────────────────────────────────────
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(NavyDeep, NavyBase)))
                    .padding(horizontal = 24.dp, vertical = 40.dp)
            ) {
                Column {
                    Text(
                        "Мой сад",
                        style = MaterialTheme.typography.headlineMedium,
                        color = NavyWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        state.levelName,
                        style = MaterialTheme.typography.titleSmall,
                        color = SkyBlue
                    )
                    Spacer(Modifier.height(16.dp))

                    // Stats row
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        StatChip("⏱ ${state.totalMinutes} мин")
                        StatChip("🔥 ${state.streakDays} дней")
                        StatChip("🌸 ${state.flowers}")
                    }

                    Spacer(Modifier.height(14.dp))

                    // Progress bar
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Уровень ${state.level}",
                                style = MaterialTheme.typography.labelMedium,
                                color = NavyGhost
                            )
                            if (state.level < 10) {
                                Text(
                                    "до ${state.nextLevelMinutes} мин",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = NavyGhost
                                )
                            } else {
                                Text("Максимальный!", style = MaterialTheme.typography.labelMedium, color = SkyBlue)
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { state.progressToNext },
                            modifier = Modifier.fillMaxWidth().height(8.dp),
                            color = SkyBlue,
                            trackColor = NavyBright,
                            strokeCap = StrokeCap.Round
                        )
                    }
                }
            }
        }

        // ── Garden canvas ────────────────────────────────────────────────────
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color = NavyCard
            ) {
                GardenCanvas(
                    level      = state.level,
                    flowers    = state.flowers,
                    hasGolden  = state.hasGoldenFlower,
                    hasRainbow = state.hasRainbowFlower,
                    decos      = state.unlockedDecorations,
                    modifier   = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(top = 8.dp)
                )
            }
        }

        // ── Achievements ─────────────────────────────────────────────────────
        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = NavyCard
            ) {
                Column(Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
                    Text(
                        "Достижения",
                        style = MaterialTheme.typography.titleMedium,
                        color = NavyWhite,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(12.dp))

                    val achievements = listOf(
                        Achievement("🪨", "Камушки",      "3 дня подряд",    "stones" in state.unlockedDecorations),
                        Achievement("🪑", "Скамейка",     "10 дней подряд",  "bench"  in state.unlockedDecorations),
                        Achievement("🏮", "Фонарик",      "50 часов практики","lantern" in state.unlockedDecorations),
                        Achievement("💧", "Ручеёк",       "100 часов практики","stream" in state.unlockedDecorations),
                        Achievement("🐦", "Птичка",       "30 дней подряд",  "bird"   in state.unlockedDecorations),
                        Achievement("🌟", "Золотой цветок","7 дней подряд",  state.hasGoldenFlower),
                        Achievement("🌈", "Радужный цветок","30 дней подряд",state.hasRainbowFlower),
                        Achievement("🏆", "Легенда",      "Уровень 10",      state.level >= 10)
                    )

                    // 2-column grid
                    for (row in achievements.chunked(2)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            row.forEach { ach ->
                                AchievementCard(ach, Modifier.weight(1f))
                            }
                            // Fill if odd last row
                            if (row.size == 1) Spacer(Modifier.weight(1f))
                        }
                        Spacer(Modifier.height(10.dp))
                    }
                }
            }
        }

        item { Spacer(Modifier.height(20.dp)) }
    }
}

// ── Subcomposables ────────────────────────────────────────────────────────────

@Composable
private fun StatChip(text: String) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = NavyBright
    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
            color = NavyWhite,
            fontSize = 12.sp
        )
    }
}

private data class Achievement(
    val icon: String, val name: String,
    val condition: String, val unlocked: Boolean
)

@Composable
private fun AchievementCard(ach: Achievement, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape  = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (ach.unlocked) NavyBright else NavyBase
        )
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                if (ach.unlocked) ach.icon else "❓",
                fontSize = 24.sp
            )
            Spacer(Modifier.width(8.dp))
            Column {
                Text(
                    ach.name,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (ach.unlocked) NavyWhite else NavyGhost
                )
                Text(
                    ach.condition,
                    fontSize = 10.sp,
                    color = NavyGhost
                )
            }
        }
    }
}

// ── Canvas garden scene ───────────────────────────────────────────────────────

@Composable
private fun GardenCanvas(
    level: Int, flowers: Int,
    hasGolden: Boolean, hasRainbow: Boolean,
    decos: Set<String>,
    modifier: Modifier = Modifier
) {
    val anim = remember { Animatable(0f) }
    LaunchedEffect(level) {
        anim.snapTo(0f)
        anim.animateTo(1f, animationSpec = tween(1400, easing = FastOutSlowInEasing))
    }
    val progress = anim.value

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawGround()
            drawTree(level, progress, density)
            if (flowers > 0) drawFlowers(flowers, hasGolden, hasRainbow)
        }

        // Decoration emojis overlaid
        if ("stones"  in decos) DecoText("🪨", Alignment.BottomStart,  offsetXDp = 28,  offsetYDp = 48)
        if ("bench"   in decos) DecoText("🪑", Alignment.BottomEnd,    offsetXDp = 28,  offsetYDp = 48)
        if ("lantern" in decos) DecoText("🏮", Alignment.BottomStart,  offsetXDp = 68,  offsetYDp = 48)
        if ("stream"  in decos) DecoText("💧", Alignment.BottomEnd,    offsetXDp = 68,  offsetYDp = 48)
        if ("bird"    in decos) DecoText("🐦", Alignment.TopCenter,    offsetXDp = 36,  offsetYDp = 24)
    }
}

@Composable
private fun BoxScope.DecoText(
    emoji: String, alignment: Alignment,
    offsetXDp: Int, offsetYDp: Int
) {
    Text(
        emoji,
        fontSize = 22.sp,
        modifier = Modifier
            .align(alignment)
            .padding(
                start  = if (alignment == Alignment.BottomStart || alignment == Alignment.TopStart) offsetXDp.dp else 0.dp,
                end    = if (alignment == Alignment.BottomEnd   || alignment == Alignment.TopEnd)   offsetXDp.dp else 0.dp,
                bottom = if (alignment == Alignment.BottomStart || alignment == Alignment.BottomEnd) offsetYDp.dp else 0.dp,
                top    = if (alignment == Alignment.TopCenter   || alignment == Alignment.TopStart   ||
                              alignment == Alignment.TopEnd) offsetYDp.dp else 0.dp
            )
    )
}

// ── DrawScope extensions ──────────────────────────────────────────────────────

private fun DrawScope.drawGround() {
    // Sky gradient
    drawRect(Brush.verticalGradient(listOf(Color(0xFF1A2E5A), Color(0xFF1E3B70))))

    // Ground oval
    val gY = size.height * 0.78f
    drawOval(
        color   = Color(0xFF1B5E20).copy(0.5f),
        topLeft = Offset(size.width * 0.1f, gY - 12.dp.toPx()),
        size    = Size(size.width * 0.8f, 30.dp.toPx())
    )
    drawOval(
        color   = Color(0xFF2E7D32).copy(0.3f),
        topLeft = Offset(0f, gY),
        size    = Size(size.width, size.height - gY)
    )
}

private fun DrawScope.drawTree(level: Int, anim: Float, density: Float) {
    val cx    = size.width / 2f
    val baseY = size.height * 0.78f

    if (level <= 2) {
        // Sprout — thin stem + two leaves
        val stemH = (20f + level * 10f) * density * anim
        drawLine(Color(0xFF6D4C41), Offset(cx, baseY), Offset(cx, baseY - stemH), 3f * density)
        if (anim > 0.4f) {
            val lr = 12f * density * ((anim - 0.4f) / 0.6f).coerceIn(0f, 1f)
            drawCircle(Color(0xFF66BB6A), lr, Offset(cx - lr * 0.8f, baseY - stemH * 0.7f))
            drawCircle(Color(0xFF66BB6A), lr, Offset(cx + lr * 0.8f, baseY - stemH * 0.7f))
        }
        return
    }

    val trunkW = (6f + level * 2.5f) * density
    val trunkH = (24f + level * 10f) * density * anim
    val topY   = baseY - trunkH

    // Trunk (trapezoid)
    val trunkPath = Path().apply {
        moveTo(cx - trunkW, baseY)
        lineTo(cx + trunkW, baseY)
        lineTo(cx + trunkW * 0.5f, topY)
        lineTo(cx - trunkW * 0.5f, topY)
        close()
    }
    drawPath(trunkPath, Color(0xFF795548))
    drawLine(Color(0xFFA1887F), Offset(cx - trunkW * 0.3f, baseY), Offset(cx - trunkW * 0.15f, topY), 1.5f * density)

    // Canopy
    val cr   = (16f + level * 11f) * density * anim
    val cy   = topY - cr * 0.38f
    val (base, light) = canopyColors(level)

    // Shadow side circles
    drawCircle(base.copy(0.65f), cr * 0.72f, Offset(cx - cr * 0.36f, cy + cr * 0.18f))
    drawCircle(base.copy(0.65f), cr * 0.72f, Offset(cx + cr * 0.36f, cy + cr * 0.18f))
    // Main
    drawCircle(base, cr, Offset(cx, cy))
    // Highlight
    drawCircle(light.copy(0.5f), cr * 0.4f, Offset(cx - cr * 0.22f, cy - cr * 0.25f))

    // Glow for level 8+
    if (level >= 8) {
        drawCircle(Color(0xFFFFEE58).copy(0.15f * anim), cr * 1.3f, Offset(cx, cy))
        drawCircle(Color(0xFFFFEE58).copy(0.07f * anim), cr * 1.6f, Offset(cx, cy))
    }
    // Stars for level 9-10
    if (level >= 9) {
        val starPositions = listOf(
            Offset(cx - cr * 0.8f, cy - cr * 0.6f),
            Offset(cx + cr * 0.85f, cy - cr * 0.5f),
            Offset(cx, cy - cr * 1.05f),
            Offset(cx + cr * 0.5f, cy + cr * 0.1f)
        )
        starPositions.forEach { pos ->
            val r = 4f * density
            drawLine(Color.White.copy(0.9f), Offset(pos.x - r, pos.y), Offset(pos.x + r, pos.y), 1.5f * density)
            drawLine(Color.White.copy(0.9f), Offset(pos.x, pos.y - r), Offset(pos.x, pos.y + r), 1.5f * density)
        }
    }
}

private fun canopyColors(level: Int): Pair<Color, Color> = when {
    level >= 9 -> Color(0xFFFFD700) to Color(0xFFFFF176)
    level >= 7 -> Color(0xFF558B2F) to Color(0xFFAED581)
    level >= 5 -> Color(0xFF2E7D32) to Color(0xFF66BB6A)
    level >= 3 -> Color(0xFF388E3C) to Color(0xFF81C784)
    else       -> Color(0xFF66BB6A) to Color(0xFFA5D6A7)
}

private fun DrawScope.drawFlowers(count: Int, hasGolden: Boolean, hasRainbow: Boolean) {
    val cx      = size.width / 2f
    val baseY   = size.height * 0.78f
    val spreadR = size.width * 0.36f

    val palette = listOf(
        Color(0xFFFF69B4), Color(0xFFFF1493), Color(0xFFFFB6C1),
        Color(0xFFDA70D6), Color(0xFFFF7043), Color(0xFFFFD700),
        Color(0xFF87CEEB), Color(0xFF98FB98)
    )

    val n = count.coerceAtMost(15)
    for (i in 0 until n) {
        val angle = PI * (i + 0.5) / n
        val fx = (cx - spreadR * cos(angle)).toFloat()
        val fy = (baseY + 4.dp.toPx() - spreadR * 0.22 * sin(angle)).toFloat()

        val color = when {
            i == n - 1 && hasRainbow -> Color(0xFFFF69B4)
            i == n - 2 && hasGolden  -> Color(0xFFFFD700)
            else -> palette[i % palette.size]
        }

        val pr = 4f.dp.toPx()
        for (p in 0..4) {
            val a = p * 72.0 * PI / 180.0
            drawCircle(color.copy(0.85f), pr, Offset((fx + pr * 1.5f * cos(a)).toFloat(), (fy + pr * 1.5f * sin(a)).toFloat()))
        }
        drawCircle(Color(0xFFFFF9C4).copy(0.95f), pr * 0.55f, Offset(fx, fy))
    }
}