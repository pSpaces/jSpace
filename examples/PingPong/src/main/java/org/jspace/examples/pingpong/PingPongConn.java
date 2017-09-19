/**
 * 
 */
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
public class PingPongConn {
	
	public final static String SPACE_URI = "tcp://127.0.0.1:9001/?keep";
	public final static String PING_URI = "tcp://127.0.0.1:9001/ping?keep";
	public final static String PONG_URI = "tcp://127.0.0.1:9001/pong?keep";
	
	public static void main( String[] argv ) throws InterruptedException, UnknownHostException, IOException {
		SpaceRepository repository = new SpaceRepository();
		repository.addGate(SPACE_URI);
		repository.add("ping", new SequentialSpace());
		repository.add("pong", new SequentialSpace());


		Thread t1 = new Thread( new PingAgent(new RemoteSpace(PING_URI), new RemoteSpace(PONG_URI)) );
		Thread t2 = new Thread( new PongAgent(new RemoteSpace(PING_URI), new RemoteSpace(PONG_URI)) );
		
		t1.start();
		t2.start();
		
		t1.join();
		t2.join();		
	}	

}
