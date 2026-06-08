package ru.mindflow.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import ru.mindflow.app.presentation.navigation.NavGraph
import ru.mindflow.app.presentation.navigation.Screen
import ru.mindflow.app.ui.theme.MindFlowTheme

class MainActivity : ComponentActivity() {

    private lateinit var container: AppContainer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        container = AppContainer(applicationContext)
        enableEdgeToEdge()
        setContent {
            MindFlowTheme {
                val navController = rememberNavController()
                var startDestination by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(Unit) {
                    startDestination = if (container.authRepository.isLoggedIn())
                        Screen.Home.route
                    else
                        Screen.Login.route
                }

                startDestination?.let { start ->
                    NavGraph(
                        navController = navController,
                        container = container,
                        startDestination = start
                    )
                }
            }
        }
    }
}
