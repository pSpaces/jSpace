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

import java.util.LinkedList;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.junit.Test;

public class TestFieldMatching {

	@Test
	public void testActualTrue() {
		ActualField f1 = new ActualField(1);
		assertTrue(f1.match(1));
	}

	@Test
	public void testActualFalse() {
		ActualField f1 = new ActualField(1);
		assertFalse(f1.match(2));
	}
	
	@Test
	public void testActualCollection() {
		LinkedList<Integer> l1 = new LinkedList<Integer>();
		l1.add(1);		
		LinkedList<Integer> l2 = new LinkedList<Integer>();
		l2.add(1);
		ActualField f1 = new ActualField(l1);
		assertTrue(f1.match(l2));
	}
	
	@Test
	public void testActualEmptyCollection() {
		LinkedList<String> l1 = new LinkedList<String>();
		LinkedList<Integer> l2 = new LinkedList<Integer>();
		ActualField f1 = new ActualField(l1);
		assertTrue(f1.match(l2));
	}


	@Test
	public void testFormalTrue() {
		FormalField f1 = new FormalField(Integer.class);
		assertTrue(f1.match(1));
	}

	@Test
	public void testFormalFalse() {
		FormalField f1 = new FormalField(Integer.class);
		assertFalse(f1.match("1"));
	}

}
