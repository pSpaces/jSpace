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

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;


import org.jspace.monitor.ActionType;
import org.jspace.monitor.MeasureListener;
import org.jspace.monitor.SpaceEvent;
import org.jspace.monitor.SpaceEventListener;
import org.jspace.monitor.SpaceMeasurement;
import org.jspace.monitor.SpaceListener;

public class SequentialSpace implements Space {
	
	protected final int bound;
	
	protected final LinkedList<Tuple> tuples = new LinkedList<>();

	private List<SpaceListener> listeners;

	/**
	 * Create an unbounded sequential space.
	 */
	public SequentialSpace() {
		this(-1);
	}
	
	/**
	 * Create a new sequential space with bound that limits the number of tuples that can be inserted in the space.
	 * 
	 * @param bound max number of tuples in the space.
	 */
	public SequentialSpace(int bound) {
		this.bound = (bound<=0?-1:bound);
		this.listeners = new LinkedList<SpaceListener>();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.jspace.Space#put(java.lang.Object[])
	 */
	@Override
	public synchronized boolean put(Object ... fields) throws InterruptedException {
		while ((this.bound>0)&&(this.tuples.size()>=bound)) {
			wait();
		}
		addTuple(new Tuple(Arrays.copyOf(fields, fields.length)));
		notifyAll();
		notifySpaceEvent(new SpaceEvent(ActionType.PUT,Arrays.copyOf(fields, fields.length)));
		return true;
	}
	
	protected synchronized void addTuple(Tuple tuple) {
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
				notifyAll();
				notifySpaceEvent(new SpaceEvent(ActionType.GET,result.getTuple()));
				return result.getTuple();
			}
			wait(); 
		}
	}

	protected synchronized Tuple findTuple(Template template,boolean toRemove) {
		return findTuple(t->true,template,toRemove);
	}

	protected synchronized Tuple findTuple(Predicate<Object[]> p, Template template,boolean toRemove) {
		Iterator<Tuple> tuplesIterator = tuples.iterator();
		while (tuplesIterator.hasNext()) {
			Tuple t = tuplesIterator.next();
			if (template.match(t)&p.test(t.getTuple())) {
				if (toRemove) {
					tuplesIterator.remove();
				}
				return t;
			}
		}
		return null;
	}

	protected synchronized LinkedList<Object[]> findAllTuples(Template template,boolean toRemove) {
		return findAllTuples(t->true,template,toRemove);
	}
	
	protected synchronized LinkedList<Object[]> findAllTuples(Predicate<Object[]> p, Template template,boolean toRemove) {
		LinkedList<Object[]> result = new LinkedList<Object[]>();
		Iterator<Tuple> tuplesIterator = tuples.iterator();
		Tuple t;
		while (tuplesIterator.hasNext()){
			t = tuplesIterator.next();
			if (template.match(t)&&p.test(t.getTuple())){
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
		if (result != null) {
			notifyAll();
			notifySpaceEvent(new SpaceEvent(ActionType.GET,result.getTuple())); 
			return result.getTuple();
		}
		return null;
	}

	

	/*
	 * (non-Javadoc)
	 * @see org.jspace.Space#getAll(org.jspace.TemplateField[])
	 */
	@Override
	public synchronized LinkedList<Object[]> getAll(TemplateField ... fields){
		LinkedList<Object[]> result = findAllTuples(new Template(Arrays.copyOf(fields,fields.length)),true);
		if (result.size()>0) {
			notifyAll();
			notifySpaceEvent(new SpaceEvent(ActionType.GETALL,result));
		}
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
				notifySpaceEvent(new SpaceEvent(ActionType.QUERY,result.getTuple()));
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
		notifySpaceEvent(new SpaceEvent(ActionType.QUERY,result.getTuple()));
		return (result==null?null:result.getTuple());
	}

	/*
	 * (non-Javadoc)
	 * @see org.jspace.Space#queryAll(org.jspace.TemplateField[])
	 */
	@Override
	public synchronized LinkedList<Object[]> queryAll(TemplateField ... fields){
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

    public LinkedList<Tuple> toListOfTuples() {
        return tuples;
    }

    public String toString() {
        String s = "";

        for (Tuple t : tuples) {
            s = String.join(", ", s, t.toString());
        }

        return s;
    }

	@Override
	public Object[] getp(Predicate<Object[]> p, TemplateField[] fields) throws InterruptedException {
		Tuple result = findTuple(p,new Template(Arrays.copyOf(fields,fields.length)),true);
		if (result != null) {
			notifyAll();
			return result.getTuple();
		}
		return null;
	}

	@Override
	public Object[] queryp(Predicate<Object[]> p, TemplateField[] fields) throws InterruptedException {
		
		Tuple result = findTuple(p,new Template(Arrays.copyOf(fields,fields.length)),false);
		return (result==null?null:result.getTuple());
		
	}

	@Override
	public Object[] getp(Predicate<Space> spaceCondition, Predicate<Object[]> p, TemplateField[] fields)
			throws InterruptedException {
		if (spaceCondition.test(this)) {
			Tuple result = findTuple(p,new Template(Arrays.copyOf(fields,fields.length)),true);
			if (result != null) {
				notifyAll();
				return result.getTuple();
			}
			return null;
		}
		return null;
		
	}

	@Override
	public Object[] queryp(Predicate<Space> spaceCondition, Predicate<Object[]> p, TemplateField[] fields)
			throws InterruptedException {
		if (spaceCondition.test(this)) {
			Tuple result = findTuple(p,new Template(Arrays.copyOf(fields,fields.length)),false);
			return (result==null?null:result.getTuple());
		}
		
		return null;
	}

	@Override
	public boolean lock() throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean unlock() throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object[] get(Predicate<Object[]> p, TemplateField[] fields) throws InterruptedException {
		while(true){
			Tuple result = findTuple(p,new Template(Arrays.copyOf(fields,fields.length)),true);
			if (result != null) {
				return result.getTuple();
			}
			wait();
		
	}
		
	}

	@Override
	public Object[] query(Predicate<Object[]> p, TemplateField[] fields) throws InterruptedException {
		while(true){
				Tuple result = findTuple(p,new Template(Arrays.copyOf(fields,fields.length)),false);
				if (result != null) {
					return result.getTuple();
				}
				wait();
			
		}
	}

	@Override
	public Object[] get(Predicate<Space> spaceCondition, Predicate<Object[]> p, TemplateField[] fields)
			throws InterruptedException {
		while(true){
			if (spaceCondition.test(this)) {
				Tuple result = findTuple(p,new Template(Arrays.copyOf(fields,fields.length)),true);
				if (result != null) {
					return result.getTuple();
				}
				wait();
			}
		}
		
	}

	@Override
	public Object[] query(Predicate<Space> spaceCondition, Predicate<Object[]> p, TemplateField[] fields)
			throws InterruptedException {
		while(true){
			if (spaceCondition.test(this)) {
				Tuple result = findTuple(p,new Template(Arrays.copyOf(fields,fields.length)),false);
				if (result != null) {
					return result.getTuple();
				}
				wait();
			}
		}
	}

	@Override
	public synchronized boolean addListener(SpaceListener listener) {
		if (this.listeners.contains(listener)) {
			return false;
		}
		this.listeners.add(listener);
		return true;
		
	}

	@Override
	public synchronized boolean removeListener(SpaceListener listener) {
		return this.listeners.remove(listener);
	}

	@Override
	public <T> boolean registerMeasure(SpaceMeasurement<T> measure) {
		measure.init(this);
        addListener(new MeasureListener(measure));
		return true;
	}
	
	@Override  //viene inserito il listener degli eventi
	public  boolean registerEvent() {
		
        addListener(new SpaceEventListener());
		return true;
	}
	
	private void notifySpaceEvent(SpaceEvent e) { 
		
		//avviso tutti i listener di un nuovo evento
		for (SpaceListener l : listeners) {
			l.handle(e);// i listener valutano il tipo di evento e agiscono di conseguenza
		} 
	
	}

	@Override
	public synchronized LinkedList<Object[]> getAll(Predicate<Space> spaceCondition, Predicate<Object[]> p, TemplateField[] fields) {
		if (spaceCondition.test(this)) {
			LinkedList<Object[]> result = findAllTuples(p,new Template(Arrays.copyOf(fields,fields.length)),true);
			if (result.size()>0) {
				notifyAll();
			}
			return result;
		}
		
		return null;
	}

	@Override
	public List<Object[]> queryAll(Predicate<Space> spaceCondition, Predicate<Object[]> p, TemplateField[] fields) {
		if (spaceCondition.test(this)) {
			LinkedList<Object[]> result = findAllTuples(p,new Template(Arrays.copyOf(fields,fields.length)),false);
			return result;
		}
		
		return null;
	}

	
}
