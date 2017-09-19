/**
 * 
 */
package org.jspace.examples.pc;


import org.jspace.ActualField;
import org.jspace.Space;

/**
 * @author loreti
 *
 */
public class ProducerAgent implements Runnable {

	private Space space;
	private String name;
	private int elements;

	public ProducerAgent(String name,Space space,int elements) {
		this.space = space;
		this.name = name;
		this.elements = elements;
	}
	
	@Override
	public void run() {
		try {
			for (int i=0 ; i<elements ; i++ ) {
				System.out.println("PRODUCER: produging item "+name+" "+i);
				Thread.sleep(100);
				space.put("ITEM",name,i);
			}
			System.out.println("PRODUCER "+name+" DONE!!!!!");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
