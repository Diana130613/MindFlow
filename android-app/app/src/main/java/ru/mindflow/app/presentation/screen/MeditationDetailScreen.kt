package ru.mindflow.app.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.mindflow.app.control.MeditationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeditationDetailScreen(
    id: Long,
    viewModel: MeditationViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val meditations by viewModel.meditations.collectAsState()
    val meditation = remember(id, meditations) { meditations.find { it.id == id } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(meditation?.title ?: "Медитация") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Назад")
                    }
                }
            )
        }
    ) { padding ->
        if (meditation == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Info chips
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                meditation.categoryName?.let {
                    AssistChip(onClick = {}, label = { Text(it) })
                }
                AssistChip(onClick = {}, label = { Text(meditation.formattedDuration()) })
                AssistChip(onClick = {}, label = { Text(meditation.difficultyLabel()) })
            }

            // Description
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Описание", style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(8.dp))
                    Text(meditation.description ?: "Описание недоступно",
                        style = MaterialTheme.typography.bodyMedium)
                }
            }

            Spacer(Modifier.weight(1f))

            // Play button
            Button(
                onClick = { /* audio playback placeholder */ },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(Icons.Default.PlayArrow, "Начать")
                Spacer(Modifier.width(8.dp))
                Text("Начать медитацию", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
