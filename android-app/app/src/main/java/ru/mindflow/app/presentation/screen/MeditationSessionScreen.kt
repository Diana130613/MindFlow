package ru.mindflow.app.presentation.screen

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import ru.mindflow.app.R
import ru.mindflow.app.audio.SoundEngine
import ru.mindflow.app.ui.theme.*
import kotlin.math.PI
import kotlin.random.Random

// ── Sparkle particles (fixed, created once) ───────────────────────────────────
private data class SparkleParticle(
    val xRatio: Float, val phase: Float, val radius: Float, val isStar: Boolean
)
private val sessionSparkles = List(22) {
    SparkleParticle(
        xRatio = (Random.nextFloat() - 0.5f) * 2f,
        phase  = Random.nextFloat(),
        radius = Random.nextFloat() * 3.5f + 1.5f,
        isStar = Random.nextBoolean()
    )
}

// ── Sound-name → SoundType mapping ────────────────────────────────────────────
private fun soundTypeFor(name: String) = when (name) {
    "Вентилятор" -> SoundEngine.SoundType.FAN
    "Статика"    -> SoundEngine.SoundType.STATIC
    "Ливень"     -> SoundEngine.SoundType.SHOWER
    "Кофемашина" -> SoundEngine.SoundType.COFFEE
    "Дождь"      -> SoundEngine.SoundType.RAIN
    "Море"       -> SoundEngine.SoundType.SEA
    "Лес"        -> SoundEngine.SoundType.FOREST
    "Птицы"      -> SoundEngine.SoundType.BIRDS
    else         -> SoundEngine.SoundType.RAIN
}

// ── Main content composable (used as a tab) ───────────────────────────────────
@Composable
fun MeditationTabContent(
    modifier: Modifier = Modifier,
    onMeditationComplete: (Long) -> Unit = {}
) {
    val durations = listOf(5, 10, 15, 20)
    var selectedDuration by remember { mutableStateOf(10) }
    var remainingSeconds by remember { mutableStateOf(10 * 60) }
    var isRunning        by remember { mutableStateOf(false) }
    var isPaused         by remember { mutableStateOf(false) }

    var selectedSound  by remember { mutableStateOf<String?>(null) }
    var expandedSound  by remember { mutableStateOf<String?>(null) }

    val whiteNoiseSounds = listOf("Вентилятор", "Статика", "Ливень", "Кофемашина")
    val natureSounds     = listOf("Дождь", "Море", "Лес", "Птицы")

    // ── Sound engine lifecycle ────────────────────────────────────────────
    val engine = remember { SoundEngine() }
    DisposableEffect(Unit) { onDispose { engine.stop() } }

    // Start/stop audio when timer state or selection changes
    LaunchedEffect(isRunning, isPaused, selectedSound) {
        if (isRunning && !isPaused && selectedSound != null) {
            engine.play(soundTypeFor(selectedSound!!))
        } else {
            engine.stop()
        }
    }

    // ── Timer reset when duration changes ────────────────────────────────
    LaunchedEffect(selectedDuration) {
        remainingSeconds = selectedDuration * 60
        isRunning = false; isPaused = false
        engine.stop()
    }

    // ── Countdown ────────────────────────────────────────────────────────
    LaunchedEffect(isRunning, isPaused) {
        while (isRunning && !isPaused && remainingSeconds > 0) {
            delay(1000L); remainingSeconds--
        }
        if (remainingSeconds == 0) {
            isRunning = false; isPaused = false; engine.stop()
            onMeditationComplete(selectedDuration.toLong())
        }
    }

    // ── Animations ───────────────────────────────────────────────────────
    val swingTr = rememberInfiniteTransition(label = "swing")
    val swingAngle by swingTr.animateFloat(
        initialValue = -3f, targetValue = 3f,
        animationSpec = infiniteRepeatable(
            tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse
        ), label = "swing"
    )

    val sparkleTr = rememberInfiniteTransition(label = "sparkles")
    val sparklePhase by sparkleTr.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            tween(2800, easing = LinearEasing), RepeatMode.Restart
        ), label = "sparkPhase"
    )

    val progress = if (selectedDuration * 60 > 0)
        remainingSeconds.toFloat() / (selectedDuration * 60f) else 1f
    val minutesLeft = remainingSeconds / 60
    val secondsLeft = remainingSeconds % 60

    // ── Layout ────────────────────────────────────────────────────────────
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1A3366), Color(0xFF1E4A9C), Color(0xFF2159B5))
                )
            )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Top section: cloud + timer + controls ─────────────────────
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    // Cloud (no line version) with swing
                    Box(
                        modifier = Modifier.graphicsLayer { rotationZ = swingAngle },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.cloud_no_line),
                            contentDescription = "Облако",
                            modifier = Modifier.width(180.dp).height(110.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(Modifier.height(28.dp))

                    // ── Circular timer ────────────────────────────────────
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(190.dp)
                            .clip(CircleShape)
                            .clickable {
                                when {
                                    !isRunning && !isPaused -> {
                                        if (remainingSeconds == 0) remainingSeconds = selectedDuration * 60
                                        isRunning = true
                                    }
                                    isRunning && !isPaused -> isPaused = true
                                    else -> isPaused = false
                                }
                            }
                    ) {
                        androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
                            val sw    = 6.dp.toPx()
                            val inset = sw / 2f
                            val arcSz = Size(size.width - sw, size.height - sw)
                            drawArc(
                                Color.White.copy(alpha = 0.15f),
                                -90f, 360f, false,
                                Offset(inset, inset), arcSz,
                                style = Stroke(sw, cap = StrokeCap.Round)
                            )
                            if (isRunning || isPaused) {
                                drawArc(
                                    Color.White,
                                    -90f, 360f * progress, false,
                                    Offset(inset, inset), arcSz,
                                    style = Stroke(sw, cap = StrokeCap.Round)
                                )
                            }
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            if (isRunning || isPaused) {
                                Text(
                                    if (minutesLeft > 0) "$minutesLeft" else "$secondsLeft",
                                    fontSize = 56.sp, fontWeight = FontWeight.Bold, color = Color.White
                                )
                                Text(
                                    if (minutesLeft > 0) "мин" else "сек",
                                    fontSize = 14.sp, color = Color.White.copy(0.8f)
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    if (isPaused) "продолжить" else "пауза",
                                    fontSize = 11.sp, color = Color.White.copy(0.55f)
                                )
                            } else {
                                Text(
                                    "$selectedDuration",
                                    fontSize = 56.sp, fontWeight = FontWeight.Bold, color = Color.White
                                )
                                Text("минут", fontSize = 14.sp, color = Color.White.copy(0.8f))
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "нажмите для старта",
                                    fontSize = 11.sp, color = Color.White.copy(0.55f)
                                )
                            }
                        }
                    }

                    // ── Duration selector — closer to timer ───────────────
                    Spacer(Modifier.height(12.dp))   // ↑ was 20.dp
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        durations.forEach { dur ->
                            val sel = dur == selectedDuration
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = if (sel) Color.White.copy(0.25f) else Color.White.copy(0.07f),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable(enabled = !isRunning) { selectedDuration = dur }
                            ) {
                                Text(
                                    "${dur}м",
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                    color = if (sel) Color.White else Color.White.copy(0.5f),
                                    fontSize = 13.sp,
                                    fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    // Stop button
                    if (isRunning) {
                        Spacer(Modifier.height(10.dp))
                        IconButton(onClick = {
                            isRunning = false; isPaused = false
                            remainingSeconds = selectedDuration * 60
                            engine.stop()
                        }) {
                            Icon(Icons.Default.Stop, "Стоп", tint = Color.White.copy(0.7f))
                        }
                    }
                }

                // ── Sparkle overlay ───────────────────────────────────────
                if (isRunning && !isPaused) {
                    androidx.compose.foundation.Canvas(Modifier.fillMaxSize()) {
                        val cx       = size.width / 2f
                        val cy       = size.height * 0.22f   // cloud center ≈ upper quarter of box
                        val spreadH  = 90.dp.toPx()
                        val fallDist = size.height * 0.6f
                        sessionSparkles.forEach { s ->
                            val lp    = ((sparklePhase + s.phase) % 1f)
                            val alpha = when {
                                lp < 0.15f -> (lp / 0.15f).coerceIn(0f, 1f)
                                lp > 0.65f -> ((1f - lp) / 0.35f).coerceIn(0f, 1f)
                                else       -> 1f
                            }
                            val x = cx + s.xRatio * spreadH * (0.3f + lp * 0.7f)
                            val y = cy + lp * fallDist
                            if (s.isStar) {
                                val r = (s.radius * density).coerceAtLeast(2f)
                                listOf(
                                    Offset(x - r, y) to Offset(x + r, y),
                                    Offset(x, y - r) to Offset(x, y + r)
                                ).forEach { (a, b) ->
                                    drawLine(Color.White.copy(alpha * 0.9f), a, b, 1.8f, cap = StrokeCap.Round)
                                }
                                val rd = r * 0.55f
                                listOf(
                                    Offset(x - rd, y - rd) to Offset(x + rd, y + rd),
                                    Offset(x + rd, y - rd) to Offset(x - rd, y + rd)
                                ).forEach { (a, b) ->
                                    drawLine(Color.White.copy(alpha * 0.55f), a, b, 1f, cap = StrokeCap.Round)
                                }
                            } else {
                                drawCircle(
                                    Color.White.copy(alpha * 0.85f),
                                    (s.radius * density).coerceAtLeast(1.5f),
                                    Offset(x, y)
                                )
                            }
                        }
                    }
                }
            }

            // ── Bottom: sound cards ───────────────────────────────────────
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color    = Color(0xFF0F1A4A).copy(alpha = 0.92f)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SessionSoundCard(
                            label    = "Белый шум",
                            imageRes = R.drawable.white_noise,
                            icon     = "🌬️",
                            selected = selectedSound in whiteNoiseSounds,
                            expanded = expandedSound == "white",
                            modifier = Modifier.weight(1f),
                            onClick  = {
                                expandedSound = if (expandedSound == "white") null else "white"
                            }
                        )
                        SessionSoundCard(
                            label    = "Звуки природы",
                            imageRes = R.drawable.nature_sounds,
                            icon     = "🌿",
                            selected = selectedSound in natureSounds,
                            expanded = expandedSound == "nature",
                            modifier = Modifier.weight(1f),
                            onClick  = {
                                expandedSound = if (expandedSound == "nature") null else "nature"
                            }
                        )
                    }

                    if (expandedSound == "white") {
                        Spacer(Modifier.height(12.dp))
                        SessionSubOptions(whiteNoiseSounds, selectedSound) {
                            selectedSound = it; expandedSound = null
                            // Start playing immediately if timer is running
                            if (isRunning && !isPaused) engine.play(soundTypeFor(it))
                        }
                    }
                    if (expandedSound == "nature") {
                        Spacer(Modifier.height(12.dp))
                        SessionSubOptions(natureSounds, selectedSound) {
                            selectedSound = it; expandedSound = null
                            if (isRunning && !isPaused) engine.play(soundTypeFor(it))
                        }
                    }

                    selectedSound?.let { sound ->
                        Spacer(Modifier.height(8.dp))
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val isPlaying = isRunning && !isPaused
                            Text(
                                if (isPlaying) "♪ Воспроизводится: $sound" else "♪ Выбрано: $sound",
                                color = if (isPlaying) SkyBlue else NavyGhost,
                                fontSize = 12.sp,
                                fontWeight = if (isPlaying) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
    }
}

// ── Sound card UI ─────────────────────────────────────────────────────────────

@Composable
private fun SessionSoundCard(
    label: String,
    imageRes: Int? = null,
    gradient: List<Color> = listOf(Color(0xFF555555), Color(0xFF333333)),
    icon: String,
    selected: Boolean, expanded: Boolean,
    modifier: Modifier = Modifier, onClick: () -> Unit
) {
    Card(
        modifier = modifier.height(100.dp).clip(RoundedCornerShape(16.dp)).clickable(onClick = onClick),
        shape  = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = if (selected || expanded)
            androidx.compose.foundation.BorderStroke(2.dp, Color.White.copy(0.6f)) else null
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomStart
        ) {
            if (imageRes != null) {
                Image(
                    painter = painterResource(imageRes),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Dark overlay for readability
                Box(Modifier.fillMaxSize().background(Color.Black.copy(0.38f)))
            } else {
                Box(Modifier.fillMaxSize().background(Brush.linearGradient(gradient)))
            }
            Box(
                modifier = Modifier.align(Alignment.Center).size(40.dp)
                    .clip(CircleShape).background(Color.Black.copy(0.35f)),
                contentAlignment = Alignment.Center
            ) {
                Text(if (expanded) "▾" else "▶", color = Color.White, fontSize = 16.sp)
            }
            Column(Modifier.padding(10.dp)) {
                Text(icon, fontSize = 20.sp)
                Text(label, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun SessionSubOptions(
    options: List<String>, selectedOption: String?,
    onSelect: (String) -> Unit
) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { opt ->
            val isSel = opt == selectedOption
            Surface(
                shape  = RoundedCornerShape(20.dp),
                color  = if (isSel) NavyAccent else NavyCard,
                modifier = Modifier.clip(RoundedCornerShape(20.dp)).clickable { onSelect(opt) }
            ) {
                Text(
                    opt,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    color      = if (isSel) NavyWhite else NavyGhost,
                    fontSize   = 12.sp,
                    fontWeight = if (isSel) FontWeight.SemiBold else FontWeight.Normal
                )
            }
        }
    }
}