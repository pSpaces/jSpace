/**
 * 
 */
package org.jspace.examples.df;

import org.jspace.SequentialSpace;
import org.jspace.Space;

/**
 * @author loreti
 *
 */
public class DiningPhilosophers {
	
	public static final int SIZE = 5;
	
	public static void main( String[] argv ) throws InterruptedException {
		Space space = new SequentialSpace();
		for( int i=0 ; i<SIZE; i++) {
			space.put("FORK",i);
		}
		
		Thread[] philosophers = new Thread[SIZE];
		
		for( int i=0 ; i<SIZE ; i++ ) {
			philosophers[i] = new Thread( new Philosopher(i, space, i, (i+1)%SIZE) );
			philosophers[i].start();
		}
		
		philosophers[0].join();
	}
	

}
