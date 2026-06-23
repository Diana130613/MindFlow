package ru.mindflow.app.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.mindflow.app.control.MeditationViewModel
import ru.mindflow.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MeditationDetailScreen(
    id: Long,
    viewModel: MeditationViewModel,
    onNavigateBack: () -> Unit
) {
    val meditations by viewModel.meditations.collectAsState()
    val meditation = remember(id, meditations) { meditations.find { it.id == id } }

    Scaffold(
        containerColor = NavyBase,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        meditation?.title ?: "Медитация",
                        color = NavyWhite,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
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
        if (meditation == null) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = SkyBlue)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Gradient hero image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(
                        Brush.linearGradient(listOf(GradPurple1, GradPink1))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🧘", fontSize = 64.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        meditation.formattedDuration(),
                        style = MaterialTheme.typography.titleMedium,
                        color = NavyWhite,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                color = NavyCard
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    // Chips
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        meditation.categoryName?.let { cat ->
                            Surface(shape = RoundedCornerShape(8.dp), color = NavyBright) {
                                Text(
                                    cat,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = SkyBlue
                                )
                            }
                        }
                        Surface(shape = RoundedCornerShape(8.dp), color = NavyBright) {
                            Text(
                                meditation.formattedDuration(),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = NavyGhost
                            )
                        }
                        Surface(shape = RoundedCornerShape(8.dp), color = NavyBright) {
                            Text(
                                meditation.difficultyLabel(),
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = NavyGhost
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))

                    Text(
                        "Описание",
                        style = MaterialTheme.typography.titleSmall,
                        color = NavyGhost
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        meditation.description ?: "Описание недоступно",
                        style = MaterialTheme.typography.bodyMedium,
                        color = NavyWhite,
                        lineHeight = 24.sp
                    )

                    Spacer(Modifier.height(32.dp))

                    Button(
                        onClick = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NavyAccent,
                            contentColor   = NavyWhite
                        )
                    ) {
                        Icon(Icons.Default.PlayArrow, "Начать", modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Начать медитацию", style = MaterialTheme.typography.titleMedium)

                    }

                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}
