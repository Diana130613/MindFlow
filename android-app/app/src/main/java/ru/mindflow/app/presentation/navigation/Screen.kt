package ru.mindflow.app.presentation.navigation

sealed class Screen(val route: String) {
    object Login           : Screen("login")
    object Register        : Screen("register")
    object Home            : Screen("home")
    object MeditationList  : Screen("meditation_list")
    object MeditationDetail: Screen("meditation_detail/{id}") {
        fun createRoute(id: Long) = "meditation_detail/$id"
    }
    object MoodDiary       : Screen("mood_diary")
    object Analytics       : Screen("analytics")
    object Profile         : Screen("profile")
}
