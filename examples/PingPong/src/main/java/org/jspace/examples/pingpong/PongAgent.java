/**
 * 
 */
package org.jspace.examples.pingpong;

import org.jspace.ActualField;
import org.jspace.Space;

/**
 * @author loreti
 *
 */
public class PongAgent implements Runnable {

	private Space ping;
	private Space pong;

	public PongAgent(Space ping, Space pong) {
		this.ping = ping;
		this.pong = pong;
	}
	
	@Override
	public void run() {
		try {
			for (int i=0 ; i<10 ; i++ ) {
				ping.put("PING");
				System.out.println("T2: getting PONG...");
				pong.get(new ActualField("PONG"));
				System.out.println("T2: I have PONG!");
			}
			System.out.println("T2 DONE!!!!!");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
