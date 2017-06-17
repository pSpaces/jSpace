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
package org.jspace.io;

import java.util.HashMap;

/**
 * @author loreti
 *
 */
public class MarshalFactory {
	
	public final static String LANGUAGE_PARAMETE = "lang";
	
	public final static String JSON_CODE = "json";

	public final static String DEFAULT_LANGAUGE = JSON_CODE;

	private final HashMap<String,Class<? extends jSpaceMarshaller>> table;

	private MarshalFactory() {
		this.table = new HashMap<>();
		init();
	}

	private void init() {
		table.put("json", JSonMarshaller.class); 
	}
	
	public jSpaceMarshaller getMarshaller( String code ) {
		try {
			return table.get(code).newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO: Add log info here!
			e.printStackTrace();
		}
		return null;
	}
	
	public void register( String code , Class<? extends jSpaceMarshaller> clazz ) {
		table.put(code, clazz);
	}
	
	private static MarshalFactory instance;
	
	public static MarshalFactory getInstance() {
		if (instance == null) {
			instance = new MarshalFactory();
		}
		return instance;
	}

	public jSpaceMarshaller getDeaultMarshaller() {
		return getMarshaller(DEFAULT_LANGAUGE);
	}
	
}
