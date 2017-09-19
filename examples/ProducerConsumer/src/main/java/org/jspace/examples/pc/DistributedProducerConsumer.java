/**
 * 
 */
package org.jspace.examples.pc;

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
public class DistributedProducerConsumer {
	
	public final static String GATE_URI = "tcp://127.0.0.1:9001/?keep";
	public final static String REMOTE_URI = "tcp://127.0.0.1:9001/aspace?keep";	
	
	public static void main( String[] argv ) throws InterruptedException, UnknownHostException, IOException {
		SpaceRepository repository = new SpaceRepository();
		repository.addGate(GATE_URI);
		repository.add("aspace", new SequentialSpace());

		Thread t1 = new Thread( new ProducerAgent("hammer", new RemoteSpace(REMOTE_URI),10) );
		Thread t2 = new Thread( new ProducerAgent("anvil", new RemoteSpace(REMOTE_URI),10) );
		Thread t3 = new Thread( new ConsumerAgent(new RemoteSpace(REMOTE_URI),20) );

		t3.start();
		t1.start();
		t2.start();
		
		
		t1.join();
		t2.join();
		t3.join();
		
	}
	

}
