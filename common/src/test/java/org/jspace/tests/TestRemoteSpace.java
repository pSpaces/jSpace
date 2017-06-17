package org.jspace.tests;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.UnknownHostException;

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
		rs.put(new Tuple(1,2,3));
		assertTrue(true);
		Template template = new Template(1,2,3);
		assertNotNull(aSpace.queryp(template));
		assertNotNull(rs.query(template));
		assertNotNull(aSpace.queryp(template));
		assertNull(aSpace.queryp(new Template(3,4,4)));
		aSpace.put(new Tuple(5,6,7));
		assertNotNull(rs.get(new Template(5,6,7)));
		assertNull(aSpace.getp(new Template(5,6,7)));
	}
	
	@Test
	public void testConnUse() throws UnknownHostException, IOException, InterruptedException {
		SpaceRepository sr = new SpaceRepository();
		sr.addGate("tcp://127.0.0.1:9902/?conn");
		Space aSpace = new SequentialSpace();
		sr.add("target", aSpace);
		RemoteSpace rs = new RemoteSpace("tcp://127.0.0.1:9902/target?conn");
		rs.put(new Tuple(1,2,3));
		assertTrue(true);
		Template template = new Template(1,2,3);
		assertNotNull(aSpace.queryp(template));
		assertNotNull(rs.query(template));
		assertNotNull(aSpace.queryp(template));
		assertNull(aSpace.queryp(new Template(3,4,4)));
		aSpace.put(new Tuple(5,6,7));
		assertNotNull(rs.get(new Template(5,6,7)));
		assertNull(aSpace.getp(new Template(5,6,7)));
	}	

}
