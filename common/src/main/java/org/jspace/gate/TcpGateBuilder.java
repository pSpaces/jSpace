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

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;

import org.jspace.io.MarshalFactory;
import org.jspace.io.jSpaceMarshaller;

/**
 * @author loreti
 *
 */
public class TcpGateBuilder implements GateBuilder {
	
	public static final int DEFAULT_BACKLOG = 10;
	public static final int DEFAULT_PORT = 9990;
	public static final String DEFAULT_MODE = "keep";
	
	public static final String KEEP_MODE = "KEEP";
	public static final String CONN_MODE = "CONN";
	public static final String PUSH_MODE = "PUSH";
	public static final String PULL_MODE = "PULL";

	/* (non-Javadoc)
	 * @see org.jspace.gate.GateBuilder#createClientGate(java.net.URI)
	 */
	@Override
	public ClientGate createClientGate(URI uri) {
		String host = uri.getHost();
		int port = uri.getPort();
		String target = uri.getPath();
		if (target.startsWith("/")) {
			target = target.substring(1);
		}
		if (port < 0) {
			port = DEFAULT_PORT;
		}
		HashMap<String,String> query = GateFactory.parseQuery(uri.getQuery());
		jSpaceMarshaller marshaller = getMarshaller(query.get(GateFactory.LANGUAGE_QUERY_ELEMENT));
		String mode = query.getOrDefault(GateFactory.MODE_QUERY_ELEMENT,DEFAULT_MODE).toUpperCase();
		if (KEEP_MODE.equals(mode)) {
			return new KeepClientGate(marshaller, host, port, target);
		}
		if (CONN_MODE.equals(mode)) {
			return new ConnClientGate(marshaller, host, port, target);
		}
		//TODO: Add here other modes!
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jspace.gate.GateBuilder#createServerGate(java.net.URI)
	 */
	@Override
	public ServerGate createServerGate(URI uri) {
		String host = uri.getHost();
		int port = uri.getPort();
		if (port < 0) {
			port = DEFAULT_PORT;
		}
		HashMap<String,String> query = GateFactory.parseQuery(uri.getQuery());
		jSpaceMarshaller marshaller = getMarshaller(query.get(GateFactory.LANGUAGE_QUERY_ELEMENT));
		String mode = query.getOrDefault(GateFactory.MODE_QUERY_ELEMENT,DEFAULT_MODE).toUpperCase();
		if (KEEP_MODE.equals(mode)) {
			return new KeepServerGate(marshaller,new InetSocketAddress(host, port),DEFAULT_BACKLOG);
		}
		//TODO: Add here other modes!
		return null;
	}

	public jSpaceMarshaller getMarshaller( String code ) {
		MarshalFactory mf = MarshalFactory.getInstance();
		if (code == null) {
			code = MarshalFactory.DEFAULT_LANGAUGE;
		} 
		return mf.getMarshaller(code);
	}
	
}
