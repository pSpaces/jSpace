package org.jspace.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.swing.plaf.SliderUI;

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
		sr.addGate("tcp://127.0.0.1:9900/?keep");
		Space aSpace = new SequentialSpace();
		sr.add("target", aSpace);
	}

	@Test
	public void testKeepUse() throws UnknownHostException, IOException, InterruptedException {
		SpaceRepository sr = new SpaceRepository();
		sr.addGate("tcp://127.0.0.1:9901/?keep");
		Space aSpace = new SequentialSpace();
		sr.add("target", aSpace);
		RemoteSpace rs = new RemoteSpace("tcp://127.0.0.1:9901/target?keep");
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
	}
	
	@Test
	public void testConnUse() throws UnknownHostException, IOException, InterruptedException {
		SpaceRepository sr = new SpaceRepository();
		sr.addGate("tcp://127.0.0.1:9902/?conn");
		Space aSpace = new SequentialSpace();
		sr.add("target", aSpace);
		RemoteSpace rs = new RemoteSpace("tcp://127.0.0.1:9902/target?conn");
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
	}	

	@Test
	public void testConcurrentKeep1() throws UnknownHostException, IOException, InterruptedException {
		SpaceRepository sr = new SpaceRepository();
		sr.addGate("tcp://127.0.0.1:9902/?keep");
		Space aSpace = new SequentialSpace();
		sr.add("target", aSpace);
		RemoteSpace rs = new RemoteSpace("tcp://127.0.0.1:9902/target?conn");
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
		Thread.sleep(1000);
		Thread t2 = new Thread( () -> {
			try {
				rs.get(new ActualField(1));
				flags[0] = true;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} );
		t2.start();
		
	}
	
}
