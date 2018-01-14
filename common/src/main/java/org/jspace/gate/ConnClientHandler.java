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
	private boolean isClosed = false;

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
		isActive  = (message == null);
		return message;
	}

	/* (non-Javadoc)
	 * @see org.jspace.gate.ClientHandler#send(org.jspace.protocol.ServerMessage)
	 */
	@Override
	public boolean send(ServerMessage m) {
		marshaller.write(m, writer);
		isActive = false;
		try {
			close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
	public synchronized void close() throws IOException {
		client.close();
		isClosed = true;
	}

	@Override
	public synchronized boolean isClosed() {
		return isClosed;
	}

}
