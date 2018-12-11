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

import java.net.Socket;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;

import org.jspace.io.jSpaceMarshaller;

/**
 * @author loreti
 *
 */
public class TlsKeepClientGate extends KeepClientGate implements ClientGate {

	public TlsKeepClientGate(jSpaceMarshaller marshaller, String host,
            int port, String target, Class messageClass) {
        super(marshaller, host, port, target, messageClass);
        System.out.println("Initializing object of class TlsKeepClientGate");
    }

    protected Socket createSocket() throws IOException {
        SSLSocketFactory sf = (SSLSocketFactory) SSLSocketFactory.getDefault();
        Socket socket = sf.createSocket(host, port);
        System.out.println("Created a new TLS Socket " + socket.getClass());
        return socket;
    }
}
