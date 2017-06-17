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
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.jspace.io.jSpaceMarshaller;
import org.jspace.protocol.ClientMessage;
import org.jspace.protocol.ServerMessage;

/**
 * @author loreti
 *
 */
public class ConnClientGate implements ClientGate {
	
	
	private final jSpaceMarshaller marshaller;
	private String host;
	private int port;
	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
	private String target;

	public ConnClientGate( jSpaceMarshaller marshaller , String host, int port, String target) {
		this.marshaller = marshaller;
		this.host = host;
		this.port = port;
		this.target = target;
	}
	
	@Override
	public ServerMessage send(ClientMessage m) throws UnknownHostException, IOException {
		socket = new Socket(host, port);
		reader = new BufferedReader( new InputStreamReader(socket.getInputStream()) );
		writer = new PrintWriter(socket.getOutputStream());
		m.setTarget(target);
		marshaller.write(m, writer);
		ServerMessage result = marshaller.read(ServerMessage.class, reader);
		socket.close();
		return result;
	}

	@Override
	public void open() throws UnknownHostException, IOException {
	}

	@Override
	public void close() throws IOException {
	}

}
