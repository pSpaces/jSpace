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
