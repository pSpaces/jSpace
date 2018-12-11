package org.jspace.protocol;

import org.jspace.protocol.SpaceKeys;
import org.jspace.protocol.SpaceType;

/**
 * Provides the properties of a space.
 * @param type is the space implementation (ie. SequentialSpace, StackSpace)
 * @param name is the name of the space
 * @param keys are the keys used to access different types of operations
 * @param uid is the unique identifier used when attaching a space
 * to a repository
 */
public class SpaceProperties {
    private SpaceType type;
    private String name;
    private SpaceKeys keys;
    private String uuid;

    public SpaceProperties(SpaceType type, String name, String uuid,
            SpaceKeys keys) {
        this.type = type;
        this.name = name;
        this.keys = keys;
        this.uuid = uuid;
    }

    public SpaceProperties() { }

    /** 
     * @return Returns the type of a space object
     */
    public SpaceType getType() {
        return this.type;
    }

    /**
     * @return Returns the name of the space
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return Returns the uid of the space
     */
    public String getUUID() {
        return this.uuid;
    }

    /**
     * @return Returns the SpaceKeys object for the space
     */
    public SpaceKeys getKeys() {
        return this.keys;
    }

    /**
     * sets the type field of a SpaceProperties object
     */
    public void setType(SpaceType type) {
        this.type = type;
    }

    /**
     * sets the name field of a SpaceProperties object
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * sets the UID field of a SpaceProperties object
     */
    public void setUUID(String uuid) {
        this.uuid = uuid;
    }

    /**
     * sets the key field of SpaceProperties object
     */
    public void setKeys(SpaceKeys keys) {
        this.keys = keys;
    }

    public String toString() {
        return "Name: " + getName() + ",\n"
            + "UUID: " + getUUID() + ",\n"
            + "Type: " + getType() + ",\n"
            + "Keys: " + getKeys();
    }
}
