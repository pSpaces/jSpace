//package org.jspace.tests;
//
//import java.net.InetSocketAddress;
//import java.net.URI;
//import java.net.URISyntaxException;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertFalse;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.junit.jupiter.api.Assertions.assertNull;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//
//import org.mockito.junit.jupiter.MockitoExtension;
//import static org.mockito.Mockito.*;
//import org.mockito.stubbing.OngoingStubbing; // thenThrow()
//
//import org.jspace.gate.ServerGate;
//import org.jspace.gate.TlsKeepServerGate;
//import org.jspace.gate.ClientHandler;
//import org.jspace.protocol.ManagementMessage;
//
//@ExtendWith(MockitoExtension.class)
//@DisplayName("A TLS Gate")
//class TestingTLSGates {
//    ServerGate gate;
//
//    @Test
//    @DisplayName("is instantiated with new TlsKeepServerGate()")
//    void isInstantiatedWithNew() {
//        new TlsKeepServerGate(null, new InetSocketAddress(9999),
//            10, ManagementMessage.class);
//    }
//
//    @Nested
//    @DisplayName("when new")
//    class WhenNew {
//        @BeforeEach
//        void createNewServerGate() {
//            gate = new TlsKeepServerGate(null, new InetSocketAddress(9999),
//            10, ManagementMessage.class);
//        }
//
//        @Disabled
//        @AfterEach
//        void cleanUpSquad() throws Exception {
//            if (!gate.isClosed()) {
//                return;
//                //gate.close();
//            }
//        }
//
//        @Test
//        @DisplayName("gate is not closed")
//        void isClosed() {
//            assertFalse(gate.isClosed());
//        }
//
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
//    }
//}
