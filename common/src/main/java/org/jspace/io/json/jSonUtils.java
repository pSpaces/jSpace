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
package org.jspace.io.json;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.jspace.ActualTemplateField;
import org.jspace.FormalTemplateField;
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
		if (field instanceof ActualTemplateField) {
			ActualTemplateField af = (ActualTemplateField) field;
			JsonObject json = new JsonObject();
			json.add(jSonUtils.FORMAL_ID, new JsonPrimitive(false));
			json.add(jSonUtils.VALUE_ID, jsonFromObject(af.getValue(), context));
			return json;
		} else {
			FormalTemplateField ff = (FormalTemplateField) field;
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
				return new FormalTemplateField(dicionary.getClass(jo.get(VALUE_ID).getAsString()));
			} catch (ClassNotFoundException e) {
				throw new JsonParseException(e);
			}
		} else {
			return new ActualTemplateField(objectFromJson(jo.get(VALUE_ID), context));
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
