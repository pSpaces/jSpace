package org.jspace.examples.df;
import java.net.UnknownHostException;
import java.io.IOException;


import java.util.Random;

import org.jspace.ActualField;
import org.jspace.FormalField;
import org.jspace.Space;
import org.jspace.NamedSpace;
import org.jspace.SpaceRepository;
import org.jspace.ServerConnection;
import org.jspace.protocol.SpaceType;

public class Philosopher implements Runnable {

	private int id;
	private Random random;
	private int first;
	private int second;
    private String REMOTE_URI = "tcp://127.0.0.1:7000/?keep";
    private ServerConnection srv;
    private SpaceRepository repository;
	private NamedSpace table;

	public Philosopher(int id, int left, int right) throws InterruptedException, UnknownHostException, IOException {
//        this.repo = repo;
//		this.table = table;
		this.id = id;
		this.random = new Random();
		this.first = Math.min(left, right);
		this.second = Math.max(left, right);
        try {
            this.srv = ServerConnection.getInstance(REMOTE_URI);
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        this.repository = srv.newRepository("dining", "passwd");
        if (this.repository == null) return;
        this.table = repository.newSpace("table", SpaceType.SEQUENTIAL, null);
	}
	
	@Override
	public void run() {
		try {
			while (true) {
				int sleeptime = random.nextInt(1000);
				System.out.println("PHIL "+this.id+"> I'am thinking! (for "+sleeptime+"s)");
				Thread.sleep(sleeptime);
				System.out.println("PHIL "+this.id+"> I'am hungry!");
				getFork(first);
				getFork(second);
				sleeptime = random.nextInt(1000);
				System.out.println("PHIL "+this.id+"> I'am eating! (for "+sleeptime+"s)");
				Thread.sleep(sleeptime);
				System.out.println("PHIL "+this.id+"> I'am full!");
				releaseFork(first);
				releaseFork(second);
			}
		} catch (InterruptedException e) {
			System.err.println("PHI "+id+" error!");
			e.printStackTrace();
		}
	}

	private void getFork( int i ) throws InterruptedException {
		System.out.println("PHIL "+this.id+"> Getting fork "+i+"!");
		table.get(new ActualField("FORK"), new ActualField(i));
		System.out.println("PHIL "+this.id+"> Fork "+i+" acquired!");
	}

	private void releaseFork( int i ) throws InterruptedException {
		System.out.println("PHIL "+this.id+"> Releasing fork "+i+"!");
		table.put("FORK", i);
		System.out.println("PHIL "+this.id+"> Fork "+i+" released!");
	}


}
