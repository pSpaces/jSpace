package org.jspace.protocol;

/**
 * A RepositoryProperties object keeps the properties of a repository.
 * @param name is the name of the repository
 * @param key is the key used for managing the repository
 */
public class RepositoryProperties {
    private String name;
    private String key;
    private int port;

    public RepositoryProperties(String name, String key) {
        this(name, key, -1);
    }

    public RepositoryProperties(String name, String key, int port) {
        this.name = name;
        this.key = key;
        this.port = port;
    }

    /**
     * @return Returns the name of the repository
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return Returns the key used for managing the repository
     */
    public String getKey() {
        return this.key;
    }

    public int getPort() {
        return this.port;
    }

    /**
     * sets the repository name to a string
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * sets the repository key to a string
     */
    public void setKey(String key) {
        this.key = key;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
