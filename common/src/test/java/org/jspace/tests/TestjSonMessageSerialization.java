package org.jspace.tests;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.URISyntaxException;

import org.jspace.FormalTemplateField;
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
				new Template(1,new FormalTemplateField(Integer.class)),
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
				new Template(1,new FormalTemplateField(Integer.class)),
				false, false, "clientSession", 
				"serverSession", 
				new URI("pspace://127.0.0.1:8080/test?KEEP")
		);
		String data = gson.toJson(message);
		System.out.println(data);
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
				new Template(1,new FormalTemplateField(Integer.class)),
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
				new Template(1,new FormalTemplateField(Integer.class)),
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
			ServerMessageType.GET_RESPONCE,
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
			ServerMessageType.GET_RESPONCE,
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
