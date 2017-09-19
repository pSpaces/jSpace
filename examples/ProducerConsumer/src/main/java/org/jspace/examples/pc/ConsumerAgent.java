/**
 * 
 */
package org.jspace.examples.pc;


import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;

/**
 * @author loreti
 *
 */
public class ConsumerAgent implements Runnable {

	private Space space;
	private int elements;

	public ConsumerAgent(Space space, int elements) {
		this.space = space;
		this.elements = elements;
	}
	
	@Override
	public void run() {
		try {
			for (int i=0 ; i<elements ; i++ ) {
				Object[] data = space.get(new ActualField("ITEM"),new FormalField(String.class),new FormalField(Integer.class));
				System.out.println("CONSUMED: "+data[1]+" "+data[2]);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
