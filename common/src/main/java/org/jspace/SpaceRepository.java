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
					if (message != null) {
						handler.send(handle(message));					
					}
				} catch (IOException e) {
					e.printStackTrace();
					message = null;
				} catch (InterruptedException e) {
					e.printStackTrace();
					message = null;
				}
			}
			try {
				handler.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	private ServerMessage handle(ClientMessage message) throws InterruptedException {
		switch (message.getMessageType()) {
		case PUT_REQUEST:
			return ServerMessage.putResponse( put( message.getTarget() , message.getTuple().getTuple() ) , message.getClientSession() );
		case GET_REQUEST:
			return handleGetRequest( message );
		case QUERY_REQUEST:
			return handleQueryRequest( message );
		default:
			return ServerMessage.badRequest(message.getClientSession());
		}
	}

	private ServerMessage handleQueryRequest(ClientMessage message) {
		Template template = message.getTemplate();
		String target = message.getTarget();
		if ((template == null)||(target == null)) {
			return ServerMessage.badRequest(message.getClientSession());
		}
		List<Object[]> tuples;
		try {
			tuples = query( message.getTemplate() , message.isBlocking(), message.getAll(), message.getTarget() );
			if (tuples != null) {
				return ServerMessage.getResponse(tuples,message.getClientSession() );
			} else {
				return ServerMessage.badRequest(message.getClientSession());
			}
		} catch (InterruptedException e) {
			return ServerMessage.internalServerError();
		}				
	}

	private ServerMessage handleGetRequest(ClientMessage message) {
		Template template = message.getTemplate();
		String target = message.getTarget();
		if ((template == null)||(target == null)) {
			return ServerMessage.badRequest(message.getClientSession());
		}
		try {
			List<Object[]> tuples = get( message.getTemplate() , message.isBlocking(), message.getAll(), message.getTarget() );				
			if (tuples != null) {
				return ServerMessage.getResponse(tuples,message.getClientSession() );
			} else {
				return ServerMessage.badRequest(message.getClientSession());
			}
		} catch (InterruptedException e) {
			return ServerMessage.internalServerError();
		}				
	}

	private List<Object[]> get(Template template, boolean blocking, boolean all, String target) throws InterruptedException {
		Space space = spaces.get(target);
		if (space == null) {
			return null;
		}
		if (all) {
			return space.getAll(template.getFields());
		}
		LinkedList<Object[]> result = new LinkedList<>();
		Object[] t;
		if (blocking) {
			t = space.get(template.getFields());
		} else {
			t = space.getp(template.getFields());
		}
		result.add(t);
		return result;
	}

	private List<Object[]> query(Template template, boolean blocking, boolean all, String target) throws InterruptedException {
		Space space = spaces.get(target);
		if (space == null) {
			return null;
		}
		if (all) {
			return space.queryAll(template.getFields());
		}
		LinkedList<Object[]> result = new LinkedList<>();
		Object[] t;
		if (blocking) {
			t = space.query(template.getFields());
		} else {
			t = space.queryp(template.getFields());
		}
		result.add(t);
		return result;
	}

	public boolean put(String target, Object ... fields) throws InterruptedException {
		if ((fields == null)||(target == null)) {
			return false;
		}
		Space space = spaces.get(target);
		if (space == null) {
			return false;
		}
		return space.put(fields);
	}
	
	@Override
	protected void finalize() throws Throwable {
		for (ServerGate g : gates) {
			g.close();
		}
		super.finalize();
	}

	
	
	

}
