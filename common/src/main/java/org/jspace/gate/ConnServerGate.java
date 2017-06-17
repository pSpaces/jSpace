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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;

import org.jspace.io.jSpaceMarshaller;

/**
 * @author loreti
 *
 */
public class ConnServerGate implements ServerGate {
	
	private jSpaceMarshaller marshaller;
	private static final String CONN_CODE = "CONN";
	private InetSocketAddress address;
	private int backlog;
	private ServerSocket ssocket;
	
	public ConnServerGate(jSpaceMarshaller marshaller, InetSocketAddress address, int backlog) {
		this.address = address;
		this.backlog = backlog;
	}
	

	@Override
	public void open() throws IOException {
		this.ssocket = new ServerSocket(address.getPort(), backlog, address.getAddress());
	}

	@Override
	public ClientHandler accept() throws IOException {		
		return new ConnClientHandler(marshaller,ssocket.accept());
	}

	@Override
	public void close() throws IOException {
		this.ssocket.close();
	}

	@Override
	public URI getURI() {
		try {
			return new URI("socket://"+address+"/"+"?"+CONN_CODE);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}

}
