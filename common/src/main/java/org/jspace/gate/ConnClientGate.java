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
	private Socket socket;
	private BufferedReader reader;
	private PrintWriter writer;
	private String target;
	private Boolean flag = false;
	private IOException exception = null;
	private ServerMessage result;

	public ConnClientGate( jSpaceMarshaller marshaller , String host, int port, String target) {
		this.marshaller = marshaller;
		this.host = host;
		this.port = port;
		this.target = target;
	}
	
	@Override
	public ServerMessage send(ClientMessage m) throws InterruptedException, UnknownHostException, IOException {
		exception = null;
		result = null;
		socket = new Socket(host, port);
		reader = new BufferedReader( new InputStreamReader(socket.getInputStream()) );
		writer = new PrintWriter(socket.getOutputStream());
		m.setTarget(target);
		marshaller.write(m, writer);
		Thread rt = new Thread( new Runnable() {

			@Override
			public void run() {
				synchronized (flag) {
					try {
						result = marshaller.read(ServerMessage.class, reader);
						flag.notify();
					} catch (IOException e) {
						exception = e;
						flag.notify();
					}

				}
			}
			
		});
		rt.start();
		try {
			synchronized (flag) {
				while ((exception == null)&&(result == null)) {
					flag.wait();
				}
			}
			if (exception != null) {
				throw exception;
			}
		} finally {
			socket.close();
		}
		return result;
	}

	@Override
	public void open() throws UnknownHostException, IOException {
	}

	@Override
	public void close() throws IOException {
	}

}
