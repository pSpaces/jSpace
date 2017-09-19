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

import java.io.IOException;
import java.net.UnknownHostException;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;

/**
 * A simple HelloWorld program.
 * 
 * 
 * @author Michele Loreti
 *
 */
public class RemoteHelloWorld {

	public final static String GATE_URI = "tcp://127.0.0.1:9001/?keep";
	public final static String REMOTE_URI = "tcp://127.0.0.1:9001/aspace?keep";	
	
	public static void main(String[] argv) throws InterruptedException {
		SpaceRepository repository = new SpaceRepository();
		repository.addGate(GATE_URI);
		repository.add("aspace", new SequentialSpace());
		
		Thread t1 = new Thread( () -> {//PONG TREAD: get pong from "PONG" and write ping to "PING" 
			try {
				RemoteSpace space = new RemoteSpace(REMOTE_URI);
				space.put("GREETING","Hello");
				System.out.println("T1 Done!");
			} catch (UnknownHostException e) {
				e.printStackTrace();
				System.out.println("T1 Error!");
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("T1 Error!");
			}			
		});		

		Thread t2 = new Thread( () -> {//PONG TREAD: get pong from "PONG" and write ping to "PING" 
			try {
				RemoteSpace space = new RemoteSpace(REMOTE_URI);
				space.put("NAME","World");
				System.out.println("T2 Done!");
			} catch (UnknownHostException e) {
				e.printStackTrace();
				System.out.println("T2 Error!");
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("T2 Error!");
			}			
		});		

		Thread t3 = new Thread( () -> {//PONG TREAD: get pong from "PONG" and write ping to "PING" 
			try {				
				RemoteSpace space = new RemoteSpace(REMOTE_URI);
				Object[] greetingData = space.get(new ActualField("GREETING"), new FormalField(String.class));				
				Object[] nameData = space.get(new ActualField("NAME"), new FormalField(String.class));				
				System.out.println(greetingData[1]+" "+nameData[1]+"!");
				System.out.println("T3 Done!");
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.out.println("T3 Error!");
			} catch (UnknownHostException e) {
				e.printStackTrace();
				System.out.println("T2 Error!");
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("T2 Error!");
			}			
		});		
		
		t1.start();
		t2.start();
		t3.start();
	}
	
}
