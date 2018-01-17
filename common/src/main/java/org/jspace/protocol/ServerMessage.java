/*******************************************************************************
 * Copyright (c) 2017 Michele Loreti and the jSpace Developers (see the included 
 * authors file).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
package org.jspace.protocol;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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

	public List<Object[]> getTuples() {
		LinkedList<Object[]> result = new LinkedList<>();
		for( int i=0 ; i<tuples.length ; i++ ) {
			result.add(tuples[i].getTuple());
		}
		return result;
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

	public static ServerMessage putResponse(boolean status, String clientSession) {
		if (status) {
			return new ServerMessage(
					ServerMessageType.PUT_RESPONSE,// messageType, 
					null, //interactionMode, 
					status, //status 
					ServerMessage.CODE200, //statusCode, 
					ServerMessage.OK_STATUS, //statusMessage, 
					null,//tuples, 
					clientSession,//clientSession, 
					null //serverSession
				);
		} else {
			return new ServerMessage(
					ServerMessageType.PUT_RESPONSE,// messageType, 
					null, //interactionMode, 
					status, //status 
					ServerMessage.CODE400, //statusCode, 
					ServerMessage.BAD_REQUEST, //statusMessage, 
					null,//tuples, 
					clientSession,//clientSession, 
					null //serverSession
					);
		}
	}

	public static ServerMessage getResponse(List<Object[]> tuples, String clientSession) {
		return new ServerMessage(
				ServerMessageType.GET_RESPONSE,// messageType, 
				null, //interactionMode, 
				true, //status 
				ServerMessage.CODE200, //statusCode, 
				ServerMessage.OK_STATUS, //statusMessage, 
				toListOfTuples(tuples),//tuples, 
				clientSession,//clientSession, 
				null //serverSession
			);
	}

	private static Tuple[] toListOfTuples(List<Object[]> tuples) {
		Tuple[] result = new Tuple[tuples.size()];
		int count = 0;
		for (Object[] fields : tuples) {
			result[count++] = new Tuple(fields);
		}
		return result;
	}

	public static ServerMessage badRequest(String clientSession) {
		return new ServerMessage(
				ServerMessageType.FAILURE,// messageType, 
				null, //interactionMode, 
				false, //status 
				ServerMessage.CODE400, //statusCode, 
				ServerMessage.BAD_REQUEST, //statusMessage, 
				null,//tuples, 
				clientSession,//clientSession, 
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
