package org.jspace.examples.remoteworld;

import java.io.IOException;
import java.net.UnknownHostException;

import org.jspace.ActualField;
import org.jspace.FormalField;

import org.jspace.Server;
import org.jspace.ServerConnection;
import org.jspace.SpaceRepository;
import org.jspace.NamedSpace;

import org.jspace.config.ServerConfig;
import org.jspace.gate.GateFactory;
import org.jspace.protocol.ManagementMessage;
import org.jspace.protocol.ManagementMessageType;
import org.jspace.protocol.RepositoryProperties;
import org.jspace.protocol.SpaceProperties;
import org.jspace.protocol.SpaceKeys;
import org.jspace.protocol.SpaceType;

public class RemoteHelloWorld {

	public final static String REMOTE_URI = "tcp://127.0.0.1:7000/?keep";
	public static void main(String[] argv) throws InterruptedException {

        ServerConnection srv;

        // FIXME this seems clunky and not very user friendly
        try {
            srv = ServerConnection.getInstance(REMOTE_URI);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

		SpaceRepository repository = srv.newRepository("HelloRepo", "passwd");
        if (repository == null) return;

        NamedSpace space = repository.newSpace("aspace", SpaceType.SEQUENTIAL, null);

		Thread t1 = new Thread( () -> {//PONG TREAD: get pong from "PONG" and write ping to "PING"
			try {
				space.put("GREETING","Hello");
				System.out.println("T1 Done!");
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("T1 Error!");
            }
		});

		Thread t2 = new Thread( () -> {//PONG TREAD: get pong from "PONG" and write ping to "PING"
			try {
				space.put("NAME","World");
				System.out.println("T2 Done!");
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("T2 Error!");
			}
		});

		Thread t3 = new Thread( () -> {//PONG TREAD: get pong from "PONG" and write ping to "PING"
			try {
				Object[] greetingData = space.get(new ActualField("GREETING"), new FormalField(String.class));
				Object[] nameData = space.get(new ActualField("NAME"), new FormalField(String.class));
				System.out.println(greetingData[1]+" "+nameData[1]+"!");
				System.out.println("T3 Done!");
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("T3 Error!");
            }
		});

		t1.start();
		t2.start();
		t3.start();
	}

}
