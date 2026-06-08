package ru.mindflow.app.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.mindflow.app.control.MeditationViewModel
import ru.mindflow.app.entity.Meditation

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
        topBar = {
            TopAppBar(
                title = { Text("Медитации") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Назад")
                    }
                },
                actions = {
                    IconButton(onClick = viewModel::sync) {
                        Icon(Icons.Default.Refresh, "Обновить")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            state.error?.let {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Text(it, modifier = Modifier.padding(12.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodySmall)
                }
            }

            if (state.isSyncing) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            Spacer(Modifier.height(8.dp))

            if (meditations.isEmpty() && !state.isSyncing) {
                Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    Text("Нет медитаций. Потяните для обновления.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(meditations, key = { it.id }) { meditation ->
                        MeditationCard(meditation = meditation,
                            onClick = { onNavigateToDetail(meditation.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun MeditationCard(meditation: Meditation, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(meditation.title, style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f))
                meditation.categoryName?.let {
                    AssistChip(onClick = {}, label = { Text(it) })
                }
            }
            meditation.description?.let {
                Spacer(Modifier.height(4.dp))
                Text(it, style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2)
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(meditation.formattedDuration(),
                    style = MaterialTheme.typography.labelSmall)
                Text("•", style = MaterialTheme.typography.labelSmall)
                Text(meditation.difficultyLabel(),
                    style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
