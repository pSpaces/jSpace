package org.jspace.gate;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import org.jspace.io.jSpaceMarshaller;

public abstract class TlsServerGate extends TcpServerGate implements ServerGate {
	public TlsServerGate(jSpaceMarshaller marshaller,
            InetSocketAddress address, int backlog, Class messageClass) {
        super(marshaller, address, backlog, messageClass);
	}

    @Override
    protected ServerSocket createSocket() throws IOException {
        System.out.println("Initializing TLS server socket");
        SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        return ssf.createServerSocket(address.getPort(), backlog, address.getAddress());
    }
}
