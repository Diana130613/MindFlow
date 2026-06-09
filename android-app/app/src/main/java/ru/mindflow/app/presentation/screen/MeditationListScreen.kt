package ru.mindflow.app.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import ru.mindflow.app.control.MeditationViewModel
import ru.mindflow.app.entity.Meditation
import ru.mindflow.app.ui.theme.*

private val cardGradients = listOf(
    listOf(GradPurple1, GradPink1),
    listOf(GradIndigo1, GradViolet1),
    listOf(GradBlue1,   GradCyan1),
    listOf(GradPurple1, GradIndigo1),
    listOf(GradViolet1, GradPink1),
    listOf(GradCyan1,   GradBlue1),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeditationListScreen(
    viewModel: MeditationViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (Long) -> Unit
) {
    val state       by viewModel.uiState.collectAsState()
    val meditations by viewModel.meditations.collectAsState()

    Scaffold(
        containerColor = NavyBase,
        topBar = {
            TopAppBar(
                title = {
                    Text("Медитации", color = NavyWhite, fontWeight = FontWeight.SemiBold)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Назад", tint = NavyWhite)
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::sync) {
                        Icon(Icons.Default.Refresh, "Обновить", tint = NavyWhite)
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
        ) {
            state.error?.let {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.2f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        it,
                        modifier = Modifier.padding(12.dp),
                        color = ErrorRed,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            if (state.isSyncing) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth(),
                    color = SkyBlue,
                    trackColor = NavyCard
                )
            }

            if (meditations.isEmpty() && !state.isSyncing) {
                Box(
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🧘", style = MaterialTheme.typography.displaySmall)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Нет медитаций",
                            style = MaterialTheme.typography.titleMedium,
                            color = NavyGhost
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Нажмите кнопку обновления",
                            style = MaterialTheme.typography.bodySmall,
                            color = NavyGhost
                        )
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    itemsIndexed(meditations, key = { _, m -> m.id }) { index, meditation ->
                        MeditationCard(
                            meditation = meditation,
                            gradientColors = cardGradients[index % cardGradients.size],
                            onClick = { onNavigateToDetail(meditation.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MeditationCard(
    meditation: Meditation,
    gradientColors: List<Color>,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyCard),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Gradient image placeholder
            Box(
                modifier = Modifier
                    .size(width = 80.dp, height = 90.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.linearGradient(gradientColors)),
                contentAlignment = Alignment.Center
            ) {
                Text("🧘", style = MaterialTheme.typography.titleLarge)
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                meditation.categoryName?.let { cat ->
                    Text(
                        cat,
                        style = MaterialTheme.typography.labelSmall,
                        color = NavyGhost
                    )
                    Spacer(Modifier.height(2.dp))
                }
                Text(
                    meditation.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = NavyWhite,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                meditation.description?.let { desc ->
                    Spacer(Modifier.height(4.dp))
                    Text(
                        desc,
                        style = MaterialTheme.typography.bodySmall,
                        color = NavyGhost,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    DurationChip(meditation.formattedDuration())
                    DifficultyChip(meditation.difficultyLabel())
                }
            }
        }
    }
}

@Composable
private fun DurationChip(text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = NavyBright
    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall,
            color = SkyBlue
        )
    }
}

@Composable
private fun DifficultyChip(text: String) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = NavyBright
    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall,
            color = NavyGhost
        )
    }
}
