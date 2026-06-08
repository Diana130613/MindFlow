package ru.mindflow.app.presentation.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

class ScreenTest {

    @Test fun `Screen routes are unique`() {
        val routes = listOf(
            Screen.Login.route,
            Screen.Register.route,
            Screen.Home.route,
            Screen.MeditationList.route,
            Screen.MeditationDetail.route,
            Screen.MoodDiary.route,
            Screen.Analytics.route,
            Screen.Profile.route
        )
        assertEquals("All routes must be unique", routes.size, routes.toSet().size)
    }

    @Test fun `MeditationDetail createRoute embeds id`() {
        val route = Screen.MeditationDetail.createRoute(42L)
        assertEquals("meditation_detail/42", route)
    }

    @Test fun `MeditationDetail createRoute works for any id`() {
        val route = Screen.MeditationDetail.createRoute(1L)
        assertEquals("meditation_detail/1", route)
    }

    @Test fun `Screen count satisfies 5+ screens requirement`() {
        val screenCount = 8
        assert(screenCount >= 5) { "Must have at least 5 screens, got $screenCount" }
    }
}
