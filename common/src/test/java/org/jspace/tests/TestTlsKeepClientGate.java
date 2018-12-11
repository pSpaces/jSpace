package org.jspace.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.jspace.gate.TlsKeepClientGate;
import org.jspace.protocol.ManagementMessage;
import org.jspace.protocol.MessageType;
import org.jspace.protocol.Status;
import org.jspace.protocol.SpaceKeys;
import org.jspace.protocol.SpaceType;
import org.jspace.protocol.SpaceProperties;
import org.jspace.protocol.RepositoryProperties;

import java.net.UnknownHostException;
import java.io.IOException;

@DisplayName("A TlsKeepClientGate")
class TestTlsKeepClientGate {
    TlsKeepClientGate gate;
    ManagementMessage msg;
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
    @DisplayName("is instantiated with new TlsKeepClientGate()")
    void isInstantiatedWithNew() {
        new TlsKeepClientGate(null, "127.0.0.1", 7000, "someTarget",
                ManagementMessage.class);
    }

    @Nested
    @DisplayName("when new")
    class WhenNew {
        @BeforeEach
        void createNewTlsKeepClientGateAndMessage() {
            gate = new TlsKeepClientGate(null, "127.0.0.1", 7000,
                "someTarget", ManagementMessage.class);
            msg = new ManagementMessage(MessageType.ATTACH,
                    "someSessionId",
                    repo,
                    target,
                    status);
        }

        @Test
        @DisplayName("open() is callable on gate")
        void isGateOpened() {
            try {
                gate.open();
            } catch (IOException e) {}
        }
    }

}
