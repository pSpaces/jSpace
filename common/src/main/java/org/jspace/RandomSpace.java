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

import java.util.Random;

public class RandomSpace extends SequentialSpace {
	
	private Random random;

	public RandomSpace() {
		this(-1,new Random());
	}
	
	public RandomSpace(int bound) {
		this(bound,new Random());
	}
	
	public RandomSpace(long seed) {
		this(-1,new Random(seed));
	}
	
	public RandomSpace(int bound, long seed) {
		this(bound,new Random(seed));
	}
	
	public RandomSpace(int bound, Random r) {
		super(bound);
		this.random = r;
	}
	
	// New version: does not provide uniform distribution on matching tuples but is more performant
	protected Tuple findTuple(Template template,boolean toRemove) {
		Tuple t;
		int j = 0;
		int startI =  random.nextInt(tuples.size());
		for (int i = 0; i < tuples.size(); i++) {
			// randomise starting index
			j = (startI+i)%tuples.size();
			t = tuples.get((startI+i)%tuples.size());
			if (template.match(t)) {
				if (toRemove) {
					tuples.remove(j);
				}
				return t;
			}
		}
		return null;
		
	}
	
	// Previous version: less performant but provides uniform choice on the set of matching tuples
	/*
	protected Tuple findTuple(Template template,boolean toRemove) {
		ArrayList<Tuple> data = new ArrayList<>();
		Iterator<Tuple> tuplesIterator = tuples.iterator();
		while (tuplesIterator.hasNext()) {
			Tuple t = tuplesIterator.next();
			if (template.match(t)) {
				data.add(t);
			}
		}
		if (data.isEmpty()) {
			return null;
		}
		Tuple t = data.get(random.nextInt(data.size()));
		if (toRemove) {
			tuples.remove(t);
		}
		return t;
	}
	*/
	
}