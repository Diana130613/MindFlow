package ru.mindflow.app.presentation.navigation

sealed class Screen(val route: String) {
    object Welcome         : Screen("welcome")
    object Login           : Screen("login")
    object Register        : Screen("register")
    object Main            : Screen("main")
    object MeditationList  : Screen("meditation_list")
    object MeditationDetail: Screen("meditation_detail/{id}") {
        fun createRoute(id: Long) = "meditation_detail/$id"
    }
    object Analytics : Screen("analytics")
    object Course    : Screen("course/{title}") {
        fun createRoute(title: String) = "course/${java.net.URLEncoder.encode(title, "UTF-8")}"
    }
    // Legacy routes kept for test compatibility
    object Home            : Screen("home")
    object MoodDiary       : Screen("mood_diary")
    object Profile         : Screen("profile")
}
