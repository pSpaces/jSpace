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

	public static final String TLS_SCHEME = "tls";
	public static final String TCP_SCHEME = "tcp";

	public static final String KEEP_MODE = "keep";

	public static final String DEFAULT_MODE = KEEP_MODE;

	/* (non-Javadoc)
	 * @see org.jspace.gate.GateBuilder#createClientGate(java.net.URI)
	 */
	@Override
	public ClientGate createClientGate(URI uri, Class messageClass) {
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

        if (uri.getScheme().equals(TLS_SCHEME)) {
            if (query.containsKey(KEEP_MODE)) {
                return new TlsKeepClientGate(marshaller, host, port, target,
                        messageClass);
            }
        } else if (uri.getScheme().equals(TCP_SCHEME)) {
            // FIXME issue warning about insecure communication channel!
            if (query.containsKey(KEEP_MODE)) {
                return new KeepClientGate(marshaller, host, port, target,
                        messageClass);
            }
        }

		//Default mode
		return new TlsKeepClientGate(marshaller, host, port, target,
                messageClass);
	}


	/* (non-Javadoc)
	 * @see org.jspace.gate.GateBuilder#createServerGate(java.net.URI)
	 */
	@Override
	public ServerGate createServerGate(URI uri, Class messageClass) {
		String host = uri.getHost();
		int port = uri.getPort();

		if (port < 0) {
			port = DEFAULT_PORT;
		}

		HashMap<String,String> query = GateFactory.parseQuery(uri.getQuery());
		jSpaceMarshaller marshaller = getMarshaller(query.get(GateFactory.LANGUAGE_QUERY_ELEMENT));
		String mode = query.getOrDefault(GateFactory.MODE_QUERY_ELEMENT,DEFAULT_MODE).toUpperCase();

        if (uri.getScheme().equals(TLS_SCHEME)) {
            if (query.containsKey(KEEP_MODE)) {
                return new TlsKeepServerGate(marshaller,
                        new InetSocketAddress(host, port),
                        DEFAULT_BACKLOG, messageClass);
            }
        } else if (uri.getScheme().equals(TCP_SCHEME)) {
            // FIXME issue warning about insecure communication channel!
            if (query.containsKey(KEEP_MODE)) {
                return new KeepServerGate(marshaller,
                        new InetSocketAddress(host, port),
                        DEFAULT_BACKLOG, messageClass);
            }
        }

		//TODO: Add here other modes!
		return new TlsKeepServerGate(marshaller,
                new InetSocketAddress(host, port),
                DEFAULT_BACKLOG, messageClass);
	}

	public jSpaceMarshaller getMarshaller( String code ) {
		MarshalFactory mf = MarshalFactory.getInstance();
		if (code == null) {
			code = MarshalFactory.DEFAULT_LANGAUGE;
		}
		return mf.getMarshaller(code);
	}
}
