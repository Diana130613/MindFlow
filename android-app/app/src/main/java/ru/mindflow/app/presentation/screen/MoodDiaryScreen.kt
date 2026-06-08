package ru.mindflow.app.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.mindflow.app.control.MoodViewModel
import ru.mindflow.app.entity.MoodEntry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodDiaryScreen(
    viewModel: MoodViewModel,
    onNavigateBack: () -> Unit
) {
    val state   by viewModel.uiState.collectAsState()
    val history by viewModel.history.collectAsState()

    var selectedScore by remember { mutableStateOf(5) }
    var note          by remember { mutableStateOf("") }
    var showForm      by remember { mutableStateOf(false) }

    LaunchedEffect(state.savedEntry) {
        if (state.savedEntry != null) {
            showForm = false
            note = ""
            selectedScore = 5
            viewModel.resetSaved()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Дневник настроения") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, "Назад")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showForm = true }) {
                Text("+", style = MaterialTheme.typography.headlineSmall)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Average card
            if (state.average > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Row(
                        Modifier.padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Среднее за 30 дней", style = MaterialTheme.typography.bodyMedium)
                        Text("%.1f / 10".format(state.average),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary)
                    }
                }
                Spacer(Modifier.height(16.dp))
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(history, key = { it.id }) { entry ->
                    MoodEntryCard(entry = entry, onDelete = { viewModel.deleteMood(entry.id) })
                }
            }
        }
    }

    // Add mood bottom sheet
    if (showForm) {
        ModalBottomSheet(onDismissRequest = { showForm = false }) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Как вы себя чувствуете?",
                    style = MaterialTheme.typography.titleLarge)

                Text("Оценка: $selectedScore / 10",
                    style = MaterialTheme.typography.bodyMedium)

                Slider(
                    value = selectedScore.toFloat(),
                    onValueChange = { selectedScore = it.toInt() },
                    valueRange = 1f..10f,
                    steps = 8
                )

                Text(when (selectedScore) {
                    1, 2  -> "😢 Очень плохо"
                    3, 4  -> "😕 Плохо"
                    5, 6  -> "😐 Нейтрально"
                    7, 8  -> "🙂 Хорошо"
                    9, 10 -> "😄 Отлично"
                    else  -> ""
                }, style = MaterialTheme.typography.headlineSmall)

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Заметка (необязательно)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                state.error?.let {
                    Text(it, color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall)
                }

                Button(
                    onClick = { viewModel.saveMood(selectedScore, note.takeIf { it.isNotBlank() }) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !state.isSaving
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Сохранить")
                    }
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun MoodEntryCard(entry: MoodEntry, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(entry.emoji(), style = MaterialTheme.typography.titleLarge)
                    Text(entry.moodLabel, style = MaterialTheme.typography.titleMedium)
                    Text("${entry.score}/10",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                entry.note?.let {
                    Spacer(Modifier.height(4.dp))
                    Text(it, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (entry.syncPending) {
                    Text("⏳ Ожидает синхронизации",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.tertiary)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "Удалить",
                    tint = MaterialTheme.colorScheme.error)
            }
        }
    }
}
