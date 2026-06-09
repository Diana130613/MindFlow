package ru.mindflow.app.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.mindflow.app.control.GardenViewModel
import ru.mindflow.app.control.HomeViewModel
import ru.mindflow.app.control.MoodViewModel
import ru.mindflow.app.control.ProfileViewModel
import ru.mindflow.app.ui.theme.*
import java.util.Calendar

private enum class MainTab { HOME, TRACKER, MEDITATION, GARDEN, PROFILE }

@Composable
fun MainScreen(
    homeViewModel: HomeViewModel,
    moodViewModel: MoodViewModel,
    profileViewModel: ProfileViewModel,
    gardenViewModel: GardenViewModel,
    onNavigateToMeditations: () -> Unit,
    onNavigateToCourse: (String) -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onLoggedOut: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(MainTab.HOME) }

    Scaffold(
        containerColor = NavyBase,
        bottomBar = {
            NavigationBar(
                containerColor = NavyCard,
                tonalElevation = 0.dp
            ) {
                MainTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick  = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = when (tab) {
                                    MainTab.HOME       -> Icons.Default.Home
                                    MainTab.TRACKER    -> Icons.Default.Mood
                                    MainTab.MEDITATION -> Icons.Default.Cloud
                                    MainTab.GARDEN     -> Icons.Default.Park
                                    MainTab.PROFILE    -> Icons.Default.Person
                                },
                                contentDescription = when (tab) {
                                    MainTab.HOME       -> "Главная"
                                    MainTab.TRACKER    -> "Трекер"
                                    MainTab.MEDITATION -> "Медитация"
                                    MainTab.GARDEN     -> "Сад"
                                    MainTab.PROFILE    -> "Профиль"
                                }
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor   = NavyWhite,
                            unselectedIconColor = NavyGhost,
                            indicatorColor      = NavyBright
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (selectedTab) {
                MainTab.HOME -> HomeTabContent(
                    viewModel               = homeViewModel,
                    onNavigateToMeditations = onNavigateToMeditations,
                    onNavigateToCourse      = onNavigateToCourse,
                    onNavigateToAnalytics   = onNavigateToAnalytics
                )
                MainTab.TRACKER    -> TrackerTabContent(viewModel = moodViewModel)
                MainTab.MEDITATION -> MeditationTabContent(
                    onMeditationComplete = { minutes -> gardenViewModel.recordMeditation(minutes) }
                )
                MainTab.GARDEN     -> GardenScreen(viewModel = gardenViewModel)
                MainTab.PROFILE    -> ProfileTabContent(
                    viewModel = profileViewModel,
                    onLoggedOut = onLoggedOut
                )
            }
        }
    }
}

// ─── Home Tab ────────────────────────────────────────────────────────────────

@Composable
private fun HomeTabContent(
    viewModel: HomeViewModel,
    onNavigateToMeditations: () -> Unit,
    onNavigateToCourse: (String) -> Unit,
    onNavigateToAnalytics: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val hour = remember { Calendar.getInstance().get(Calendar.HOUR_OF_DAY) }
    val greeting = remember(hour) {
        when (hour) {
            in 5..11  -> "Доброе утро"
            in 12..16 -> "Добрый день"
            in 17..20 -> "Добрый вечер"
            else      -> "Доброй ночи"
        }
    }
    val quotes = remember {
        listOf(
            "Спокойствие — не что иное, как надлежащий порядок в мыслях",
            "Разум — это всё. Ты становишься тем, что думаешь",
            "Каждый день — это новая возможность стать лучше",
            "Ваше благополучие начинается с ваших мыслей",
            "Дышите глубже. Всё под контролем"
        )
    }
    val quote = remember {
        quotes[Calendar.getInstance().get(Calendar.DAY_OF_YEAR) % quotes.size]
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(NavyDeep, NavyBase)))
                    .padding(horizontal = 24.dp, vertical = 48.dp)
            ) {
                Column {
                    Text(
                        text = if (state.user != null) "$greeting, ${state.user!!.displayName()}!"
                               else "$greeting!",
                        style = MaterialTheme.typography.headlineMedium,
                        color = NavyWhite,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = "«$quote»",
                        style = MaterialTheme.typography.titleSmall,
                        color = NavyWhite.copy(alpha = 0.85f),
                        lineHeight = 22.sp
                    )
                    state.todayMood?.let { mood ->
                        Spacer(Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(mood.emoji(), fontSize = 20.sp)
                            Spacer(Modifier.width(6.dp))
                            Text(
                                "Сегодня: ${mood.moodLabel}",
                                style = MaterialTheme.typography.bodySmall,
                                color = NavyGhost
                            )
                        }
                    }
                }
            }
        }

        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                color = NavyCard
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        "Рекомендации",
                        style = MaterialTheme.typography.titleLarge,
                        color = NavyWhite,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(Modifier.height(16.dp))

                    RecommendationCard(
                        category      = "курс",
                        title         = "Спокойствие сознания",
                        description   = "Практический видеокурс, который научит вас управлять своим внутренним состоянием в условиях постоянного стресса и информационной перегрузки.",
                        imageOnRight  = true,
                        gradientStart = GradPurple1,
                        gradientEnd   = GradPink1,
                        cardIcon      = "🌙",
                        onClick       = { onNavigateToCourse("Спокойствие сознания") }
                    )

                    Spacer(Modifier.height(12.dp))

                    RecommendationCard(
                        category      = "курс",
                        title         = "Медитация — слои сознания",
                        description   = "Глубокое погружение в архитектуру собственного сознания через практику осознанности. Вы научитесь не просто медитировать, а понимать что именно происходит в уме.",
                        imageOnRight  = false,
                        gradientStart = GradIndigo1,
                        gradientEnd   = GradViolet1,
                        cardIcon      = "🧘",
                        onClick       = { onNavigateToCourse("Медитация — слои сознания") }
                    )

                    Spacer(Modifier.height(12.dp))

                    RecommendationCard(
                        category      = "практика",
                        title         = "Дыхание и покой",
                        description   = "Короткие дыхательные упражнения для снятия тревоги и восстановления внутреннего баланса за 5 минут.",
                        imageOnRight  = true,
                        gradientStart = GradBlue1,
                        gradientEnd   = GradCyan1,
                        cardIcon      = "🌊",
                        onClick       = { onNavigateToCourse("Дыхание и покой") }
                    )

                    Spacer(Modifier.height(20.dp))

                    OutlinedButton(
                        onClick = onNavigateToAnalytics,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyWhite),
                        border = androidx.compose.foundation.BorderStroke(1.dp, NavyBright)
                    ) {
                        Icon(Icons.Default.BarChart, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text(
                            if (state.weeklyAverage > 0)
                                "Аналитика · Среднее за неделю: %.1f".format(state.weeklyAverage)
                            else
                                "Посмотреть аналитику"
                        )
                    }

                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun RecommendationCard(
    category: String,
    title: String,
    description: String,
    imageOnRight: Boolean,
    gradientStart: Color,
    gradientEnd: Color,
    cardIcon: String = "✨",
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyBright),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!imageOnRight) {
                GradientImageBox(gradientStart, gradientEnd, cardIcon)
                Spacer(Modifier.width(12.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    category,
                    style = MaterialTheme.typography.labelSmall,
                    color = NavyGhost
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                    color = NavyWhite,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall,
                    color = NavyGhost,
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
            }
            if (imageOnRight) {
                Spacer(Modifier.width(12.dp))
                GradientImageBox(gradientStart, gradientEnd, cardIcon)
            }
        }
    }
}

@Composable
private fun GradientImageBox(start: Color, end: Color, icon: String = "✨") {
    Box(
        modifier = Modifier
            .size(width = 96.dp, height = 112.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Brush.linearGradient(listOf(start, end))),
        contentAlignment = Alignment.Center
    ) {
        Text(icon, fontSize = 40.sp, modifier = Modifier.graphicsLayer(alpha = 0.85f))
    }
}

// ─── Tracker Tab ─────────────────────────────────────────────────────────────

private data class MoodOption(val emoji: String, val label: String, val score: Int)

private val moodOptions = listOf(
    MoodOption("😄", "Отлично",     10),
    MoodOption("😴", "Устал",        5),
    MoodOption("😐", "Нейтрально",   6),
    MoodOption("😅", "Нервничаю",    4),
    MoodOption("😵", "Растерян",     3),
    MoodOption("😤", "Злюсь",        3),
    MoodOption("😢", "Грустно",      2),
    MoodOption("😰", "Тревожно",     3),
    MoodOption("😑", "Безразлично",  5)
)

@Composable
private fun TrackerTabContent(viewModel: MoodViewModel) {
    val state   by viewModel.uiState.collectAsState()
    val history by viewModel.history.collectAsState()

    val today = remember {
        val cal = Calendar.getInstance()
        "%02d.%02d.%d".format(
            cal.get(Calendar.DAY_OF_MONTH),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.YEAR)
        )
    }

    var justSaved by remember { mutableStateOf(false) }
    LaunchedEffect(state.savedEntry) {
        if (state.savedEntry != null) {
            justSaved = true
            viewModel.resetSaved()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(NavyDeep, NavyBase)))
                .padding(horizontal = 24.dp, vertical = 40.dp)
        ) {
            Column {
                Text(
                    "Трекер настроения",
                    style = MaterialTheme.typography.headlineMedium,
                    color = NavyWhite,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.DateRange,
                        null,
                        tint = NavyGhost,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "Сегодня, $today",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NavyGhost
                    )
                }
            }
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = NavyCard
        ) {
            LazyColumn(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(0.dp)
            ) {
                if (justSaved) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = NavyBright),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Check, null, tint = SkyBlue, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Настроение записано!", color = NavyWhite, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }
                }

                item {
                    // 3×3 emoji grid
                    for (row in 0..2) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            for (col in 0..2) {
                                val opt = moodOptions[row * 3 + col]
                                EmojiMoodButton(
                                    emoji   = opt.emoji,
                                    label   = opt.label,
                                    enabled = !state.isSaving,
                                    onClick = { viewModel.saveMood(opt.score, null) }
                                )
                            }
                        }
                        if (row < 2) Spacer(Modifier.height(4.dp))
                    }
                }

                if (history.isNotEmpty()) {
                    item {
                        Spacer(Modifier.height(24.dp))
                        Text(
                            "История",
                            style = MaterialTheme.typography.titleSmall,
                            color = NavyGhost
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                    items(history.take(5), key = { it.id }) { entry ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(entry.emoji(), fontSize = 20.sp)
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    entry.moodLabel,
                                    color = NavyWhite,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Text(
                                entry.recordedAt.take(10),
                                color = NavyGhost,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                        HorizontalDivider(color = NavyBright.copy(alpha = 0.5f))
                    }
                }
            }
        }
    }
}

@Composable
private fun EmojiMoodButton(emoji: String, label: String, enabled: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .border(2.dp, NavyBright, CircleShape)
                .background(NavyBase.copy(alpha = 0.6f))
                .clickable(enabled = enabled, onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 34.sp)
        }
        Spacer(Modifier.height(4.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = NavyGhost,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(max = 80.dp)
        )
    }
}

// ─── Profile Tab ─────────────────────────────────────────────────────────────

@Composable
private fun ProfileTabContent(
    viewModel: ProfileViewModel,
    onLoggedOut: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.loggedOut) {
        if (state.loggedOut) onLoggedOut()
    }

    var showEditDialog by remember { mutableStateOf(false) }
    var reminders      by remember { mutableStateOf(true) }
    var notifications  by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(NavyDeep, NavyBase)))
                .padding(horizontal = 24.dp, vertical = 40.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Профиль",
                        style = MaterialTheme.typography.headlineMedium,
                        color = NavyWhite,
                        fontWeight = FontWeight.Bold
                    )
                    if (state.daysWithUs > 0) {
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Уже ${state.daysWithUs} дней с нами!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = NavyGhost
                        )
                    }
                }

                Box {
                    Surface(
                        shape = CircleShape,
                        color = NavyBright,
                        modifier = Modifier.size(72.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("😊", fontSize = 32.sp)
                        }
                    }
                    IconButton(
                        onClick = { showEditDialog = true },
                        modifier = Modifier
                            .size(26.dp)
                            .align(Alignment.TopEnd)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            "Редактировать",
                            tint = NavyGhost,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            color = NavyCard
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {
                state.user?.let { user ->
                    Text(
                        user.displayName(),
                        style = MaterialTheme.typography.titleMedium,
                        color = NavyWhite,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        user.email,
                        style = MaterialTheme.typography.bodySmall,
                        color = NavyGhost
                    )
                    Spacer(Modifier.height(20.dp))
                }

                SettingsToggleRow(
                    label = "Напоминания",
                    checked = reminders,
                    onCheckedChange = { reminders = it }
                )
                Spacer(Modifier.height(8.dp))
                SettingsToggleRow(
                    label = "Уведомления",
                    checked = notifications,
                    onCheckedChange = { notifications = it }
                )

                Spacer(Modifier.height(24.dp))

                ProfileLinkRow("Наш сайт")
                ProfileLinkRow("Мы на Rutube")

                Spacer(Modifier.height(24.dp))

                OutlinedButton(
                    onClick = viewModel::logout,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ErrorRed.copy(alpha = 0.5f))
                ) {
                    Text("Выйти из аккаунта")
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }

    if (showEditDialog) {
        var editedName by remember { mutableStateOf(state.user?.name ?: "") }
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            containerColor   = NavyBright,
            title = {
                Text("Редактировать имя", color = NavyWhite)
            },
            text = {
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    label = { Text("Имя") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor     = NavyWhite,
                        unfocusedTextColor   = NavyWhite,
                        focusedLabelColor    = SkyBlue,
                        unfocusedLabelColor  = NavyGhost,
                        focusedBorderColor   = SkyBlue,
                        unfocusedBorderColor = NavyGhost
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (editedName.isNotBlank()) {
                            viewModel.updateName(editedName.trim())
                        }
                        showEditDialog = false
                    }
                ) { Text("Сохранить", color = SkyBlue) }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Отмена", color = NavyGhost)
                }
            }
        )
    }
}

@Composable
private fun SettingsToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape  = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = NavyBase)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                label,
                style    = MaterialTheme.typography.bodyMedium,
                color    = NavyWhite,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor  = NavyWhite,
                    checkedTrackColor  = NavyAccent,
                    uncheckedThumbColor = NavyGhost,
                    uncheckedTrackColor = NavyCard
                )
            )
        }
    }
}

@Composable
private fun ProfileLinkRow(label: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style          = MaterialTheme.typography.bodyMedium,
            color          = NavyWhite,
            textDecoration = TextDecoration.Underline
        )
        Icon(
            Icons.AutoMirrored.Filled.ArrowForward,
            null,
            tint     = NavyWhite,
            modifier = Modifier.size(16.dp)
        )
    }
    HorizontalDivider(color = NavyBright.copy(alpha = 0.5f))
}