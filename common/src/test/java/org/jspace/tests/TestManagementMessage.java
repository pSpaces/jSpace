package org.jspace.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import org.jspace.protocol.ManagementMessage;
import org.jspace.protocol.MessageType;
import org.jspace.protocol.Status;
import org.jspace.protocol.SpaceKeys;
import org.jspace.protocol.SpaceProperties;
import org.jspace.protocol.SpaceType;
import org.jspace.protocol.RepositoryProperties;
import org.jspace.Space;
import org.jspace.SequentialSpace;

@DisplayName("A Management Message")
class TestManagementMessage {

    ManagementMessage message;
    static SpaceKeys keys;
    static RepositoryProperties repo;
    static SpaceProperties target;
    static Status status;

    @BeforeAll
    static void initAllObjects() {
        keys = new SpaceKeys("mgmt", "put", "get", "query");
        target = new SpaceProperties(SpaceType.SEQUENTIAL, "name", "uid", keys);
        repo = new RepositoryProperties("repo", "key");
        status = new Status(200, "OK");
    }

    @Test
    @DisplayName("is instantiated with new ManagementMessage(operation)")
    void isInstantiatedWithNew() {
        new ManagementMessage(MessageType.ATTACH,
               "someSessionId",
               repo,
               target,
               status);
    }

    @Nested
    @DisplayName("when new")
    class WhenNew {

        @BeforeEach
        void createNewMessage() {
            message = new ManagementMessage(MessageType.ATTACH,
                   "someSessionId",
                   repo,
                   target,
                   status);
        }

        @Test
        @DisplayName("operation is set to a string")
        void hasOperationSet() {
            assertEquals(MessageType.ATTACH, message.getOperation());
        }

//        @Disabled
//        @Test
//        @DisplayName("optional fields are unset")
//        void optionalFieldsNotSet() {
//            assertNull(message.getServerKey());
//            assertNull(message.getRepository());
//            assertNull(message.getStatus());
//            assertNull(message.getSpace());
//        }

        @Test
        @DisplayName("server key is settable")
        void serverKeySettable() {
            String newKey = "some key";
            message.setServerKey(newKey);
            assertEquals(newKey, message.getServerKey());
        }

        @Test
        @DisplayName("repository is settable")
        void repositorySettable() {
            message.setRepository(repo);
            assertEquals(repo, message.getRepository());
        }

        @Test
        @DisplayName("status is settable")
        void statusSettable() {
            message.setStatus(status);
            assertEquals(status, message.getStatus());
        }

        @Test
        @DisplayName("space is settable")
        void spaceSettable() {
            message.setSpace(target);
            assertEquals(target, message.getSpace());
        }
    }
}
