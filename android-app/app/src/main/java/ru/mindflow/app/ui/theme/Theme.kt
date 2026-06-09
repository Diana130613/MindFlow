package ru.mindflow.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val MindFlowColors = darkColorScheme(
    primary              = NavyAccent,
    onPrimary            = NavyWhite,
    primaryContainer     = NavyBright,
    onPrimaryContainer   = NavyWhite,
    secondary            = SkyBlue,
    onSecondary          = NavyWhite,
    secondaryContainer   = NavyCard,
    onSecondaryContainer = NavyWhite,
    tertiary             = GradPurple1,
    onTertiary           = NavyWhite,
    background           = NavyBase,
    onBackground         = NavyWhite,
    surface              = NavyCard,
    onSurface            = NavyWhite,
    surfaceVariant       = NavyBright,
    onSurfaceVariant     = NavyGhost,
    outline              = NavyBright,
    error                = ErrorRed,
    onError              = NavyWhite,
)

@Composable
fun MindFlowTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = MindFlowColors,
        typography  = Typography,
        content     = content
    )
}
