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
	
	private final jSpaceMarshaller marshaller;
	private static final String CONN_CODE = "conn";
	private final InetSocketAddress address;
	private final int backlog;
	private ServerSocket ssocket;
	private boolean isOpen = false;
	
	public ConnServerGate(jSpaceMarshaller marshaller, InetSocketAddress address, int backlog) {
		this.address = address;
		this.backlog = backlog;
		this.marshaller = marshaller;
	}
	

	@Override
	public void open() throws IOException {
		this.isOpen = true;
		this.ssocket = new ServerSocket(address.getPort(), backlog, address.getAddress());
	}

	@Override
	public ClientHandler accept() throws IOException {		
		return new ConnClientHandler(marshaller,ssocket.accept());
	}

	@Override
	public void close() throws IOException {
		this.isOpen = false;
		if (this.ssocket != null) {
			this.ssocket.close();
		}
	}
	
	@Override
	public boolean isClosed() {
		return !this.isOpen;
	}

	@Override
	public URI getURI() {
		try {
			return new URI("tcp:/"+address+"/"+"?"+CONN_CODE);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}
}
