package org.jspace.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.jspace.config.TLSConfig;

@DisplayName("A TLSConfig object")
public class TestTLSConfig {

    TLSConfig tls;
    static String keystore = "/some/absolute/path";
    static String passwd = "myPassword";
    static String truststore = "someRelativePath";

    @Test
    @DisplayName("is istantiated with new TLSConfig(all parameters)")
    void isInstantiatedWithNew() {
        new TLSConfig(keystore, passwd, truststore);
    }

    @Nested
    @DisplayName("when new")
    class WhenNew {
        @BeforeEach
        void createNewConfig() {
            tls = new TLSConfig(keystore, passwd, truststore);
        }

        @Test
        @DisplayName("absolute path is set correctly")
        void testAbsolutePath() {
            assertEquals("/some/absolute/path", tls.getKeyStore());
        }

        @Test
        @DisplayName("relative path is set correctly")
        void testRelativePath() {
            assertEquals(System.getProperty("user.dir") + "/" + truststore, tls.getTrustStore());
        }

        @Test
        @DisplayName("keystore password is set to a string")
        void testPassword() {
            assertEquals(passwd, tls.getKeyStorePassword());
        }

        @Test
        @DisplayName("keyStorePath is settable to a string")
        void testSetKeyStorePath() {
            tls.setKeyStorePath("/some/path");
            assertEquals("/some/path", tls.getKeyStore());
        }

        @Test
        @DisplayName("keystore password is settable to a string")
        void testSetKeyStorePassword() {
            tls.setKeyStorePassword("mypassword");
            assertEquals("mypassword", tls.getKeyStorePassword());
        }

        @Test
        @DisplayName("trustStorePath is settable to a string")
        void testTrustStorePath() {
            tls.setTrustStorePath("/my/path");
            assertEquals("/my/path", tls.getTrustStore());
        }
    }
}
