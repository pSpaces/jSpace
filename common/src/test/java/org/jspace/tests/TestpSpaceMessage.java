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

import org.jspace.protocol.pSpaceMessage;
import org.jspace.protocol.MessageType;
import org.jspace.protocol.DataProperties;
import org.jspace.protocol.SpaceProperties;
import org.jspace.protocol.SpaceType;
import org.jspace.protocol.SpaceKeys;
import org.jspace.protocol.Status;
import org.jspace.Tuple;


@DisplayName("A pSpace Message")
class TestpSpaceMessage {

    pSpaceMessage message;
    static DataProperties data;
    static SpaceProperties target;
    static Tuple tuple;
    static SpaceKeys keys;
    static Status status;

    @BeforeAll
    static void initAllObjects() {
        keys = new SpaceKeys("mgmt", "put", "get", "query");
        target = new SpaceProperties(SpaceType.SEQUENTIAL, "name", "uid", keys);
        tuple = new Tuple("Hello", "World!");
        data = new DataProperties("tuple", tuple);
        status = new Status(200, "OK");
    }

    @Test
    @DisplayName("istantiate with new pSpaceMessage(type)")
    void isIstantiatedWithNew() {
        new pSpaceMessage(MessageType.PUT,
               "someSessionId",
               target,
               data,
               status);
    }

    @Nested
    @DisplayName("when new")
    class WhenNew {
        @BeforeEach
        void createNewMessage() {
            message = new pSpaceMessage(MessageType.PUT,
                    "someSessionId",
                    target,
                    data,
                    status);
        }

        @Test
        @DisplayName("type is set to a string")
        void hasTypeSet() {
            assertEquals(MessageType.PUT, message.getOperation());
        }

//        @Disabled
//        @Test
//        @DisplayName("optional fields are unset")
//        void optionalFieldsNotSet() {
//            assertNull(message.getTarget());
//            assertNull(message.getData());
//            assertNull(message.getStatus());
//        }

        @Test
        @DisplayName("target space is settable")
        void targetSpaceSettable() {
            message.setTarget(target);
            assertEquals(target, message.getTarget());
        }

        @Test
        @DisplayName("space data is settable")
        void dataSettable() {
            message.setData(data);
            assertEquals(data, message.getData());
        }

        @Test
        @DisplayName("status is settable")
        void statusSettable() {
            message.setStatus(status);
            assertEquals(status, message.getStatus());
        }
    }
}
