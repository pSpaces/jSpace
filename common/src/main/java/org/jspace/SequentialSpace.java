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

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class SequentialSpace implements Space {
	
	protected final LinkedList<Tuple> tuples = new LinkedList<>();

	/*
	 * (non-Javadoc)
	 * @see org.jspace.Space#put(java.lang.Object[])
	 */
	@Override
	public synchronized boolean put(Object ... fields) {
		addTuple(new Tuple(Arrays.copyOf(fields, fields.length)));
		notifyAll();
		return true;
	}
	
	protected void addTuple(Tuple tuple) {
		tuples.add(tuple);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.jspace.Space#get(org.jspace.TemplateField[])
	 */
	@Override
	public synchronized Object[] get(TemplateField ... fields) throws InterruptedException {
		while (true) {
			Tuple result = findTuple(new Template(Arrays.copyOf(fields,fields.length)),true);
			if (result != null) {
				return result.getTuple();
			}
			wait(); 
		}
	}
	
	protected Tuple findTuple(Template template,boolean toRemove) {
		Iterator<Tuple> tuplesIterator = tuples.iterator();
		while (tuplesIterator.hasNext()) {
			Tuple t = tuplesIterator.next();
			if (template.match(t)) {
				if (toRemove) {
					tuplesIterator.remove();
				}
				return t;
			}
		}
		return null;
	}

	protected LinkedList<Object[]> findAllTuples(Template template,boolean toRemove) {
		LinkedList<Object[]> result = new LinkedList<Object[]>();
		Iterator<Tuple> tuplesIterator = tuples.iterator();
		Tuple t;
		while (tuplesIterator.hasNext()){
			t = tuplesIterator.next();
			if (template.match(t)){
				result.add(t.getTuple());
				if (toRemove)
					tuplesIterator.remove();
			}
		}
		return result;
	}
	

	/*
	 * (non-Javadoc)
	 * @see org.jspace.Space#getp(org.jspace.TemplateField[])
	 */
	@Override
	public synchronized Object[] getp(TemplateField ... fields) {
		Tuple result = findTuple(new Template(Arrays.copyOf(fields,fields.length)),true);
		return (result==null?null:result.getTuple());
	}

	/*
	 * (non-Javadoc)
	 * @see org.jspace.Space#getAll(org.jspace.TemplateField[])
	 */
	@Override
	public synchronized LinkedList<Object[]> getAll(TemplateField ... fields){
		LinkedList<Object[]> result = findAllTuples(new Template(Arrays.copyOf(fields,fields.length)),true);
		return result;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.jspace.Space#query(org.jspace.TemplateField[])
	 */
	@Override
	public synchronized Object[] query(TemplateField ... fields) throws InterruptedException {
		while(true){
			Tuple result = findTuple(new Template(Arrays.copyOf(fields,fields.length)),false);
			if (result != null) {
				return result.getTuple();
			}
			wait();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.jspace.Space#queryp(org.jspace.TemplateField[])
	 */
	@Override
	public synchronized Object[] queryp(TemplateField ... fields){
		Tuple result = findTuple(new Template(Arrays.copyOf(fields,fields.length)),false);
		return (result==null?null:result.getTuple());
	}

	/*
	 * (non-Javadoc)
	 * @see org.jspace.Space#queryAll(org.jspace.TemplateField[])
	 */
	@Override
	public LinkedList<Object[]> queryAll(TemplateField ... fields){
		return findAllTuples(new Template(Arrays.copyOf(fields,fields.length)),false);
	}
	

//	@Override
//	public Space map(Function<Tuple, Tuple> f) throws InterruptedException {
//		LinkedList<Tuple> result = new LinkedList<Tuple>();
//		Tuple temp;
//		for(Tuple t:this.tuples){
//			temp = f.apply(t);
//			result.add(temp);
//		}
//		return new SequentialSpace(result);
//	}
//
//	@Override
//	public <T1> T1 reduce(BiFunction<Tuple, T1, T1> f, Comparator<Tuple> comp, T1 v) throws InterruptedException {
//		LinkedList<Tuple> temp = queryAll();
//		temp.sort(comp);
//		for (Tuple t:temp)
//			v = f.apply(t, v);
//		return v;
//	}

	@Override
	public synchronized int size() {
		return tuples.size();
	}
	
}