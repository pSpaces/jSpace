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
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	private Map<Long, ClientGate> threadSpaceMap;

	public RemoteSpace( URI uri ) throws UnknownHostException, IOException {
		this.uri = uri;
		ClientGate gate = GateFactory.getInstance().getGateBuilder( uri.getScheme() ).createClientGate(uri);
		gate.open();

		this.threadSpaceMap = new HashMap<>();
		this.threadSpaceMap.put(Thread.currentThread().getId(), gate);
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
	public boolean put(Object ... fields) throws InterruptedException {
		ServerMessage response;
		try {
			response = getGate().send(ClientMessage.putRequest(new Tuple(fields)));
		} catch (IOException e) {
			//TODO: Replace with a specific exception
			throw new InterruptedException(e.getMessage());
		}
		return response.isSuccessful();
	}

	@Override
	public Object[] get(TemplateField ... fields) throws InterruptedException {
		return _get(new Template(fields),true);
	}

	@Override
	public Object[]  getp(TemplateField ... fields) throws InterruptedException {
		return _get(new Template(fields),false);
	}

	private Object[] _get(Template template, boolean isBlocking) throws InterruptedException {
		ServerMessage response;
		try {
			response = getGate().send(ClientMessage.getRequest(template,isBlocking,false));
		} catch (IOException e) {
			//TODO: Replace with a specific exception
			throw new InterruptedException(e.getMessage());
		}
		if (response.isSuccessful()) {
			List<Object[]> tuples = response.getTuples();
			if (tuples.size()==0) {
				return null;
			}
			return tuples.get(0);
		}
		return null;
	}

	@Override
	public List<Object[]> getAll(TemplateField ... fields) throws InterruptedException {
		ServerMessage response;
		try {
			response = getGate().send(ClientMessage.getRequest(new Template(fields),false,true));
		} catch (IOException e) {
			//TODO: Replace with a specific exception
			throw new InterruptedException(e.getMessage());
		}
		if (response.isSuccessful()) {
			return response.getTuples();
		}
		return null;
	}


	@Override
	public Object[] query(TemplateField ... fields) throws InterruptedException {
		return _query(new Template(fields), true);
	}

	@Override
	public Object[] queryp(TemplateField ... fields) throws InterruptedException {
		return _query(new Template(fields), false);
	}

	private Object[] _query(Template template, boolean isBlocking) throws InterruptedException {
		ServerMessage response;
		try {
			response = getGate().send(ClientMessage.queryRequest(template,isBlocking,false));
		} catch (IOException e) {
			throw new InterruptedException(e.getMessage());
		}
		if (response.isSuccessful()) {
			List<Object[]> tuples = response.getTuples();
			if (tuples.size()==0) {
				return null;
			}
			return tuples.get(0);
		}
		return null;
	}

	@Override
	public List<Object[]> queryAll(TemplateField ... fields) throws InterruptedException {
		ServerMessage response;
		try {
			response = getGate().send(ClientMessage.queryRequest(new Template(fields),false,true));
		} catch (IOException e) {
			//TODO: Replace with a specific exception
			throw new InterruptedException(e.getMessage());
		}
		if (response.isSuccessful()) {
			return response.getTuples();
		}
		return null;
	}


	public URI getUri() {
		return uri;
	}

	private ClientGate getGate() {
		long threadID = Thread.currentThread().getId();
		if (!this.threadSpaceMap.containsKey(threadID)) {
			ClientGate gate = null;
			try {
				gate = openNewGate();
			} catch (IOException e) {
				// TODO: Use log
				e.printStackTrace();
			}
			this.threadSpaceMap.put(threadID, gate);
		}
		return this.threadSpaceMap.get(threadID);
	}

	private ClientGate openNewGate() throws IOException {
		ClientGate gate = GateFactory.getInstance().getGateBuilder( uri.getScheme() ).createClientGate(uri);
		gate.open();
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
