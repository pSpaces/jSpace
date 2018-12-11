package org.jspace;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.lang.InterruptedException;
import javax.naming.AuthenticationException;

import org.jspace.SpaceRepository;
import org.jspace.gate.ClientGate;
import org.jspace.gate.GateFactory;
import org.jspace.protocol.Message;
import org.jspace.protocol.MessageType;
import org.jspace.protocol.ManagementMessage;
import org.jspace.protocol.RepositoryProperties;
import org.jspace.config.ServerConfig;

 /**
  * Provides direct communication to a server and
  * a method to create a SpaceRepository 
  */
public class ServerConnection {
	private final URI uri;
	private final ClientGate gate;

    private static ServerConnection instance;

    /**
     * @param uri is the identifier used to instantiate a ServerConnection
     */
	private ServerConnection(URI uri) throws UnknownHostException, IOException {
        // Set necessary TLS properties by initializing a ServerConfig object
        new ServerConfig();
		this.uri = uri;
		this.gate = GateFactory.getInstance().getGateBuilder(uri.getScheme())
            .createClientGate(uri, ManagementMessage.class);
		this.gate.open();
	}

    /**
     * @param uri is the string used to create an URI
     */
	private ServerConnection(String uri) throws UnknownHostException, IOException {
		this(URI.create(uri));
	}

    public static ServerConnection getInstance(URI uri)
            throws UnknownHostException, IOException {
        if (instance == null) {
            instance = new ServerConnection(uri);
        }

        return instance;
    }

    public static ServerConnection getInstance(String uri)
            throws UnknownHostException, IOException {
        if (instance == null) {
            instance = new ServerConnection(uri);
        }

        return instance;
    }



	public SpaceRepository newRepository(String name, String key)
            throws InterruptedException {

		ManagementMessage response;
        Message request = new ManagementMessage(
                MessageType.CREATE_REPOSITORY,
                "", // FIXME serverkey?
                new RepositoryProperties(
                    name,
                    key
                ),
                null, // FIXME
                null); // FIXME


		try {
			response = (ManagementMessage) gate.send(request);
            if (response.getStatus().getCode() == 400) {
                //throw new AuthenticationException();
                System.out.println("Credentials are incorrect "
                        + "returned status code was 400");
                return null;
            } else if (response.getStatus().getCode() == 500) {
                //throw new InternalServerErrorException();
                System.out.println("Something bad happened with the server, "
                        + "returned status code was 500");
                return null;
            }

            // TODO ensure that only code "200" status is good
            int port = response.getRepository().getPort();

            // construct URI for the repository
            try {
                URI repoUri = new URI(uri.getScheme(), uri.getUserInfo(),
                        uri.getHost(), port, uri.getPath(), uri.getQuery(),
                        uri.getFragment());

                return new SpaceRepository(name, key, repoUri);
            } catch (URISyntaxException e) {
                // FIXME proper logging
                e.printStackTrace();
                return null;
            }
		} catch (IOException e) {
			//TODO: Replace with a specific exception
			throw new InterruptedException();
		}
	}

    /**
     * @return Returns the URI of a ServerConnection
     */
	public URI getUri() {
		return uri;
	}

    /**
     * @return Returns the gate of a ServerConnection
     */
	public ClientGate getGate() {
		return gate;
	}

    /**
     * closes the gate of a ServerConnection
     */
	public void close() throws IOException {
		gate.close();
	}
}
