package org.jspace.protocol;

/**
 * A SpaceKeys object keeps the keys for a Space object
 * @param management is the key for managing the space properties
 * @param put is the key needed when issuing put operations
 * @param get is the key needed when issuing get operations
 * @param query is the key needed when issuing query operations
 */
public class SpaceKeys {
    private String management;
    private String put;
    private String get;
    private String query;

    public SpaceKeys() {
        this.management = null;
        this.put = null;
        this.get = null;
        this.query = null;
    }

    public SpaceKeys(String mgmt, String put, String get, String query) {
        this.management = mgmt;
        this.put = put;
        this.get = get;
        this.query = query;
    }

    /**
     * @return Returns the management key
     */
    public String getManagementKey() {
        return this.management;
    }

    /**
     * @return Returns the put key
     */
    public String getPutKey() {
        return this.put;
    }

    /**
     * @return Returns the get key
     */
    public String getGetKey() {
        return this.get;
    }

    /**
     * @return Returns the query key
     */
    public String getQueryKey() {
        return this.query;
    }

    /**
     * Sets the Management key
     * @param key the new key used for managing the space
     */
    public void setManagementKey(String key) {
        this.management = key;
    }

    /**
     * Sets the Put key
     * @param key the new key used for Put operations
     */
    public void setPutKey(String key) {
        this.put = key;
    }

    /**
     * Sets the Get key
     * @param key the new key used for Get operations
     */
    public void setGetKey(String key) {
        this.get = key;
    }

    /**
     * Sets the Query key
     * @param key the new key used for Query operations
     */
    public void setQueryKey(String key) {
        this.query = key;
    }

    public String toString() {
        return "[\nManagement: " + getManagementKey() + ",\n"
            + "Put: " + getPutKey() + ",\n"
            + "Get: " + getGetKey() + ",\n"
            + "Query: " + getQueryKey() + "]";
    }
}
