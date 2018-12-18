package org.jspace;

import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;

import org.jspace.SpaceWrapper;

import org.jspace.config.ServerConfig;
import org.jspace.monitor.RepositoryMonitor;
import org.jspace.protocol.Message;
import org.jspace.protocol.MessageType;
import org.jspace.protocol.Status;
import org.jspace.protocol.pSpaceMessage;
import org.jspace.protocol.ManagementMessage;
import org.jspace.protocol.KeyType;
import org.jspace.protocol.SpaceProperties;
import org.jspace.protocol.DataProperties;

/** A Repository represents a server-side repository.
 * it runs in its own thread and acts on requests sent over a ServerGate
 */

public class Repository extends Server {
    protected final String name;
    private ManagementServer mgmt; // management server
    private RepositoryMonitor monitor;
   

    // spaces maps space names to references
    protected final HashMap<String, SpaceWrapper> spaces = new HashMap<String, SpaceWrapper>();
    protected final HashMap<String, MonitoredRepository> monitors = new HashMap<String,MonitoredRepository>();
	private final String key;

    public Repository(String name, String key) {
        super(new ServerConfig(0, key), pSpaceMessage.class); // param '0' listens to a random port
        this.name = name;
        this.key = key;
        init();
    }

    private void init() {
        this.mgmt = ManagementServer.getInstance();
     
     }

    protected Message handle(Message message) throws InterruptedException {
        switch (message.getOperation()) {
            case PUT:
                pSpaceMessage msg = (pSpaceMessage) message;
                return handlePutRequest((pSpaceMessage) message);
            case GET:
                return handleGetRequest((pSpaceMessage) message);
            case GETP:
                return handleGetPRequest((pSpaceMessage) message);
            case GETALL:
                return handleGetAllRequest((pSpaceMessage) message);
            case QUERY:
                return handleQueryRequest((pSpaceMessage) message);
            case QUERYP:
                return handleQueryPRequest((pSpaceMessage) message);
            case QUERYALL:
                return handleQueryAllRequest((pSpaceMessage) message);
            default:
                return ManagementMessage.badRequest(message.getSession());
        }
    }

    protected pSpaceMessage generateFailureMsg(pSpaceMessage message) {
        return new pSpaceMessage(
            MessageType.FAILURE, //MessageType op,
            message.getSession(), //String session,
            message.getTarget(), //SpaceProperties target,
            null, //DataProperties data,
            new Status(400, "BAD REQUEST: Keys are incorrect") //Status status)
        );
    }

    public pSpaceMessage handlePutRequest(pSpaceMessage message)
        throws InterruptedException {
        SpaceProperties target = message.getTarget();

        // validate key
        if (!validateSpaceKey(KeyType.PUT, target)) {
            message.getTarget().setKeys(null); // clear keys from the response
            return generateFailureMsg(message);
        }

        // handle the request
        message.getTarget().setKeys(null); // clear keys from the response

        SpaceWrapper sw = spaces.get(target.getName());
        Space space = sw.getSpace();
        SpaceProperties props= sw.getProperties();

        if (space != null) {
            Tuple tuple = message.getTuple();
            
            //se la classe è monitorata,le azioni passano attraverso il suo monitor
            if(this.isMonitored()) {
            	monitors.get(this.getName()).getMonitor().put( sw, message.getTuple().getTuple());
            }
            else {
            	 spaces.get(target.getName()).getSpace().put(message.getTuple().getTuple());
            }
           

            return new pSpaceMessage(
                MessageType.ACK, //MessageType op,
                message.getSession(), //String session,
                message.getTarget(), //SpaceProperties target,
                null, //DataProperties data,
                new Status(200, "OK") //Status status)
            );
        } else {
            return generateFailureMsg(message);
        }
    }

    private boolean isMonitored() {
		
    	
    	if(monitors.get(this.getName()) != null) {
	    	if(monitors.get(this.getName()).getMonitor() != null) {
	    		return true;
	    	}
    	}
    	return false;
	}

	public pSpaceMessage handleGetRequest(pSpaceMessage message)
        throws InterruptedException {
        SpaceProperties target = message.getTarget();
        //String key = message.getTarget().getKeys().getGetKey();

        // validate key
//        if (!validateSpaceKey(KeyType.GET, target)) {
//            message.getTarget().setKeys(null); // clear keys from the response
//            return generateFailureMsg(message);
//        }

        // handle the request
        message.getTarget().setKeys(null); // clear keys from the response
        // TODO insert template
        SpaceWrapper sw = spaces.get(target.getName());
        Space space = spaces.get(target.getName()).getSpace();
        Template t = message.getTemplate();
        List<Tuple> tuples = space.toListOfTuples();
        Tuple tuple = null;
        
        if(this.isMonitored()) {
        	monitor = monitors.get(this.getName()).getMonitor();
        	tuple = new Tuple(monitor.get(sw ,t.getFields()));
			
        }
        else {
        	tuple = new Tuple(space.get(t.getFields()));
        }
        
        pSpaceMessage response = new pSpaceMessage(
            MessageType.GET, //MessageType op,
            message.getSession(), //String session,
            message.getTarget(), //SpaceProperties target,
            // TODO attach template to data
            new DataProperties("tuple", tuple), //DataProperties data,
            new Status(200, "OK") //Status status)
        );

        return response;
    }

    public pSpaceMessage handleGetPRequest(pSpaceMessage message)
        throws InterruptedException {
        SpaceProperties target = message.getTarget();
        SpaceWrapper sw = spaces.get(target.getName());
        //String key = message.getTarget().getKeys().getGetKey();

        // validate key
        if (!validateSpaceKey(KeyType.GET, target)) {
            message.getTarget().setKeys(null); // clear keys from the response
            return generateFailureMsg(message);
        }

        // handle the request
        message.getTarget().setKeys(null); // clear keys from the response
        Template t = message.getTemplate();
        
        Tuple tuple = null;
        
        if(this.isMonitored()) {
        	monitor = monitors.get(this.getName()).getMonitor();
        	tuple = new Tuple(monitor.getp(sw ,t.getFields()));
			
        }
        else {
        	//rimasto come era prima, manca il template
        	spaces.get(target.getName()).getSpace().getp(null);
        }

        pSpaceMessage response = new pSpaceMessage(
            MessageType.GETP, //MessageType op,
            message.getSession(), //String session,
            message.getTarget(), //SpaceProperties target,
            // TODO attach template to data
            null, //DataProperties data,
            new Status(200, "OK") //Status status)
        );

        return response;
    }

    public pSpaceMessage handleGetAllRequest(pSpaceMessage message)
        throws InterruptedException {
        SpaceProperties target = message.getTarget();
        SpaceWrapper sw = spaces.get(target.getName());
        //String key = message.getTarget().getKeys().getGetKey();

        // validate key
        if (!validateSpaceKey(KeyType.GET, target)) {
            message.getTarget().setKeys(null); // clear keys from the response
            return generateFailureMsg(message);
        }

        // handle the request
        message.getTarget().setKeys(null); // clear keys from the response
        
        // get all restituisce una lista, in questa funzione non era implementato 
        //abbiamo creato una lista
        List<Object[]> tuples = null;
        Template t = message.getTemplate();
        if(this.isMonitored()) {
        	monitor = monitors.get(this.getName()).getMonitor();
        	tuples = monitor.getAll(sw ,t.getFields());
			
        }
        else {
        	// lasciato come era prima, manca il valore di ritorno da passare al response
        	spaces.get(target.getName()).getSpace().getAll(message.getTemplate().getFields());
        }
        
        pSpaceMessage response = new pSpaceMessage(
            MessageType.GETALL, //MessageType op,
            message.getSession(), //String session,
            message.getTarget(), //SpaceProperties target,
            // TODO attach template to data
            null, //DataProperties data,
            new Status(200, "OK") //Status status)
        );

        return response;
    }

    public pSpaceMessage handleQueryRequest(pSpaceMessage message)
        throws InterruptedException {
        SpaceProperties target = message.getTarget();
        SpaceWrapper sw = spaces.get(target.getName());
        //String key = message.getTarget().getKeys().getQueryKey();

        // validate key
        if (!validateSpaceKey(KeyType.QUERY, target)) {
            message.getTarget().setKeys(null); // clear keys from the response
            return generateFailureMsg(message);
        }

        // handle the request
        message.getTarget().setKeys(null); // clear keys from the response
        Template t = message.getTemplate();
        Tuple tuple = null;
        Space space = spaces.get(target.getName()).getSpace();
        
        if(this.isMonitored()) {
        	monitor = monitors.get(this.getName()).getMonitor();
        	tuple = new Tuple(monitor.query(sw ,t.getFields()));
			
        }
        else {
        	//funziona come prima, ma abbiamo ridotto il codice
        	tuple = new Tuple(space.query(t.getFields()));
        }
        // commentato il codice precedente
        //Tuple tuple = new Tuple(spaces.get(target.getName()).getSpace().query(message.getTemplate().getFields()));

        pSpaceMessage response = new pSpaceMessage(
            MessageType.QUERY, //MessageType op,
            message.getSession(), //String session,
            message.getTarget(), //SpaceProperties target,
            new DataProperties("tuple", tuple), //DataProperties data,
            new Status(200, "OK") //Status status)
        );

        return response;
    }

    public pSpaceMessage handleQueryPRequest(pSpaceMessage message)
        throws InterruptedException {
        SpaceProperties target = message.getTarget();
        SpaceWrapper sw = spaces.get(target.getName());
//        String key = message.getTarget().getKeys().getQueryKey();

        // validate key
        if (!validateSpaceKey(KeyType.QUERY, target)) {
            message.getTarget().setKeys(null); // clear keys from the response
            return generateFailureMsg(message);
        }

        // handle the request
        message.getTarget().setKeys(null); // clear keys from the response
        // TODO insert template
        Template t = message.getTemplate();
        Tuple tuple = null;
        Space space = spaces.get(target.getName()).getSpace();
        
        if(this.isMonitored()) {
        	monitor = monitors.get(this.getName()).getMonitor();
        	tuple = new Tuple(monitor.queryp(sw ,t.getFields()));
			
        }
        else {
        	spaces.get(target.getName()).getSpace().queryp(null);
        }
       

        pSpaceMessage response = new pSpaceMessage(
            MessageType.QUERYP, //MessageType op,
            message.getSession(), //String session,
            message.getTarget(), //SpaceProperties target,
            // TODO attach template to data
            null, //DataProperties data,
            new Status(200, "OK") //Status status)
        );

        return response;
    }

    public pSpaceMessage handleQueryAllRequest(pSpaceMessage message)
        throws InterruptedException {
        SpaceProperties target = message.getTarget();
        SpaceWrapper sw = spaces.get(target.getName());
//        String key = message.getTarget().getKeys().getQueryKey();

        // validate key
        if (!validateSpaceKey(KeyType.QUERY, target)) {
            message.getTarget().setKeys(null); // clear keys from the response
            return generateFailureMsg(message);
        }

        // handle the request
        message.getTarget().setKeys(null); // clear keys from the response
        Template t = message.getTemplate();
        List<Object[]> tuples = null;
        if(this.isMonitored()) {
        	monitor = monitors.get(this.getName()).getMonitor();
        	tuples = monitor.queryAll(sw ,t.getFields());
			
        }
        else {
        	spaces.get(target.getName()).getSpace().queryAll(message.getTemplate().getFields());
        }
        

        pSpaceMessage response = new pSpaceMessage(
            MessageType.QUERYALL, //MessageType op,
            message.getSession(), //String session,
            message.getTarget(), //SpaceProperties target,
            // TODO attach template to data
            null, //DataProperties data,
            new Status(200, "OK") //Status status)
        );

        return response;
    }

    public void attach(Space space) {
    }

    public void removeSpace(String name) {
    }

    public String getName() {
        return name;
    }

    public boolean validateSpaceKey(KeyType type, SpaceProperties props) {
        SpaceWrapper space = spaces.get(props.getName());

        if (space.getKeys() == null) return true; // no keys are set
        if (props.getKeys() == null) return false; // keys are set but no keys supplied

        String actualKey = null;
        String givenKey = null;

        // find the keys
        switch (type) {
            case PUT:
                actualKey = space.getKeys().getPutKey();
                givenKey = props.getKeys().getPutKey();
                break;
            case GET:
                actualKey = space.getKeys().getGetKey();
                givenKey = props.getKeys().getGetKey();
                break;
            case QUERY:
                actualKey = space.getKeys().getQueryKey();
                givenKey = props.getKeys().getQueryKey();
                break;
        }

        if (actualKey == null) return true; // allow all ops if key is unset

        // check if givenKey is unset when it shouldn't be
        if (actualKey != null || givenKey == null) {
            return false;
        }

        // compare the keys
        if (givenKey.equals(actualKey)) {
            return true;
        }

        // keys did not match
        return false;
    }


    public boolean validateKey(String key) {
        if (getConfig().getServerKey().equals(key)) { // FIXME this could be returned directly
            return true;
        }

        return false;
    }

    public void addSpace(SpaceWrapper space) {
        spaces.put(space.getProperties().getName(), space);
    }

    public SpaceWrapper getSpace(String name) {
        return spaces.get(name);
    }

	public String getKey() {
		return key;
	}
}
