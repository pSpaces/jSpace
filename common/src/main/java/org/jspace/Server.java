package org.jspace;

import org.jspace.config.ServerConfig;
import org.jspace.gate.GateFactory;
import org.jspace.gate.ServerGate;
import org.jspace.gate.ClientHandler;

import org.jspace.protocol.RepositoryProperties;
import org.jspace.protocol.Message;
import org.jspace.protocol.ManagementMessage;
import org.jspace.protocol.MessageType;
import org.jspace.protocol.Status;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.HashMap;

import java.net.SocketException;
import java.io.IOException;
import java.net.URISyntaxException;


/**
 * Provides the core logic needed for server instantiation
 * and message handling
 */
public abstract class Server {
    private ExecutorService executor = Executors.newCachedThreadPool();
    private ServerGate gate;
    private ServerConfig config;
    private ClientHandler handler;
    private Class messageClass;

    public Server(ServerConfig config, Class messageClass) {
        this.config = config;
        this.messageClass = messageClass;
        addGate(messageClass);
        System.out.println("Server Initialized with config:\n" + this.config);
    }

    public Server(Class messageClass) {
        this(new ServerConfig(), messageClass);
    }

    public synchronized boolean addGate(Class messageClass) {
        try {
            gate = GateFactory.getInstance()
                .getGateBuilder(config.getProtocol())
                .createServerGate(config.getURI(), messageClass);

            try {
                gate.open();
            } catch (IOException e) {
                // FIXME handle errors better
                System.out.println(e);
                return false;
            }

            // launch executor and handler
            executor.execute(() -> {
                try {
                    while (!gate.isClosed()) {
                        handler = gate.accept();
                        if (handler != null) {
                            addHandler(handler);
                        }
                    }
                } catch (SocketException e) {
                    // FIXME handle errors better
                    System.out.println(e);
                } catch (IOException e) {
                    // FIXME handle errors better
                    System.out.println(e);
                    try {
                        gate.close();
                    } catch (IOException e1) {
                        // FIXME handle errors better
                        System.out.println(e1);
                    }
                }
            }); // gate has been added
        } catch (URISyntaxException e) {
            // FIXME handle errors better
            System.out.println(e);
        }
        return true;
    }

    private synchronized void addHandler(ClientHandler handler) {
        executor.execute(() -> {
            try {
                while (handler.isActive()) {
                    // FIXME management message?
                    Message message = handler.receive();
                    if (message != null) {
                        executor.execute(() -> {
                            try {
                                handler.send(handle(message));
                            } catch (InterruptedException e) {
                                handler.send(new Message(
                                            MessageType.FAILURE, // type
                                            message.getSession(), //null, // session
                                            new Status(500, "Internal Server Error")
                                            ));
                            }
                        });
                    }
                }
            } catch (IOException e) {
                // FIXME proper error log
                System.out.println(e);
                try {
                    if (!handler.isClosed()) {
                        handler.close();
                    }
                } catch (IOException e2) {
                    // FIXME proper error log
                    System.out.println(e2);
                }
            }
        });
    }

    protected abstract Message handle(Message msg) throws InterruptedException;

    private void closeGate() {
        if (gate == null) {
            return;
        }

        try {
            gate.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

	public synchronized void shutDown() {
		this.closeGate();
		try {
			if (!handler.isClosed()) {
				handler.close();
			}
		} catch (IOException e) {
            e.printStackTrace();
		}
		this.executor.shutdownNow();
	}

    public void close() {
        try {
            gate.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // kill handlers
        executor.shutdown();
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * @return Returns the port number of a server gate
     */
    public int getPort() {
        return this.gate.getPort();
    }

    public ServerConfig getConfig() {
        return this.config;
    }
}
