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
import org.jspace.protocol.ManagementMessage;
import org.jspace.protocol.pSpaceMessage;
import org.jspace.protocol.SpaceKeys;
import org.jspace.protocol.DataProperties;
import org.jspace.protocol.RepositoryProperties;
import org.jspace.protocol.SpaceProperties;
import org.jspace.protocol.SpaceType;
import org.jspace.protocol.MessageType;
import org.jspace.protocol.InteractionMode;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import nl.jqno.equalsverifier.EqualsVerifier;

public class TestjSonMessageSerialization {

    @Test
    public void testEqualsContract() {
        EqualsVerifier.forClass(ManagementMessage.class).verify();
    }
    @Test
    public void testEqualsContractpspace() {
        EqualsVerifier.forClass(pSpaceMessage.class).verify();
    }

	@Test
	public void testpSpaceMessageSerialize() {
		jSonUtils utils = jSonUtils.getInstance();
		Gson gson = utils.getGson();
        SpaceKeys keys = new SpaceKeys("mgmt", "put", "get", "query");
		Tuple someTuple = new Tuple(1,true,3.0,"4");
        SpaceProperties target = new SpaceProperties(SpaceType.SEQUENTIAL, "spaceName", "spaceUid", keys);
        DataProperties data = new DataProperties("tuple", someTuple);
		pSpaceMessage message = new pSpaceMessage(
				MessageType.PUT,
//				InteractionMode.KEEP, 
				"someSessionId",
                target,
                data,
                null
//				false, false, "session", 
//				new URI("pspace://127.0.0.1:8080/test?KEEP")
		);
		assertNotNull(gson.toJson(message));
	}

	@Test
	public void testpSpaceMessageSerializeDeserialize() {
		jSonUtils utils = jSonUtils.getInstance();
		Gson gson = utils.getGson();
        SpaceKeys keys = new SpaceKeys("mgmt", "put", "get", "query");
		Tuple someTuple = new Tuple(1,true,3.0,"4");
        SpaceProperties target = new SpaceProperties(SpaceType.SEQUENTIAL, "spaceName", "spaceUid", keys);
        DataProperties data = new DataProperties("tuple", someTuple);
		pSpaceMessage message = new pSpaceMessage(
				MessageType.PUT,
//				InteractionMode.KEEP, 
				"someSessionId",
                target,
                data,
                null
//				false, false, "session", 
//				new URI("pspace://127.0.0.1:8080/test?KEEP")
		);
		pSpaceMessage obtained = gson.fromJson(gson.toJson(message),
                pSpaceMessage.class);
		assertEquals(message, obtained);
	}

	@Test
	public void testpSpaceMessageSerializeDeserializeFailure() {
		jSonUtils utils = jSonUtils.getInstance();
		Gson gson = utils.getGson();
        SpaceKeys keys = new SpaceKeys("mgmt", "put", "get", "query");
		Tuple someTuple = new Tuple(1,true,3.0,"4");
        SpaceProperties target = new SpaceProperties(SpaceType.SEQUENTIAL, "spaceName", "spaceUid", keys);
        DataProperties data = new DataProperties("tuple", someTuple);
		pSpaceMessage message = new pSpaceMessage(
				MessageType.PUT,
				"someSessionId",
                target,
                data,
                null
		);

		pSpaceMessage message2 = new pSpaceMessage(
				MessageType.GET,
				"anotherSessionId",
                target,
                data,
                null
		);
		String jSonData = gson.toJson(message);
		pSpaceMessage obtained = gson.fromJson(jSonData, pSpaceMessage.class);
		assertFalse(message2.equals( obtained ));
	}

	@Test
	public void testManagementMessageSerialize() {
		jSonUtils utils = jSonUtils.getInstance();
		Gson gson = utils.getGson();
		ManagementMessage message = new ManagementMessage(
			MessageType.CREATE_REPOSITORY,
            "theKey", // serverKey
			new RepositoryProperties("repoName", "repoKey"),
            null, // target SpaceProperties
            null // Status
            );
		assertNotNull(gson.toJson(message));
	}

	@Test
	public void testManagementMessageSerializeDeserialize() {
		jSonUtils utils = jSonUtils.getInstance();
		Gson gson = utils.getGson();
		ManagementMessage message = new ManagementMessage(
			MessageType.CREATE_REPOSITORY,
            "theKey", // serverKey
			new RepositoryProperties("repoName", "repoKey"),
            null, // target SpaceProperties
            null // Status
            );
		String data = gson.toJson(message);
		ManagementMessage obtained = gson.fromJson(data, ManagementMessage.class);
		assertEquals(message,obtained);
	}
}
