package org.jspace;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;
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
import org.jspace.protocol.SpaceType;
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

        
        if (put( target.getName() , message.getTuple().getTuple() )) {
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

 

	public boolean put(String name, Object[] tuple) throws InterruptedException {
        SpaceWrapper sw = spaces.get(name);
        Space space = sw.getSpace();

        if (space != null) {
            return space.put(tuple);
        } else {
            return false;
        }
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
        
        
        Object[] tuple = get( target.getName(), message.getTemplate());
        if (tuple != null ) {
            return new pSpaceMessage(
                    MessageType.GET, //MessageType op,
                    message.getSession(), //String session,
                    message.getTarget(), //SpaceProperties target,
                    // TODO attach template to data
                    new DataProperties("tuple", new Tuple(tuple)), //DataProperties data,
                    new Status(200, "OK") //Status status)
             );            
        } else {
            return generateFailureMsg(message);
        }
      
    }

    public Object[] get(String name, Template t) throws InterruptedException {
    	SpaceWrapper sw = spaces.get(name);
        Space space = sw.getSpace();
        if (space != null) {
        	return space.get(t.getFields());
        }
        else {
        	return null;
        }
	}
    
    public Object[] get(String name,Predicate<Repository> repoCondition,Predicate<Object[]> p, Template t) throws InterruptedException {
    	SpaceWrapper sw = spaces.get(name);
        Space space = sw.getSpace();
        if (space != null) {
        	if (repoCondition.test(this)) {
        		return space.get(p,t.getFields());
        	}
        	return null;
        }
        else {
        	return null;
        }
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
        
        
        Object[] tuple = getp( target.getName(), message.getTemplate());
        if (tuple != null ) {
            return new pSpaceMessage(
                    MessageType.GETP, //MessageType op,
                    message.getSession(), //String session,
                    message.getTarget(), //SpaceProperties target,
                    // TODO attach template to data
                    new DataProperties("tuple", new Tuple(tuple)), //DataProperties data,
                    new Status(200, "OK") //Status status)
             );            
        } else {
            return generateFailureMsg(message);
        }
    }
	
    public Object[] getp(String name, Template t) throws InterruptedException {
    	SpaceWrapper sw = spaces.get(name);
        Space space = sw.getSpace();
        if (space != null) {
        	return space.getp(t.getFields());
        }
        else {
        	return null;
        }
	}
    
    public Object[] getp(String name,Predicate<Repository> repoCondition,Predicate<Object[]> p, Template t) throws InterruptedException {
    	SpaceWrapper sw = spaces.get(name);
        Space space = sw.getSpace();
        if (space != null) {
        	if (repoCondition.test(this)) {
        		return space.getp(p,t.getFields());
        	}
        	return null;
        }
        else {
        	return null;
        }
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
        
        List<Object[]> tuples = getAll( target.getName(), message.getTemplate());
        if (tuples != null ) {
            return new pSpaceMessage(
                    MessageType.GETALL, //MessageType op,
                    message.getSession(), //String session,
                    message.getTarget(), //SpaceProperties target,
                    // TODO attach template to data
                    new DataProperties("tuples", tuples), //DataProperties data,
                    new Status(200, "OK") //Status status)
             );            
        } else {
            return generateFailureMsg(message);
        }
        
    }
    
    public List<Object[]> getAll(String name, Template t) throws InterruptedException {
    	SpaceWrapper sw = spaces.get(name);
        Space space = sw.getSpace();
        if (space != null) {
        	return space.getAll(t.getFields());
        }
        else {
        	return null;
        }
	}
    
    public List<Object[]> getAll(String name,Predicate<Repository> repoCondition,Predicate<Object[]> p, Template t) throws InterruptedException {
    	SpaceWrapper sw = spaces.get(name);
        Space space = sw.getSpace();
        if (space != null) {
        	if (repoCondition.test(this)) {
        		return space.getAll(p,t.getFields());
        	}
        	return null;
        }
        else {
        	return null;
        }
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
        
        Object[] tuple = query( target.getName(), message.getTemplate());
        if (tuple != null ) {
            return new pSpaceMessage(
                    MessageType.QUERY, //MessageType op,
                    message.getSession(), //String session,
                    message.getTarget(), //SpaceProperties target,
                    // TODO attach template to data
                    new DataProperties("tuple", new Tuple(tuple)), //DataProperties data,
                    new Status(200, "OK") //Status status)
             );            
        } else {
            return generateFailureMsg(message);
        }
        
    }
    
    
    public Object[] query(String name, Template t) throws InterruptedException {
    	SpaceWrapper sw = spaces.get(name);
        Space space = sw.getSpace();
        if (space != null) {
        	return space.query(t.getFields());
        }
        else {
        	return null;
        }
	}
    
    public Object[] query(String name,Predicate<Repository> repoCondition,Predicate<Object[]> p, Template t) throws InterruptedException {
    	SpaceWrapper sw = spaces.get(name);
        Space space = sw.getSpace();
        if (space != null) {
        	if (repoCondition.test(this)) {
        		return space.query(p,t.getFields());
        	}
        	return null;
        }
        else {
        	return null;
        }
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
        
        Object[] tuple = queryp( target.getName(), message.getTemplate());
        if (tuple != null ) {
            return new pSpaceMessage(
                    MessageType.QUERYP, //MessageType op,
                    message.getSession(), //String session,
                    message.getTarget(), //SpaceProperties target,
                    // TODO attach template to data
                    new DataProperties("tuple", new Tuple(tuple)), //DataProperties data,
                    new Status(200, "OK") //Status status)
             );            
        } else {
            return generateFailureMsg(message);
        }
    }
    
    public Object[] queryp(String name, Template t) throws InterruptedException {
    	SpaceWrapper sw = spaces.get(name);
        Space space = sw.getSpace();
        if (space != null) {
        	return space.queryp(t.getFields());
        }
        else {
        	return null;
        }
	}
    
    public Object[] queryp(String name,Predicate<Repository> repoCondition,Predicate<Object[]> p, Template t) throws InterruptedException {
    	SpaceWrapper sw = spaces.get(name);
        Space space = sw.getSpace();
        if (space != null) {
        	if (repoCondition.test(this)) {
        		return space.queryp(p,t.getFields());
        	}
        	return null;
        }
        else {
        	return null;
        }
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
        
        List<Object[]> tuples = queryAll( target.getName(), message.getTemplate());
        if (tuples != null ) {
            return new pSpaceMessage(
                    MessageType.QUERYALL, //MessageType op,
                    message.getSession(), //String session,
                    message.getTarget(), //SpaceProperties target,
                    // TODO attach template to data
                    new DataProperties("tuples", tuples), //DataProperties data,
                    new Status(200, "OK") //Status status)
             );            
        } else {
            return generateFailureMsg(message);
        }
        
    }
    
    public List<Object[]> queryAll(String name, Template t) throws InterruptedException {
    	SpaceWrapper sw = spaces.get(name);
        Space space = sw.getSpace();
        if (space != null) {
        	return space.queryAll(t.getFields());
        }
        else {
        	return null;
        }
	}
    
    public List<Object[]> queryAll(String name,Predicate<Repository> repoCondition,Predicate<Object[]> p, Template t) throws InterruptedException {
    	SpaceWrapper sw = spaces.get(name);
        Space space = sw.getSpace();
        if (space != null) {
        	if (repoCondition.test(this)) {
        		return space.queryAll(p,t.getFields());
        	}
        	return null;
        }
        else {
        	return null;
        }
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

	public SpaceWrapper newSpace(String name, SpaceProperties props) throws InterruptedException {
		if (spaces.containsKey(name)) {
			return spaces.get(name);
		}
        props.setUUID(UUID.randomUUID().toString());
		
    SpaceWrapper space = null;

    switch (props.getType()) {
        case PILE:
            space = new SpaceWrapper(new PileSpace(), props);
        case QUEUE:
            space = new SpaceWrapper(new QueueSpace(), props);
        case RANDOM:
            space = new SpaceWrapper(new RandomSpace(), props);
        case SEQUENTIAL:
            space = new SpaceWrapper(new SequentialSpace(), props);
        case STACK:
            space = new SpaceWrapper(new StackSpace(), props);
        default:
            break;
    }	
    	spaces.put(props.getName(), space);
    	return space;
	}
	
}
