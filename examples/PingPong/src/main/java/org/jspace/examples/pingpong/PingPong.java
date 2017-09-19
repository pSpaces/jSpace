/**
 * 
 */
package org.jspace.examples.pingpong;

import org.jspace.SequentialSpace;
import org.jspace.Space;

/**
 * @author loreti
 *
 */
public class PingPong {
	
	
	public static void main( String[] argv ) throws InterruptedException {
		Space ping = new SequentialSpace();
		Space pong = new SequentialSpace();

		Thread t1 = new Thread( new PingAgent(ping, pong) );
		Thread t2 = new Thread( new PongAgent(ping, pong) );
		
		t1.start();
		t2.start();
		
		t1.join();
		t2.join();
		
	}
	

}
