package org.jspace.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.jspace.protocol.Status;


@DisplayName("A Message Status")
class TestStatus {

    Status status;

    @Test
    @DisplayName("is instantiated with new Status(code, message)")
    void isInstantiatedWithNew() {
        new Status(200, "OK");
    }

    @Nested
    @DisplayName("when new")
    class WhenNew {
        @BeforeEach
        void createNewStatus() {
            status = new Status(200, "OK");
        }
 
        @Test
        @DisplayName("has code set to an int")
        void hasCodeSet() {
            assertEquals(200, status.getCode());
        }
 
        @Test
        @DisplayName("has message set to a string")
        void hasMessageSet() {
            assertEquals("OK", status.getMessage());
        }

        @Test
        @DisplayName("toString() returns string of format 'code: message'")
        void correctToString() {
            assertEquals("200: OK", status.toString());
        }

        @Test
        @DisplayName("code field is settable to an integer")
        void testSetCode() {
            status.setCode(500);
            assertEquals(500, status.getCode());
        }

        @Test
        @DisplayName("message field is settable to a string")
        void testSetMessage() {
            status.setMessage("myMessage");
            assertEquals("myMessage", status.getMessage());
        }
    }
}
