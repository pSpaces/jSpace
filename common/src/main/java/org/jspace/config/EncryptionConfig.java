package org.jspace.config;

public class EncryptionConfig {
    private String algorithm;
    private String key;

    public EncryptionConfig() {
    }

    public EncryptionConfig(String algorithm, String key) {
        this.algorithm = algorithm;
        this.key = key;
    }

    public String getAlgorithm() {
        return this.algorithm;
    }

    public String getKey() {
        return this.key;
    }

    public String toString() {
        return "[" + this.algorithm + ", " + this.key + "]";
    }
}
