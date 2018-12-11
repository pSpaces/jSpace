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

import java.lang.reflect.Type;

import org.jspace.Tuple;
import org.jspace.Template;
import org.jspace.protocol.DataProperties;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/**
 * This class is used to deserialize an {@link Tuple} from a {@link JsonElement}
 * (see {@link JsonDeserializer}).
 */
public class DataPropertiesDeserializer implements JsonDeserializer<DataProperties> {
	@Override
	public DataProperties deserialize(JsonElement json, Type typeOfT,
            JsonDeserializationContext context) throws JsonParseException {

        String type = json.getAsJsonObject().get("type").getAsString();
        JsonElement value = json.getAsJsonObject().get("value");

		if (!value.isJsonArray()) {
			throw new JsonParseException("Unexpected JsonElement!");
		}

		JsonArray jsa = (JsonArray) value;
		Object[] data = new Object[jsa.size()];
		jSonUtils util = jSonUtils.getInstance();

        if (type.equals("tuple")) {
            for (int i = 0; i < jsa.size(); i++) {
                data[i] = util.objectFromJson(jsa.get(i), context);
            }
            Tuple tuple = new Tuple(data);
            return new DataProperties(type, new Tuple(data));
        } else if (type.equals("tuples")) {
            for (int i = 0; i < jsa.size(); i++) {
                data[i] = util.objectFromJson(jsa.get(i), context);
            }
            return null;
        } else { // template
            for (int i = 0; i < jsa.size(); i++) {
                data[i] = util.templateFromJSon(jsa.get(i), context);
            }

            Template t = new Template(data);
            return new DataProperties(type, t);
        }
	}
}
