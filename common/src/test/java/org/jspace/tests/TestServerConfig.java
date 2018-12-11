package org.jspace.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.jspace.config.ServerConfig;
import org.jspace.config.TLSConfig;
import org.jspace.config.EncryptionConfig;
import org.jspace.Server;


@DisplayName("A server configuration object")
public class TestServerConfig {

    ServerConfig SrvConfDefault;
    ServerConfig SrvConfYaml;
    static String file = "config.yaml";


    @DisplayName("is istantiated with new ServerConfig()")
    @Test
    void isIstantiatedWithNew() {
        new ServerConfig();
    }

    @DisplayName("is istantiated with new ServerConfig(config.yaml)")
    @Test
    void isIstantiatedWithNewFile() {
        new ServerConfig(file);
    }

    @Nested
    @DisplayName("when new")
    class WhenNew {
        @BeforeEach
        void createNewConfig() {
            SrvConfDefault = new ServerConfig();
            SrvConfYaml = new ServerConfig(file);
        }

        @Test
        @DisplayName("default protocol is set to a string")
        void testDefaultProtocol() {
            assertEquals("tls", SrvConfDefault.getProtocol());
        }

        @Test
        @DisplayName("config file protocol is set to a string")
        void testFileProtocol() {
            assertEquals("tls", SrvConfYaml.getProtocol());
        }

        @Test
        @DisplayName("default address is set to a string")
        void testDefaultAddress() {
            assertEquals("localhost", SrvConfDefault.getAddress());
        }

        @Test
        @DisplayName("config file address is set to a string")
        void testFileAddress() {
            assertEquals("127.0.0.1", SrvConfYaml.getAddress());
        }

        @Test
        @DisplayName("default port is set to an integer")
        void testDefaultPort() {
            assertEquals(7000, SrvConfDefault.getPort());
        }

        @Test
        @DisplayName("config file port is set to an integer")
        void testFilePort() {
            assertEquals(7001, SrvConfYaml.getPort());
        }

        @Test
        @DisplayName("default server key is set to a string")
        void testDefaultServerKey() {
            assertEquals("", SrvConfDefault.getServerKey());
        }

        @Test
        @DisplayName("config file server key is set to a string")
        void testFileServerKey() {
            assertEquals("SomeExampleServerKey", SrvConfYaml.getServerKey());
        }

        @Test
        @DisplayName("default marshaller is set to a string")
        void testDefaultMarshaller() {
            assertEquals("json", SrvConfDefault.getMarshaller());
        }

        @Test
        @DisplayName("config file marshaller is set to a string")
        void testFileMarshaller() {
            assertEquals("json", SrvConfYaml.getMarshaller());
        }

        @Test
        @DisplayName("default verbosity lvl is set to an int")
        void testDefaultVerbosity() {
            assertEquals(0, SrvConfDefault.getVerbosity());
        }

        @Test
        @DisplayName("config file verbosity lvl is set to an int")
        void testFileVerbosity() {
            assertEquals(2, SrvConfYaml.getVerbosity());
        }

        @Test
        @DisplayName("default encryption object is set")
        void testDefaultEncryption() {
            assertEquals("aes", SrvConfDefault.getEncryption().getAlgorithm());
            assertEquals("changeMe", SrvConfDefault.getEncryption().getKey());
        }

        @Test
        @DisplayName("config file encryption object is set")
        void testFileEncryption() {
            assertEquals("aes", SrvConfYaml.getEncryption().getAlgorithm());
            assertEquals("SomeCoolEncryptionKey",
                    SrvConfYaml.getEncryption().getKey());
        }
    }
}
