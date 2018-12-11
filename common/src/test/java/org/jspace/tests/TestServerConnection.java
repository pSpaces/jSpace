package org.jspace.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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

import java.net.URI;

import org.jspace.ServerConnection;
import org.jspace.Server;
import org.jspace.ManagementServer;

@DisplayName("A ServerConnection")
class TestingServerConnection {
    static Server srv;
    ServerConnection conn;
    String REMOTE_URI = "tls://127.0.0.1:7000/?keep";

    @BeforeAll
    public static void init() {
        srv = ManagementServer.getInstance();
    }

    @Test
    @DisplayName("is instantiated with new ServerConnection(String uri)")
    void isInstantiatedWithNew() throws Exception {
        conn = ServerConnection.getInstance(REMOTE_URI);
    }

    @Nested
    @DisplayName("when new")
    class WhenNew {
        @BeforeEach
        void createNewServerConnection() throws Exception {
            conn = ServerConnection.getInstance(REMOTE_URI);
        }

        @Test
        @DisplayName("URI field is returned by getUri()")
        void uriIsReturned() throws Exception {
            assertEquals(new URI(REMOTE_URI), conn.getUri());
        }

        @Disabled
        @Test
        @DisplayName("Creating a repository returns a port")
        void creatingARepository() throws Exception {
            //String port = conn.createRepository("myRepo");
            assertTrue(false);
            //assertEquals(new URI(REMOTE_URI), conn.getUri());
        }




//        @Test
//        @DisplayName("open() throws IllegalStateException if gate is closed")
//        void throwsExceptionIsClosed() throws Exception {
//            gate.open(); // required for calling close() (ssocket is null)
//            gate.close(); // sets isClosed = true
//            assertThrows(IllegalStateException.class, () -> gate.open());
//        }
//
//        @Test
//        @DisplayName("open() throws IllegalStateException if already open")
//        void throwsExceptionWhenAlreadyOpen() throws Exception {
//            gate.open(); // need to open twice
//            assertThrows(IllegalStateException.class, () -> gate.open());
//        }
//
//        @Test
//        @DisplayName("close() throws IllegalStateException if gate is already closed")
//        void throwsExceptionWhenAlreadyClosed() throws Exception {
//            gate.open();
//            gate.close();
//            assertThrows(IllegalStateException.class, () -> gate.close());
//        }
//
//        @Test
//        @DisplayName("close() throws IllegalStateException when if gate hasn't been opened yet")
//        void throwsExceptionWhenNeverOpened() throws Exception {
//            assertThrows(IllegalStateException.class, () -> gate.close());
//        }
//
//        @Test
//        @DisplayName("accept() throws IOException when it has not yet been opened")
//        void throwsExceptionWhenAcceptNeverOpened() throws Exception {
//            gate.open(); // necessary before we can close it
//            gate.close(); // to set isClosed = true
//            assertThrows(IllegalStateException.class, () -> gate.accept());
//        }
//
//        @Test
//        @DisplayName("accept() throws IOException when its closed")
//        void throwsExceptionWhenAcceptIsClosed() throws Exception {
//            assertThrows(IllegalStateException.class, () -> gate.accept());
//        }
//
//        @Disabled
//        @Test
//        @DisplayName("accept() returns a ClientHandler")
//        void returnsHandlerOnAccept() throws Exception {
//            gate.open(); // necessary before we can close it
//            ClientHandler handler = null;
//            // FIXME the test runner hangs here :(
//            handler = gate.accept();
//            assertNotNull(handler);
//
//            handler.close();
//            gate.close();
//        }
//
//        @Test
//        @DisplayName("getURI returns new URI object")
//        void getURIReturnsURI() throws Exception {
//            URI expected = new URI("tls://0.0.0.0/0.0.0.0:9999/?keep");
//            assertEquals(expected, gate.getURI());
//        }
//
//        // FIXME find a nice way to test caught exceptions (
//        @Disabled
//        @Test
//        @DisplayName("getURI() catches URISyntaxException and returns null")
//        void throwsExceptionWhenGarbledURI() {
//            // mock URI object which throws URISyntaxException
////            whenNew(URI.class).withArguments("").thenThrow(new URISyntaxException("", "", 0));
//            // let the mocked object throw exception when getURI() is invoked
//
//
//            // OLD:
//            ServerGate gateSpy = spy(gate);
//            doReturn(null).when(gateSpy).getURI();
//            //doThrow(new URISyntaxException("", "", 0)).when(gateSpy).getURI();
//            //when(gateSpy.getURI()).thenThrow(URISyntaxException.class);
//
//            gateSpy.getURI();
//            //assertThrows(URISyntaxException.class, () -> gate.getURI());
//        }
    }
}
