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
package org.jspace.gate;

import java.net.URI;
import java.util.HashMap;
import java.util.function.Function;

import org.jspace.io.MarshalFactory;

/**
 * @author loreti
 *
 */
public class GateFactory {
	
	public final static String LANGUAGE_QUERY_ELEMENT = "lang";
	public final static String MODE_QUERY_ELEMENT = "mode";
	
	public final static String TCP_PROTOCOL = "tcp";			
	public final static String UDP_PROTOCOL = "udp";			
	public final static String HTTP_PROTOCOL = "http";			
	public final static String HTTPS_PROTOCOL = "https";
	private static GateFactory instance;				
	
	private HashMap<String,GateBuilder> gateBuilders; 
	
	private GateFactory( ) {
		this.gateBuilders = new HashMap<>();
		init();
	}

	private void init() {
		this.gateBuilders.put(TCP_PROTOCOL, new TcpGateBuilder());
		this.gateBuilders.put(UDP_PROTOCOL, new UdpGateBuilder());
	}
	
	public static HashMap<String,String> parseQuery(String query) {
		String[] elements = query.split("&");
		HashMap<String,String> values = new HashMap<>();
		for (String string : elements) {
			String[] pair = string.split("=");
			if (pair.length>1) {
				values.put(pair[0], pair[1]);
			} else {
				values.put(pair[0], "");
			}
		}
		return values;		
	}

	public static GateFactory getInstance() {
		if (instance == null) {
			instance = new GateFactory();
		}
		return instance;
	}

	public GateBuilder getGateBuilder(String scheme) {
		return gateBuilders.get(scheme);
	}
	
	public void register( String scheme, GateBuilder builder) {
		gateBuilders.put(scheme, builder);
	}
}
