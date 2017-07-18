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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class FIFOSpace extends SequentialSpace {
	
	@Override
	protected void addTuple(Tuple tuple) {
		tuples.add(tuple);
	}
	
	@Override
	protected Tuple findTuple(Template template,boolean toRemove) {
		Tuple t = tuples.peek();
		if ((t!=null)&&(template.match(t))) {
			if (toRemove) {
				tuples.poll();
			}
			return t;
		} else {
			return null;
		}		
	}

	@Override
	protected LinkedList<Object[]> findAllTuples(Template template,boolean toRemove) {
		LinkedList<Object[]> result = new LinkedList<Object[]>();
		Iterator<Tuple> tuplesIterator = tuples.iterator();
		Tuple t;
		while (tuplesIterator.hasNext()){
			t = tuplesIterator.next();
			if (template.match(t)) {
				result.add(t.getTuple());
				if (toRemove)
					tuplesIterator.remove();
			} else {
				break ;
			}
		}
		return result;
	}

}
