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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.mindflow.app.ui.theme.*

// ── Data model ───────────────────────────────────────────────────────────────

private data class Lesson(
    val title: String,
    val duration: String,
    val description: String,
    val videoUrl: String
)

private data class CourseData(
    val title: String,
    val shortDesc: String,
    val fullDesc: String,
    val icon: String,
    val gradientStart: Color,
    val gradientEnd: Color,
    val lessons: List<Lesson>
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
        shortDesc    = "5-минутные практики на каждый день",
        fullDesc     = "Короткие дыхательные упражнения для снятия тревоги и восстановления " +
                "внутреннего баланса. Каждое занятие занимает не более 5–10 минут и легко " +
                "вписывается в любой распорядок дня — утром, в перерыве или перед сном.",
        icon         = "🌊",
        gradientStart = GradBlue1,
        gradientEnd   = GradCyan1,
        lessons = listOf(
            Lesson(
                "Дыхание 4-7-8", "5 мин",
                "Вдох 4 сек → задержка 7 сек → выдох 8 сек. Мощное успокоение нервной системы.",
                "https://www.youtube.com/results?search_query=дыхание+4-7-8+техника"
            ),
            Lesson(
                "Коробочное дыхание", "5 мин",
                "Техника ВМС США для быстрого снижения стресса: 4×4×4×4 секунды.",
                "https://www.youtube.com/results?search_query=коробочное+дыхание+box+breathing"
            ),
            Lesson(
                "Дыхание животом", "7 мин",
                "Активация парасимпатической нервной системы через диафрагмальное дыхание.",
                "https://www.youtube.com/results?search_query=диафрагмальное+дыхание+упражнение"
            ),
            Lesson(
                "Полное расслабление", "10 мин",
                "Нидра-практика: постепенное введение тела и ума в состояние глубокого покоя.",
                "https://www.youtube.com/results?search_query=йога+нидра+расслабление"
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

            // Section header
            item {
                Box(
                    Modifier.fillMaxWidth().background(NavyCard).padding(horizontal = 20.dp, vertical = 4.dp)
                ) {
                    Text(
                        "Уроки",
                        style = MaterialTheme.typography.titleSmall,
                        color = NavyGhost
                    )
                }
            }

            // Lesson cards
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