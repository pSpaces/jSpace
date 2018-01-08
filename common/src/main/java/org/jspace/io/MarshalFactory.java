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

package org.jspace.io;

import java.util.HashMap;

/**
 * @author loreti
 *
 */
public class MarshalFactory {
	
	public final static String LANGUAGE_PARAMETER = "lang";
	
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
