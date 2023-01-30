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
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
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

	/**
	 * Fields used in tests to access (via the Reflection API) the threads if needed.
	 */
	private Thread outboxThread;
	private Thread inboxThread;

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
		String sessionId;

		synchronized (outbox) {
			if (!this.status) {
				throw new InterruptedException("Gate is closed!");
			}
			sessionId = ""+sessionCounter;
			sessionCounter++;

			m.setTarget(target);
			m.setClientSession(sessionId);

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
		this.outboxThread = new Thread(() -> outboxHandlingMethod());
		this.inboxThread = new Thread(() -> inboxHandlingMethod());
		this.outboxThread.start();
		this.inboxThread.start();
	}

	@Override
	public void close() throws IOException {
		synchronized (outbox) {
			this.status = false;
			outbox.notify();
		}
		this.socket.close();
		// Closing the socket above should also close the reader and writer.
		this.reader.close();
		this.writer.close();
		inbox.interruptAll();
	}


	private void outboxHandlingMethod() {
		try {
			synchronized (outbox) {
				while (status) {
					while (status && outbox.isEmpty()) {
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
					if (session != null && inbox.canSet(session)) {
						inbox.set(session, m);
					} else {
						// TODO: Add Log!
						System.err.println("Unexpected session id!");
					}
				} else {
					// m == null ==> EOF ==> Socket was closed from server.
					this.close();
					break;
				}
			}
		} catch (IOException e) {
			synchronized (outbox) {
				if (!this.status) {
					// Socket was closed by this client.
					return;
				}
			}
			// TODO Add Log!
			e.printStackTrace();
			try {
				// The gate should be closed in this case.
				this.close();
			} catch (IOException e2) {
				// TODO Add Log!
				e2.printStackTrace();
			}
		}
	}

}
