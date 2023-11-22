package dev.triamylo.learnwebapp.model;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testEquals() {
        User user1 = new User();
        user1.setUsername("user1");
        User user2 = new User();
        user2.setUsername("user2");

        var hash1 = user1.hashCode();
        var hash2 = user2.hashCode();

        assertEquals(hash1, hash2);
        assertEquals(user1, user2);
        assertTrue(user1.equals(user2));
        assertTrue(user2.equals(user1));
        assertNotSame(user1, user2);

        Map<User, String> map = new HashMap<>();
        assertNull(map.get(user1));

        map.put(user1, "user1");
        assertNotNull(map.get(user1));
        assertEquals("user1", map.get(user1));

        assertNotNull(map.get(user2));
        assertEquals("user1", map.get(user2));

        user1.setUuid("1");
        user2.setUuid("2");

        assertNull(map.get(user2));
        map.put(user1, "user1");
        assertEquals("user1", map.get(user1));

        assertEquals("user1", map.get(user1));

        hash1 = user1.hashCode();
        hash2 = user2.hashCode();

        assertNotEquals(hash1, hash2);
        assertNotEquals(user1, user2);
        assertNotSame(user1, user2);

    }

    @Test
    void testEquals2() {
        User user1 = new User();
        user1.setUsername("user1");

        Map<User, User> map = new HashMap<>();
        assertNull(map.get(user1));

        map.put(user1, user1);
        assertNotNull(map.get(user1));
        assertEquals("user1", map.get(user1).getUsername());

        map.get(user1).setUsername("newUser1");
        assertEquals("newUser1", map.get(user1).getUsername());
    }

}