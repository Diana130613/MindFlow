package ru.mindflow.app.presentation.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.mindflow.app.ui.theme.*

// ── Data models ───────────────────────────────────────────────────────────────

private data class Lesson(
    val title: String,
    val duration: String,
    val description: String,
    val videoUrl: String
)

private data class RhythmBlock(
    val label: String,   // "Вдох", "Задержка", "Выдох"
    val seconds: Int,
    val color: Color
)

private data class BreathingExercise(
    val name: String,
    val duration: String,
    val tagline: String,
    val rhythm: List<RhythmBlock>,
    val steps: List<String>,
    val benefits: List<String>
)

private data class CourseData(
    val title: String,
    val shortDesc: String,
    val fullDesc: String,
    val icon: String,
    val gradientStart: Color,
    val gradientEnd: Color,
    val lessons: List<Lesson> = emptyList(),
    val breathingExercises: List<BreathingExercise>? = null
)

// ── Static course catalogue ───────────────────────────────────────────────────

private val courseCatalogue = listOf(
    CourseData(
        title        = "Спокойствие сознания",
        shortDesc    = "Управление стрессом через осознанность",
        fullDesc     = "Практический видеокурс, который научит вас управлять своим внутренним " +
                "состоянием в условиях постоянного стресса и информационной перегрузки. " +
                "Вы освоите конкретные техники для успокоения ума, научитесь замечать " +
                "тревожные мысли и мягко отпускать их.",
        icon         = "🌙",
        gradientStart = GradPurple1,
        gradientEnd   = GradPink1,
        lessons = listOf(
            Lesson(
                "Введение: что такое спокойствие?", "3 мин",
                "Разберём, почему ум постоянно в движении и как это изменить.",
                "https://www.youtube.com/results?search_query=медитация+для+начинающих+успокоение"
            ),
            Lesson(
                "Дыхание — ключ к покою", "10 мин",
                "Техника диафрагмального дыхания. Практика с таймером.",
                "https://www.youtube.com/results?search_query=дыхательные+упражнения+для+успокоения"
            ),
            Lesson(
                "Сканирование тела", "15 мин",
                "Последовательное расслабление всех групп мышц от ног до головы.",
                "https://www.youtube.com/results?search_query=сканирование+тела+медитация"
            ),
            Lesson(
                "Осознанность в повседневной жизни", "20 мин",
                "Как применять практику в моменты стресса — на работе, дома, в транспорте.",
                "https://www.youtube.com/results?search_query=осознанность+mindfulness+практика"
            )
        )
    ),
    CourseData(
        title        = "Медитация — слои сознания",
        shortDesc    = "Глубокое погружение в природу ума",
        fullDesc     = "Глубокое погружение в архитектуру собственного сознания через практику " +
                "осознанности. Вы научитесь не просто медитировать, а понимать, что именно " +
                "происходит в вашем уме во время практики: какие слои восприятия существуют " +
                "и как с ними работать.",
        icon         = "🧘",
        gradientStart = GradIndigo1,
        gradientEnd   = GradViolet1,
        lessons = listOf(
            Lesson(
                "Слои восприятия", "5 мин",
                "Ощущения → эмоции → мысли → наблюдатель. Модель четырёх слоёв.",
                "https://www.youtube.com/results?search_query=осознанная+медитация+слои+сознания"
            ),
            Lesson(
                "Практика: наблюдатель", "10 мин",
                "Техника «шага назад»: как занять позицию наблюдателя за своими мыслями.",
                "https://www.youtube.com/results?search_query=медитация+наблюдатель+мыслей"
            ),
            Lesson(
                "Работа с эмоциями", "15 мин",
                "Принятие неприятных чувств без подавления и без растворения в них.",
                "https://www.youtube.com/results?search_query=медитация+работа+с+эмоциями"
            ),
            Lesson(
                "Интеграция: целостное присутствие", "20 мин",
                "Объединение всех уровней в единую практику полного присутствия.",
                "https://www.youtube.com/results?search_query=медитация+присутствие+mindfulness"
            )
        )
    ),
    CourseData(
        title        = "Дыхание и покой",
        shortDesc    = "4 техники · от 5 до 10 минут",
        fullDesc     = "Короткие дыхательные упражнения для снятия тревоги и восстановления " +
                "внутреннего баланса. Каждая техника занимает 5–10 минут и легко " +
                "вписывается в любой распорядок — утром, в перерыве на работе или перед сном.",
        icon         = "🌊",
        gradientStart = GradBlue1,
        gradientEnd   = GradCyan1,
        breathingExercises = listOf(
            BreathingExercise(
                name     = "Дыхание 4-7-8",
                duration = "5 мин",
                tagline  = "Быстрое успокоение нервной системы",
                rhythm   = listOf(
                    RhythmBlock("Вдох",     4, Color(0xFF42A5F5)),
                    RhythmBlock("Задержка", 7, Color(0xFFAB47BC)),
                    RhythmBlock("Выдох",    8, Color(0xFF26A69A))
                ),
                steps = listOf(
                    "Сядьте прямо, закройте глаза",
                    "Прижмите кончик языка к нёбу за верхними зубами",
                    "Сделайте полный выдох через рот со звуком «фух»",
                    "Закройте рот и вдыхайте через нос 4 секунды",
                    "Задержите дыхание на 7 секунд",
                    "Выдыхайте через рот со звуком «фух» 8 секунд",
                    "Повторите цикл 4 раза"
                ),
                benefits = listOf("Снимает тревогу", "Улучшает сон", "Снижает давление")
            ),
            BreathingExercise(
                name     = "Коробочное дыхание",
                duration = "5 мин",
                tagline  = "Техника концентрации и контроля стресса",
                rhythm   = listOf(
                    RhythmBlock("Вдох",     4, Color(0xFF42A5F5)),
                    RhythmBlock("Задержка", 4, Color(0xFFAB47BC)),
                    RhythmBlock("Выдох",    4, Color(0xFF26A69A)),
                    RhythmBlock("Пауза",    4, Color(0xFFFF7043))
                ),
                steps = listOf(
                    "Примите удобное положение сидя",
                    "Медленно выдохните весь воздух",
                    "Вдыхайте через нос, считая до 4",
                    "Задержите дыхание на счёт 4",
                    "Выдыхайте через рот на счёт 4",
                    "Сделайте паузу без воздуха на счёт 4",
                    "Повторите 4–6 раз"
                ),
                benefits = listOf("Фокус и ясность ума", "Быстрый антистресс", "Используется в ВМС США")
            ),
            BreathingExercise(
                name     = "Дыхание животом",
                duration = "7 мин",
                tagline  = "Активация парасимпатической системы",
                rhythm   = listOf(
                    RhythmBlock("Вдох",  5, Color(0xFF42A5F5)),
                    RhythmBlock("Выдох", 6, Color(0xFF26A69A))
                ),
                steps = listOf(
                    "Лягте на спину или сядьте удобно",
                    "Положите одну руку на грудь, другую на живот",
                    "Вдыхайте медленно носом: живот поднимается, грудь неподвижна",
                    "Задержите на 1–2 секунды",
                    "Выдыхайте медленно через рот, живот опускается",
                    "Рука на груди должна оставаться почти неподвижной",
                    "Практикуйте 7–10 минут"
                ),
                benefits = listOf("Снижает кортизол", "Расслабляет мышцы", "Улучшает пищеварение")
            ),
            BreathingExercise(
                name     = "Полное расслабление",
                duration = "10 мин",
                tagline  = "Йога-нидра: погружение в покой",
                rhythm   = listOf(
                    RhythmBlock("Вдох",     4, Color(0xFF42A5F5)),
                    RhythmBlock("Задержка", 2, Color(0xFFAB47BC)),
                    RhythmBlock("Выдох",    6, Color(0xFF26A69A)),
                    RhythmBlock("Пауза",    2, Color(0xFF78909C))
                ),
                steps = listOf(
                    "Лягте в позу шавасана (на спине, руки вдоль тела)",
                    "Закройте глаза и позвольте телу расслабиться",
                    "Дышите мягко, удлиняя выдох",
                    "Мысленно проходите по телу: пальцы ног → голени → бёдра → живот → грудь → руки → голова",
                    "На каждой части тела делайте паузу и отпускайте напряжение",
                    "Если появляются мысли — просто наблюдайте, не вовлекаясь",
                    "Завершите несколькими глубокими вдохами и мягко откройте глаза"
                ),
                benefits = listOf("Глубокое восстановление", "Снижает бессонницу", "Уменьшает боль")
            )
        )
    )
)

private fun findCourse(title: String): CourseData =
    courseCatalogue.find { it.title == title } ?: courseCatalogue[0]

// ── Screen ────────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseScreen(
    title: String,
    onNavigateBack: () -> Unit
) {
    val course  = findCourse(title)
    val context = LocalContext.current

    Scaffold(
        containerColor = NavyBase,
        topBar = {
            TopAppBar(
                title = {
                    Text(course.title, color = NavyWhite, fontWeight = FontWeight.SemiBold, maxLines = 1)
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
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // Hero banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .background(Brush.linearGradient(listOf(course.gradientStart, course.gradientEnd))),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(course.icon, fontSize = 72.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            course.shortDesc,
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Description card
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                    color = NavyCard
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Text(
                            "О курсе",
                            style = MaterialTheme.typography.titleMedium,
                            color = NavyGhost,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            course.fullDesc,
                            style = MaterialTheme.typography.bodyMedium,
                            color = NavyWhite,
                            lineHeight = 24.sp
                        )
                        Spacer(Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            CourseChip("${course.lessons.size} урока")
                            CourseChip(
                                course.lessons.sumOf { it.duration.filter(Char::isDigit).toIntOrNull() ?: 0 }
                                    .let { "$it мин общее" }
                            )
                        }
                    }
                }
            }

            if (course.breathingExercises != null) {
                // ── Breathing practice layout ──────────────────────────────
                item {
                    Box(Modifier.fillMaxWidth().background(NavyCard)
                        .padding(horizontal = 20.dp, vertical = 4.dp)) {
                        Text("Техники", style = MaterialTheme.typography.titleSmall, color = NavyGhost)
                    }
                }
                itemsIndexed(course.breathingExercises) { _, exercise ->
                    Box(Modifier.fillMaxWidth().background(NavyCard)) {
                        BreathingExerciseCard(exercise)
                    }
                }
            } else {
                // ── Video lesson layout ────────────────────────────────────
                item {
                    Box(Modifier.fillMaxWidth().background(NavyCard)
                        .padding(horizontal = 20.dp, vertical = 4.dp)) {
                        Text("Уроки", style = MaterialTheme.typography.titleSmall, color = NavyGhost)
                    }
                }
                itemsIndexed(course.lessons) { index, lesson ->
                    Box(Modifier.fillMaxWidth().background(NavyCard)) {
                        LessonCard(
                            index   = index + 1,
                            lesson  = lesson,
                            onWatch = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(lesson.videoUrl))
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LessonCard(index: Int, lesson: Lesson, onWatch: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape  = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = NavyBright)
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Lesson number circle
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(NavyAccent),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "$index",
                                color = NavyWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                        Spacer(Modifier.width(10.dp))
                        Column {
                            Text(
                                lesson.title,
                                style = MaterialTheme.typography.titleSmall,
                                color = NavyWhite,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                lesson.duration,
                                style = MaterialTheme.typography.labelSmall,
                                color = NavyGhost
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))
                Text(
                    lesson.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = NavyGhost,
                    lineHeight = 19.sp
                )

                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onWatch,
                    modifier = Modifier.fillMaxWidth().height(42.dp),
                    shape  = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NavyAccent,
                        contentColor   = NavyWhite
                    )
                ) {
                    Icon(
                        Icons.Default.PlayCircle,
                        null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("Смотреть видео", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
private fun CourseChip(text: String) {
    Surface(shape = RoundedCornerShape(8.dp), color = NavyBright) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = SkyBlue
        )
    }
}

@Composable
private fun BreathingExerciseCard(exercise: BreathingExercise) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape  = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = NavyBright)
        ) {
            Column(Modifier.padding(16.dp)) {

                // ── Header ────────────────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        exercise.name,
                        style = MaterialTheme.typography.titleSmall,
                        color = NavyWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(shape = RoundedCornerShape(8.dp), color = NavyAccent.copy(0.5f)) {
                        Text(
                            exercise.duration,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = NavyWhite
                        )
                    }
                }
                Spacer(Modifier.height(2.dp))
                Text(exercise.tagline, style = MaterialTheme.typography.bodySmall, color = SkyBlue)

                Spacer(Modifier.height(14.dp))

                // ── Breathing rhythm ──────────────────────────────────────
                Text(
                    "Ритм дыхания",
                    style = MaterialTheme.typography.labelMedium,
                    color = NavyGhost,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    exercise.rhythm.forEachIndexed { i, block ->
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(block.color.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        "${block.seconds}с",
                                        color = block.color,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                block.label,
                                style = MaterialTheme.typography.labelSmall,
                                color = NavyGhost,
                                textAlign = TextAlign.Center
                            )
                        }
                        if (i < exercise.rhythm.lastIndex) {
                            Text(
                                "→",
                                color = NavyGhost,
                                modifier = Modifier.align(Alignment.CenterVertically),
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // ── Steps ─────────────────────────────────────────────────
                Text(
                    "Как выполнять",
                    style = MaterialTheme.typography.labelMedium,
                    color = NavyGhost,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))
                exercise.steps.forEachIndexed { i, step ->
                    Row(
                        modifier = Modifier.padding(vertical = 3.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(androidx.compose.foundation.shape.CircleShape)
                                .background(NavyAccent.copy(0.6f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("${i + 1}", color = NavyWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(
                            step,
                            style = MaterialTheme.typography.bodySmall,
                            color = NavyWhite,
                            lineHeight = 19.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                // ── Benefits ──────────────────────────────────────────────
                Text(
                    "Польза",
                    style = MaterialTheme.typography.labelMedium,
                    color = NavyGhost,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    exercise.benefits.forEach { benefit ->
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = GradCyan1.copy(alpha = 0.2f)
                        ) {
                            Text(
                                "✓ $benefit",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF80DEEA)
                            )
                        }
                    }
                }
            }
        }
    }
}