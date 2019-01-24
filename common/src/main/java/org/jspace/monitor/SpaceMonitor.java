package org.jspace.monitor;


import java.util.List;
import java.util.function.Predicate;

import org.jspace.Space;
import org.jspace.TemplateField;


public class SpaceMonitor {
	
	private Rule rule;
	
	
	public SpaceMonitor(Rule rule) {
		this.rule = rule;
	}

	public boolean put(Space space, Object[] fields) throws InterruptedException {
		
		RuleEvaluation<Boolean> re = rule.put(space, fields);
		this.rule = re.getRule();
		return re.getValue();
	
	}

	public Object[] get(Space space, TemplateField[] fields) throws InterruptedException {
		
		while(true) {
			RuleEvaluation<Object[]> re = rule.get(space,fields);
				if(re.getValue()==null) {
					wait();
				}
				this.rule = re.getRule();
				return re.getValue();
		}
	}

	public Object[] getp(Space space, TemplateField[] fields) throws InterruptedException {
		  RuleEvaluation<Object[]> re = rule.getp(space,fields);
		  this.rule = re.getRule();
		  return re.getValue();
	}

	public List<Object[]> getAll(Space space, TemplateField[] fields)throws InterruptedException {
		  RuleEvaluation<List<Object[]>> re = rule.getAll(space,fields);
		  this.rule = re.getRule();
		  return re.getValue();
	}

	public Object[] query(Space space, TemplateField[] fields) throws InterruptedException {
		while(true) {
			RuleEvaluation<Object[]> re = rule.query(space,fields);
			if(re.getValue()==null) {
				wait();
			}
			this.rule = re.getRule();
			return re.getValue();
		}
	}

	public Object[] queryp(Space space, TemplateField[] fields) throws InterruptedException {
		 RuleEvaluation<Object[]> re = rule.queryp(space,fields);
		 this.rule = re.getRule();
		 return re.getValue();
	}

	public List<Object[]> queryAll(Space space, TemplateField[] fields) throws InterruptedException {
		  RuleEvaluation<List<Object[]>> re = rule.queryAll(space,fields);
		  this.rule = re.getRule();
		  return re.getValue();
	}

	public Object[] get(Space space, Predicate<Object[]> p, TemplateField[] fields) throws InterruptedException {
		
		while(true) {
			RuleEvaluation<Object[]> re = rule.get(space,p,fields);
			if(re.getValue()==null) {
				wait();
			}
			this.rule = re.getRule();
			return re.getValue();
		}
	}

	public Object[] query(Space space, Predicate<Object[]> p, TemplateField[] fields) throws InterruptedException {
		while(true) {
			RuleEvaluation<Object[]> re = rule.query(space,p,fields);
			if(re.getValue()==null) {
				wait();
			}
			this.rule = re.getRule();
			return re.getValue();
		}
	}

	public Object[] query(Space space, Predicate<Space> spaceCondition, Predicate<Object[]> p, TemplateField[] fields) throws InterruptedException {
		
		while(true) {
			RuleEvaluation<Object[]> re = rule.query(space,spaceCondition,p,fields);
			if(re.getValue()==null) {
				wait();
			}
			this.rule = re.getRule();
			return re.getValue();
		}
	}

	public Object[] get(Space space, Predicate<Space> spaceCondition, Predicate<Object[]> p, TemplateField[] fields) throws InterruptedException {
		while(true) {
			RuleEvaluation<Object[]> re = rule.get(space,spaceCondition,p,fields);
			if(re.getValue()==null) {
				wait();
			}
			this.rule = re.getRule();
			return re.getValue();
		}
	}

	public Object[] getp(Space space, Predicate<Object[]> p, TemplateField[] fields) throws InterruptedException {
		RuleEvaluation<Object[]> re = rule.getp(space,p,fields);
		this.rule = re.getRule();
		return re.getValue();
	}

	public Object[] queryp(Space space, Predicate<Object[]> p, TemplateField[] fields) throws InterruptedException {
		RuleEvaluation<Object[]> re = rule.queryp(space,p,fields);
		this.rule = re.getRule();
		return re.getValue();
	}

	public Object[] getp(Space space, Predicate<Space> spaceCondition, Predicate<Object[]> p, TemplateField[] fields) throws InterruptedException {
		RuleEvaluation<Object[]> re = rule.getp(space,spaceCondition,p,fields);
		this.rule = re.getRule();
		return re.getValue();
	}

	public Object[] queryp(Space space, Predicate<Space> spaceCondition, Predicate<Object[]> p, TemplateField[] fields) throws InterruptedException {
		RuleEvaluation<Object[]> re = rule.queryp(space,spaceCondition,p,fields);
		this.rule = re.getRule();
		return re.getValue();
	}

	public List<Object[]> getAll(Space space, Predicate<Object[]> p, TemplateField[] fields) throws InterruptedException {
		 RuleEvaluation<List<Object[]>> re = rule.getAll(space,p,fields);
		 this.rule = re.getRule();
		 return re.getValue();
	}

	public List<Object[]> queryAll(Space space, Predicate<Object[]> p, TemplateField[] fields) throws InterruptedException {
		 RuleEvaluation<List<Object[]>> re = rule.queryAll(space,p,fields);
		 this.rule = re.getRule();
		 return re.getValue();
	}

	public List<Object[]> queryAll(Space space, Predicate<Space> spaceCondition, Predicate<Object[]> p,
			TemplateField[] fields) throws InterruptedException {
		RuleEvaluation<List<Object[]>> re = rule.queryAll(space,spaceCondition,p,fields);
		this.rule = re.getRule();
		return re.getValue();
	}

	public List<Object[]> getAll(Space space, Predicate<Space> spaceCondition, Predicate<Object[]> p,
			TemplateField[] fields)throws InterruptedException {
		RuleEvaluation<List<Object[]>> re = rule.getAll(space,spaceCondition,p,fields);
		this.rule = re.getRule();
		return re.getValue();
	}

	

}
