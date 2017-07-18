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
package org.jspace.gate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.jspace.io.jSpaceMarshaller;
import org.jspace.protocol.ClientMessage;
import org.jspace.protocol.ServerMessage;

/**
 * @author loreti
 *
 */
public class ConnClientHandler implements ClientHandler {

	private jSpaceMarshaller marshaller;
	private Socket client;
	private BufferedReader reader;
	private PrintWriter writer;
	private boolean isActive = true;

	public ConnClientHandler(jSpaceMarshaller marshaller, Socket client) throws IOException {
		this.marshaller = marshaller;
		this.client = client;
		this.reader = new BufferedReader(new InputStreamReader( client.getInputStream() ));
		this.writer = new PrintWriter( client.getOutputStream() );
	}

	/* (non-Javadoc)
	 * @see org.jspace.gate.ClientHandler#receive()
	 */
	@Override
	public ClientMessage receive() throws IOException {
		ClientMessage message = marshaller.read(ClientMessage.class, reader);
		isActive  = (message != null);
		return message;
	}

	/* (non-Javadoc)
	 * @see org.jspace.gate.ClientHandler#send(org.jspace.protocol.ServerMessage)
	 */
	@Override
	public boolean send(ServerMessage m) {
		if (!isActive) {
			return false;
		}
		marshaller.write(m, writer);
		isActive = false;
		return true;
	}

	/* (non-Javadoc)
	 * @see org.jspace.gate.ClientHandler#isActive()
	 */
	@Override
	public boolean isActive() {
		return isActive;
	}

	@Override
	public void close() throws IOException {
		client.close();
	}

}
