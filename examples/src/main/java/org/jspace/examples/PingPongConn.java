/**
 * 
 */
package org.jspace.examples;

import java.io.IOException;

import org.jspace.ActualField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;
import org.jspace.Template;
import org.jspace.Tuple;

/**
 * @author loreti
 *
 */
public class PingPongConn {
	
	public final static String SPACE_URI = "tcp://127.0.0.1:9001/?keep";
	public final static String PING_URI = "tcp://127.0.0.1:9001/ping?keep";
	public final static String PONG_URI = "tcp://127.0.0.1:9001/pong?keep";
	
	public static void main( String[] argv ) throws InterruptedException {
		SpaceRepository repository = new SpaceRepository();
		repository.addGate(SPACE_URI);
		repository.add("ping", new SequentialSpace());
		repository.add("pong", new SequentialSpace());

		Thread t1 = new Thread( () -> {//PONG TREAD: get pong from "PONG" and write ping to "PING" 
			try {
				RemoteSpace ping = new RemoteSpace(PING_URI);
				RemoteSpace pong = new RemoteSpace(PONG_URI);
				for (int i=0 ; i<10 ; i++ ) {
					System.out.println("T1: getting PING...");
					ping.get(new ActualField("PING"));
					System.out.println("T1: I have PING!");
					pong.put("PONG");
				}
				System.out.println("T1 DONE!!!!!");
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		});
		Thread t2 = new Thread( () -> {//PONG TREAD: get pong from "PONG" and write ping to "PING" 
			try {
				RemoteSpace ping = new RemoteSpace(PING_URI);
				RemoteSpace pong = new RemoteSpace(PONG_URI);
				for (int i=0 ; i<10 ; i++ ) {
					ping.put("PING");
					System.out.println("T2: getting PONG...");
					pong.get(new ActualField("PONG"));
					System.out.println("T2: I have PONG!");
				}
				System.out.println("T2 DONE!!!!!");
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		});
		
		t1.start();
		t2.start();
		
		t1.join();
		t2.join();
		
	}
	
	
	public class PongThread implements Runnable {
		public void run() {
			
			
		}
	}
	

}
