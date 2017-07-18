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


import java.util.List;


/**
 * A space is a concurrent data structure that can be used to coordinate activities of threads. 
 * Each space is a collection of tuples. These are sequences of values. Each space provides operations
 * that can be used to add, read and remove tuples from the space. Tuples are retrieved/selected from
 * the tuple space via pattern matching. 
 * 
 */
public interface Space {

	/**
	 * Returns the number of tuples that are stored in the space.
	 */
	public int size();

	/**
	 * Adds a tuple in the space.
	 * 
	 * @param fields fields of inserted tuple
	 * @return true if the action has been successfully executed false otherwise.
	 * @throws InterruptedException if any thread interrupted the current thread before 
	 * the action is executed.
	 */
	public boolean put(Object ... fields)  throws InterruptedException;
	
	/**
	 * Retrieves (and remove) a tuple matching the requested template. A template is rendered as
	 * an array of {@link TemplateField}. The returned, say <code>result</code> will satisfy
	 * the following properties:
	 * <ul>
	 * <li> <code>result.length==fields.length</code>;
	 * <li> for any <code>i</code>, <code>fields[i].match(return[i])==true</code>.
	 * </ul>
	 * 
	 * Current thread is suspended until a tuple matching the requested template is found. 
	 * 
	 * @param fields an array of template fields representing the requested template 
	 * @return a tuple matching the requested template
	 * @throws InterruptedException if any thread interrupted the current thread before 
	 * the action is executed.
	 */
	public Object[] get(TemplateField ... fields) throws InterruptedException;

	/**
	 * Retrieves (and remove) a tuple matching the requested template. A template is rendered as
	 * an array of {@link TemplateField}. The returned, say <code>result</code> will satisfy
	 * the following properties:
	 * <ul>
	 * <li> <code>result.length==fields.length</code>;
	 * <li> for any <code>i</code>, <code>fields[i].match(return[i])==true</code>.
	 * </ul>
	 * 
	 * If a tuple matching the requested tempalte is not found, <code>null</code> is returned.
	 * 
	 * @param fields an array of template fields representing a template 
	 * @return a tuple matching the template or <code>null</code> if no tuple matches the template
	 * @throws InterruptedException if any thread interrupted the current thread before 
	 * the action is executed.
	 */	
	public Object[] getp(TemplateField ... fields) throws InterruptedException;

	/**
	 * Retrieves (and remove) all the tuples matching a template. If no matching tuple is found, the
	 * empty list is returned.
	 * 
	 * @param fields an array of template fields representing a template
	 * @return a list containing all the tuples matching the template
	 * @throws InterruptedException if any thread interrupted the current thread before 
	 * the action is executed.
	 */
	public List<Object[]> getAll(TemplateField ... fields) throws InterruptedException;
	
	/**
	 * Reads (without removing) a tuple matching the requested template. A template is rendered as
	 * an array of {@link TemplateField}. The returned, say <code>result</code> will satisfy
	 * the following properties:
	 * <ul>
	 * <li> <code>result.length==fields.length</code>;
	 * <li> for any <code>i</code>, <code>fields[i].match(return[i])==true</code>.
	 * </ul>
	 * 
	 * Current thread is suspended until a tuple matching the requested template is found. 
	 * 
	 * @param fields an array of template fields representing the requested template 
	 * @return a tuple matching the requested template
	 * @throws InterruptedException if any thread interrupted the current thread before 
	 * the action is executed.
	 */
	public Object[] query(TemplateField ... fields) throws InterruptedException;
	
	/**
	 * Reads (without removing) a tuple matching the requested template. A template is rendered as
	 * an array of {@link TemplateField}. The returned, say <code>result</code> will satisfy
	 * the following properties:
	 * <ul>
	 * <li> <code>result.length==fields.length</code>;
	 * <li> for any <code>i</code>, <code>fields[i].match(return[i])==true</code>.
	 * </ul>
	 * 
	 * Value <code>null</code> is returned if no matching tuple is found.
	 * 
	 * @param fields an array of template fields representing the requested template 
	 * @return a list containing all the tuples matching the template
	 * @throws InterruptedException if any thread interrupted the current thread before 
	 * the action is executed.
	 */
	public Object[] queryp(TemplateField ... fields) throws InterruptedException;
	
	/**
	 * Reads (without removing) all the tuples matching the requested template. A template is rendered as
	 * an array of {@link TemplateField}. The returned, say <code>result</code> will satisfy
	 * the following properties:
	 * <ul>
	 * <li> <code>result.length==fields.length</code>;
	 * <li> for any <code>i</code>, <code>fields[i].match(return[i])==true</code>.
	 * </ul>
	 * 
	 * @param fields an array of template fields representing the requested template 
	 * @return a list containing all the tuples matching the template
	 * @throws InterruptedException if any thread interrupted the current thread before 
	 * the action is executed.
	 */
	public List<Object[]> queryAll(TemplateField ... fields) throws InterruptedException;

//  TODO: The following methods will be included in future implementations.
//	public Space map(Function<Tuple, Tuple> f) throws InterruptedException;
//	
//	public <T1> T1 reduce(BiFunction<Tuple, T1, T1> f, Comparator<Tuple> comp, T1 v) throws InterruptedException;
	
}
