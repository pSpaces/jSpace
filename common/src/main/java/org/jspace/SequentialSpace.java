package org.jspace;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.BiFunction;
import java.util.function.Function;

public class SequentialSpace implements Space {
	
	private final LinkedList<Tuple> tuples;
	
	public SequentialSpace(){
		this.tuples = new LinkedList<Tuple>();
	}
	
	public SequentialSpace(LinkedList<Tuple> tuples){
		this();
		this.tuples.addAll( tuples );
	}

	@Override
	public synchronized boolean put(Tuple t) {
		boolean result = tuples.add(t);
		notifyAll();
		return result;
	}

	@Override
	public synchronized Tuple get(Template template) throws InterruptedException {
		Tuple result = null;
		while(result == null){
			result = findTuple(template,true);
			if (result == null)
				wait();
			else
				return result;
		}
		return result;
	}
	
	private Tuple findTuple(Template template,boolean toRemove){
		Tuple result = null;
		Iterator<Tuple> tuplesIterator = tuples.iterator();
		while(tuplesIterator.hasNext()){
			result = tuplesIterator.next();
			if (template.match(result)){
				if (toRemove)
					tuplesIterator.remove();
				return result;
			}
		}
		return null;
	}
	
	@Override
	public Tuple getp(Template template){
		Tuple result = findTuple(template,true);
		return result;
	}

	@Override
	public LinkedList<Tuple> getAll(Template template){
		LinkedList<Tuple> result = findAllTuples(template,true);
		return result;
	}
	
	public LinkedList<Tuple> getAll(){
		LinkedList<Tuple> result = findAllTuples(true);
		return result;
	}
	
	private synchronized LinkedList<Tuple> findAllTuples(Template template,boolean toRemove){
		LinkedList<Tuple> result = new LinkedList<Tuple>();
		Iterator<Tuple> tuplesIterator = tuples.iterator();
		Tuple t;
		while(tuplesIterator.hasNext()){
			t = tuplesIterator.next();
			if (template.match(t)){
				result.add(t);
				if (toRemove)
					tuplesIterator.remove();
			}
		}
		return result;
	}
	
	private synchronized LinkedList<Tuple> findAllTuples(boolean toRemove){
		LinkedList<Tuple> result = new LinkedList<Tuple>();
		Iterator<Tuple> tuplesIterator = tuples.iterator();
		Tuple t;
		while(tuplesIterator.hasNext()){
			t = tuplesIterator.next();
			result.add(t);
			if (toRemove)
				tuplesIterator.remove();
		}
		return result;
	}

	@Override
	public synchronized Tuple query(Template template) throws InterruptedException {
		Tuple result = null;
		while(result == null){
			result = findTuple(template,false);
			if (result == null)
				wait();
			else
				return result;
		}
		return result;
	}

	@Override
	public Tuple queryp(Template template){
		Tuple result = findTuple(template,false);
		return result;
	}

	@Override
	public LinkedList<Tuple> queryAll(Template template){
		LinkedList<Tuple> result = findAllTuples(template,false);
		return result;
	}
	
	public LinkedList<Tuple> queryAll(){
		LinkedList<Tuple> result = findAllTuples(false);
		return result;
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
	public int size() {
		return tuples.size();
	}
	
}