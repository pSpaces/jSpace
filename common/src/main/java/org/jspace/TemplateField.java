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


import java.io.Serializable;

/**
 * Identifies a generic template field. 
 */
public interface TemplateField extends Serializable {

	/**
	 * Checks if the object <code>o</code> matches against this field.
	 *
	 * @param o
	 *            a generic object
	 * @return <code>true</code> if the object <code>o</code> matches against
	 *         this field.
	 */
	public boolean match(Object o);

}
