package org.jspace;

import org.jspace.config.ServerConfig;
import org.jspace.gate.GateFactory;
import org.jspace.gate.ServerGate;
import org.jspace.gate.ClientHandler;
import org.jspace.protocol.RepositoryProperties;
import org.jspace.protocol.SpaceProperties;
import org.jspace.protocol.Message;
import org.jspace.protocol.MessageType;
import org.jspace.protocol.SpaceType;
import org.jspace.protocol.ManagementMessage;
import org.jspace.protocol.Status;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import java.util.HashSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import java.security.MessageDigest;

import java.net.SocketException;
import java.net.URISyntaxException;
import java.io.IOException;

/**
 * Provides handling of incoming management messages
 * and data structures of spaces and repositories
 */
public class ManagementServer extends Server {
    // spaces maps UUIDs to space references
    private final HashMap<String, SpaceWrapper> spaces = new HashMap<String, SpaceWrapper>();
    // repositories maps repository names to references
    private final HashMap<String, Repository> repositories = new HashMap<String, Repository>();
    private final HashSet keys = new HashSet();
    private static ManagementServer instance;

    private ManagementServer(ServerConfig config) {
        super(config, ManagementMessage.class);
    }

    private ManagementServer() {
        super(ManagementMessage.class);
    }

    public static ManagementServer getInstance() {
        if (instance == null) {
            instance = new ManagementServer();
        }

        return instance;
    }

    public static ManagementServer getInstance(ServerConfig config) {
        if (instance == null) {
            instance = new ManagementServer(config);
        }

        return instance;
    }

    protected Message handle(Message message) throws InterruptedException {
        switch (message.getOperation()) {
            case CREATE_REPOSITORY:
                return handleCreateRepository((ManagementMessage) message);
            case CREATE_SPACE:
                return handleCreateSpace((ManagementMessage) message);
            case REMOVE_REPOSITORY:
                return handleRemoveRepository(message);
            case REMOVE_SPACE:
                return handleRemoveSpace(message);
            case ATTACH:
                return handleAttachSpace(message);
            default:
                return ManagementMessage.badRequest(message.getSession());
        }
    }

    /** Handles the creation of a new repository on the server
     *  The message must contain a valid server key,
     *  a @see org.jspace.protocol.RepositoryProperties object
     *  ... */
    private Message handleCreateRepository(ManagementMessage in) {
        // authorized?
        if (!validateKey(in.getServerKey())) {
            // not authorized
            return ManagementMessage.badRequest(in.getSession(),
                    "Not authorized: incorrect server key");
        }

        // message has RepositoryProperties?
        if (in.getRepository() == null || in.getRepository().getName() == null) {
            return ManagementMessage.badRequest(in.getSession(),
                    "Malformed message: missing or malformed 'Repository' field");
        }

        // check if the name already exists
        if (repositories.containsKey(in.getRepository().getName())) {
            Repository existing = repositories.get(in.getRepository().getName());
            // repository exists, check key and return information
            if (!existing.validateKey(in.getRepository().getKey())) {
                // bad key
                return ManagementMessage.badRequest(in.getSession(),
                        "Not authorized: incorrect repository key");
            } else {
                // everything was okay, return info of existing repo
                ManagementMessage out = new ManagementMessage(
                        MessageType.CREATE_REPOSITORY,
                        "",
                        new RepositoryProperties(
                            in.getRepository().getName(),
                            "", // repository key
                            existing.getPort()
                        ),
                        null, // space properties
                        new Status(200, "OK"),
                        in.getSession()
                );
                return out;
            }
        }

        // repository didn't exist
        Repository newRepo = new Repository(
                in.getRepository().getName(),
                in.getRepository().getKey());

        // add key to the key set
        keys.add(in.getRepository().getKey());

        repositories.put(newRepo.getName(), newRepo);


        ManagementMessage out = new ManagementMessage(
                MessageType.CREATE_REPOSITORY,
                "", // serverkey
                new RepositoryProperties(
                    newRepo.getName(),
                    "", // repo key FIXME what should it be?
                    newRepo.getPort()),
                null, // spaceproperties
                new Status(200, "OK"),
                in.getSession()); // status

        return out;
    }

    // remove a repository
    private Message handleRemoveRepository(Message msg) {
        return null;
    }

    // create a space FIXME not message passing, method called by Repository
    private Message handleCreateSpace(ManagementMessage in) {
        RepositoryProperties repo = in.getRepository();
        // authorized? (Repository Manager requires _ANY_ repo key)
        if (repo != null && !keys.contains(repo.getKey())) {
            // not authorized - invalid repo key
            return ManagementMessage.badRequest(in.getSession(),
                    "Not authorized: unknown repository key");
        }

        // message has RepositoryProperties and SpaceProperties?
        if ((repo == null || repo.getName() == null)
                && (in.getSpace() == null || in.getSpace().getName() == null)) {
            return ManagementMessage.badRequest(in.getSession(),
                    "Malformed message: missing or malformed 'Repository' "
                    + "field or 'Space' field");
        }

        // check if the space already exists in repo
        if (repositories.containsKey(repo.getName())) {
            Repository repository = repositories.get(repo.getName());
            // repository exists, check key and return information
            if (!repository.validateKey(repo.getKey())) {
                // bad key
                return ManagementMessage.badRequest(in.getSession(),
                        "Not authorized: incorrect repository key");
            } else {
                // repository is good, check for space existence and create a
                // new space if  needed
                SpaceProperties props = in.getSpace();
                SpaceWrapper existingSpace = repository.getSpace(props.getName());

                if (existingSpace == null) {
                    // create new space
                    props.setUUID(UUID.randomUUID().toString());
                    SpaceWrapper space = null;

                    switch (props.getType()) {
                        case PILE:
                            space = new SpaceWrapper(new PileSpace(), props);
                        case QUEUE:
                            space = new SpaceWrapper(new QueueSpace(), props);
                        case RANDOM:
                            space = new SpaceWrapper(new RandomSpace(), props);
                        case SEQUENTIAL:
                            space = new SpaceWrapper(new SequentialSpace(), props);
                        case STACK:
                            space = new SpaceWrapper(new StackSpace(), props);
                        default:
                            break;
                            // FIXME below return statement does not work but is
                            // necessary :(
                            //        return ManagementMessage.badRequest(in.getSession(),
                            //            "Malformed message: unknown space type");
                    }

                    spaces.put(props.getUUID(), space);
                    repositories.get(in.getRepository().getName()).addSpace(space);

                    return new ManagementMessage(
                            MessageType.CREATE_SPACE,
                            "", // serverkey
                            new RepositoryProperties(
                                in.getRepository().getName(),
                                null),
                            new SpaceProperties(
                                props.getType(),
                                props.getName(),
                                props.getUUID(),
                                null),
                            new Status(200, "OK"),
                            in.getSession()); // status
                }

                // space did exist, return its properties
                props = existingSpace.getProperties();
                return new ManagementMessage(
                        MessageType.CREATE_SPACE,
                        "", // serverkey
                        new RepositoryProperties(
                            in.getRepository().getName(),
                            null),
                        new SpaceProperties(
                            props.getType(),
                            props.getName(),
                            props.getUUID(),
                            null),
                        new Status(200, "OK"),
                        in.getSession());

            }
        }
        return null;
    }

    // remove a space TODO figure out suitable logic here. Spaces should
    // not just be removed without removing all references
    private Message handleRemoveSpace(Message msg) {
        return null;
    }

    // link a space and repository
    private Message handleAttachSpace(Message msg) {
        return null;
    }

    // TODO decide if we want this implementation
    private boolean validateKey(String key) {
        System.out.println("## WARNING: server key validation always return true");
        return true;
    }
}
