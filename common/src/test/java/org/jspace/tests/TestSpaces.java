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

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;

import org.jspace.FormalField;
import org.jspace.PileSpace;
import org.jspace.QueueSpace;
import org.jspace.SequentialSpace;
import org.jspace.StackSpace;
import org.junit.Test;

public class TestSpaces {

	@Test
	public void testSequential() throws InterruptedException {
		SequentialSpace sp = new SequentialSpace();
		sp.put(1);
		sp.put(2);
		sp.put(3);
		Object[] o = sp.query(new FormalField(Integer.class));
		assertEquals(1, o.length);
		assertEquals(1, o[0]);
 	}

	@Test
	public void testSequentialAll() throws InterruptedException {
		SequentialSpace sp = new SequentialSpace();
		sp.put(1);
		sp.put("2");
		sp.put(3);
		LinkedList<Object[]> data = sp.getAll(new FormalField(Integer.class));
		assertEquals(2, data.size());
		assertEquals(1, data.get(0)[0]);
		assertEquals(3, data.get(1)[0]);
 	}

	@Test
	public void testSequentialQAll() throws InterruptedException {
		SequentialSpace sp = new SequentialSpace();
		sp.put(1);
		sp.put("2");
		sp.put(3);
		LinkedList<Object[]> data = sp.queryAll(new FormalField(Integer.class));
		assertEquals(2, data.size());
		assertEquals(1, data.get(0)[0]);
		assertEquals(3, data.get(1)[0]);
 	}

	@Test
	public void testPile() throws InterruptedException {
		PileSpace sp = new PileSpace();
		sp.put(1);
		sp.put(2);
		sp.put(3);
		Object[] o = sp.query(new FormalField(Integer.class));
		assertEquals(1, o.length);
		assertEquals(3, o[0]);
 	}

	@Test
	public void testPileAll() throws InterruptedException {
		PileSpace sp = new PileSpace();
		sp.put(1);
		sp.put("2");
		sp.put(3);
		LinkedList<Object[]> data = sp.getAll(new FormalField(Integer.class));
		assertEquals(2, data.size());
		assertEquals(3, data.get(0)[0]);
		assertEquals(1, data.get(1)[0]);
 	}

	@Test
	public void testPileQAll() throws InterruptedException {
		PileSpace sp = new PileSpace();
		sp.put(1);
		sp.put("2");
		sp.put(3);
		LinkedList<Object[]> data = sp.queryAll(new FormalField(Integer.class));
		assertEquals(2, data.size());
		assertEquals(3, data.get(0)[0]);
		assertEquals(1, data.get(1)[0]);
 	}

	@Test
	public void testStack() throws InterruptedException {
		StackSpace sp = new StackSpace();
		sp.put(1);
		sp.put(2);
		sp.put(3);
		Object[] o = sp.query(new FormalField(Integer.class));
		assertEquals(1, o.length);
		assertEquals(3, o[0]);
 	}

	@Test
	public void testStackAll() throws InterruptedException {
		StackSpace sp = new StackSpace();
		sp.put(1);
		sp.put("2");
		sp.put(3);
		LinkedList<Object[]> data = sp.getAll(new FormalField(Integer.class));
		assertEquals(1, data.size());
		assertEquals(3, data.get(0)[0]);
 	}

	@Test
	public void testStackQAll() throws InterruptedException {
		StackSpace sp = new StackSpace();
		sp.put(1);
		sp.put("2");
		sp.put(3);
		LinkedList<Object[]> data = sp.queryAll(new FormalField(Integer.class));
		assertEquals(1, data.size());
		assertEquals(3, data.get(0)[0]);
 	}

	@Test
	public void testQueue() throws InterruptedException {
		QueueSpace sp = new QueueSpace();
		sp.put(1);
		sp.put(2);
		sp.put(3);
		Object[] o = sp.query(new FormalField(Integer.class));
		assertEquals(1, o.length);
		assertEquals(1, o[0]);
 	}

	@Test
	public void testQueueAll() throws InterruptedException {
		QueueSpace sp = new QueueSpace();
		sp.put(1);
		sp.put("2");
		sp.put(3);
		LinkedList<Object[]> data = sp.getAll(new FormalField(Integer.class));
		assertEquals(1, data.size());
		assertEquals(1, data.get(0)[0]);
 	}

	@Test
	public void testQueueQAll() throws InterruptedException {
		QueueSpace sp = new QueueSpace();
		sp.put(1);
		sp.put("2");
		sp.put(3);
		LinkedList<Object[]> data = sp.queryAll(new FormalField(Integer.class));
		assertEquals(1, data.size());
		assertEquals(1, data.get(0)[0]);
 	}

}
