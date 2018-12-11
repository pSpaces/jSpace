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
package org.jspace.examples.chat.chatBob;

import java.io.IOException;
import java.net.UnknownHostException;
import java.net.URI;

import org.jspace.Server;
import org.jspace.FormalField;
import org.jspace.ServerConnection;
import org.jspace.SpaceRepository;
import org.jspace.NamedSpace;

import org.jspace.config.ServerConfig;
import org.jspace.gate.GateFactory;
import org.jspace.protocol.ManagementMessage;
import org.jspace.protocol.ManagementMessageType;
import org.jspace.protocol.RepositoryProperties;
import org.jspace.protocol.SpaceProperties;
import org.jspace.protocol.SpaceKeys;
import org.jspace.protocol.SpaceType;

/**
 * A simple HelloWorld program.
 * 
 * 
 * @author Michele Loreti
 *
 */
public class Bob {
	public final static String REMOTE_URI = "tls://127.0.0.1:7000/?keep";

	public static void main(String[] argv) throws InterruptedException {

        // client code goes here
        System.out.println("Connecting to the management server");
        ServerConnection srv;

        // FIXME this seems clunky and not very user friendly
        try {
            srv = ServerConnection.getInstance(REMOTE_URI);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        System.out.println("Initializing repository");
        
        SpaceRepository repository = srv.newRepository("chatRepository", "passwd");
        if (repository == null) return;

        NamedSpace chat = repository.newSpace("chat", SpaceType.SEQUENTIAL, null);

        while (true) {
            Object[] t = chat.get(new FormalField(String.class), new FormalField(String.class));
            System.out.println(t[0] + ": " + t[1]);
        }
	}
}
