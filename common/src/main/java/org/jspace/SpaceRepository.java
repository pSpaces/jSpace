/**
 * 
 * jSpace: a Java Framework for Programming Concurrent and Distributed Applications with Spaces
 * 
 * http://pspace.github.io/jSpace/	
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * 
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *      Alberto Lluch Lafuente
 *      Michele Loreti
 *      Francesco Terrosi
 */
package org.jspace;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.jspace.gate.ClientHandler;
import org.jspace.gate.GateFactory;
import org.jspace.gate.ServerGate;
import org.jspace.protocol.ClientMessage;
import org.jspace.protocol.ServerMessage;

/**
 * A repository is a container for a group of spaces each of which is identified by a name.
 * Spaces in a repository can be accessed either locally or remotely. 
 */
public class SpaceRepository {
	
	private final HashMap<String,Space> spaces = new HashMap<String, Space>();
	private ExecutorService executor = Executors.newCachedThreadPool();
	private GateFactory gateFactory;
	private LinkedList<ServerGate> gates = new LinkedList<>();
	
	/**
	 * Creates a new respository.
	 */
	public SpaceRepository() {
		this.gateFactory = GateFactory.getInstance();
	}
	
	/**
	 * Returns true if the repository is empty.
	 * 
	 * @return true, if the repository is empty.
	 */
	public boolean isEmpty() {
		return spaces.isEmpty();
	}

	/**
	 * Returns the number of spaces in the repository.
	 * 
	 * @return the number of spaces in the repository.
	 */
	public int size() {
		return spaces.size();	
	}

	/**
	 * Adds a new space named <code>name</code> to the repository.
	 * 
	 * @param name space name
	 * @param space space added to the repository
	 */
	public void add(String name, Space space) {
		if (spaces.containsKey(name)) {
			throw new IllegalStateException("Name "+name+" is already used in the repository!");
		}
		spaces.put(name, space);
	}

	/**
	 * Returns the space named <code>name</code> or null if this space does not exist.
	 * 
	 * @param name space name
	 * @return the space named <code>name</code> or null if this space does not exist.
	 */
	public Space get(String name) {
		return spaces.get(name);
	}

	/**
	 * Removes the space named <code>name</code>. 
	 * 
	 * @param name the name of the space to remove
	 * @return the space previously identified by <code>name</code>, 
	 * null if no space is named <code>name</code>.
	 */
	public Space remove(String name) {
		return spaces.remove(name);
	}
	
	public void addGate( String uri ) {
		this.addGate(URI.create(uri));
	}
	
	public void addGate( URI uri ) {
		ServerGate gate = gateFactory.getGateBuilder(uri.getScheme()).createServerGate(uri);
		this.addGate(gate);
	}
	
	public void addGate( ServerGate gate ) {
		gates.add(gate);
		executor.execute(() -> {
			try {
				gate.open();
				while (true) {
					ClientHandler handler = gate.accept();
					addHandler( handler );
				}
			} catch (IOException e) {
				e.printStackTrace();
				try {
					gate.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}

	private void addHandler(ClientHandler handler) {
		executor.execute(() -> {
			while (handler.isActive()) {
				ClientMessage message;
				try {
					message = handler.receive();
				} catch (IOException e) {
					e.printStackTrace();
					message = null;
				}
				if (message != null) {
					handler.send(handle(message));					
				}
			}
			try {
				handler.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	private ServerMessage handle(ClientMessage message) {
		switch (message.getMessageType()) {
		case PUT_REQUEST:
			return ServerMessage.putResponce( put( message.getTuple() , message.getTarget() ) );
		case GET_REQUEST:
			return handleGetRequest( message );
		case QUERY_REQUEST:
			return handleQueryRequest( message );
		default:
			return ServerMessage.badRequest();
		}
	}

	private ServerMessage handleQueryRequest(ClientMessage message) {
		Template template = message.getTemplate();
		String target = message.getTarget();
		if ((template == null)||(target == null)) {
			return ServerMessage.badRequest();
		}
		Tuple[] tuples;
		try {
			tuples = query( message.getTemplate() , message.isBlocking(), message.getAll(), message.getTarget() );
			if (tuples != null) {
				return ServerMessage.getResponce(tuples);
			} else {
				return ServerMessage.badRequest();
			}
		} catch (InterruptedException e) {
			return ServerMessage.internalServerError();
		}				
	}

	private ServerMessage handleGetRequest(ClientMessage message) {
		Template template = message.getTemplate();
		String target = message.getTarget();
		if ((template == null)||(target == null)) {
			return ServerMessage.badRequest();
		}
		try {
			Tuple[] tuples = get( message.getTemplate() , message.isBlocking(), message.getAll(), message.getTarget() );				
			if (tuples != null) {
				return ServerMessage.getResponce(tuples);
			} else {
				return ServerMessage.badRequest();
			}
		} catch (InterruptedException e) {
			return ServerMessage.internalServerError();
		}				
	}

	private Tuple[] get(Template template, boolean blocking, boolean all, String target) throws InterruptedException {
		Space space = spaces.get(target);
		if (space == null) {
			return null;
		}
		if (all) {
			List<Tuple> tuples = space.getAll(template);
			return tuples.toArray(new Tuple[tuples.size()]);
		}
		Tuple t;
		if (blocking) {
			t = space.get(template);
		} else {
			t = space.getp(template);
		}
		return (t==null?new Tuple[] {}:new Tuple[] { t });
	}

	private Tuple[] query(Template template, boolean blocking, boolean all, String target) throws InterruptedException {
		Space space = spaces.get(target);
		if (space == null) {
			return null;
		}
		if (all) {
			List<Tuple> tuples = space.queryAll(template);
			return tuples.toArray(new Tuple[tuples.size()]);
		}
		Tuple t;
		if (blocking) {
			t = space.query(template);
		} else {
			t = space.queryp(template);
		}
		return (t==null?new Tuple[] {}:new Tuple[] { t });
	}

	public boolean put(Tuple tuple, String target) {
		if ((tuple == null)||(target == null)) {
			return false;
		}
		Space space = spaces.get(target);
		if (space == null) {
			return false;
		}
		return space.put(tuple);
	}
	
	@Override
	protected void finalize() throws Throwable {
		for (ServerGate g : gates) {
			g.close();
		}
		super.finalize();
	}

	
	
	

}
