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
