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

import java.net.URI;

import org.jspace.Template;
import org.jspace.Tuple;

/**
 * A RequestMessage represents a message sent by a server to the client.
 */
public class ClientMessage {

	private final ClientMessageType messageType;
	
	private final InteractionMode interactionMode;
	
	private String target;
	
	private final boolean blocking;
	
	private final boolean all;
	
	private final String statusCode;
	
	private final String statusMessage;
	
	private final Tuple tuple;
	
	private final Template template;
	
	private String clientSession;
	
	private final String serverSession;
	
	private final URI clientURI;

	/**
	 * @param messageType
	 * @param interactionMode
	 * @param statusCode
	 * @param statusMessage
	 * @param tuple
	 * @param template
	 * @param clientSession
	 * @param serverSession
	 * @param clientURI
	 */
	public ClientMessage(ClientMessageType messageType, 
			InteractionMode interactionMode, 
			String target,
			String statusCode,
			String statusMessage, 
			Tuple tuple, 
			Template template,
			boolean blocking,
			boolean all,
			String clientSession, 
			String serverSession,
			URI clientURI) {
		super();
		this.messageType = messageType;
		this.interactionMode = interactionMode;
		this.target = target;
		this.statusCode = statusCode;
		this.statusMessage = statusMessage;
		this.tuple = tuple;
		this.template = template;
		this.clientSession = clientSession;
		this.serverSession = serverSession;
		this.clientURI = clientURI;
		this.blocking = blocking;
		this.all = all;
	}
	
	public ClientMessageType getMessageType() {
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

	public Tuple getTuple() {
		return tuple;
	}

	public Template getTemplate() {
		return template;
	}

	public String getClientSession() {
		return clientSession;
	}

	public String getServerSession() {
		return serverSession;
	}

	public URI getClientURI() {
		return clientURI;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (blocking ? 1231 : 1237);
		result = prime * result + ((clientSession == null) ? 0 : clientSession.hashCode());
		result = prime * result + ((clientURI == null) ? 0 : clientURI.hashCode());
		result = prime * result + ((interactionMode == null) ? 0 : interactionMode.hashCode());
		result = prime * result + ((messageType == null) ? 0 : messageType.hashCode());
		result = prime * result + ((serverSession == null) ? 0 : serverSession.hashCode());
		result = prime * result + ((statusCode == null) ? 0 : statusCode.hashCode());
		result = prime * result + ((statusMessage == null) ? 0 : statusMessage.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		result = prime * result + ((template == null) ? 0 : template.hashCode());
		result = prime * result + ((tuple == null) ? 0 : tuple.hashCode());
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
		ClientMessage other = (ClientMessage) obj;
		if (blocking != other.blocking)
			return false;
		if (clientSession == null) {
			if (other.clientSession != null)
				return false;
		} else if (!clientSession.equals(other.clientSession))
			return false;
		if (clientURI == null) {
			if (other.clientURI != null)
				return false;
		} else if (!clientURI.equals(other.clientURI))
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
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		if (template == null) {
			if (other.template != null)
				return false;
		} else if (!template.equals(other.template))
			return false;
		if (tuple == null) {
			if (other.tuple != null)
				return false;
		} else if (!tuple.equals(other.tuple))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ClientMessage [" + (messageType != null ? "messageType=" + messageType + ", " : "")
				+ (interactionMode != null ? "interactionMode=" + interactionMode + ", " : "")
				+ (target != null ? "target=" + target + ", " : "") + "blocking=" + blocking + ", "
				+ (statusCode != null ? "statusCode=" + statusCode + ", " : "")
				+ (statusMessage != null ? "statusMessage=" + statusMessage + ", " : "")
				+ (tuple != null ? "tuple=" + tuple + ", " : "")
				+ (template != null ? "template=" + template + ", " : "")
				+ (clientSession != null ? "clientSession=" + clientSession + ", " : "")
				+ (serverSession != null ? "serverSession=" + serverSession + ", " : "")
				+ (clientURI != null ? "clientURI=" + clientURI : "") + "]";
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public static ClientMessage putRequest(Tuple t) {
		return new ClientMessage(
			ClientMessageType.PUT_REQUEST, //REQUEST
			null, 
			null, 
			null, 
			null, 
			t, 
			null, 
			false, 
			false,
			null, 
			null, 
			null
		);
	}

	public static ClientMessage getRequest(Template template, boolean isBlocking, boolean all) {
		return new ClientMessage(
			ClientMessageType.GET_REQUEST, //messageType, 
			null, //interactionMode, 
			null, //target, 
			null, //statusCode, 
			null, //statusMessage, 
			null, //tuple, 
			template, //template
			isBlocking, //isBlocking
			all, //all
			null, //clientSession, 
			null, //serverSession, 
			null //clientURI
		);
	}

	public static ClientMessage queryRequest(Template template, boolean isBlocking, boolean all) {
		return new ClientMessage(
			ClientMessageType.QUERY_REQUEST, //messageType, 
			null, //interactionMode, 
			null, //target, 
			null, //statusCode, 
			null, //statusMessage, 
			null, //tuple, 
			template, //template
			isBlocking, //isBlocking
			all, //all
			null, //clientSession, 
			null, //serverSession, 
			null //clientURI
		);
	}

	public boolean isBlocking() {
		return blocking;
	}

	public boolean getAll() {
		return all;
	}

	public void setClientSession(String clientSession) {
		this.clientSession = clientSession;
	}


}
