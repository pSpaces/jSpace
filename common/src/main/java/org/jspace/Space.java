package org.jspace;


import java.util.List;


public interface Space {
	
	public int size();
	
	public boolean put(Tuple t);
	
	public Tuple get(Template template) throws InterruptedException;
	
	public Tuple getp(Template template) throws InterruptedException;
	
	public List<Tuple> getAll(Template template) throws InterruptedException;
	
	public Tuple query(Template template) throws InterruptedException;
	
	public Tuple queryp(Template template) throws InterruptedException;
	
	public List<Tuple> queryAll(Template template) throws InterruptedException;

//  TODO: The following methods will be included in future implementations.
//	public Space map(Function<Tuple, Tuple> f) throws InterruptedException;
//	
//	public <T1> T1 reduce(BiFunction<Tuple, T1, T1> f, Comparator<Tuple> comp, T1 v) throws InterruptedException;
	
}
