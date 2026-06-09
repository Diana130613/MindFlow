package ru.mindflow.app.entity

import org.junit.Assert.*
import org.junit.Test

class UserTest {

    @Test fun `isAdmin returns true for ROLE_ADMIN`() {
        assertTrue(User("a@b.com", "Admin", "ROLE_ADMIN").isAdmin())
    }

    @Test fun `isAdmin returns false for ROLE_USER`() {
        assertFalse(User("a@b.com", "User", "ROLE_USER").isAdmin())
    }

    @Test fun `displayName returns name when not blank`() {
        assertEquals("Alice", User("a@b.com", "Alice").displayName())
    }

    @Test fun `displayName falls back to email when name is blank`() {
        assertEquals("a@b.com", User("a@b.com", "").displayName())
    }

    @Test fun `default role is ROLE_USER`() {
        assertEquals("ROLE_USER", User("a@b.com", "Bob").role)
    }

    @Test fun `displayName falls back to email when name is whitespace only`() {
        assertEquals("a@b.com", User("a@b.com", "   ").displayName())
    }

    @Test fun `isAdmin returns false for empty role`() {
        assertFalse(User("a@b.com", "User", "").isAdmin())
    }

    @Test fun `isAdmin is case-sensitive`() {
        assertFalse(User("a@b.com", "User", "role_admin").isAdmin())
    }

    @Test fun `equality based on all fields`() {
        assertEquals(User("a@b.com", "Alice", "ROLE_USER"), User("a@b.com", "Alice", "ROLE_USER"))
    }
}
