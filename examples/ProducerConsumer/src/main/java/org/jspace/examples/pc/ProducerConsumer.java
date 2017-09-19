/**
 * 
 */
package org.jspace.examples.pc;

import org.jspace.SequentialSpace;
import org.jspace.Space;

/**
 * @author loreti
 *
 */
public class ProducerConsumer {
	
	
	public static void main( String[] argv ) throws InterruptedException {
		Space space = new SequentialSpace();

		Thread t1 = new Thread( new ProducerAgent("hammer", space,10) );
		Thread t2 = new Thread( new ProducerAgent("anvil", space,10) );
		Thread t3 = new Thread( new ConsumerAgent(space,20) );

		t3.start();
		t1.start();
		t2.start();
		
		
		t1.join();
		t2.join();
		t3.join();
		
	}
	

}
