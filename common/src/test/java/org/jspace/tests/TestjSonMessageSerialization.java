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
package org.jspace.tests;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;

import org.jspace.FormalField;
import org.jspace.Template;
import org.jspace.Tuple;
import org.jspace.io.json.jSonUtils;
import org.jspace.protocol.ClientMessage;
import org.jspace.protocol.ClientMessageType;
import org.jspace.protocol.InteractionMode;
import org.jspace.protocol.ServerMessage;
import org.jspace.protocol.ServerMessageType;
import org.junit.Test;

import com.google.gson.Gson;

public class TestjSonMessageSerialization {

	@Test
	public void testClientMessageSerialize() throws URISyntaxException {
		jSonUtils utils = jSonUtils.getInstance();
		Gson gson = utils.getGson();
		ClientMessage message = new ClientMessage(
				ClientMessageType.PUT_REQUEST,
				InteractionMode.KEEP, 
				"target",
				"202", 
				"OK", 
				new Tuple(1,true,3.0,"4"), 
				new Template(1,new FormalField(Integer.class)),
				false, false, "clientSession", 
				"serverSession", 
				new URI("pspace://127.0.0.1:8080/test?KEEP")
		);
		assertNotNull(gson.toJson(message));
	}

	@Test
	public void testClientMessageSerializeDeserialize() throws URISyntaxException {
		jSonUtils utils = jSonUtils.getInstance();
		Gson gson = utils.getGson();
		ClientMessage message = new ClientMessage(
				ClientMessageType.PUT_REQUEST,
				InteractionMode.KEEP, 
				"target",
				"202", 
				"OK", 
				new Tuple(1,true,3.0,"4\r\n\t\"\""), 
				new Template(1,new FormalField(Integer.class)),
				false, false, "clientSession", 
				"serverSession", 
				new URI("pspace://127.0.0.1:8080/test?KEEP")
		);
		String data = gson.toJson(message);
		ClientMessage obtained = gson.fromJson(data, ClientMessage.class);
		assertEquals(message,obtained);
	}

	@Test
	public void testClientMessageSerializeDeserializeFailure() throws URISyntaxException {
		jSonUtils utils = jSonUtils.getInstance();
		Gson gson = utils.getGson();
		ClientMessage message = new ClientMessage(
				ClientMessageType.PUT_REQUEST,
				InteractionMode.KEEP, 
				"target",
				"202", 
				"OK", 
				new Tuple(1,true,3.0,"4"), 
				new Template(1,new FormalField(Integer.class)),
				false, false, "clientSession", 
				"serverSession", 
				new URI("pspace://127.0.0.1:8080/test?KEEP")
		);
		ClientMessage message2 = new ClientMessage(
				ClientMessageType.PUT_REQUEST,
				InteractionMode.KEEP, 
				"targeti",
				"202", 
				"OK", 
				new Tuple(1,true,3.0,"4"), 
				new Template(1,new FormalField(Integer.class)),
				false, false, "clientSession", 
				"serverSession", 
				new URI("pspace://127.0.0.1:8080/test?KEEP")
		);
		String data = gson.toJson(message);
		ClientMessage obtained = gson.fromJson(data, ClientMessage.class);
		assertFalse(message2.equals( obtained ));
	}
	
	@Test
	public void testServerMessageSerialize() {
		jSonUtils utils = jSonUtils.getInstance();
		Gson gson = utils.getGson();
		ServerMessage message = new ServerMessage(
			ServerMessageType.GET_RESPONSE,
			InteractionMode.CONN, 
			true,
			"202",
			"OK", 
			new Tuple[] { new Tuple(1,2,3) , new Tuple(2,3,4) },
			"clientSession", 
			"serverSession");
		assertNotNull(gson.toJson(message));
	}

	@Test
	public void testServerMessageSerializeDeserialize() {
		jSonUtils utils = jSonUtils.getInstance();
		Gson gson = utils.getGson();
		ServerMessage message = new ServerMessage(
			ServerMessageType.GET_RESPONSE,
			InteractionMode.CONN, 
			true,
			"202",
			"OK", 
			new Tuple[] { new Tuple(1,2,3) , new Tuple(2,3,4) },
			"clientSession", 
			"serverSession");
		String data = gson.toJson(message);
		ServerMessage obtained = gson.fromJson(data, ServerMessage.class);
		assertEquals(message,obtained);
	}
}
