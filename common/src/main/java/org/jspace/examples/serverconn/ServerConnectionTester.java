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
package org.jspace.examples.serverconn;

import java.io.IOException;
import java.net.UnknownHostException;
import java.net.URI;

import org.jspace.Server;
import org.jspace.ServerConnection;
import org.jspace.SpaceRepository;
import org.jspace.NamedSpace;
import org.jspace.Tuple;
import org.jspace.ActualField;

import org.jspace.config.ServerConfig; import org.jspace.gate.GateFactory;
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
public class ServerConnectionTester {
	public final static String REMOTE_URI = "tcp://127.0.0.1:7000/?keep";

	public static void main(String[] argv) throws InterruptedException {
        // client code goes here
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

        SpaceRepository repo = srv.newRepository("FØRSTE", "PwEt");
        if (repo == null) return;

        NamedSpace space = repo.newSpace("firstSpace", SpaceType.SEQUENTIAL, null);
        space.put("Hello", "World");
        Object[] tuple1 = space.get(new ActualField("Hello"), new ActualField("World"));
        System.out.println(tuple1 + " " + new Tuple(tuple1));
        space.put("Hello", "World");
        space.put("Hello", "World");
        Object[] tuple2 = space.get(new ActualField("Hello"), new ActualField("World"));
        Object[] tuple3 = space.get(new ActualField("Hello"), new ActualField("World"));
        System.out.println(new Tuple(tuple1) + " and " + new Tuple(tuple2) + " fetched ");
        Object[] tuple4 = space.get(new ActualField("Hello"), new ActualField("World"));
        System.out.println("cant get here");

//        space.put("tuple", 1);
//        space.query("tuple", 1);
//        space.put("tuple", 2);
//        space.queryAll("tuple", 1);
//        space.getAll("tuple", 1);

//        try {
//            srv.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

//        serverObject.shutDown();
	}
}
