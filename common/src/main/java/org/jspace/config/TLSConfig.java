package org.jspace.config;

public class TLSConfig {
    private String keyStorePath;
    private String keyStorePassword;
    private String trustStorePath;

    public TLSConfig(String keyStorePath, String keyStorePassword,
           String trustStorePath) {
        this.keyStorePath = getPath(keyStorePath);
        this.keyStorePassword = keyStorePassword;
        this.trustStorePath = getPath(trustStorePath);
    }

    /**
     * @return Returns a path determined by input string
     */
    private String getPath(String path) {
        if (path.startsWith("/") || path.startsWith(".")) {
            // assume absolute path
            return path;
        }

        // else assume relative path
        return System.getProperty("user.dir") + "/" + path;
    }

    /**
     * @return Returns the path to the keyStore
     */
    public String getKeyStore() {
        return this.keyStorePath;
    }

    /**
     * @return Returns the password to the keyStore
     */
    public String getKeyStorePassword() {
        return this.keyStorePassword;
    }

    /**
     * @return Returns the path to the trustStore
     */
    public String getTrustStore() {
        return this.trustStorePath;
    }

    /**
     * @return Returns the TLSConfig as a string representation
     */
    public String toString() {
        return
            "{key store path: " + this.keyStorePath +
            ", key store password: " + this.keyStorePassword +
            ", trust store path: " + this.trustStorePath +
            "}";
    }

    /**
     * sets the keyStorePath to a string
     */
    public void setKeyStorePath(String path) {
        this.keyStorePath = path;
    }

    /**
     * sets the keyStorePassword to a string
     */
    public void setKeyStorePassword(String passwd) {
        this.keyStorePassword = passwd;
    }

    /**
     * sets the trustStore to a string
     */
    public void setTrustStorePath(String path) {
        this.trustStorePath = path;
    }
}
