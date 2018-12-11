package org.jspace;

import java.util.List;
import java.lang.InterruptedException;

import org.jspace.SpaceRepository;
import org.jspace.TemplateField;
import org.jspace.protocol.SpaceKeys;
import org.jspace.protocol.SpaceProperties;

/** A NamedSpace is a wrapper around a repository and the properties of a
 * remotely located space.
 * It is the Space object which is meant to be used by the end-user. */
public class NamedSpace {
    private final SpaceRepository repository;
    private final SpaceProperties properties;

    public NamedSpace(SpaceRepository repository, SpaceProperties properties) {
        this.repository = repository;
        this.properties = properties;
    }

    public SpaceKeys getKeys() {
        return this.properties.getKeys();
    }

    public SpaceRepository getRepository() {
        return this.repository;
    }

    public boolean put(Object ... fields) throws InterruptedException {
        return getRepository().put(properties, fields);
    }

    public Object[] get(TemplateField ... fields) throws InterruptedException {
        return getRepository().get(properties, fields);
    }

    public Object[] getp(TemplateField ... fields)
            throws InterruptedException {
        return getRepository().getp(properties, fields);
    }

    public List<Object[]> getAll(TemplateField ... fields)
            throws InterruptedException {
        return getRepository().getAll(properties, fields);
    }

    public Object[] query(TemplateField ... fields)
            throws InterruptedException {
        return getRepository().query(properties, fields);
    }

    public Object[] queryp(TemplateField ... fields)
            throws InterruptedException {
        return getRepository().queryp(properties, fields);
    }

    public List<Object[]> queryAll(TemplateField ... fields)
            throws InterruptedException {
        return getRepository().queryAll(properties, fields);
    }
}
