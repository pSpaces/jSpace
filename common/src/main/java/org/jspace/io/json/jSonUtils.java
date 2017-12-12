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

package org.jspace.io.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Template;
import org.jspace.TemplateField;
import org.jspace.Tuple;
import org.jspace.io.ClassDictionary;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 *
 */
public class jSonUtils {
	
	/**
	 * Json tag indicating if a template field is a formal or an actual.
	 */
	public static final String FORMAL_ID = "formal";

	/**
	 * Json tag associated to string referring to {@link Class} names.
	 */
	public static final String TYPE_ID = "type";

	/**
	 * Json tag associated to generic objects. The type of the specific object
	 * is determined by relying on attribute {@link jSonUtils#TYPE_ID}
	 */
	public static final String VALUE_ID = "value";

	
	private static jSonUtils instance;
	
	private final GsonBuilder builder;
	
	private final ClassDictionary dicionary;
	
	private jSonUtils() {
		this.builder = new GsonBuilder();
		this.dicionary = new ClassDictionary();
		init();
	}
	
	private void init() {
		builder.registerTypeAdapter(Tuple.class, new TupleSerializer());
		builder.registerTypeAdapter(Tuple.class, new TupleDeserializer());
		builder.registerTypeAdapter(Template.class, new TemplateSerializer());
		builder.registerTypeAdapter(Template.class, new TemplateDeserializer());
	}

	public static jSonUtils getInstance() {
		if (instance == null) {
			instance = new jSonUtils();
		}
		return instance;
	}
	
	public String toString(Object o) {
		Gson gson = builder.create();		
		return gson.toJson(o);
	}
	
	public byte[] toByte(Object o) {
		return toString(o).getBytes();
	}

	public <T> T fromByte(Class<T> clazz, byte[] data) {
		return fromString(clazz, new String(data));
	}

	public <T> T fromString(Class<T> clazz, String message) {
		Gson gson = builder.create();		
		return gson.fromJson(message, clazz);
	}

	public void write( PrintWriter w, Object o ) {
		String message = toString(o);
		w.println(message);
		w.flush();
	}
	
	public <T> T read( BufferedReader r, Class<T> clazz ) throws IOException {
		String message = r.readLine();
		return fromString( clazz , message );
	}
	
	
	/**
	 * Serialize an object into a {@link JsonElement}. The object is rendered as
	 * a {@link JsonObject} containing two attributes:
	 * <ul>
	 * <li><code>type</code>, containing a string with the fully qualified name
	 * of the serialized object;
	 * <li><code>value</code>, containing the {@link JsonElement} associated to
	 * the serialized object.
	 * </ul>
	 * 
	 * When the object will be deserialized, the first attribute will be used to
	 * identify the object class, while the second one will be used to retrieve
	 * object status.
	 * 
	 * 
	 * @param o
	 *            object to serialize
	 * @param context
	 *            Context for serialization
	 * @return a json representation of o
	 */
	public  JsonElement jsonFromObject(Object o, JsonSerializationContext context) {
		JsonObject json = new JsonObject();
		json.add(jSonUtils.TYPE_ID, new JsonPrimitive(dicionary.getURI(o.getClass())));
		json.add(jSonUtils.VALUE_ID, context.serialize(o));
		return json;
	}

	/**
	 * Deserialize an object from a {@link JsonElement}. We assume that the
	 * received JsonElement is a {@link JsonObject} providing two attributes:
	 * <ul>
	 * <li><code>type</code>, containing a string with the fully qualified name
	 * of the serialized object;
	 * <li><code>value</code>, containing the {@link JsonElement} associated to
	 * the serialized object.
	 * </ul>
	 * 
	 * 
	 * @param json
	 *            element to deserialize
	 * @param context
	 *            context for serialization
	 * @return the object represented by json
	 */
	public Object objectFromJson(JsonElement json, JsonDeserializationContext context) {
		if (!json.isJsonObject()) {
			throw new JsonParseException("Unexpected JsonElement!");
		}
		JsonObject jo = (JsonObject) json;
		if ((!jo.has("type")) || (!jo.has("value"))) {
			throw new JsonParseException("Required attributes are not available!");
		}
		String uri = jo.get(TYPE_ID).getAsString();
		try {
			Class<?> c = dicionary.getClass(uri);
			return context.deserialize(jo.get(VALUE_ID), c);
		} catch (ClassNotFoundException e) {
			throw new JsonParseException(e);
		}
	}
	
	
	public JsonElement jsonFromTeplate( TemplateField field, JsonSerializationContext context ) {
		if (field instanceof ActualField) {
			ActualField af = (ActualField) field;
			JsonObject json = new JsonObject();
			json.add(jSonUtils.FORMAL_ID, new JsonPrimitive(false));
			json.add(jSonUtils.VALUE_ID, jsonFromObject(af.getValue(), context));
			return json;
		} else {
			FormalField ff = (FormalField) field;
			JsonObject json = new JsonObject();
			json.add(jSonUtils.FORMAL_ID, new JsonPrimitive(true));
			json.add(jSonUtils.VALUE_ID, new JsonPrimitive(dicionary.getURI(ff.getFormalFieldType())));
			return json;
		}
	}
	
	public TemplateField templateFromJSon( JsonElement json, JsonDeserializationContext context ) {
		if (!json.isJsonObject()) {
			throw new JsonParseException("Unexpected JsonElement!");
		}
		JsonObject jo = (JsonObject) json;
		if ((!jo.has(jSonUtils.FORMAL_ID)) || (!jo.has(jSonUtils.VALUE_ID))) {
			throw new JsonParseException("Required attributes are not available!");
		}
		boolean isFormal = jo.get(FORMAL_ID).getAsBoolean();
		if (isFormal) {
			try {
				return new FormalField(dicionary.getClass(jo.get(VALUE_ID).getAsString()));
			} catch (ClassNotFoundException e) {
				throw new JsonParseException(e);
			}
		} else {
			return new ActualField(objectFromJson(jo.get(VALUE_ID), context));
		}
	}	
	
	public void register( String uri , Class<?> clazz ) {
		this.register(uri, clazz,null,null);
	}
	
	public <T> void register( String uri , Class<T> clazz , JsonSerializer<T> serializer , JsonDeserializer<T> deserializer ) {
		this.dicionary.register(uri, clazz);
		if (serializer != null) {
			builder.registerTypeAdapter(clazz, serializer);
		}
		if (deserializer != null) {
			builder.registerTypeAdapter(clazz, deserializer);
		}
	}

	public Gson getGson() {
		return builder.create();
	}


}
