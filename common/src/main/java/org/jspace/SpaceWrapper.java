package org.jspace;

import org.jspace.Space;
import org.jspace.protocol.SpaceProperties;
import org.jspace.protocol.SpaceKeys;

/** Wrapper around a Space, which also attaches important sattelite
 * information */
public class SpaceWrapper {
    private Space space;
    private SpaceProperties properties;

    SpaceWrapper(Space space, SpaceProperties properties) {
        this.space = space;
        this.properties = properties;
    }

    public Space getSpace() {
        return this.space;
    }

    public SpaceKeys getKeys() {
        return this.properties.getKeys();
    }

    public SpaceProperties getProperties() {
        return this.properties;
    }
}
