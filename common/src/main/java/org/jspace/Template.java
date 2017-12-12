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
 * An instance of class <code>Template</code> is used as a pattern to select
 * tuples in a space.
 */
public final class Template implements Iterable<TemplateField>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * A list of template fields.
	 */
	private TemplateField[] fields;

	/**
	 * Creates a new template starting from its fields.
	 * 
	 * @param fields
	 */
	public Template(TemplateField...fields) {
		this.fields = fields;
	}
	
	public Template(Object...fields) {
		this(Template.toTemplateFields(fields));
	}

	private static TemplateField[] toTemplateFields(Object[] values) {
		TemplateField[] fields = new TemplateField[values.length];
		for( int i=0 ; i<values.length ; i++) {
			if (values[i] instanceof TemplateField) {
				fields[i] = (TemplateField) values[i];
			} else {
				fields[i] = new ActualField(values[i]);
			}
		}
		return fields;
	}

	/**
	 * Check if tuple <code>t</code> matches the tempalte.
	 * 
	 * @param t
	 *            tuple to match
	 * @return <code>true</code> if the tuple matches against this template,
	 *         <code>false</code> otherwise.
	 */
	public boolean match(Tuple t) {
		int size = this.length();
		if (size != t.length()) {
			return false;
		}
		for (int i = 0; i < size; i++) {
			if (!fields[i].match(t.getElementAt(i))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the number of fields in the template.
	 * 
	 * @return number of fields in the template.
	 */
	public int length() {
		return fields.length;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Template) {
			return Arrays.deepEquals(fields, ((Template) obj).fields);
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
	public Iterator<TemplateField> iterator() {
		return new Iterator<TemplateField>() {

			private int current = 0;

			@Override
			public boolean hasNext() {
				return (current < fields.length);
			}

			@Override
			public TemplateField next() {
				return fields[current++];
			}

			@Override
			public void remove() {
			}
		};
	}

	public TemplateField getElementAt(int i) {
		return fields[i];
	}

	public TemplateField[] getFields() {
		return fields;
	}

}
