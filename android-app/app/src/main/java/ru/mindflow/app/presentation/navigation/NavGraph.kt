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
    val authVm  = remember { AuthViewModel(container.authRepository) }
    val homeVm  = remember { HomeViewModel(container.authRepository, container.moodRepository) }
    val meditVm = remember { MeditationViewModel(container.meditationRepository) }
    val moodVm  = remember { MoodViewModel(container.moodRepository) }
    val profVm  = remember { ProfileViewModel(container.authRepository) }

    NavHost(navController = navController, startDestination = startDestination) {

        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authVm,
                onSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authVm,
                onSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                viewModel = homeVm,
                onNavigateToMeditations = { navController.navigate(Screen.MeditationList.route) },
                onNavigateToMoodDiary   = { navController.navigate(Screen.MoodDiary.route) },
                onNavigateToAnalytics   = { navController.navigate(Screen.Analytics.route) },
                onNavigateToProfile     = { navController.navigate(Screen.Profile.route) }
            )
        }

        composable(Screen.MeditationList.route) {
            MeditationListScreen(
                viewModel = meditVm,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetail = { id ->
                    navController.navigate(Screen.MeditationDetail.createRoute(id))
                }
            )
        }

        composable(
            route = Screen.MeditationDetail.route,
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getLong("id") ?: return@composable
            MeditationDetailScreen(
                id = id,
                viewModel = meditVm,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.MoodDiary.route) {
            MoodDiaryScreen(
                viewModel = moodVm,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Analytics.route) {
            AnalyticsScreen(
                viewModel = moodVm,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen(
                viewModel = profVm,
                onNavigateBack = { navController.popBackStack() },
                onLoggedOut = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
