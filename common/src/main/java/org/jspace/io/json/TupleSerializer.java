/**
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
package org.jspace.io.json;

import java.lang.reflect.Type;

import org.jspace.Tuple;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * This class is used to serialize a {@link Tuple} into a {@link JsonElement}
 * (see {@link JsonDeserializer}).
 */
public class TupleSerializer implements JsonSerializer<Tuple> {

	@Override
	public JsonElement serialize(Tuple src, Type typeOfSrc, JsonSerializationContext context) {
		JsonArray toReturn = new JsonArray();
		jSonUtils utils = jSonUtils.getInstance();
		for (Object o : src) {
			toReturn.add(utils.jsonFromObject(o, context));
		}
		return toReturn;
	}

}
