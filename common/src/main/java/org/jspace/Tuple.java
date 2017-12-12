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

package org.jspace;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;

/**
 * A tuple identifies the basic information item. It consists of a sequence of
 * values that can be collected into a knowledge repository.
 */
public final class Tuple implements Iterable<Object>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Tuple fields.
	 */
	private Object[] fields;

	/**
	 * Creates a new tuple.
	 * 
	 * @param fields
	 *            fields of new created tuple.
	 */
	public Tuple(Object... fields) {
		this.fields = fields;
	}

	/**
	 * Returns the tuple length.
	 * 
	 * @return the tuple length.
	 */
	public int length() {
		return fields.length;
	}

	/**
	 * Returns the element at index <code>i</code>.
	 * 
	 * @param i
	 *            element index.
	 * @return the element at index <code>i</code>.
	 */
	public Object getElementAt(int i) {
		return fields[i];
	}

	/**
	 * Returns the class <code>c</code> of the element with index <code>i</code>
	 * .
	 * 
	 * @param i
	 *            element index
	 * @return the class <code>c</code> of the element with index <code>i</code>
	 *         .
	 */
	public Class<?> getTypeAt(int i) {
		return fields[i].getClass();
	}

	/**
	 * Returns the instance of class <code>c</code> at element <code>i</code>.
	 * This method is equivalent to <code>c.cast(getElementAt(i))</code>. A
	 * <code>ClassCastException</code> is thrown if the <code>i</code>-th
	 * element of the tuple is not an instance if <code>c</code>.
	 * 
	 * @param c
	 *            expected class
	 * @param i
	 *            element index
	 * @return the instance of class <code>c</code> at element <code>i</code>.
	 */
	public <T> T getElementAt(Class<T> c, int i) {
		Object o = getElementAt(i);
		if (o == null) {
			return null;
		}
		if (c.isInstance(o)) {
			return c.cast(o);
		}
		throw new ClassCastException();
	}

	/**
	 * Checks if the element at position <code>i</code> is instance of <code>c</code>.
	 * 
	 * @param c Class 
	 * @param i Element index
	 * @return true if element at <code>i</code> is instance of <code>c</code>.
	 */
	public boolean isInstance(Class<?> c, int i) {
		return c.isInstance(fields[i]);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Tuple) {
			return Arrays.deepEquals(fields, ((Tuple) obj).fields);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(fields);
	}

	@Override
	public String toString() {
		return Arrays.deepToString(fields);
	}

	@Override
	public Iterator<Object> iterator() {
		return new Iterator<Object>() {

			private int current = 0;

			@Override
			public boolean hasNext() {
				return current < fields.length;
			}

			@Override
			public Object next() {
				return fields[current++];
			}

			@Override
			public void remove() {
			}

		}; 
	}

	public Object[] getTuple() {
		return Arrays.copyOf(this.fields,this.fields.length);
	}

}
