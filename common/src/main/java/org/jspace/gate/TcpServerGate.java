package org.jspace.gate;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

import org.jspace.io.jSpaceMarshaller;

public abstract class TcpServerGate implements ServerGate {

	protected final jSpaceMarshaller marshaller;
	protected final InetSocketAddress address;
	protected final int backlog;
	private ServerSocket ssocket;
	protected boolean isClosed;

	public TcpServerGate(jSpaceMarshaller marshaller, InetSocketAddress address, int backlog) {
		this.address = address;
		this.backlog = backlog;
		this.marshaller = marshaller;
		this.isClosed = false;
	}

	@Override
	public synchronized void open() throws IOException {
		if (this.isClosed) {
			throw new IllegalStateException("Gate is closed!");
		}
		if (this.ssocket != null) {
			throw new IllegalStateException("Gate is already opened!");
		}
		this.ssocket = new ServerSocket(address.getPort(), backlog, address.getAddress());
	}

	@Override
	public ClientHandler accept() throws IOException {		
		if ((this.isClosed)||(this.ssocket == null)) {
			//throw new IllegalStateException("Gate is not opened!");
			return null;
		}
		return getClientHandler(ssocket.accept());
	}

	protected abstract ClientHandler getClientHandler(Socket socket) throws IOException;

	@Override
	public synchronized void close() throws IOException {
		if ((this.isClosed)||(this.ssocket==null)) {
			//throw new IllegalStateException("Gate is not opened!");
			return ;
		}
		this.isClosed = true;
		this.ssocket.close();		
	}

	@Override
	public URI getURI() {
		try {
			return new URI("tcp:/"+address+"/"+"?"+getConnectionCode());
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	protected abstract String getConnectionCode();

	@Override
	public synchronized boolean isClosed() {
		return isClosed;
	}

}