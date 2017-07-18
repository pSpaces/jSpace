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
import java.util.LinkedList;

import org.jspace.io.jSpaceMarshaller;
import org.jspace.protocol.ClientMessage;
import org.jspace.protocol.ServerMessage;
import org.jspace.util.Rendezvous;

/**
 * @author loreti
 *
 */
public class KeepClientGate implements ClientGate {
	
	
	private final jSpaceMarshaller marshaller;
	private String host;
	private int port;
	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
	private String target;
	private final Rendezvous<String, ServerMessage> inbox;
	private final LinkedList<ClientMessage> outbox;
	private boolean status = true;
	private int sessionCounter = 0;

	public KeepClientGate( jSpaceMarshaller marshaller , String host, int port, String target) {
		this.marshaller = marshaller;
		this.host = host;
		this.port = port;
		this.target = target;
		this.inbox = new Rendezvous<>();
		this.outbox = new LinkedList<>();
	}
	
	@Override
	public ServerMessage send(ClientMessage m) throws IOException, InterruptedException {
		String sessionId = ""+(sessionCounter++);
		m.setTarget(target);
		m.setClientSession(sessionId);
		synchronized (outbox) {
			outbox.add(m);
			outbox.notify();
		}
		return inbox.call(sessionId);
	}

	@Override
	public void open() throws UnknownHostException, IOException {
		this.socket = new Socket(host, port);
		this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.writer = new PrintWriter(socket.getOutputStream());
		new Thread( () -> outboxHandlingMethod() ).start();
		new Thread( () -> inboxHandlingMethod() ).start();
	}

	@Override
	public void close() throws IOException {
		this.status = false;
		synchronized (outbox) {
			outbox.notify();
		}
		this.reader.close();
		this.writer.close();
		this.socket.close();
	}

	
	private void outboxHandlingMethod() {
		try {
			synchronized (outbox) {
				while (status) {
					while (status&&outbox.isEmpty()) {
						outbox.wait();
					}
					if (status) {
						marshaller.write(outbox.poll(), writer);
					}
				}
			}
		} catch (InterruptedException e) {
			// TODO Add Log!
			e.printStackTrace();
		} 
	}
	
	private void inboxHandlingMethod() {
		try {
			while (true) {
				ServerMessage m = marshaller.read(ServerMessage.class, reader);
				String session = m.getClientSession();
				if ((session != null)&&(inbox.canSet(session))) {
					inbox.set(session, m);
				} else {
					//TODO: Add Log!
					System.err.println("Unexpected session id!");
				}
			}
		} catch (IOException e) {
			// TODO Add Log!
			e.printStackTrace();
		}
	}

}
