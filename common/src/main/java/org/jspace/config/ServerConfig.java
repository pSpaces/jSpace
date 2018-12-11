package org.jspace.config;

import org.ho.yaml.Yaml;

import java.io.File;
import java.util.HashMap;
import java.net.URI;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;

import org.jspace.protocol.Message;

/**
 * Provides a way to handle configuration of servers
 */
public class ServerConfig {
    private String protocol = "tls";
    private String address = "localhost";
    private int port = 7000;
    private String serverKey = "";
    private String marshaller = "json";
    private int verbosity = 0;
    private EncryptionConfig encryption = new EncryptionConfig("aes",
            "changeMe");
    private TLSConfig tls = new TLSConfig("myKeyStore.jks", "password",
            "myTrustStore.jks");

    public ServerConfig(int port, String serverKey) {
        this.port = port;
        this.serverKey = serverKey;
    }

    /**
     * Provides a construct to handle configuration from file
     */
    public ServerConfig(String configFile) {
        // load file and override supplied settings
        try {
            HashMap<String, Object> config = Yaml.loadType(new File(configFile),
                    java.util.HashMap.class);

            config.forEach((prop, val) -> {
                    if (prop.toLowerCase().equals("protocol")) {
                        this.protocol = val.toString().toLowerCase();
                    } else if (prop.toLowerCase().equals("address")) {
                        this.address = val.toString().toLowerCase();
                    } else if (prop.toLowerCase().equals("port")) {
                        this.port = (Integer) val;
                    } else if (prop.toLowerCase().equals("server_key")) {
                        this.serverKey = val.toString();
                    } else if (prop.toLowerCase().equals("marshaller")) {
                        this.marshaller = val.toString().toLowerCase();
                    } else if (prop.toLowerCase().equals("verbosity")) {
                        this.verbosity = (Integer) val;
                    } else if (prop.toLowerCase().equals("encryption")) {
                        HashMap encryptOpts = (HashMap<String, String>) val;
                        this.encryption = new EncryptionConfig(
                                encryptOpts.get("algorithm").toString(),
                                encryptOpts.get("key").toString());
                    } else if (prop.toLowerCase().equals("tls_properties")) {
                        HashMap tlsOpts = (HashMap<String, String>) val;
                        this.tls = new TLSConfig(
                                tlsOpts.get("key_store_path").toString(),
                                tlsOpts.get("key_store_pass").toString(),
                                tlsOpts.get("trust_store_path").toString());
                    } else {
                        System.out.println("Unknown configuration option in "
                                + configFile + ": '" + prop);
                    }
            });
        } catch (FileNotFoundException e) {
            // FIXME handle missing config files correctly
            System.out.println("WARN: Could not read config file, "
                    + "using default values");
        }

        // set TLS properties
        System.setProperty("javax.net.ssl.keyStore", this.tls.getKeyStore());
        System.setProperty("javax.net.ssl.keyStorePassword",
                this.tls.getKeyStorePassword());
        System.setProperty("javax.net.ssl.trustStorePassword",
                this.tls.getKeyStorePassword());
        System.setProperty("javax.net.ssl.trustStore",
                this.tls.getTrustStore());
        System.setProperty("javax.net.ssl.trustStoreType","JKS");
        System.setProperty("javax.net.ssl.keyStoreType","JKS");

//        System.out.println("TLS Props are set to "
//                + System.getProperty("javax.net.ssl.keyStore") + " "
//                + System.getProperty("javax.net.ssl.keyStorePassword") + " "
//                + System.getProperty("javax.net.ssl.trustStore") + " "
//                + System.getProperty("javax.net.ssl.trustStorePassword") + " "
//                + System.getProperty("javax.net.ssl.keyStoreType") + " "
//                + System.getProperty("javax.net.ssl.trustStoreType") + " "
//                );

        if (this.verbosity == 2) {
            System.out.println("Server configuration set using options from "
                    + configFile + ": \n" + this.toString());
        }
    }

    public ServerConfig() {
        // use default config file path
        this("./config.yml");
    }

    /**
     * @return Returns the protocol field of a server configuration
     */
    public String getProtocol() {
        return this.protocol;
    }

    /**
     * @return Returns the address field of a server configuration
     */
    public String getAddress() {
        return this.address;
    }

    /**
     * @return Returns the port field of a server configuration
     */
    public int getPort() {
        return this.port;
    }

    /**
     * @return Returns the serverKey field of a server configuration
     */
    public String getServerKey() {
        return this.serverKey;
    }

    /**
     * @return Returns the marshaller field of a server configuration
     */
    public String getMarshaller() {
        return this.marshaller;
    }

    /**
     * @return Returns the verbosity level field of a server configuration
     */
    public int getVerbosity() {
        return this.verbosity;
    }

    /**
     * @return Returns the encryption configuration field of a server
     * configuration
     */
    public EncryptionConfig getEncryption() {
        return this.encryption;
    }

    /**
     * @return Returns the TLS configuration field of a server
     * configuration
     */
    public TLSConfig getTLSConfig() {
        return this.tls;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * @return Returns the URI field of a server configuration
     */
    public URI getURI() throws URISyntaxException {
        return new URI(this.protocol, null, this.address, this.port, null, "keep", null);
    }

    public String toString() {
        return "Protocol: " + this.protocol + "\n" +
            "Address: " + this.address + "\n" +
            "Port: " + this.port + "\n" +
            "Server key: " + this.serverKey + "\n" +
            "Marshaller: " + this.marshaller + "\n" +
            "Verbosity: " + this.verbosity + "\n" +
            "Encryption: " + this.encryption + "\n" +
            "TLS config: " + this.tls;
    }
}
