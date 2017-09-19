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
public class PingAgent implements Runnable {

	private Space ping;
	private Space pong;

	public PingAgent(Space ping, Space pong) {
		this.ping = ping;
		this.pong = pong;
	}
	
	@Override
	public void run() {
		try {
			for (int i=0 ; i<10 ; i++ ) {
				System.out.println("T1: getting PING...");
				ping.get(new ActualField("PING"));
				System.out.println("T1: I have PING!");
				pong.put("PONG");
			}
			System.out.println("T1 DONE!!!!!");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
