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
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import org.jspace.gate.ClientGate;
import org.jspace.gate.GateFactory;
import org.jspace.protocol.ClientMessage;
import org.jspace.protocol.ServerMessage;

/**
 * @author loreti
 *
 */
public class RemoteSpace implements Space {
	
	private final URI uri;
	private final ClientGate gate;

	public RemoteSpace( URI uri ) throws UnknownHostException, IOException {
		this.uri = uri;
		this.gate = GateFactory.getInstance().getGateBuilder( uri.getScheme() ).createClientGate(uri);
		this.gate.open();
	}
	

	public RemoteSpace(String uri) throws UnknownHostException, IOException {
		this(URI.create(uri));
	}


	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean put(Tuple t) {		
		ServerMessage response;
		try {
			response = gate.send(ClientMessage.putRequest(t));
		} catch (IOException e) {
			// TODO: use log
			e.printStackTrace();
			return false;
		}
		return response.isSuccessful();
	}

	@Override
	public Tuple get(Template template) throws InterruptedException {
		return _get(template,true);
	}

	@Override
	public Tuple getp(Template template) throws InterruptedException {
		return _get(template,false);
	}

	private Tuple _get(Template template, boolean isBlocking) {
		ServerMessage response;
		try {
			response = gate.send(ClientMessage.getRequest(template,isBlocking,false));
		} catch (IOException e) {
			// TODO: Use log
			e.printStackTrace();
			return null;
		}
		if (response.isSuccessful()) {
			Tuple[] tuples = response.getTuples();
			if (tuples.length==0) {
				return null;
			}
			return tuples[0];
		}
		return null;
	}

	@Override
	public List<Tuple> getAll(Template template) throws InterruptedException {
		ServerMessage response;
		try {
			response = gate.send(ClientMessage.getRequest(template,false,true));
		} catch (IOException e) {
			// TODO: Use log
			e.printStackTrace();
			return null;
		}
		if (response.isSuccessful()) {
			return Arrays.asList(response.getTuples());
		} 
		return null;		
	}


	@Override
	public Tuple query(Template template) throws InterruptedException {
		return _query(template, true);
	}

	@Override
	public Tuple queryp(Template template) throws InterruptedException {
		return _query(template, false);
	}

	private Tuple _query(Template template, boolean isBlocking) {
		ServerMessage response;
		try {
			response = gate.send(ClientMessage.queryRequest(template,isBlocking,false));
		} catch (IOException e) {
			// TODO: Use log
			e.printStackTrace();
			return null;
		}
		Tuple[] tuples = response.getTuples();
		if (tuples.length==0) {
			return null;
		}
		return tuples[0];
	}

	@Override
	public List<Tuple> queryAll(Template template) throws InterruptedException {
		ServerMessage response;
		try {
			response = gate.send(ClientMessage.queryRequest(template,false,true));
		} catch (IOException e) {
			// TODO: Use log
			e.printStackTrace();
			return null;
		}
		if (response.isSuccessful()) {
			return Arrays.asList(response.getTuples());
		} 
		return null;
	}


	public URI getUri() {
		return uri;
	}


	public ClientGate getGate() {
		return gate;
	}

//	@Override
//	public Space map(Function<Tuple, Tuple> f) throws InterruptedException {
//		// TODO Auto-generated method stub
//		return null;
//	}
//
//	@Override
//	public <T1> T1 reduce(BiFunction<Tuple, T1, T1> f, Comparator<Tuple> comp, T1 v) throws InterruptedException {
//		// TODO Auto-generated method stub
//		return null;
//	}

}
