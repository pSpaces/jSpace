/**
 * 
 */
package org.jspace.examples.df;

import java.io.IOException;
import java.net.UnknownHostException;

import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;

/**
 * @author loreti
 *
 */
public class DistributedDiningPhilosophers {
	
	public final static String GATE_URI = "tcp://127.0.0.1:9001/?keep";
	public final static String REMOTE_URI = "tcp://127.0.0.1:9001/table?keep";	

	public static final int SIZE = 5;
	
	public static void main( String[] argv ) throws InterruptedException, UnknownHostException, IOException {
		SpaceRepository repository = new SpaceRepository();
		repository.addGate(GATE_URI);
		repository.add("table", new SequentialSpace());

		for( int i=0 ; i<SIZE; i++) {
			repository.put("table","FORK",i);
		}
		
		Thread[] philosophers = new Thread[SIZE];
		
		for( int i=0 ; i<SIZE ; i++ ) {
			philosophers[i] = new Thread( new Philosopher(i,  new RemoteSpace(REMOTE_URI), i, (i+1)%SIZE) );
			philosophers[i].start();
		}
		
		philosophers[0].join();
	}
	

}
