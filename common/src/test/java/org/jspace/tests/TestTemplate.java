package org.jspace.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.jspace.ActualTemplateField;
import org.jspace.FormalTemplateField;
import org.jspace.Template;
import org.jspace.TemplateField;
import org.jspace.Tuple;
import org.junit.Test;

public class TestTemplate {

	@Test
	public void testLength() {
		Template template = new Template(1,2,3);
		assertEquals(3,template.length());
	}

	@Test
	public void testEquals1() {
		Template t1 = new Template(1,2,3);
		Template t2 = new Template(1,2,3);
		assertEquals(t1,t2);
	}

	@Test
	public void testEquals2() {
		Template t1 = new Template(1,2,3);
		Template t2 = new Template(new ActualTemplateField(1),new ActualTemplateField(2),new ActualTemplateField(3));
		assertEquals(t1,t2);
	}

	@Test
	public void testEquals3() {
		Template t1 = new Template(new FormalTemplateField(String.class),1);
		Template t2 = new Template(new FormalTemplateField(String.class),1);
		assertEquals(t1,t2);
	}

	@Test
	public void testToString1() {
		Template t1 = new Template(new FormalTemplateField(String.class),1);
		assertEquals("[?{java.lang.String}, 1]",t1.toString());
	}

	@Test
	public void testIterator() {
		Template t1 = new Template(1,2,3);
		int counter = 1;
		for (TemplateField templateField : t1) {
			assertEquals(new ActualTemplateField(counter++), templateField);
		}
	}

	@Test
	public void testGetElementAt() {
		Template t1 = new Template(1,2,3);
		assertEquals(new ActualTemplateField(1), t1.getElementAt(0));
		assertEquals(new ActualTemplateField(2), t1.getElementAt(1));
		assertEquals(new ActualTemplateField(3), t1.getElementAt(2));
	}

	@Test
	public void testMatch1() {
		Template t1 = new Template(1,2,3);
		Tuple t2 = new Tuple(1,2,3);
		assertTrue(t1.match(t2));
	}

	@Test
	public void testMatch2() {
		Template t1 = new Template(1,new FormalTemplateField(Integer.class),3);
		Tuple t2 = new Tuple(1,2,3);
		assertTrue(t1.match(t2));
	}

	@Test
	public void testMatch3() {
		Template t1 = new Template(1,2,3);
		Tuple t2 = new Tuple(1,2);
		assertFalse(t1.match(t2));
	}


}
