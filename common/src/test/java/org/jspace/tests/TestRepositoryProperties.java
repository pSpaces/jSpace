package org.jspace.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.jspace.protocol.RepositoryProperties;

class TestRepositoryProperties {
    static RepositoryProperties repoData;
    static String name;
    static String key;

    @BeforeAll
    static void initObject() {
        repoData = new RepositoryProperties("repoName", "repoKey");
    }

    @Test
    @DisplayName("Get string from name field of repository")
    void testGetName() {
        assertEquals("repoName", repoData.getName());
    }

    @Test
    @DisplayName("Get string from key field of repository")
    void testGetKey() {
        assertEquals("repoKey", repoData.getKey());
    }

    @Test
    @DisplayName("Name field of repository object is settable")
    void testSetName() {
        repoData.setName("name");
        assertEquals("name", repoData.getName());
    }

    @Test
    @DisplayName("Key field of repository object is settable")
    void testSetKey() {
        repoData.setKey("key");
        assertEquals("key", repoData.getKey());
    }
}
