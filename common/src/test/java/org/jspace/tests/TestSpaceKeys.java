package org.jspace.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.jspace.protocol.SpaceKeys;

@DisplayName("A SpaceKey object")
class TestSpaceKeys {

    SpaceKeys keys;

    @Test
    @DisplayName("is instantiated with new SpaceKeys()")
    void isInstantiatedWithNew() {
        new SpaceKeys();
    }

    @Test
    @DisplayName("is instantiated with new SpaceKeys(mgmtk, putk, getk, queryk)")
    void isInstantiatedWithNewParams() {
        new SpaceKeys("mgmt", "put", "get", "query");
    }

    @Test
    @DisplayName("all keys are NULL when instantiated with SpaceKeys()")
    void withoutParameters() {
        keys = new SpaceKeys();
        assertNull(keys.getManagementKey());
        assertNull(keys.getPutKey());
        assertNull(keys.getGetKey());
        assertNull(keys.getQueryKey());
    }

    @Test
    @DisplayName("all keys are set when instantiated with SpaceKeys(mgmtk, putk, getk, queryk)")
    void withParameters() {
        keys = new SpaceKeys("mgmt", "put", "get", "query");
        assertEquals("mgmt", keys.getManagementKey());
        assertEquals("put", keys.getPutKey());
        assertEquals("get", keys.getGetKey());
        assertEquals("query", keys.getQueryKey());
    }

    // FIXME continue from here
    @Test
    void testSetManagementKey() {
        SpaceKeys keys = new SpaceKeys();
        keys.setManagementKey("mgmt");
        assertEquals(keys.getManagementKey(), "mgmt");
    }

    @Test
    void testSetPutKey() {
        SpaceKeys keys = new SpaceKeys();
        keys.setPutKey("put");
        assertEquals(keys.getPutKey(), "put");
    }

    @Test
    void testSetGetKey() {
        SpaceKeys keys = new SpaceKeys();
        keys.setGetKey("get");
        assertEquals(keys.getGetKey(), "get");
    }

    @Test
    void testSetQueryKey() {
        SpaceKeys keys = new SpaceKeys();
        keys.setQueryKey("query");
        assertEquals(keys.getQueryKey(), "query");
    }
}
