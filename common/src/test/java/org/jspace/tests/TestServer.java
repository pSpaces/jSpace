package org.jspace.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.jspace.Server;
import org.jspace.ManagementServer;
import org.jspace.config.ServerConfig;
import org.jspace.gate.GateFactory;
import org.jspace.gate.ServerGate;
import org.jspace.gate.ClientHandler;
import org.jspace.protocol.ManagementMessage;
import org.jspace.protocol.MessageType;
import org.jspace.protocol.Status;
import org.jspace.protocol.SpaceKeys;
import org.jspace.protocol.SpaceType;
import org.jspace.protocol.SpaceProperties;
import org.jspace.protocol.RepositoryProperties;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.HashMap;

import java.net.SocketException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;

@DisplayName("A Server")
class TestServer {

    static Server srv;
    static ManagementMessage msg;
    static SpaceKeys keys;
    static RepositoryProperties repo;
    static SpaceProperties space;
    static Status status;
    static ServerConfig config;

    @BeforeAll
    static void initAllObjects() {
        keys = new SpaceKeys("mgmtKey", "putKey", "getKey", "queryKey");
        space = new SpaceProperties(SpaceType.SEQUENTIAL, "name", "uid", keys);
        repo = new RepositoryProperties("repoName", "key");
        status = new Status(200, "OK");
        config = new ServerConfig();
        msg = new ManagementMessage(
                    MessageType.CREATE_REPOSITORY,
                    "someSessionId",
                    repo,
                    space,
                    status);
    }

    @Test
    @DisplayName("is instantiated with new Server()")
    void isInstantiatedWithNewNoParameters() {
        srv = ManagementServer.getInstance();
    }

    @Nested
    @DisplayName("when new")
    class WhenNew {

        @BeforeEach
        void createNewServer() {
            srv = ManagementServer.getInstance();
        }

//        @Test
//        @DisplayName("server is configured and started with default config")
//        void hasLoadedDefaultConfig() {
//            assertFalse(srv.addGate());
//        }

        @Test
        @DisplayName("server handles a message")
        void serverHandlesMessage() {
        }
    }
}
