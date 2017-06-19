/**
 * 
 * jSpace: a Java Framework for Programming Concurrent and Distributed Applications with Spaces
 * 
 * http://pspace.github.io/jSpace/	
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * 
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *      Alberto Lluch Lafuente
 *      Michele Loreti
 *      Francesco Terrosi
 */
package org.jspace.protocol;

import java.util.Arrays;

import org.jspace.Tuple;

/**
 *
 */
public class ServerMessage {

	public static final String CODE200 = "200";

	public static final String OK_STATUS = "OK";
	
	public static final String CODE400 = "400";
	
	public static final String BAD_REQUEST = "Bad Request";
	
	public static final String CODE500 = "500";

	public static final String SERVER_ERROR = "Internal Server Error";

	private final ServerMessageType messageType;
	
	private final InteractionMode interactionMode;
	
	private final boolean status;
	
	private final String statusCode;
	
	private final String statusMessage;
	
	private final Tuple[] tuples;
	
	private final String clientSession;
	
	private final String serverSession;

	/**
	 * @param messageType
	 * @param interactionMode
	 * @param statusCode
	 * @param statusMessage
	 * @param tuple
	 * @param template
	 * @param clientSession
	 * @param serverSession
	 */
	public ServerMessage(
			ServerMessageType messageType, 
			InteractionMode interactionMode, 
			boolean status,
			String statusCode,
			String statusMessage, 
			Tuple[] tuples, 
			String clientSession, 
			String serverSession) {
		super();
		this.messageType = messageType;
		this.interactionMode = interactionMode;
		this.status = status;
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
		this.tuples = tuples;
		this.clientSession = clientSession;
		this.serverSession = serverSession;
	}

	public ServerMessageType getMessageType() {
		return messageType;
	}

	public InteractionMode getInteractionMode() {
		return interactionMode;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public String getStatusMessage() {
		return statusMessage;
	}

	public Tuple[] getTuples() {
		return tuples;
	}

	public String getClientSession() {
		return clientSession;
	}

	public String getServerSession() {
		return serverSession;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((clientSession == null) ? 0 : clientSession.hashCode());
		result = prime * result + ((interactionMode == null) ? 0 : interactionMode.hashCode());
		result = prime * result + ((messageType == null) ? 0 : messageType.hashCode());
		result = prime * result + ((serverSession == null) ? 0 : serverSession.hashCode());
		result = prime * result + (status ? 1231 : 1237);
		result = prime * result + ((statusCode == null) ? 0 : statusCode.hashCode());
		result = prime * result + ((statusMessage == null) ? 0 : statusMessage.hashCode());
		result = prime * result + Arrays.hashCode(tuples);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ServerMessage other = (ServerMessage) obj;
		if (clientSession == null) {
			if (other.clientSession != null)
				return false;
		} else if (!clientSession.equals(other.clientSession))
			return false;
		if (interactionMode != other.interactionMode)
			return false;
		if (messageType != other.messageType)
			return false;
		if (serverSession == null) {
			if (other.serverSession != null)
				return false;
		} else if (!serverSession.equals(other.serverSession))
			return false;
		if (status != other.status)
			return false;
		if (statusCode == null) {
			if (other.statusCode != null)
				return false;
		} else if (!statusCode.equals(other.statusCode))
			return false;
		if (statusMessage == null) {
			if (other.statusMessage != null)
				return false;
		} else if (!statusMessage.equals(other.statusMessage))
			return false;
		if (!Arrays.equals(tuples, other.tuples))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ServerMessage [" + (messageType != null ? "messageType=" + messageType + ", " : "")
				+ (interactionMode != null ? "interactionMode=" + interactionMode + ", " : "") + "status=" + status
				+ ", " + (statusCode != null ? "statusCode=" + statusCode + ", " : "")
				+ (statusMessage != null ? "statusMessage=" + statusMessage + ", " : "")
				+ (tuples != null ? "tuples=" + Arrays.toString(tuples) + ", " : "")
				+ (clientSession != null ? "clientSession=" + clientSession + ", " : "")
				+ (serverSession != null ? "serverSession=" + serverSession : "") + "]";
	}

	public boolean isSuccessful() {
		return status;
	}

	public static ServerMessage putResponse(boolean status) {
		return new ServerMessage(
			ServerMessageType.PUT_RESPONSE,// messageType, 
			null, //interactionMode, 
			status, //status 
			ServerMessage.CODE200, //statusCode, 
			ServerMessage.OK_STATUS, //statusMessage, 
			null,//tuples, 
			null,//clientSession, 
			null //serverSession
		);
	}

	public static ServerMessage getResponse(Tuple[] tuples) {
		return new ServerMessage(
				ServerMessageType.GET_RESPONSE,// messageType, 
				null, //interactionMode, 
				true, //status 
				ServerMessage.CODE200, //statusCode, 
				ServerMessage.OK_STATUS, //statusMessage, 
				tuples,//tuples, 
				null,//clientSession, 
				null //serverSession
			);
	}

	public static ServerMessage badRequest() {
		return new ServerMessage(
				ServerMessageType.FAILURE,// messageType, 
				null, //interactionMode, 
				false, //status 
				ServerMessage.CODE400, //statusCode, 
				ServerMessage.BAD_REQUEST, //statusMessage, 
				null,//tuples, 
				null,//clientSession, 
				null //serverSession
			);
	}

	public static ServerMessage internalServerError() {
		return new ServerMessage(
				ServerMessageType.FAILURE,// messageType, 
				null, //interactionMode, 
				false, //status 
				ServerMessage.CODE500, //statusCode, 
				ServerMessage.SERVER_ERROR, //statusMessage, 
				null,//tuples, 
				null,//clientSession, 
				null //serverSession
			);
	}

	
	
}
