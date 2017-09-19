/**
 * 
 * jSpace: a Java Framework for Programming Concurrent and Distributed Applications with Spaces
 * 
 * http://pspace.github.io/jSpace/	
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *      Alberto Lluch Lafuente
 *      Michele Loreti
 *      
 */
package org.jspace.examples.helloworld;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.SequentialSpace;
import org.jspace.Space;

/**
 * A simple HelloWorld program.
 * 
 * 
 * @author Michele Loreti
 *
 */
public class HelloWorld {

	public static void main(String[] argv) throws InterruptedException {
		Space space = new SequentialSpace();
		
		space.put("GREETING","Hello");
		space.put("NAME","World");
		Object[] greetingData = space.get(new ActualField("GREETING"), new FormalField(String.class));				
		Object[] nameData = space.get(new ActualField("NAME"), new FormalField(String.class));				
		System.out.println(greetingData[1]+" "+nameData[1]+"!");

	}
	
}
