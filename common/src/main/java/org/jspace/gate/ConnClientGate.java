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
	private String target;

	public ConnClientGate( jSpaceMarshaller marshaller , String host, int port, String target) {
		this.marshaller = marshaller;
		this.host = host;
		this.port = port;
		this.target = target;
	}
	
	@Override
	public ServerMessage send(ClientMessage m) throws InterruptedException, UnknownHostException, IOException {
		ConnInteractionHandler handler = new ConnInteractionHandler();
		new Thread( () -> handler.send(m) ).start();
		return handler.getResponce();
	}

	@Override
	public void open() throws UnknownHostException, IOException {
	}

	@Override
	public void close() throws IOException {
	}
	
	public class ConnInteractionHandler {
		
		private ServerMessage message;
		private IOException exception;
		private Socket socket;
		private BufferedReader reader;
		private PrintWriter writer;
		
		public ConnInteractionHandler( ) throws UnknownHostException, IOException {
			socket = new Socket(host, port);
			reader = new BufferedReader( new InputStreamReader(socket.getInputStream()) );
			writer = new PrintWriter(socket.getOutputStream());
		}
		
		public void send( ClientMessage m ) {
			m.setTarget(target);
			marshaller.write(m, writer);
			try {
				setMessage(marshaller.read(ServerMessage.class, reader));
			} catch (IOException e) {
				setException(e);
			}
		}
		
		public synchronized void setMessage( ServerMessage message ) {
			this.message = message;
			notifyAll();
		}
		
		public synchronized void setException( IOException exception ) {
			this.exception = exception;
			notifyAll();
		}
		
		public synchronized ServerMessage getResponce( ) throws InterruptedException, IOException  {
			while ((message == null)&&(exception==null)) {
				wait();
			}
			if (exception != null) {
				throw exception;
			}
			return message;
		}
	}

}
