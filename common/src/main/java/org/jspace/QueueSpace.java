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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class QueueSpace extends SequentialSpace {
	
	public QueueSpace() {
		this(-1);
	}
	
	public QueueSpace(int bound) {
		super(bound);
	}

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
