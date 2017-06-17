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
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * This class is used to deserialize an {@link Tuple} from a {@link JsonElement}
 * (see {@link JsonDeserializer}).
 */
public class TupleDeserializer implements JsonDeserializer<Tuple> {

	@Override
	public Tuple deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException {
		if (!json.isJsonArray()) {
			throw new JsonParseException("Unexpected JsonElement!");
		}
		JsonArray jsa = (JsonArray) json;
		Object[] data = new Object[jsa.size()];
		jSonUtils util = jSonUtils.getInstance();
		for (int i = 0; i < jsa.size(); i++) {
			data[i] = util.objectFromJson(jsa.get(i), context);
		}
		return new Tuple(data);
	}

}
