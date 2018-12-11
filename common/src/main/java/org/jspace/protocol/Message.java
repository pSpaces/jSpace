package org.jspace.protocol;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jspace.Tuple;
import org.jspace.Template;
import org.jspace.protocol.MessageType;
import org.jspace.io.json.jSonUtils;

/**
 * Provides the common message functionality used
 * for both management messages and pSpace messages
 */
public class Message {
    public String session;
    private Status status;
    public InteractionMode interactionMode;
    public MessageType messageType;
//    public Tuple[] tuples; // replace with data
//    public String statusCode;
//    public String statusMessage;
//    public boolean status;
    public void setTarget(String target) {};
//    public Tuple getTuple() { return null; } // FIXME replace with getData
//    public Template getTemplate() { return null; } // FIXME replace with getData

    public Message(MessageType op, String session) {
        this.messageType = op;
        this.session = session;
    }

    public Message(MessageType op, String session, Status status) {
        this(op, session);
        this.status = status;
    }

    public boolean isSuccessful() {
        // FIXME is this necessary to have?
        return false;
    }

    /**
     * sets the session field of a message to a string
     */
    public void setSession(String session) {
        this.session = session;
    }

    /**
     * sets the status field of a message to a status object
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * @return Returns the session field of a message
     */
    public String getSession() {
        return this.session;
    }

    /**
     * @return Returns the MessageType field of a message
     */
    public MessageType getOperation() {
        return this.messageType;
    }

    /**
     * @return Returns the Status field of a message
     */
    public Status getStatus() {
        return this.status;
    }

    /**
     * @return Returns the InteractionMode field of a message
     */
	public InteractionMode getInteractionMode() {
		return this.interactionMode;
	}

    @Override
    public String toString() {
        Gson gson = jSonUtils.getInstance().getGson();
        return gson.toJson(this);
    }
}
