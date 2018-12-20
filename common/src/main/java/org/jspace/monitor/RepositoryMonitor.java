package org.jspace.monitor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.jspace.MonitoredRepository;
import org.jspace.NamedSpace;
import org.jspace.PileSpace;
import org.jspace.QueueSpace;
import org.jspace.RandomSpace;
import org.jspace.Repository;
import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceWrapper;
import org.jspace.StackSpace;
import org.jspace.Template;
import org.jspace.TemplateField;
import org.jspace.Tuple;
import org.jspace.protocol.DataProperties;
import org.jspace.protocol.ManagementMessage;
import org.jspace.protocol.Message;
import org.jspace.protocol.MessageType;
import org.jspace.protocol.RepositoryProperties;
import org.jspace.protocol.SpaceProperties;
import org.jspace.protocol.Status;
import org.jspace.protocol.pSpaceMessage;

public class RepositoryMonitor{
	
	
	// controllare esattezza del costruttore
	Rule rule;
	public RepositoryMonitor(Rule rule) {
		this.rule = rule;
	}
	
	//FIXME Capire se il monitor crea uno spazio monitorato, quindi bisogna creare anche lo space monitor?
	public SpaceWrapper createSpace(Repository repo, SpaceProperties spaceProperties) {
		
		RuleEvaluation<SpaceWrapper> re = rule.create(repo, spaceProperties);
		
		return re.getValue();
	}

	public boolean put(SpaceWrapper sw, Object[] fields) throws InterruptedException {
		
		Space space = sw.getSpace();
		RuleEvaluation<Boolean> re = rule.put(space, fields);
		
		return re.getValue();
		
	}

	public Object[] get(SpaceWrapper sw, TemplateField[] fields) throws InterruptedException {
		Space space = sw.getSpace();
		while(true) {
			RuleEvaluation<Object[]> re = rule.get(space,fields);
			if(re.getValue()==null) {
				wait();
			}
			return re.getValue();
		}
		
	}

	public Object[] getp(SpaceWrapper sw, TemplateField[] fields) throws InterruptedException {
		Space space = sw.getSpace();
		 RuleEvaluation<Object[]> re = rule.getp(space,fields);
		 return re.getValue();
	}

	public List<Object[]> getAll(SpaceWrapper sw, TemplateField[] fields) throws InterruptedException {
		Space space = sw.getSpace();
		 RuleEvaluation<List<Object[]>> re = rule.getAll(space,fields);
		  return re.getValue();
	}

	public Object[] query(SpaceWrapper sw, TemplateField[] fields) throws InterruptedException {
		Space space = sw.getSpace();
		while(true) {
			RuleEvaluation<Object[]> re = rule.query(space,fields);
			if(re.getValue()==null) {
				wait();
			}
			return re.getValue();
		}
	}

	public Object[] queryp(SpaceWrapper sw, TemplateField[] fields) throws InterruptedException {
		Space space = sw.getSpace();
		RuleEvaluation<Object[]> re = rule.queryp(space,fields);
		return re.getValue();
	}

	public List<Object[]> queryAll(SpaceWrapper sw, TemplateField[] fields) throws InterruptedException {
		Space space = sw.getSpace();
		 RuleEvaluation<List<Object[]>> re = rule.queryAll(space,fields);
		  return re.getValue();
	}
	
	

}
