package org.jspace.examples.df;

import java.io.IOException;
import java.net.UnknownHostException;

import org.jspace.SequentialSpace;
import org.jspace.Space;
import org.jspace.SpaceRepository;

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

public class DistributedDiningPhilosophers {

	public final static String REMOTE_URI = "tcp://127.0.0.1:7000/?keep";

	public static final int SIZE = 5;

	public static void main( String[] argv ) throws InterruptedException, UnknownHostException, IOException {

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

		SpaceRepository repository = srv.newRepository("dining", "passwd");
        if (repository == null) return;

		NamedSpace table = repository.newSpace("table", SpaceType.SEQUENTIAL, null);

		for( int i=0 ; i<SIZE; i++) {
			table.put("table","FORK",i);
		}

		Thread[] philosophers = new Thread[SIZE];

		for( int i=0 ; i<SIZE ; i++ ) {
//			philosophers[i] = new Thread( new Philosopher(i,  new RemoteSpace(REMOTE_URI), i, (i+1)%SIZE) );
			philosophers[i] = new Thread( new Philosopher(i, i, (i+1)%SIZE) );
//			philosophers[i] = new Thread( new Philosopher(i,  table, repository, i, (i+1)%SIZE) );
			philosophers[i].start();
		}

		philosophers[0].join();
	}


}
