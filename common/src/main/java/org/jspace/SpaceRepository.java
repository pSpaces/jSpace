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

import java.io.IOException;
import java.net.SocketException;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.jspace.gate.ClientHandler;
import org.jspace.gate.ExceptionLogger;
import org.jspace.gate.GateFactory;
import org.jspace.gate.ServerGate;
import org.jspace.protocol.ClientMessage;
import org.jspace.protocol.ServerMessage;
import org.jspace.protocol.pSpaceMessage;

/**
 * A repository is a container for a group of spaces each of which is identified by a name.
 * Spaces in a repository can be accessed either locally or remotely. 
 */
public class SpaceRepository {
	
	private final HashMap<String,Space> spaces = new HashMap<String, Space>();
	private ExecutorService executor = Executors.newCachedThreadPool();
	private GateFactory gateFactory;
	private LinkedList<ServerGate> gates = new LinkedList<>();
	private LinkedList<ClientHandler> handlers = new LinkedList<>();
	private ExceptionLogger logger = null;
	
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
	public synchronized void add(String name, Space space) {
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
	public synchronized Space remove(String name) {
		return spaces.remove(name);
	}
	
	public boolean addGate( String uri ) {
		return this.addGate(URI.create(uri));
	}
	
	public boolean addGate( URI uri ) {
		ServerGate gate = gateFactory.getGateBuilder(uri.getScheme()).createServerGate(uri);
		return this.addGate(gate);
	}
	
	public synchronized boolean addGate( ServerGate gate ) {
		try {
			gate.open();
		} catch (IOException e) {
			logException(e);
			return false;
		}
		gates.add(gate);
		executor.execute(() -> {
			try {
				while (!gate.isClosed()) {
					ClientHandler handler = gate.accept();
					if (handler != null) {
						addHandler( handler );
					}
				}
			} catch (SocketException e) {
				logException(e);
			} catch (IOException e) {
				logException(e);
				try {
					gate.close();
				} catch (IOException e1) {
					logException(e1);
				}
			}
		});
		return true;
	}
	
	/**
	 * Closes the gate represented by the specific uri, and terminates the underlying thread.
	 * 
	 * @param uri
	 */
    public void closeGate(String uri) {
    		closeGate(URI.create(uri));
    }

	/**
	 * Closes the gate represented by the specific uri, and terminates the underlying thread.
	 * 
	 * @param uri
	 */
	public void closeGate(URI uri) {
		this.gates.stream()
					.filter(g -> g.getURI().equals(uri))
					.findFirst()
					.ifPresent(g -> {
						try {
							this.gates.remove(g);
							g.close();
						} catch (IOException e) {
							logException(e);
						}
					});
	}

	public void closeGate(ServerGate gate) {
		if (gate == null) {
			return ;
		}
		boolean flag = this.gates.remove(gate);
		if (flag) {
			try {
				gate.close();
			} catch (IOException e) {
				logException(e);
			}
		}
	}
	
	private synchronized void addHandler(ClientHandler handler) {
		handlers.add(handler);
		executor.execute(() -> {
			try {
				while (handler.isActive()) {
					ClientMessage message = handler.receive();
					if (message != null) {
						executor.execute(() -> {
							try {
								handler.send(handle(message));
							} catch (InterruptedException e) {
								handler.send(ServerMessage.internalServerError());
							}
						});					
					}
				}
			} catch (IOException e) {
				logException(e);
				try {
					if (!handler.isClosed()) {
						handler.close();
					}
				} catch (IOException e2) {
					logException(e2);
				}
			} 
			removeHandler(handler);
		});
	}
	
	private synchronized void removeHandler(ClientHandler handler) {
		handlers.remove(handler);
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
		if (t != null) {
		    result.add(t);
		}
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
		if (t != null) {
			result.add(t);
		}
		return result;
	}

	/**
	 * Adds a tuple in the space.
	 * 
	 * @param target target space
	 * @param fields fields fields of inserted tuple
	 * @return true if the action has been successfully executed false otherwise.
	 * @throws InterruptedException if any thread interrupted the current thread before 
	 * the action is executed.
	 */
	public boolean put(String target, Object ... fields) throws InterruptedException {
		if ((fields == null)||(target == null)) {
			return false;
		}
		Space space;
		synchronized (this) {
			space = spaces.get(target);			
		}
		if (space == null) {
			return false;
		}
		return space.put(fields);
	}
	
	@Override
	protected void finalize() throws Throwable {
		this.closeGates();
	}

	public void closeGates() {
		for (ServerGate g : gates) {
			try {
				g.close();
			} catch (IOException e) {
				logException(e);
			}
		}
		this.gates = new LinkedList<>();
	}

	
	public synchronized void shutDown() {
		this.closeGates();
		for (ClientHandler handler : handlers) {
			try {
				if (!handler.isClosed()) {
					handler.close();
				}
			} catch (IOException e) {
				logException(e);
			}
		}
		this.handlers = new LinkedList<>();
		this.executor.shutdownNow();
	}
	
	
	private void logException( Exception e ) {
		if (logger != null) {
			logger.logException(e);
		}
	}

	public void setExceptionLogger( ExceptionLogger logger ) {
		this.logger = logger;
	}
}
