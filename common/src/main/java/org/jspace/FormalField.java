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

/**
 * Identifies a formal template field.
 */
public class FormalField implements TemplateField {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Type of matching value.
	 */
	private Class<?> type;

	/**
	 * Creates a template field matching any value of type <code>type</code>.
	 * 
	 * @param type Class of expected field.
	 */
	public FormalField(Class<?> type) {
		this.type = type;
	}

	/***
	 * Returns <code>true</code> if parameters <code>o</code> is instance of <code>this.type</code>.
	 * 
	 * @see org.pspaces.jspace.cmg.jresp.knowledge.TemplateField#match(java.lang.Object)
	 */
	@Override
	public boolean match(Object o) {
		Object value = o;
		if (value == null) {//TODO: Are 'null' values allowed in Tuples? 
			return false;
		} 
		if (value instanceof ActualField) {
			value = ((ActualField) value).getValue();
		}
		return (o == null) || (type.isInstance(o));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FormalField) {
			return type.equals(((FormalField) obj).type);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return type.hashCode();
	}

	@Override
	public String toString() {
		return "?{" + type.getName()+ "}";
	}

	public Class<?> getFormalFieldType() {
		return type;
	}

}
