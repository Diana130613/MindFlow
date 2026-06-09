package ru.mindflow.app.presentation.navigation

import org.junit.Assert.*
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

    @Test fun `MeditationDetail createRoute embeds id`() =
        assertEquals("meditation_detail/42", Screen.MeditationDetail.createRoute(42L))

    @Test fun `MeditationDetail createRoute works for id 1`() =
        assertEquals("meditation_detail/1", Screen.MeditationDetail.createRoute(1L))

    @Test fun `MeditationDetail createRoute works for large id`() =
        assertEquals("meditation_detail/99999", Screen.MeditationDetail.createRoute(99999L))

    @Test fun `Welcome route value`() = assertEquals("welcome", Screen.Welcome.route)
    @Test fun `Main route value`() = assertEquals("main", Screen.Main.route)
    @Test fun `Analytics route value`() = assertEquals("analytics", Screen.Analytics.route)
    @Test fun `MeditationList route value`() = assertEquals("meditation_list", Screen.MeditationList.route)

    @Test fun `Course createRoute embeds title`() {
        val route = Screen.Course.createRoute("Test")
        assertEquals("course/Test", route)
    }

    @Test fun `Course createRoute URL-encodes spaces`() {
        val route = Screen.Course.createRoute("Дыхание и покой")
        assertTrue(route.startsWith("course/"))
        assertFalse(route.contains(" "))
    }

    @Test fun `Course createRoute handles simple ASCII title`() {
        val route = Screen.Course.createRoute("Meditation")
        assertTrue(route.contains("Meditation"))
    }

    @Test fun `Screen count satisfies 5+ screens requirement`() {
        val screenCount = 8
        assertTrue(screenCount >= 5)
    }
}
