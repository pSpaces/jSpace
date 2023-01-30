/*******************************************************************************
 * Copyright (c) 2017 Michele Loreti and the jSpace Developers (see the included 
 * authors file).
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
package org.jspace.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.UnknownHostException;

import org.jspace.ActualField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;
import org.jspace.Template;
import org.jspace.Tuple;
import org.junit.Test;

public class TestRemoteSpace {

		
	@Test
	public void testKeepCreation() throws InterruptedException {
		SpaceRepository sr = new SpaceRepository();
		sr.addGate("tcp://127.0.0.1:9990/?keep");
		Space aSpace = new SequentialSpace();
		sr.add("target", aSpace);
		sr.closeGates();
	}

	@Test
	public void testKeepUse() throws UnknownHostException, IOException, InterruptedException {
		SpaceRepository sr = new SpaceRepository();
		sr.addGate("tcp://127.0.0.1:9990/?keep");
		Space aSpace = new SequentialSpace();
		sr.add("target", aSpace);
		RemoteSpace rs = new RemoteSpace("tcp://127.0.0.1:9990/target?keep");
		rs.put(new Tuple(1,2,3).getTuple());
		assertTrue(true);
		Template template = new Template(1,2,3);
		assertNotNull(aSpace.queryp(template.getFields()));
		assertNotNull(rs.query(template.getFields()));
		assertNotNull(aSpace.queryp(template.getFields()));
		assertNull(aSpace.queryp(new Template(3,4,4).getFields()));
		aSpace.put(5,6,7);
		assertNotNull(rs.get(new Template(5,6,7).getFields()));
		assertNull(aSpace.getp(new Template(5,6,7).getFields()));
		sr.closeGates();
	}

	@Test
	public void testKeepPutWrongSpace() throws UnknownHostException, IOException, InterruptedException {
		SpaceRepository sr = new SpaceRepository();
		sr.addGate("tcp://127.0.0.1:9990/?keep");
		Space aSpace = new SequentialSpace();
		sr.add("target", aSpace);
		RemoteSpace rs = new RemoteSpace("tcp://127.0.0.1:9990/another?keep");
		assertFalse(rs.put(new Tuple(1,2,3).getTuple()));
		sr.closeGates();
	}

	/*
	@Test
	public void testKeepPutWrongMethod() throws UnknownHostException, IOException, InterruptedException {
		SpaceRepository sr = new SpaceRepository();
		sr.addGate("tcp://127.0.0.1:9990/?keep");
		Space aSpace = new SequentialSpace();
		sr.add("target", aSpace);
		RemoteSpace rs = new RemoteSpace("tcp://127.0.0.1:9990/target?conn");
		assertFalse(rs.put(new Tuple(1,2,3).getTuple()));
		assertFalse(rs.put(new Tuple(1,2,3).getTuple()));
		sr.closeGates();
	}
	*/

	@Test
	public void testKeepGetWrongSpace() throws UnknownHostException, IOException, InterruptedException {
		SpaceRepository sr = new SpaceRepository();
		sr.addGate("tcp://127.0.0.1:9990/?keep");
		Space aSpace = new SequentialSpace();
		sr.add("target", aSpace);
		RemoteSpace rs = new RemoteSpace("tcp://127.0.0.1:9990/another?keep");
		
		try {
			rs.get(new ActualField(1));
			fail();
		} catch (InterruptedException e) {
		}

		sr.closeGates();
	}

	@Test
	public void testKeepQueryWrongSpace() throws UnknownHostException, IOException, InterruptedException {
		SpaceRepository sr = new SpaceRepository();
		sr.addGate("tcp://127.0.0.1:9990/?keep");
		Space aSpace = new SequentialSpace();
		sr.add("target", aSpace);
		RemoteSpace rs = new RemoteSpace("tcp://127.0.0.1:9990/another?keep");
		try {
			rs.query(new ActualField(1));
			fail();
		} catch (InterruptedException e) {
		}
		sr.closeGates();
	}


	@Test
	public void testConnUse() throws UnknownHostException, IOException, InterruptedException {
		SpaceRepository sr = new SpaceRepository();
		sr.addGate("tcp://127.0.0.1:9990/?conn");
		Space aSpace = new SequentialSpace();
		sr.add("target", aSpace);
		RemoteSpace rs = new RemoteSpace("tcp://127.0.0.1:9990/target?conn");
		rs.put(1,2,3);
		assertTrue(true);
		Template template = new Template(1,2,3);
		assertNotNull(aSpace.queryp(template.getFields()));
		assertNotNull(rs.query(template.getFields()));
		assertNotNull(aSpace.queryp(template.getFields()));
		assertNull(aSpace.queryp(new Template(3,4,4).getFields()));
		aSpace.put(new Tuple(5,6,7).getTuple());
		assertNotNull(rs.get(new Template(5,6,7).getFields()));
		assertNull(aSpace.getp(new Template(5,6,7).getFields()));
		sr.closeGates();
	}	

	@Test
	public void testConcurrentKeep1() throws UnknownHostException, IOException, InterruptedException {
		SpaceRepository sr = new SpaceRepository();
		sr.addGate("tcp://127.0.0.1:9990/?keep");
		Space aSpace = new SequentialSpace();
		sr.add("target", aSpace);
		RemoteSpace rs = new RemoteSpace("tcp://127.0.0.1:9990/target?keep");
		boolean[] flags = new boolean[] { false , false };
		Thread t1 = new Thread( () -> {
			try {
				rs.get(new ActualField(1));
				flags[0] = true;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} );
		t1.start();
		Thread t2 = new Thread( () -> {
			try {
				rs.put(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			flags[1] = true;
		} );
		t2.start();
		t1.join();
		t2.join();
		assertTrue( flags[0] );
		assertTrue( flags[1] );
		sr.closeGates();
	}

	@Test
	public void testConcurrentConn1() throws UnknownHostException, IOException, InterruptedException {
		SpaceRepository sr = new SpaceRepository();
		sr.addGate("tcp://127.0.0.1:9990/?conn");
		Space aSpace = new SequentialSpace();
		sr.add("target", aSpace);
		RemoteSpace rs = new RemoteSpace("tcp://127.0.0.1:9990/target?conn");
		boolean[] flags = new boolean[] { false , false };
		Thread t1 = new Thread( () -> {
			try {
				rs.get(new ActualField(1));
				flags[0] = true;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} );
		t1.start();
		Thread t2 = new Thread( () -> {
			try {
				rs.put(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			flags[1] = true;
		} );
		t2.start();
		t1.join();
		t2.join();
		assertTrue( flags[0] );
		assertTrue( flags[1] );
		sr.closeGates();
	}

	@Test
	public void testCloseGate() {
		String uri = "tcp://127.0.0.1:9994/?keep";
		
		SpaceRepository sr = new SpaceRepository();
		sr.closeGate(uri);
		sr.addGate(uri);
		sr.closeGate(uri);
	}
}
