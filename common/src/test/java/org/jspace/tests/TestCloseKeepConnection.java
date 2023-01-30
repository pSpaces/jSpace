package org.jspace.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.Socket;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.jspace.ActualField;
import org.jspace.RemoteSpace;
import org.jspace.SequentialSpace;
import org.jspace.SpaceRepository;
import org.jspace.gate.KeepClientGate;
import org.junit.Test;

/**
 * Various tests for checking that everything is actually closed/handled
 * properly when closing a keep connection (or the network connection is lost).
 * Tests are mainly aimed at the client-side.
 *
 * Note: The tests pass and fail semi-randomly, so there must still be some concurrency bug.
 *       Using debugging mode, adding print statements, using another process(-mode) or
 *       changing timing in other ways affects the results.
 *
 * @author Bogoe
 */
public class TestCloseKeepConnection {

	private static Object getField(Object obj, String name) {
		try {
			Field f = obj.getClass().getDeclaredField(name);
			f.setAccessible(true);
			return f.get(obj);
		} catch (Exception e) {}
		return null;
	}

	// @Test
	public void testServerClose() {
		int threadCount = Thread.activeCount();
		HashSet<String> locks = new HashSet<>();
		Object[] objects = new Object[3];
		Thread serverThread = new Thread(() -> {
			SpaceRepository repository = new SpaceRepository();
			SequentialSpace space = new SequentialSpace();
			repository.addGate("tcp://127.0.0.1:9991/?keep");
			repository.add("space", space);
			synchronized (locks) {
				locks.add("serverReady");
				locks.notifyAll();
				while (!locks.contains("clientReady")) {
					try {
						locks.wait();
					} catch (InterruptedException e) {}
				}
			}
			repository.shutDown();
			ExecutorService executor = (ExecutorService) getField(repository, "executor");
			objects[0] = executor;
			try {
				executor.awaitTermination(10000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {}
			synchronized (locks) {
				locks.add("serverClosed");
				locks.notifyAll();
			}
		});
		Thread clientThread = new Thread(() -> {
			try {
				synchronized (locks) {
					while (!locks.contains("serverReady")) {
						try {
							locks.wait();
						} catch (InterruptedException e) {}
					}
				}
				RemoteSpace space = new RemoteSpace("tcp://127.0.0.1:9991/space?keep");
				KeepClientGate gate = (KeepClientGate) space.getGate();
				Socket socket = (Socket) getField(gate, "socket");
				Thread outboxThread = (Thread) getField(gate, "outboxThread");
				Thread inboxThread = (Thread) getField(gate, "inboxThread");
				objects[1] = outboxThread;
				objects[2] = inboxThread;
				while (!socket.isConnected()) {
					Thread.sleep(10);
				}
				synchronized (locks) {
					locks.add("clientReady");
					locks.notifyAll();
					while (!locks.contains("serverClosed")) {
						try {
							locks.wait();
						} catch (InterruptedException e) {}
					}
				}
				outboxThread.join();
				inboxThread.join();
			} catch (IOException e) {

			} catch (InterruptedException e) {

			}
		});
		serverThread.start();
		clientThread.start();
		try {
			serverThread.join();
			clientThread.join();
		} catch (InterruptedException e) {}
		assertFalse("Server Thread did not terminate (within the time limit)", serverThread.isAlive());
		assertFalse("Client Thread did not terminate (within the time limit)", clientThread.isAlive());
		assertNotNull("Server Thread did not have an ExecutorService", objects[0]);
		assertTrue("Server Thread threadpool was not shut down", ((ExecutorService) objects[0]).isShutdown());
		assertNotNull("Client Thread did not have a thread for the outbox", objects[1]);
		assertFalse("Client Outbox Thread did not terminate (within the time limit)", ((Thread) objects[1]).isAlive());
		assertNotNull("Client Thread did not have a thread for the inbox", objects[2]);
		assertFalse("Client Inbox Thread did not terminate (within the time limit)", ((Thread) objects[2]).isAlive());
		assertEquals("The number of threads in the start does not match the number of threads in the end", threadCount, Thread.activeCount());
	}

	// @Test
	public void testClientSendThenServerClose() {
		int threadCount = Thread.activeCount();
		HashSet<String> locks = new HashSet<>();
		Object[] objects = new Object[4];
		Thread serverThread = new Thread(() -> {
			SpaceRepository repository = new SpaceRepository();
			SequentialSpace space = new SequentialSpace();
			repository.addGate("tcp://127.0.0.1:9992/?keep");
			repository.add("space", space);
			synchronized (locks) {
				locks.add("serverReady");
				locks.notifyAll();
				while (!locks.contains("clientSent")) {
					try {
						locks.wait();
					} catch (InterruptedException e) {}
				}
			}
			repository.shutDown();
			ExecutorService executor = (ExecutorService) getField(repository, "executor");
			objects[0] = executor;
			try {
				executor.awaitTermination(10000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {}
			synchronized (locks) {
				locks.add("serverClosed");
				locks.notifyAll();
			}
		});
		Thread clientThread = new Thread(() -> {
			try {
				synchronized (locks) {
					while (!locks.contains("serverReady")) {
						try {
							locks.wait();
						} catch (InterruptedException e) {}
					}
				}
				RemoteSpace space = new RemoteSpace("tcp://127.0.0.1:9992/space?keep");
				KeepClientGate gate = (KeepClientGate) space.getGate();
				Thread outboxThread = (Thread) getField(gate, "outboxThread");
				Thread inboxThread = (Thread) getField(gate, "inboxThread");
				objects[1] = outboxThread;
				objects[2] = inboxThread;
				Thread requestThread = new Thread(() -> {
					try {
						space.get(new ActualField("Blocked Request"));
						objects[3] = false;
					} catch (InterruptedException e) {
						objects[3] = true;
					}
				});
				requestThread.start();
				synchronized (locks) {
					while (requestThread.getState() != Thread.State.WAITING) {
						Thread.sleep(10);
					}
					locks.add("clientSent");
					locks.notifyAll();
					while (!locks.contains("serverClosed")) {
						try {
							locks.wait();
						} catch (InterruptedException e) {}
					}
				}
				outboxThread.join();
				inboxThread.join();
				requestThread.join();
			} catch (IOException e) {

			} catch (InterruptedException e) {

			}
		});
		serverThread.start();
		clientThread.start();
		try {
			serverThread.join();
			clientThread.join();
		} catch (InterruptedException e) {}
		assertFalse("Server Thread did not terminate (within the time limit)", serverThread.isAlive());
		assertFalse("Client Thread did not terminate (within the time limit)", clientThread.isAlive());
		assertNotNull("Server Thread did not have an ExecutorService", objects[0]);
		assertTrue("Server Thread threadpool was not shut down", ((ExecutorService) objects[0]).isShutdown());
		assertNotNull("Client Thread did not have a thread for the outbox", objects[1]);
		assertFalse("Client Outbox Thread did not terminate (within the time limit)", ((Thread) objects[1]).isAlive());
		assertNotNull("Client Thread did not have a thread for the inbox", objects[2]);
		assertFalse("Client Inbox Thread did not terminate (within the time limit)", ((Thread) objects[2]).isAlive());
		assertNotNull("Client Thread, message did not succeeded and were interrupted", objects[3]);
		assertTrue("Client Thread, message was not interrupted as expected", (Boolean) objects[3]);
		assertEquals("The number of threads in the start does not match the number of threads in the end", threadCount, Thread.activeCount());
	}

	// @Test
	public void testServerCloseThenClientSend() {
		int threadCount = Thread.activeCount();
		HashSet<String> locks = new HashSet<>();
		Object[] objects = new Object[4];
		Thread serverThread = new Thread(() -> {
			SpaceRepository repository = new SpaceRepository();
			SequentialSpace space = new SequentialSpace();
			repository.addGate("tcp://127.0.0.1:9993/?keep");
			repository.add("space", space);
			synchronized (locks) {
				locks.add("serverReady");
				locks.notifyAll();
				while (!locks.contains("clientReady")) {
					try {
						locks.wait();
					} catch (InterruptedException e) {}
				}
			}
			repository.shutDown();
			ExecutorService executor = (ExecutorService) getField(repository, "executor");
			objects[0] = executor;
			try {
				executor.awaitTermination(10000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {}
			synchronized (locks) {
				locks.add("serverClosed");
				locks.notifyAll();
			}
		});
		Thread clientThread = new Thread(() -> {
			try {
				synchronized (locks) {
					while (!locks.contains("serverReady")) {
						try {
							locks.wait();
						} catch (InterruptedException e) {}
					}
				}
				RemoteSpace space = new RemoteSpace("tcp://127.0.0.1:9993/space?keep");
				KeepClientGate gate = (KeepClientGate) space.getGate();
				Socket socket = (Socket) getField(gate, "socket");
				Thread outboxThread = (Thread) getField(gate, "outboxThread");
				Thread inboxThread = (Thread) getField(gate, "inboxThread");
				objects[1] = outboxThread;
				objects[2] = inboxThread;
				while (!socket.isConnected()) {
					Thread.sleep(10);
				}
				synchronized (locks) {
					locks.add("clientReady");
					locks.notifyAll();
					while (!locks.contains("serverClosed")) {
						try {
							locks.wait();
						} catch (InterruptedException e) {}
					}
				}
				outboxThread.join();
				inboxThread.join();
				try {
					space.put(new ActualField("Not send request"));
					objects[3] = false;
				} catch (InterruptedException e) {
					objects[3] = true;
				}
			} catch (IOException e) {

			} catch (InterruptedException e) {

			}
		});
		serverThread.start();
		clientThread.start();
		try {
			serverThread.join();
			clientThread.join();
		} catch (InterruptedException e) {}
		assertFalse("Server Thread did not terminate (within the time limit)", serverThread.isAlive());
		assertFalse("Client Thread did not terminate (within the time limit)", clientThread.isAlive());
		assertNotNull("Server Thread did not have an ExecutorService", objects[0]);
		assertTrue("Server Thread threadpool was not shut down", ((ExecutorService) objects[0]).isShutdown());
		assertNotNull("Client Thread did not have a thread for the outbox", objects[1]);
		assertFalse("Client Outbox Thread did not terminate (within the time limit)", ((Thread) objects[1]).isAlive());
		assertNotNull("Client Thread did not have a thread for the inbox", objects[2]);
		assertFalse("Client Inbox Thread did not terminate (within the time limit)", ((Thread) objects[2]).isAlive());
		assertNotNull("Client Thread, message did not succeeded and were interrupted", objects[3]);
		assertTrue("Client Thread, message was not interrupted as expected", (Boolean) objects[3]);
		assertEquals("The number of threads in the start does not match the number of threads in the end", threadCount, Thread.activeCount());
	}

	// @Test
	public void testClientClose() {
		int threadCount = Thread.activeCount();
		HashSet<String> locks = new HashSet<>();
		Object[] objects = new Object[3];
		Thread serverThread = new Thread(() -> {
			SpaceRepository repository = new SpaceRepository();
			SequentialSpace space = new SequentialSpace();
			repository.addGate("tcp://127.0.0.1:9994/?keep");
			repository.add("space", space);
			synchronized (locks) {
				locks.add("serverReady");
				locks.notifyAll();
				while (!locks.contains("clientClosed")) {
					try {
						locks.wait();
					} catch (InterruptedException e) {}
				}
			}
			repository.shutDown();
			ExecutorService executor = (ExecutorService) getField(repository, "executor");
			objects[0] = executor;
			try {
				executor.awaitTermination(10000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {}
		});
		Thread clientThread = new Thread(() -> {
			try {
				synchronized (locks) {
					while (!locks.contains("serverReady")) {
						try {
							locks.wait();
						} catch (InterruptedException e) {}
					}
				}
				RemoteSpace space = new RemoteSpace("tcp://127.0.0.1:9994/space?keep");
				KeepClientGate gate = (KeepClientGate) space.getGate();
				Socket socket = (Socket) getField(gate, "socket");
				Thread outboxThread = (Thread) getField(gate, "outboxThread");
				Thread inboxThread = (Thread) getField(gate, "inboxThread");
				objects[1] = outboxThread;
				objects[2] = inboxThread;
				while (!socket.isConnected()) {
					Thread.sleep(10);
				}
				space.close();
				outboxThread.join();
				inboxThread.join();
				synchronized (locks) {
					locks.add("clientClosed");
					locks.notifyAll();
				}
			} catch (IOException e) {

			} catch (InterruptedException e) {

			}
		});
		serverThread.start();
		clientThread.start();
		try {
			clientThread.join();
			serverThread.join();
		} catch (InterruptedException e) {}
		assertFalse("Server Thread did not terminate (within the time limit)", serverThread.isAlive());
		assertFalse("Client Thread did not terminate (within the time limit)", clientThread.isAlive());
		assertNotNull("Server Thread did not have an ExecutorService", objects[0]);
		assertTrue("Server Thread threadpool was not shut down", ((ExecutorService) objects[0]).isShutdown());
		assertNotNull("Client Thread did not have a thread for the outbox", objects[1]);
		assertFalse("Client Outbox Thread did not terminate (within the time limit)", ((Thread) objects[1]).isAlive());
		assertNotNull("Client Thread did not have a thread for the inbox", objects[2]);
		assertFalse("Client Inbox Thread did not terminate (within the time limit)", ((Thread) objects[2]).isAlive());
		assertEquals("The number of threads in the start does not match the number of threads in the end", threadCount, Thread.activeCount());
	}

	// @Test
	public void testClientSendThenClientClose() {
		int threadCount = Thread.activeCount();
		HashSet<String> locks = new HashSet<>();
		Object[] objects = new Object[4];
		Thread serverThread = new Thread(() -> {
			SpaceRepository repository = new SpaceRepository();
			SequentialSpace space = new SequentialSpace();
			repository.addGate("tcp://127.0.0.1:9995/?keep");
			repository.add("space", space);
			synchronized (locks) {
				locks.add("serverReady");
				locks.notifyAll();
				while (!locks.contains("clientClosed")) {
					try {
						locks.wait();
					} catch (InterruptedException e) {}
				}
			}
			repository.shutDown();
			ExecutorService executor = (ExecutorService) getField(repository, "executor");
			objects[0] = executor;
			try {
				executor.awaitTermination(10000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {}
		});
		Thread clientThread = new Thread(() -> {
			try {
				synchronized (locks) {
					while (!locks.contains("serverReady")) {
						try {
							locks.wait();
						} catch (InterruptedException e) {}
					}
				}
				RemoteSpace space = new RemoteSpace("tcp://127.0.0.1:9995/space?keep");
				KeepClientGate gate = (KeepClientGate) space.getGate();
				Thread outboxThread = (Thread) getField(gate, "outboxThread");
				Thread inboxThread = (Thread) getField(gate, "inboxThread");
				objects[1] = outboxThread;
				objects[2] = inboxThread;
				Thread requestThread = new Thread(() -> {
					try {
						space.get(new ActualField("Blocked Request"));
						objects[3] = false;
					} catch (InterruptedException e) {
						objects[3] = true;
					}
				});
				requestThread.start();
				synchronized (locks) {
					while (requestThread.getState() != Thread.State.WAITING) {
						Thread.sleep(10);
					}
				}
				space.close();
				outboxThread.join();
				inboxThread.join();
				requestThread.join();
				synchronized (locks) {
					locks.add("clientClosed");
					locks.notifyAll();
				}
			} catch (IOException e) {

			} catch (InterruptedException e) {

			}
		});
		serverThread.start();
		clientThread.start();
		try {
			clientThread.join();
			serverThread.join();
		} catch (InterruptedException e) {}
		assertFalse("Server Thread did not terminate (within the time limit)", serverThread.isAlive());
		assertFalse("Client Thread did not terminate (within the time limit)", clientThread.isAlive());
		assertNotNull("Server Thread did not have an ExecutorService", objects[0]);
		assertTrue("Server Thread threadpool was not shut down", ((ExecutorService) objects[0]).isShutdown());
		assertNotNull("Client Thread did not have a thread for the outbox", objects[1]);
		assertFalse("Client Outbox Thread did not terminate (within the time limit)", ((Thread) objects[1]).isAlive());
		assertNotNull("Client Thread did not have a thread for the inbox", objects[2]);
		assertFalse("Client Inbox Thread did not terminate (within the time limit)", ((Thread) objects[2]).isAlive());
		assertNotNull("Client Thread, message did not succeeded and were interrupted", objects[3]);
		assertTrue("Client Thread, message was not interrupted as expected", (Boolean) objects[3]);
		assertEquals("The number of threads in the start does not match the number of threads in the end", threadCount, Thread.activeCount());
	}

	@Test
	public void testClientCloseThenClientSend() {
		int threadCount = Thread.activeCount();
		HashSet<String> locks = new HashSet<>();
		Object[] objects = new Object[4];
		Thread serverThread = new Thread(() -> {
			SpaceRepository repository = new SpaceRepository();
			SequentialSpace space = new SequentialSpace();
			repository.addGate("tcp://127.0.0.1:9999/?keep");
			repository.add("space", space);
			synchronized (locks) {
				locks.add("serverReady");
				locks.notifyAll();
				while (!locks.contains("clientClosed")) {
					try {
						locks.wait();
					} catch (InterruptedException e) {}
				}
			}
			repository.shutDown();
			ExecutorService executor = (ExecutorService) getField(repository, "executor");
			objects[0] = executor;
			try {
				executor.awaitTermination(10000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {}
		});
		Thread clientThread = new Thread(() -> {
			try {
				synchronized (locks) {
					while (!locks.contains("serverReady")) {
						try {
							locks.wait();
						} catch (InterruptedException e) {}
					}
				}
				RemoteSpace space = new RemoteSpace("tcp://127.0.0.1:9999/space?keep");
				KeepClientGate gate = (KeepClientGate) space.getGate();
				Socket socket = (Socket) getField(gate, "socket");
				Thread outboxThread = (Thread) getField(gate, "outboxThread");
				Thread inboxThread = (Thread) getField(gate, "inboxThread");
				objects[1] = outboxThread;
				objects[2] = inboxThread;
				while (!socket.isConnected()) {
					Thread.sleep(10);
				}
				space.close();
				outboxThread.join();
				inboxThread.join();
				synchronized (locks) {
					locks.add("clientClosed");
					locks.notifyAll();
				}
				try {
					space.put(new ActualField("Not send request"));
					objects[3] = false;
				} catch (InterruptedException e) {
					objects[3] = true;
				}
			} catch (IOException e) {

			} catch (InterruptedException e) {

			}
		});
		serverThread.start();
		clientThread.start();
		try {
			clientThread.join();
			serverThread.join();
		} catch (InterruptedException e) {}
		assertFalse("Server Thread did not terminate (within the time limit)", serverThread.isAlive());
		assertFalse("Client Thread did not terminate (within the time limit)", clientThread.isAlive());
		assertNotNull("Server Thread did not have an ExecutorService", objects[0]);
		assertTrue("Server Thread threadpool was not shut down", ((ExecutorService) objects[0]).isShutdown());
		assertNotNull("Client Thread did not have a thread for the outbox", objects[1]);
		assertFalse("Client Outbox Thread did not terminate (within the time limit)", ((Thread) objects[1]).isAlive());
		assertNotNull("Client Thread did not have a thread for the inbox", objects[2]);
		assertFalse("Client Inbox Thread did not terminate (within the time limit)", ((Thread) objects[2]).isAlive());
		assertNotNull("Client Thread, message did not succeeded and were interrupted", objects[3]);
		assertTrue("Client Thread, message was not interrupted as expected", (Boolean) objects[3]);
		assertEquals("The number of threads in the start does not match the number of threads in the end", threadCount, Thread.activeCount());
	}
}
