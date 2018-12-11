package org.jspace.protocol;

import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import org.jspace.Tuple;
import org.jspace.Template;
import org.jspace.protocol.DataProperties;
import org.jspace.protocol.SpaceProperties;
import org.jspace.protocol.Status;
import org.jspace.protocol.MessageType;

/**
 * pSpaceMessage provides the message type used when basic operations
 * are perfomed like 'put', 'get' and 'query'
 */
public class pSpaceMessage extends Message {
    private SpaceProperties target;
    private DataProperties data; 
    public pSpaceMessage(MessageType op,
            String session,
            SpaceProperties target,
            DataProperties data,
            Status status) {
        super(op, session, status);
        this.target = target;
        this.data = data;
    }

    /**
     * @return Returns the target space properties
     */
    public SpaceProperties getTarget() {
        return this.target;
    }

    /**
     * @return Returns the message data properties
     */
    public DataProperties getData() {
        return this.data;
    }

    /**
     * Sets the target field to a SpaceProperties object
     */
    public void setTarget(SpaceProperties target) {
        this.target = target;
    }

    /**
     * Sets the data field to a DataProperties object
     */
    public void setData(DataProperties data) {
        this.data = data;
    }

    public Tuple getTuple() {
        if (getData().getType().equals("tuple") && getData().getValue() instanceof Tuple) {
            return (Tuple) getData().getValue();
        }

        return null;
    }

    public List<Tuple> getTuples() {
        if (getData().getType().equals("tuples") && getData().getValue() instanceof List) {
            return (List<Tuple>) getData().getValue();
        }

        return null;
    }

    public Template getTemplate() {
        if (getData().getType().equals("template") && getData().getValue() instanceof Template) {
            return (Template) getData().getValue();
        }

        return null;
    }

    @Override
	final public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(messageType)
            .append(session)
            .append(getStatus())
   //         .append(interactionMode)
            .append(data)
            .append(target)
            .toHashCode();
    }

    @Override
    final public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof pSpaceMessage)) return false;

        pSpaceMessage other = (pSpaceMessage) obj;

        return new EqualsBuilder()
            .append(messageType, other.messageType)
            .append(session, other.session)
            .append(getStatus(), other.getStatus())
    //        .append(interactionMode, other.interactionMode)
            .append(data, other.data)
            .append(target, other.target)
            .isEquals();
    }
}
