package ru.mindflow.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import ru.mindflow.app.AppContainer
import ru.mindflow.app.control.*
import ru.mindflow.app.presentation.screen.*

@Composable
fun NavGraph(
    navController: NavHostController,
    container: AppContainer,
    startDestination: String
) {
    // authVm shared between Login + Register; meditVm shared between List + Detail
    // homeVm/moodVm kept here so moodVm can also feed AnalyticsScreen
    val authVm  = remember { AuthViewModel(container.authRepository) }
    val homeVm  = remember { HomeViewModel(container.authRepository, container.moodRepository) }
    val meditVm = remember { MeditationViewModel(container.meditationRepository) }
    val moodVm  = remember { MoodViewModel(container.moodRepository) }

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Screen.Welcome.route) {
            WelcomeScreen(onStart = { navController.navigate(Screen.Login.route) })
        }

        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authVm,
                onSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authVm,
                onSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable(Screen.Main.route) {
            // profVm created fresh here — prevents stale loggedOut = true after re-login
            val profVm = remember { ProfileViewModel(container.authRepository) }

            MainScreen(
                homeViewModel           = homeVm,
                moodViewModel           = moodVm,
                profileViewModel        = profVm,
                onNavigateToMeditations = { navController.navigate(Screen.MeditationList.route) },
                onNavigateToCourse      = { title ->
                    navController.navigate(Screen.Course.createRoute(title))
                },
                onNavigateToAnalytics   = { navController.navigate(Screen.Analytics.route) },
                onLoggedOut = {
                    navController.navigate(Screen.Welcome.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.MeditationList.route) {
            MeditationListScreen(
                viewModel          = meditVm,
                onNavigateBack     = { navController.popBackStack() },
                onNavigateToDetail = { id ->
                    navController.navigate(Screen.MeditationDetail.createRoute(id))
                }
            )
        }

        composable(
            route = Screen.MeditationDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { back ->
            val id = back.arguments?.getLong("id") ?: return@composable
            MeditationDetailScreen(
                id             = id,
                viewModel      = meditVm,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Analytics.route) {
            AnalyticsScreen(
                viewModel      = moodVm,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.Course.route,
            arguments = listOf(navArgument("title") {
                type = NavType.StringType; defaultValue = "Спокойствие сознания"
            })
        ) { back ->
            val raw   = back.arguments?.getString("title") ?: ""
            val title = java.net.URLDecoder.decode(raw, "UTF-8")
            CourseScreen(
                title          = title,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
