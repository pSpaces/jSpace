package org.jspace.monitor;


import java.util.List;



import org.jspace.Repository;

import org.jspace.SpaceWrapper;

import org.jspace.Template;

import org.jspace.protocol.SpaceProperties;


public class RepositoryMonitor{
	
	RepositoryRule rule;
	
	public RepositoryMonitor(RepositoryRule rule) {
		this.rule = rule;
	}
	
	


	public boolean put(Repository repo, String name, Object[] tuple) throws InterruptedException {
		
		RuleEvaluation<Boolean> re = rule.put(repo,name, tuple);
		this.rule = re.getRepoRule();
		return re.getValue();
		
	}




	public Object[] get(Repository repo, String name, Template template) throws InterruptedException {
		RuleEvaluation<Object[]> re = rule.get(repo,name, template);
		if(re.getValue()==null) {
			wait();
		}
		this.rule = re.getRepoRule();
		return re.getValue();
	}




	public Object[] getp(Repository repo, String name, Template template) throws InterruptedException {
		RuleEvaluation<Object[]> re = rule.getp(repo,name, template);
		this.rule = re.getRepoRule();
		return re.getValue();
	}




	public List<Object[]> getAll(Repository repo, String name, Template template) throws InterruptedException {
		RuleEvaluation<List<Object[]>> re = rule.getAll(repo,name, template);
		this.rule = re.getRepoRule();
		return re.getValue();
	}




	public Object[] query(Repository repo, String name, Template template) throws InterruptedException {
		RuleEvaluation<Object[]> re = rule.query(repo,name, template);
		if(re.getValue()==null) {
			wait();
		}
		this.rule = re.getRepoRule();
		return re.getValue();
	}




	public Object[] queryp(Repository repo, String name, Template template) throws InterruptedException {
		RuleEvaluation<Object[]> re = rule.queryp(repo,name, template);
		this.rule = re.getRepoRule();
		return re.getValue();
	}




	public List<Object[]> queryAll(Repository repo, String name, Template template) throws InterruptedException {
		RuleEvaluation<List<Object[]>> re = rule.queryAll(repo,name, template);
		this.rule = re.getRepoRule();
		return re.getValue();
	}

	public SpaceWrapper newSpace(Repository repo, String name, SpaceProperties props) throws InterruptedException {
		RuleEvaluation<SpaceWrapper> re = rule.createSpace(repo,name,props);
		this.rule = re.getRepoRule();
		return re.getValue();
	}


}
