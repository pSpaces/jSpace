package org.jspace.tests;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.jspace.ActualTemplateField;
import org.jspace.FormalTemplateField;
import org.junit.Test;

public class TestFieldMatching {

	@Test
	public void testActualTrue() {
		ActualTemplateField f1 = new ActualTemplateField(1);
		assertTrue(f1.match(1));
	}

	@Test
	public void testActualFalse() {
		ActualTemplateField f1 = new ActualTemplateField(1);
		assertFalse(f1.match(2));
	}
	
	@Test
	public void testActualCollection() {
		LinkedList<Integer> l1 = new LinkedList<Integer>();
		l1.add(1);		
		LinkedList<Integer> l2 = new LinkedList<Integer>();
		l2.add(1);
		ActualTemplateField f1 = new ActualTemplateField(l1);
		assertTrue(f1.match(l2));
	}
	
	@Test
	public void testActualEmptyCollection() {
		LinkedList<String> l1 = new LinkedList<String>();
		LinkedList<Integer> l2 = new LinkedList<Integer>();
		ActualTemplateField f1 = new ActualTemplateField(l1);
		assertTrue(f1.match(l2));
	}


	@Test
	public void testFormalTrue() {
		FormalTemplateField f1 = new FormalTemplateField(Integer.class);
		assertTrue(f1.match(1));
	}

	@Test
	public void testFormalFalse() {
		FormalTemplateField f1 = new FormalTemplateField(Integer.class);
		assertFalse(f1.match("1"));
	}

}
