package org.jspace.protocol;

/**
 * Provides a set of constants that the operation field of messages
 * must use
 */
public enum MessageType {
    // space operations
	PUT,
	GET,
    GETP,
    GETALL,
	QUERY,
    QUERYP,
    QUERYALL,
    // management operations
    CREATE_REPOSITORY,
    CREATE_SPACE,
    REMOVE_REPOSITORY,
    REMOVE_SPACE,
    ATTACH,
    // generic responses
	ACK,
	FAILURE 
}
