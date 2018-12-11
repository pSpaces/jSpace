package org.jspace.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.jspace.protocol.SpaceProperties;
import org.jspace.protocol.SpaceType;
import org.jspace.protocol.SpaceKeys;

@DisplayName("Properties for the space field of the message")
class TestSpaceProperties {
    static SpaceKeys keys;
    static SpaceProperties target;
    static SpaceProperties noParams;

    @BeforeAll
    static void initAllObjects() {
        keys = new SpaceKeys("mgmt", "put", "get", "query");
        target = new SpaceProperties(SpaceType.SEQUENTIAL, "spaceName", "spaceUid", keys);
        noParams = new SpaceProperties();
    }

    @Test
    @DisplayName("SpaceProperties instantiated without a name")
    void testGetEmptyName() {
        assertNull(noParams.getName());
    }

    @Test
    @DisplayName("SpaceProperties instantiated without a uid")
    void testGetEmptyUid() {
        assertNull(noParams.getUUID());
    }

    @Test
    @DisplayName("SpaceProperties instantiated without keys")
    void testGetEmptyKeys() {
        assertNull(noParams.getKeys());
    }

    @Test
    @DisplayName("SpaceProperties instantiated without SpaceType")
    void testGetEmptyType() {
        assertNull(noParams.getType());
    }

    @Test
    @DisplayName("SpaceProperties instantiated with a string as name")
    void testGetName() {
        assertEquals("spaceName", target.getName());
    }

    @Test
    @DisplayName("SpaceProperties instantiated with sequential space as type")
    void testGetType() {
        assertEquals(SpaceType.SEQUENTIAL, target.getType());
    }

    @Test
    @DisplayName("SpaceProperties instantiated with a string as uid")
    void testGetUid() {
        assertEquals("spaceUid", target.getUUID());
    }

    @Test
    @DisplayName("SpaceProperties instantiated with a keys-object")
    void testGetKeys() {
        assertEquals(keys, target.getKeys());
    }

    @Test
    @DisplayName("Space type is settable")
    void testSetType() {
        target.setType(SpaceType.SEQUENTIAL);
        assertEquals(SpaceType.SEQUENTIAL, target.getType());
    }

    @Test
    @DisplayName("Name is settable")
    void testSetName() {
        target.setName("aspace");
        assertEquals("aspace", target.getName());
    }

    @Test
    @DisplayName("Uid is settable")
    void testSetUid() {
        target.setUUID("myUid");
        assertEquals("myUid", target.getUUID());
    }

    @Test
    @DisplayName("Key are settable")
    void testSetKeys() {
        target.setKeys(keys);
        assertEquals(keys, target.getKeys());
    }
}
