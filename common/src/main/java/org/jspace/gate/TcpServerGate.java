package org.jspace.gate;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;

import org.jspace.io.jSpaceMarshaller;

import javax.net.ssl.SSLServerSocketFactory;


public abstract class TcpServerGate implements ServerGate {
	protected final jSpaceMarshaller marshaller;
	protected final InetSocketAddress address;
	protected final int backlog;
	private ServerSocket ssocket;
	protected boolean isClosed;
    protected Class messageClass;

	public TcpServerGate(jSpaceMarshaller marshaller,
            InetSocketAddress address, int backlog, Class messageClass) {
		this.address = address;
		this.backlog = backlog;
		this.marshaller = marshaller;
		this.isClosed = false;
        this.messageClass = messageClass;
	}

    protected ServerSocket createSocket() throws IOException {
        System.out.println("Initializing TCP server socket");
        SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
//        return ssf.createServerSocket(address.getPort(), backlog, address.getAddress());
		return new ServerSocket(address.getPort(), backlog,
                address.getAddress());
    }

	@Override
	public synchronized void open() throws IOException {
		if (this.isClosed) {
			throw new IllegalStateException("Gate is closed!");
		}

		if (this.ssocket != null) {
			throw new IllegalStateException("Gate is already opened!");
		}

        this.ssocket = createSocket();
	}

	@Override
	public ClientHandler accept() throws IOException {
		if ((this.isClosed) || (this.ssocket == null)) {
			throw new IllegalStateException("Gate is not opened!");
		}

		return getClientHandler(ssocket.accept());
	}

	protected abstract ClientHandler getClientHandler(Socket socket)
            throws IOException;

	@Override
	public synchronized void close() throws IOException {
		if ((this.isClosed) || (this.ssocket == null)) {
			throw new IllegalStateException("Gate is not opened!");
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

    public int getPort() {
        // FIXME ssocket.getLocalPort could be a potential candidate here
        return ssocket.getLocalPort();
    }
}
