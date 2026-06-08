package ru.mindflow.app.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.mindflow.app.control.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToMeditations: () -> Unit,
    onNavigateToMoodDiary: () -> Unit,
    onNavigateToAnalytics: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MindFlow") },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, contentDescription = "Профиль")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Greeting card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text(
                        "Привет, ${state.user?.displayName() ?: ""}!",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(Modifier.height(4.dp))
                    state.todayMood?.let { mood ->
                        Text("Сегодня ваше настроение: ${mood.emoji()} ${mood.moodLabel}",
                            style = MaterialTheme.typography.bodyMedium)
                    } ?: Text("Как вы себя чувствуете сегодня?",
                        style = MaterialTheme.typography.bodyMedium)
                    if (state.weeklyAverage > 0) {
                        Spacer(Modifier.height(4.dp))
                        Text("Среднее за неделю: %.1f / 10".format(state.weeklyAverage),
                            style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            // Quick actions
            Text("Быстрые действия", style = MaterialTheme.typography.titleMedium)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    title = "Медитации",
                    subtitle = "Найти практику",
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToMeditations
                )
                QuickActionCard(
                    title = "Настроение",
                    subtitle = "Записать",
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToMoodDiary
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickActionCard(
                    title = "Аналитика",
                    subtitle = "Мой прогресс",
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToAnalytics
                )
                Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(subtitle, style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
