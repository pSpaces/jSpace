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
package org.jspace.examples.pingpong;

import java.io.IOException;
import java.net.UnknownHostException;

import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;

/**
 * @author loreti
 *
 */
public class PingPongKeep {
	
	public final static String SPACE_URI = "tcp://127.0.0.1:9001/?keep";
	public final static String PING_URI = "tcp://127.0.0.1:9001/ping?keep";
	public final static String PONG_URI = "tcp://127.0.0.1:9001/pong?keep";
	
	public static void main( String[] argv ) throws InterruptedException, UnknownHostException, IOException {
		SpaceRepository repository = new SpaceRepository();
		repository.addGate(SPACE_URI);
		repository.add("ping", new SequentialSpace());
		repository.add("pong", new SequentialSpace());

		RemoteSpace rs1 = new RemoteSpace(PONG_URI) ;
		RemoteSpace rs2 = new RemoteSpace(PONG_URI);

		Thread t1 = new Thread( new PingAgent(new RemoteSpace(PING_URI),rs1));
		Thread t2 = new Thread( new PongAgent(new RemoteSpace(PING_URI), rs2) );
		
		t1.start();
		t2.start();
		
		t1.join();
		t2.join();	

		repository.shutDown();
	}
	

}
