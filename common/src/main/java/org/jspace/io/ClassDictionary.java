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

import org.jspace.Template;
import org.jspace.Tuple;

/**
 *
 */
public class ClassDictionary {
	
	public static final String BOOLEAN_URI = "pspace:boolean";
	public static final String BYTE_URI = "pspace:byte";
	public static final String CHAR_URI = "pspace:char";
	public static final String INTEGER_URI = "pspace:int";
	public static final String LONG_URI = "pspace:long";
	public static final String FLOAT_URI = "pspace:float";
	public static final String DOUBLE_URI = "pspace:double";
	public static final String STRING_URI = "pspace:string";
	public static final String TUPLE_URI = "pspace:tuple";
	public static final String TEMPLATE_URI = "pspace:template";
	
	

	private final HashMap<String,Class<?>> uriToClass = new HashMap<>();
	private final HashMap<Class<?>,String> classToUri = new HashMap<>();
	
	public ClassDictionary() {
		init();
	}

	private void init() {
		uriToClass.put(BOOLEAN_URI, Boolean.class);
		uriToClass.put(BYTE_URI, Byte.class);
		uriToClass.put(CHAR_URI, Character.class);
		uriToClass.put(INTEGER_URI, Integer.class);
		uriToClass.put(LONG_URI, Long.class);
		uriToClass.put(FLOAT_URI, Float.class);
		uriToClass.put(DOUBLE_URI, Double.class);
		uriToClass.put(STRING_URI, String.class);
		uriToClass.put(TUPLE_URI, Tuple.class);
		uriToClass.put(TEMPLATE_URI, Template.class);
		
		classToUri.put(Boolean.class, BOOLEAN_URI);
		classToUri.put(Byte.class, BYTE_URI);
		classToUri.put(Character.class, CHAR_URI);
		classToUri.put(Integer.class, INTEGER_URI);
		classToUri.put(Long.class, LONG_URI);
		classToUri.put(Float.class, FLOAT_URI);
		classToUri.put(Double.class, DOUBLE_URI);
		classToUri.put(String.class, STRING_URI);	
		classToUri.put(Tuple.class, TUPLE_URI);
		classToUri.put(Template.class, TEMPLATE_URI);
	}
	
	public void register(String uri, Class<?> clazz) {
		if (uriToClass.containsKey(uri)) {
			throw new IllegalArgumentException("Duplicated uri!");
		}
		if (classToUri.containsKey(clazz)) {
			throw new IllegalArgumentException("Duplicated class!");
		}
	}
	
	public boolean isRegistered( String uri ) {
		return uriToClass.containsKey(uri);
	}
	
	public boolean isRegistered( Class<?> clazz ) {
		return classToUri.containsKey(clazz);
	}
	
	public Class<?> getClass( String uri ) throws ClassNotFoundException {
		Class<?> toReturn = uriToClass.get(uri);
		if (toReturn == null) {
			toReturn = Class.forName(uri);
		}
		return toReturn;
	}
	
	public String getURI( Class<?> clazz ) {
		String toReturn = classToUri.get(clazz);
		if (toReturn == null) {
			toReturn = clazz.getName();
		}
		return toReturn;
	}
}
