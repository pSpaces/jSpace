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
				if (m != null) {
					String session = m.getClientSession();
					if ((session != null)&&(inbox.canSet(session))) {
						inbox.set(session, m);
					} else {
						//TODO: Add Log!
						System.err.println("Unexpected session id!");
					}
				}
			}
		} catch (IOException e) {
			// TODO Add Log!
			e.printStackTrace();
		}
	}

}
