/**
 * 
 * jSpace: a Java Framework for Programming Concurrent and Distributed Applications with Spaces
 * 
 * http://pspace.github.io/jSpace/	
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *      Alberto Lluch Lafuente
 *      Michele Loreti
 *      Francesco Terrosi
 */
package org.jspace;

/**
 * Identifies a formal template field.
 */
public class FormalTemplateField implements TemplateField {

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
	public FormalTemplateField(Class<?> type) {
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
		if (value instanceof ActualTemplateField) {
			value = ((ActualTemplateField) value).getValue();
		}
		return (o == null) || (type.isInstance(o));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FormalTemplateField) {
			return type.equals(((FormalTemplateField) obj).type);
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
