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
import java.lang.InterruptedException;
import java.net.UnknownHostException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;

import org.jspace.Tuple;

import org.jspace.gate.GateFactory;
import org.jspace.gate.ClientGate;

import org.jspace.protocol.Message;
import org.jspace.protocol.SpaceType;
import org.jspace.protocol.MessageType;
import org.jspace.protocol.pSpaceMessage;
import org.jspace.protocol.ManagementMessage;
import org.jspace.protocol.SpaceProperties;
import org.jspace.protocol.SpaceKeys;
import org.jspace.protocol.RepositoryProperties;
import org.jspace.protocol.DataProperties;

/**
 * A repository is a container for a group of spaces each of which is identified by a name.
 * Spaces in a repository can be accessed either locally or remotely. 
 */
public class SpaceRepository {
	private final HashMap<String, Space> spaces = new HashMap<String, Space>();
	private ClientGate gate;
    private String name;
    private String key;
    private URI uri;

	/**
	 * Creates a new respository.
	 */
	public SpaceRepository(String name, String key, URI uri)
            throws UnknownHostException, IOException {
        this.name = name;
        this.key = key;
        this.uri = uri;
		this.gate = GateFactory.getInstance().getGateBuilder(uri.getScheme())
            .createClientGate(uri, pSpaceMessage.class);
        this.gate.open();
	}

    public NamedSpace newSpace(String name, SpaceType type, SpaceKeys keys)
            throws InterruptedException {
        // FIXME should return some sort of space reference
        ManagementMessage response;
        Message request = new ManagementMessage(
                MessageType.CREATE_SPACE,
                "", // serverkey
                new RepositoryProperties(
                    this.name,
                    this.key),
                new SpaceProperties(
                    type,
                    name,
                    null,
                    keys),
                null, // status
                null // session
        );

        // TODO it should be possible to create a common handler for response
        // codes. Could it return an object?
        try {
            ServerConnection conn = ServerConnection.getInstance(this.uri);
            response = (ManagementMessage) conn.getGate().send(request);
            if (response.getStatus().getCode() == 400) {
                //throw new AuthenticationException();
                System.out.println("[Status 400]: Credentials are incorrect");
                return null;
            } else if (response.getStatus().getCode() == 500) {
                //throw new InternalServerErrorException();
                System.out.println("[Status 500]: Something bad happened with the server");
                return null;
            } else if (response.getStatus().getCode() == 200) {
                SpaceProperties props = response.getSpace();
                props.setKeys(keys);
                return new NamedSpace(this, props);
            }

            // unknown response code
            System.out.println("[UNKNOWN] The system does not know how to "
                    + "handle a response with code "
                    + response.getStatus().getCode());
            return null;


		} catch (IOException e) {
			//TODO: Replace with a specific exception
			throw new InterruptedException();
		}
    }

    public boolean put(SpaceProperties properties, Object ... fields)
            throws InterruptedException {
        pSpaceMessage response;
        Message request = new pSpaceMessage(
                MessageType.PUT, // operation
                null, // session
                properties, // target
                new DataProperties("tuple", new Tuple(fields)), // data
                null // status
        );

        try {
            response = (pSpaceMessage) gate.send(request);
            if (response.getStatus().getCode() == 400) {
                //throw new AuthenticationException();
                System.out.println("[Status 400]: Credentials are incorrect");
                return false;
            } else if (response.getStatus().getCode() == 500) {
                //throw new InternalServerErrorException();
                System.out.println("[Status 500]: Something bad happened with the server");
                return false;
            }

            if (response.getStatus().getCode() == 200) {
                return true;
            }

            return false;
		} catch (IOException e) {
			//TODO: Replace with a specific exception
			throw new InterruptedException();
		}
    }

    private List<Object[]> _getAll(boolean destructive, SpaceProperties properties,
            TemplateField ... fields) throws InterruptedException {
        pSpaceMessage response;

        MessageType type;
        if (destructive) { // 'get' ops
            type = MessageType.GETALL;
        } else { // 'query' ops
            type = MessageType.QUERYALL;
        }

        Message request = new pSpaceMessage(
                type, // operation
                null, // session
                properties, // target
                new DataProperties("template", new Template(fields)), // data
                null // status
        );

        try {
            response = (pSpaceMessage) gate.send(request);
            if (response.getStatus().getCode() == 400) {
                //throw new AuthenticationException();
                System.out.println("[Status 400]: Credentials are incorrect");
                return null;
            } else if (response.getStatus().getCode() == 500) {
                //throw new InternalServerErrorException();
                System.out.println("[Status 500]: Something bad happened with the server");
                return null;
            }

            if (response.getStatus().getCode() == 200) {
                return null; // FIXME return obj
            }

            return null;
		} catch (IOException e) {
			//TODO: Replace with a specific exception
			throw new InterruptedException();
		}
    }

    private Object[] _get(boolean destructive, boolean blocking,
            SpaceProperties properties, TemplateField ... fields)
            throws InterruptedException {
        pSpaceMessage response;

        MessageType type;
        if (destructive) { // 'get' ops
            if (blocking) {
                type = MessageType.GETP;
            } else {
                type = MessageType.GET;
            }
        } else { // 'query' ops
            if (blocking) {
                type = MessageType.QUERYP;
            } else {
                type = MessageType.QUERY;
            }
        }

        Message request = new pSpaceMessage(
                type, // operation
                null, // session
                properties, // target
                new DataProperties("template", new Template(fields)), // data
                null // status
        );

        try {
            response = (pSpaceMessage) gate.send(request);
            if (response.getStatus().getCode() == 400) {
                //throw new AuthenticationException();
                System.out.println("[Status 400]: Credentials are incorrect");
                return null;
            } else if (response.getStatus().getCode() == 500) {
                //throw new InternalServerErrorException();
                System.out.println("[Status 500]: Something bad happened with the server");
                return null;
            }

            if (response.getStatus().getCode() == 200) {
                Tuple t = (Tuple) response.getData().getValue();
                return t.getTuple();
            }

            return null;
		} catch (IOException e) {
			//TODO: Replace with a specific exception
			throw new InterruptedException();
		}
    }

    public Object[] get(SpaceProperties properties, TemplateField ... fields)
            throws InterruptedException {
        return _get(true, false, properties, fields);
    }

    public List<Object[]> getAll(SpaceProperties properties, TemplateField ... fields)
            throws InterruptedException {
        return _getAll(true, properties, fields);
    }

    public Object[] getp(SpaceProperties properties, TemplateField ... fields)
            throws InterruptedException {
        return _get(true, true, properties, fields);
    }

    public Object[] query(SpaceProperties properties, TemplateField ... fields)
            throws InterruptedException {
        return _get(false, false, properties, fields);
    }

    public Object[] queryp(SpaceProperties properties,
            TemplateField ... fields) throws InterruptedException {
        return _get(false, true, properties, fields);
    }

    public List<Object[]> queryAll(SpaceProperties properties,
            TemplateField ... fields) throws InterruptedException {
        return _getAll(false, properties, fields);
    }

//	/**
//	 * Returns true if the repository is empty.
//	 * 
//	 * @return true, if the repository is empty.
//	 */
//	public boolean isEmpty() {
//		return spaces.isEmpty();
//	}
//
//	/**
//	 * Returns the number of spaces in the repository.
//	 * 
//	 * @return the number of spaces in the repository.
//	 */
//	public int size() {
//	k
//		return spaces.size();
//	}
//
//	/**
//	 * Adds a new space named <code>name</code> to the repository.
//	 * 
//	 * @param name space name
//	 * @param space space added to the repository
//	 */
//	public synchronized void add(String name, Space space) {
//		if (spaces.containsKey(name)) {
//			throw new IllegalStateException("Name "+name+" is already used in the repository!");
//		}
//		spaces.put(name, space);
//	}
//
//	/**
//	 * Returns the space named <code>name</code> or null if this space does not exist.
//	 * 
//	 * @param name space name
//	 * @return the space named <code>name</code> or null if this space does not exist.
//	 */
//	public Space get(String name) {
//		return spaces.get(name);
//	}
//
//	/**
//	 * Removes the space named <code>name</code>. 
//	 * 
//	 * @param name the name of the space to remove
//	 * @return the space previously identified by <code>name</code>, 
//	 * null if no space is named <code>name</code>.
//	 */
//	public synchronized Space remove(String name) {
//		return spaces.remove(name);
//	}
//
//	public boolean addGate( String uri ) {
//		return this.addGate(URI.create(uri));
//	}
//
//	public boolean addGate( URI uri ) {
//		ServerGate gate = gateFactory.getGateBuilder(uri.getScheme())
//            .createServerGate(uri, ClientMessage.class);
//		return this.addGate(gate);
//	}
//
//	public synchronized boolean addGate(ServerGate gate) {
//		try {
//			gate.open();
//		} catch (IOException e) {
//			logException(e);
//			return false;
//		}
//		gates.add(gate);
//		executor.execute(() -> {
//			try {
//				while (!gate.isClosed()) {
//					ClientHandler handler = gate.accept();
//					if (handler != null) {
//						addHandler( handler );
//					}
//				}
//			} catch (SocketException e) {
//				logException(e);
//			} catch (IOException e) {
//				logException(e);
//				try {
//					gate.close();
//				} catch (IOException e1) {
//					logException(e1);
//				}
//			}
//		});
//		return true;
//	}
//
//	/**
//	 * Closes the gate represented by the specific uri, and terminates the underlying thread.
//	 * 
//	 * @param uri
//	 */
//    public void closeGate(String uri) {
//    		closeGate(URI.create(uri));
//    }
//
//	/**
//	 * Closes the gate represented by the specific uri, and terminates the underlying thread.
//	 * 
//	 * @param uri
//	 */
//	public void closeGate(URI uri) {
//		this.gates.stream()
//					.filter(g -> g.getURI().equals(uri))
//					.findFirst()
//					.ifPresent(g -> {
//						try {
//							this.gates.remove(g);
//							g.close();
//						} catch (IOException e) {
//							logException(e);
//						}
//					});
//	}
//
//	public void closeGate(ServerGate gate) {
//		if (gate == null) {
//			return ;
//		}
//		boolean flag = this.gates.remove(gate);
//		if (flag) {
//			try {
//				gate.close();
//			} catch (IOException e) {
//				logException(e);
//			}
//		}
//	}
//
//	private synchronized void addHandler(ClientHandler handler) {
//		handlers.add(handler);
//		executor.execute(() -> {
//			try {
//				while (handler.isActive()) {
//					ClientMessage message = (ClientMessage) handler.receive();
//					if (message != null) {
//						executor.execute(() -> {
//							try {
//								handler.send(handle(message));
//							} catch (InterruptedException e) {
//								handler.send(ServerMessage.internalServerError());
//							}
//						});
//					}
//				}
//			} catch (IOException e) {
//				logException(e);
//				try {
//					if (!handler.isClosed()) {
//						handler.close();
//					}
//				} catch (IOException e2) {
//					logException(e2);
//				}
//			}
//			removeHandler(handler);
//		});
//	}
//
//	private synchronized void removeHandler(ClientHandler handler) {
//		handlers.remove(handler);
//	}
//
//	private ServerMessage handle(ClientMessage message) throws InterruptedException {
//		switch (message.getMessageType()) {
//		case PUT_REQUEST:
//            System.out.println(message.toString());
//			//return ServerMessage.putResponse( put( message.getTarget() , message.getTuple().getTuple() ) , message.getClientSession() );
//			return ServerMessage.putResponse( put( message.getTarget() , message.getTuple().getTuple() ) , message.getSession() );
//		case GET_REQUEST:
//			return handleGetRequest( message );
//		case QUERY_REQUEST:
//			return handleQueryRequest( message );
//		default:
//			//return ServerMessage.badRequest(message.getClientSession());
//			return ServerMessage.badRequest(message.getSession());
//		}
//	}
//
//	private ServerMessage handleQueryRequest(ClientMessage message) {
//		Template template = message.getTemplate();
//		String target = message.getTarget();
//        System.out.println(message.toString());
//		if ((template == null)||(target == null)) {
//			//return ServerMessage.badRequest(message.getClientSession());
//			return ServerMessage.badRequest(message.getSession());
//		}
//		List<Object[]> tuples;
//		try {
//			tuples = query( message.getTemplate() , message.isBlocking(), message.getAll(), message.getTarget() );
//			if (tuples != null) {
//				//return ServerMessage.getResponse(tuples,message.getClientSession() );
//				return ServerMessage.getResponse(tuples,message.getSession() );
//			} else {
//				//return ServerMessage.badRequest(message.getClientSession());
//				return ServerMessage.badRequest(message.getSession());
//			}
//		} catch (InterruptedException e) {
//			return ServerMessage.internalServerError();
//		}
//	}
//
//	private ServerMessage handleGetRequest(ClientMessage message) {
//		Template template = message.getTemplate();
//		String target = message.getTarget();
//        System.out.println(message.toString());
//		if ((template == null)||(target == null)) {
//			//return ServerMessage.badRequest(message.getClientSession());
//			return ServerMessage.badRequest(message.getSession());
//		}
//		try {
//			List<Object[]> tuples = get( message.getTemplate() , message.isBlocking(), message.getAll(), message.getTarget() );
//			if (tuples != null) {
//				//return ServerMessage.getResponse(tuples,message.getClientSession() );
//				return ServerMessage.getResponse(tuples,message.getSession() );
//			} else {
//				//return ServerMessage.badRequest(message.getClientSession());
//				return ServerMessage.badRequest(message.getSession());
//			}
//		} catch (InterruptedException e) {
//			return ServerMessage.internalServerError();
//		}
//	}
//
//	private List<Object[]> get(Template template, boolean blocking, boolean all, String target) throws InterruptedException {
//		Space space = spaces.get(target);
//		if (space == null) {
//			return null;
//		}
//		if (all) {
//			return space.getAll(template.getFields());
//		}
//		LinkedList<Object[]> result = new LinkedList<>();
//		Object[] t;
//		if (blocking) {
//			t = space.get(template.getFields());
//		} else {
//			t = space.getp(template.getFields());
//		}
//		if (t != null) {
//		    result.add(t);
//		}
//		return result;
//	}
//
//	private List<Object[]> query(Template template, boolean blocking, boolean all, String target) throws InterruptedException {
//		Space space = spaces.get(target);
//		if (space == null) {
//			return null;
//		}
//		if (all) {
//			return space.queryAll(template.getFields());
//		}
//		LinkedList<Object[]> result = new LinkedList<>();
//		Object[] t;
//		if (blocking) {
//			t = space.query(template.getFields());
//		} else {
//			t = space.queryp(template.getFields());
//		}
//		if (t != null) {
//			result.add(t);
//		}
//		return result;
//	}
//
//	/**
//	 * Adds a tuple in the space.
//	 * 
//	 * @param target target space
//	 * @param fields fields fields of inserted tuple
//	 * @return true if the action has been successfully executed false otherwise.
//	 * @throws InterruptedException if any thread interrupted the current thread before 
//	 * the action is executed.
//	 */
//	public boolean put(String target, Object ... fields) throws InterruptedException {
//		if ((fields == null)||(target == null)) {
//			return false;
//		}
//		Space space;
//		synchronized (this) {
//			space = spaces.get(target);
//		}
//		if (space == null) {
//			return false;
//		}
//		return space.put(fields);
//	}
//
//	@Override
//	protected void finalize() throws Throwable {
//		this.closeGates();
//	}
//
//	public void closeGates() {
//		for (ServerGate g : gates) {
//			try {
//				g.close();
//			} catch (IOException e) {
//				logException(e);
//			}
//		}
//		this.gates = new LinkedList<>();
//	}
//
//
//	public synchronized void shutDown() {
//		this.closeGates();
//		for (ClientHandler handler : handlers) {
//			try {
//				if (!handler.isClosed()) {
//					handler.close();
//				}
//			} catch (IOException e) {
//				logException(e);
//			}
//		}
//		this.handlers = new LinkedList<>();
//		this.executor.shutdownNow();
//	}
//
//
//	private void logException( Exception e ) {
//		if (logger != null) {
//			logger.logException(e);
//		}
//	}
//
//	public void setExceptionLogger( ExceptionLogger logger ) {
//		this.logger = logger;
//	}
}
