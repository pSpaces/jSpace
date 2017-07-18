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
