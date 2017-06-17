package org.jspace.tests;

import static org.junit.Assert.*;

import org.jspace.Tuple;
import org.junit.Test;

public class TestTuple {

	@Test
	public void testLength() {
		assertEquals(1, new Tuple("1").length());
	}

	@Test
	public void testGetElementAtInt() {
		Tuple t = new Tuple("1");
		assertEquals("1", t.getElementAt(0));
	}

	@Test(expected = IndexOutOfBoundsException.class) 
	public void testGetElementAtIntException() {
		Tuple t = new Tuple("1");
		t.getElementAt(2);
	}

	@Test
	public void testGetTypeAt() {
		Tuple t = new Tuple("1");
		assertEquals(String.class,t.getTypeAt(0));
	}

	@Test(expected = IndexOutOfBoundsException.class) 
	public void testGetTypeAtException() {
		Tuple t = new Tuple("1");
		t.getElementAt(2);
	}


	@Test
	public void testGetElementAtClassOfTInt() {
		Tuple t = new Tuple("1");
		assertEquals("1", t.getElementAt(String.class,0));
	}

	@Test(expected = ClassCastException.class) 
	public void testGetElementAtClassOfTIntCastException() {
		Tuple t = new Tuple("1");
		t.getElementAt(Integer.class,0);
	}

	@Test(expected = IndexOutOfBoundsException.class) 
	public void testGetElementAtClassOfTIntIndexException() {
		Tuple t = new Tuple("1");
		t.getElementAt(String.class,2);
	}

	@Test
	public void testIsInstanceTrue() {
		Tuple t = new Tuple("1");
		assertTrue(t.isInstance(String.class,0));
	}

	@Test
	public void testIsInstanceFalse() {
		Tuple t = new Tuple("1");
		assertFalse(t.isInstance(Integer.class,0));
	}

	@Test
	public void testEqualsObject() {
		Tuple t1 = new Tuple("1");
		Tuple t2 = new Tuple("1");
		assertTrue(t1.equals(t2));
	}

	@Test
	public void testToString() {
		Tuple t1 = new Tuple( "1" , 1 , true );
		assertEquals("[1, 1, true]",t1.toString());
	}

}
