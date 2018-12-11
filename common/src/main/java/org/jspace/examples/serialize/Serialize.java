package org.jspace.examples.serialize;

import org.jspace.protocol.*;
import org.jspace.Tuple;

import org.jspace.io.json.jSonUtils;

import com.google.gson.Gson;

public class Serialize {
    public static void main(String[] argv) {
        SpaceKeys keys = new SpaceKeys("manage", "put", "get", "que");
        Tuple myTuple = new Tuple(1,true,3.0,"4");
        SpaceProperties target = new SpaceProperties(SpaceType.SEQUENTIAL, "SpaceName", "SpaceUID", keys);
        DataProperties data = new DataProperties("tuple", myTuple);
        pSpaceMessage message = new pSpaceMessage(
                MessageType.PUT,
                "fancySessionId",
                target,
                data,
                null
                );
//        System.out.println(data);
        System.out.println(message.getData());

        jSonUtils utils = jSonUtils.getInstance();
        Gson gson = utils.getGson();

 //       System.out.println(utils.toString(data));

        String marshalled = gson.toJson(message.getData());

        pSpaceMessage unmarshalled = gson.fromJson(marshalled, pSpaceMessage.class);
        System.out.println(unmarshalled.getData());
    }
}
