package org.jspace.protocol;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;

import org.jspace.protocol.RepositoryProperties;
import org.jspace.protocol.SpaceProperties;
import org.jspace.protocol.Status;
import org.jspace.protocol.MessageType;

/**
 * A ManagementMessage is a message sent to the server which contains
 * instructions for modifying the server properties
 * It is used for adding and removing repositories and spaces.
 */
public class ManagementMessage extends Message {
    private String serverKey;
    private RepositoryProperties repository;
    private SpaceProperties space;

    public static final Status OK = new Status(200, "OK");
    public static final Status BAD_REQUEST = new Status(400, "Bad Request");
    public static final Status SERVER_ERROR = new Status(500, "Internal Server Error");

    public ManagementMessage(MessageType op,
            String serverKey,
            RepositoryProperties repository,
            SpaceProperties space,
            Status status,
            String session) {
        super(op, session, status);
        this.serverKey = serverKey;
        this.repository = repository;
        this.space = space;
    }

    public ManagementMessage(MessageType type, String serverKey, RepositoryProperties repository,
            SpaceProperties space,
            Status status) {
        this(type, serverKey, repository, space, status, "-10");
    }


    /**
     * @return Returns the key field of the message
     */
    public String getServerKey() {
        return this.serverKey;
    }

    /**
     * @return Returns the repository properties of the message
     */
    public RepositoryProperties getRepository() {
        return this.repository;
    }

    /**
     * @return Returns the space properties field of the message
     */
    public SpaceProperties getSpace() {
        return this.space;
    }

    /**
     * Sets the serverKey field of the message to a string
     */
    public void setServerKey(String key) {
        this.serverKey = key;
    }

    /**
     * Sets the repository field of the message to a RepositoryProperties object
     */
    public void setRepository(RepositoryProperties repository) {
        this.repository = repository;
    }

    /**
     * Sets the space field of the message to a SpaceProperties object
     */
    public void setSpace(SpaceProperties space) {
        this.space = space;
    }

	@Override
	final public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(messageType)
            .append(serverKey)
            .append(repository)
            .append(space)
            .append(interactionMode)
            .append(getStatus())
            .append(session)
            .toHashCode();
	}

    @Override
	final public boolean equals(Object obj) {
		if (obj == this) return true;
        if (!(obj instanceof ManagementMessage)) return false;

		ManagementMessage other = (ManagementMessage) obj;

        return new EqualsBuilder()
            .append(messageType, other.messageType)
            .append(serverKey, other.serverKey)
            .append(repository, other.repository)
            .append(space, other.space)
            .append(interactionMode, other.interactionMode)
            .append(getStatus(), other.getStatus())
            .append(session, other.session)
            .isEquals();
	}

    /**
     * @return Returns a default bad request messsage with necessary fields
     * set accordingly
     */
	public static ManagementMessage badRequest(String session) {
		return new ManagementMessage(
				MessageType.FAILURE, // MessageType,
                null, //serverKey,
                null, //repository,
                null, //space,
                BAD_REQUEST,
                session
			);
	}

    /**
     * @return Returns a bad request messsage with a specific string
     * and necessary fields set accordingly
     */
	public static ManagementMessage badRequest(String session, String msg) {
		return new ManagementMessage(
				MessageType.FAILURE, // MessageType,
                null, //serverKey,
                null, //repository,
                null, //space,
                new Status(400, msg),
                session
			);
	}

    /**
     * @return Returns an internal server error messsage with
     * necessary fields set accordingly
     */
	public static ManagementMessage internalServerError() {
		return new ManagementMessage(
				MessageType.FAILURE, // MessageType,
                null, //serverKey,
                null, //repository,
                null, //space,
                SERVER_ERROR
			);
	}
}
