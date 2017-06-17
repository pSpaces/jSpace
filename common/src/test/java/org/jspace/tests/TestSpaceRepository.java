package org.jspace.tests;

import static org.junit.Assert.*;

import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;
import org.junit.Test;

public class TestSpaceRepository {

	@Test
	public void testCreate() {
		SpaceRepository repository = new SpaceRepository();
		assertNotNull(repository);
	}
	
	@Test
	public void testEmpty() {
		SpaceRepository repository = new SpaceRepository();
		assertTrue(repository.isEmpty());
	}

	@Test
	public void testSize() {
		SpaceRepository repository = new SpaceRepository();
		assertEquals(0,repository.size());
	}

	@Test
	public void addNewSpace() {
		SpaceRepository repository = new SpaceRepository();
		repository.add("name",new SequentialSpace());
		assertFalse(repository.isEmpty());
	}
	
	@Test
	public void getUknown() {
		SpaceRepository repository = new SpaceRepository();
		assertNull(repository.get("aspace"));
	}
	
	@Test
	public void addAndGet() {
		SpaceRepository repository = new SpaceRepository();
		repository.add("aspace",new SequentialSpace());
		assertNotNull(repository.get("aspace"));
	}
	
	@Test(expected = IllegalStateException.class)
	public void addTwoSpacesWithTheSameName() {
		SpaceRepository repository = new SpaceRepository();
		repository.add("aspace",new SequentialSpace());
		repository.add("aspace",new SequentialSpace());
	}
	
	@Test
	public void addAndRemove() {
		SpaceRepository repository = new SpaceRepository();
		repository.add("aspace",new SequentialSpace());
		assertNotNull(repository.get("aspace"));
		repository.remove("aspace");
		assertNull(repository.get("aspace"));
	}
	
}
