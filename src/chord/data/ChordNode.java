package chord.data;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Set;

import chord.interfaces.IChordNode;
import chord.interfaces.IMyChord;
import chord.utils.ChordUtils;

public class ChordNode extends UnicastRemoteObject implements Serializable, IChordNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private FingerTable fingerTable;

	private Entries entries;
	private Entries replicas;
	
	private int nextEntry = 0;
	private String ip;
	private String serviceName;
	private int port;
	private int keySize;
	private long identifier;
	// This list stores information about replicas.
	public static IChordNode[] successors = new IChordNode[] { null, null, null };
	private IChordNode predecessor;
	private boolean replicateData;
	
	public ChordNode(String ip, int port, String serviceName, int keySize, boolean isTask3) throws RemoteException {
		this.ip = ip;
		this.port = port;
		this.serviceName = serviceName;
		this.keySize = keySize;
		this.identifier = calculateChordId(ip + ":" + port);
		this.replicateData = isTask3;
		init();
	}

	public IChordNode getPredecessor() {
		return predecessor;
	}

	public void setPredecessor(IChordNode predecessor) {
		this.predecessor = predecessor;
	}

	public String getIp() {
		return ip;
	}

	public String getServiceName() {
		return serviceName;
	}

	public int getPort() {
		return port;
	}

	public int getKeySize() {
		return keySize;
	}

	public long getIdentifier() {
		return identifier;
	}
	
	public void setIdentifier(long identifier) {
		this.identifier = identifier;
	}

	private long calculateChordId(String IpAndPort) {
		long chordID = 0;
		
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-1");
			byte[] bla = md.digest(IpAndPort.getBytes());
			
			long number = ByteBuffer.wrap(bla).getLong();
			
			long mod = (long)Math.pow(2, keySize);
			
			chordID = number % mod;
			if(chordID < 0) {
				chordID += (long)Math.pow(2, keySize);
			}
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		
		return chordID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (identifier ^ (identifier >>> 32));
		result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		result = prime * result + port;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChordNode other = (ChordNode) obj;
		if (identifier != other.getIdentifier())
			return false;
		if (ip == null) {
			if (other.getIp() != null)
				return false;
		} else if (!ip.equals(other.getIp()))
			return false;
		if (port != other.getPort())
			return false;
		return true;
	}
	
	
	
	public ChordNode(long manualId, String ip, int port, String serviceName, int keySize, boolean isTask3) throws RemoteException {
		this(ip, port, serviceName, keySize, isTask3);
		
		setIdentifier(manualId);
		init();
	}
	
	private void init() throws RemoteException {
		System.out.println(new Date() + " created me {");
		System.out.println(new Date() + " \t IP: " + getIp());
		System.out.println(new Date() + " \t Port: " + getPort());
		System.out.println(new Date() + " \t ServiceName: " + getServiceName());
		System.out.println(new Date() + " \t ChordIdentifier: " + getIdentifier());
		System.out.println(new Date() + " }");
				
		this.fingerTable = new FingerTable(this);
		this.entries = new Entries();
		this.replicas = new Entries();

	}
	
	public void create() {
		setPredecessor(null);
		setSuccessor(this);
		
		System.out.println(new Date() + " i'm the first one");
	}
	
	public void join(IChordNode nodeToJoin) throws RemoteException {
		
		if(nodeToJoin instanceof ChordNode) {
			nodeToJoin = getInterfaceObjectFromRealObject(nodeToJoin);
		}
		
		System.out.println(new Date() + " joining node: " + nodeToJoin.getIdentifier());
		
		setPredecessor(null);

		try {
			IChordNode node = nodeToJoin.findSuccessor(getIdentifier());
			
			setSuccessor(node);
        	
        	if(node != null) {
        		Set<MyValue> migratableValues = node.migrateDataAfterJoin(this);
        		
        		for(MyValue myVal : migratableValues) {
        			this.entries.add(myVal);
        		}
        		System.out.println(new Date() + " my successor is: " + node.getIdentifier());
        	}
			
		
		} catch (RemoteException e) { 
			e.printStackTrace();
		}
	}
	
	private IChordNode getInterfaceObjectFromRealObject(IChordNode nodeToJoin) {
		try {
			IMyChord contact = (IMyChord)Naming.lookup("rmi://" + nodeToJoin.getIp() + ":" + nodeToJoin.getPort() + "/" + nodeToJoin.getServiceName());
			
			return contact.getRemoteChordNodeObject();
		} catch (Exception ex) {
			
		}
		return null;
	}

	public IChordNode findSuccessor(long id) {
		IChordNode retVal = null;
		try {
			IChordNode predecessor = findPredecessor(id);
			retVal = predecessor.getSuccessor();
		} catch(Exception ex) {
			
		}
		
		return retVal;
	}
	
	public IChordNode findPredecessor(long id) throws RemoteException{
		IChordNode node = this;
		while (!ChordUtils.inRangeLeftOpenIntervall(id, node.getIdentifier(), node.getSuccessor().getIdentifier())) {
			if (node.getIdentifier() == getIdentifier()) {
				node = closestPrecedingFinger(id);
			} else {
				try {
					node = node.closestPrecedingFinger(id);
				} catch (RemoteException e) { 
					e.printStackTrace();
				}
			}
		}
		
		return node;
	}
	
	public IChordNode closestPrecedingFinger(long id) throws RemoteException {
		for (int i = fingerTable.size() - 1; i >= 0; i--) {
			if (fingerTable.get(i).getNode() != null && ChordUtils.inRangeOpenIntervall(fingerTable.get(i).getNode().getIdentifier(), getIdentifier(), id)) {
				return fingerTable.get(i).getNode();
			}
		}
		
		return this;
	}
	
	public void stabilize() throws RemoteException {
		IChordNode node = null;
		try {
			node = getSuccessor().getPredecessor();
		
		
			if (node != null) {
				if (ChordUtils.inRangeOpenIntervall(node.getIdentifier(), getIdentifier(), getSuccessor().getIdentifier())) {
					setSuccessor(node);
				}
			}
			
			if (getSuccessor() != null && getSuccessor().getIdentifier() != getIdentifier()) {
				IChordNode successor = getSuccessor();
				
				try {
					successor.notify(this);
				} catch (RemoteException e) { 
					e.printStackTrace();
				}
			}
		} catch(Exception ex) {
			successorDiedMakeReconnect(0);
		}
	}
	
	private void successorDiedMakeReconnect(int i) {
		System.out.println("Successor " + i + " died trying to make a reconnect.");
		if(i+1 < successors.length) {
			IChordNode succ = successors[i+1];
			try {
				System.out.println("Sending a ping now to " + i+1);
				succ.ping();
				System.out.println("Ping finished.");
				setSuccessor(succ);
				succ.notify(this);
				System.out.println("Successor set.");
			} catch (Exception ex) {
				successorDiedMakeReconnect(i+1);
			}
		} else {
			System.out.println("Too many successors died.");
		}
	}

	public void notify(IChordNode node) {
		//System.out.println(new Date() + " got notfication from: " + node.getIdentifier());
		try {
			if (getPredecessor() == null) {
				setPredecessor(node);
			} else if (ChordUtils.inRangeOpenIntervall(node.getIdentifier(), getPredecessor().getIdentifier(), getIdentifier())) {
				getPredecessor().notifyPredecessor(node);
				setPredecessor(node);
			} else if(node.getIdentifier() == getPredecessor().getIdentifier()) {
				setPredecessor(node);
			}
		} catch (RemoteException e) {
			setPredecessor(node);
		}
	}
	
	public void notifyPredecessor(IChordNode node) throws RemoteException {
		try {
			if (getSuccessor() == null || ChordUtils.inRangeOpenIntervall(node.getIdentifier(), getIdentifier(), getSuccessor().getIdentifier())) {
				setSuccessor(node);
			}
		} catch (RemoteException re) {
			successorDiedMakeReconnect(0);
			notifyPredecessor(node);
		}
	}
	
	public void fixFingers() {
		//System.out.println(new Date() + " (" + me.getIdentifier() + ") fix fingers: start(" + fingerTable.get(i).getStart() + ")");
		IChordNode node = findSuccessor(fingerTable.get(nextEntry).getStart());
		
		if (nextEntry == 0) {
			setSuccessor(node);
		} else {
			fingerTable.get(nextEntry).setNode(node);
		}
		
		if (nextEntry == getKeySize() - 1) {
			nextEntry = 0;
		} else {
			nextEntry++;
		}
	}
	
	public void printStatus() throws RemoteException {
		String s = new Date() + " me: " + getIdentifier() + " successor: "; 
		s += getSuccessor() == null ? "null" : getSuccessor().getIdentifier();
		s += " predecessor: ";
		s += getPredecessor() == null ? "null" : getPredecessor().getIdentifier();
		s += " finger table size: " + fingerTable.size();
		
		for (int i = 0; i < fingerTable.size(); i++) {
			if (i % 7 == 0) {
				System.out.println(s);
				s = new Date() + " ";
				
				if (fingerTable.get(i).getNode() != null)
					s += fingerTable.get(i).getNode().getIdentifier() + " ";
			} else {
				if (fingerTable.get(i).getNode() != null)
					s += fingerTable.get(i).getNode().getIdentifier() + " ";
			}
		}
		
		System.out.println(s);
	}
	
	@Override
	public IChordNode getSuccessor() {
		if (successors[0] == null) {
			successors[0] = fingerTable.get(0).getNode();
		}
		
		return successors[0];
	}
	
	public void setSuccessor(IChordNode node) {
		if(fingerTable != null) {
			fingerTable.get(0).setNode(node);
		}
		successors[0] = node;
	}
	
	public IChordNode getMe() {
		return this;
	}
	
	public FingerTable getFingerTable() {
		return fingerTable;
	}
	
	public Entries getEntries() {
		return entries;
	}
	
	public IChordNode[] getSuccessors() {
		return successors;
	}
	
	public String getIdAndEntryCount() {
		return "(" + entries.getNumberOfStoredEntries() + ") " + getIdentifier();
	}
	
	public void insertEntry(MyValue toInsert) {
		
		// add entry to local repository
		this.entries.add(toInsert);

		// create set containing this entry for insertion of replicates at all
		// nodes in successor list
		if(replicateData) {
			try {
				getSuccessor().addReplica(toInsert);
				if(getPredecessor() != null) {
					getPredecessor().addReplica(toInsert);
				}
			} catch (RemoteException e) {
				successorDiedMakeReconnect(0);
				insertEntry(toInsert);
			}
		}
	}
	
	public void insertData(MyValue entryToInsert) {
		// check parameters
		if (entryToInsert.getData() == null) {
			throw new NullPointerException(
					"Neither parameter may have value null!");
		}

		boolean inserted = false;
		while (!inserted) {
			// find successor of id
			IChordNode responsibleNode = this.findSuccessor(entryToInsert.getId());

			// invoke insertEntry method
			try {
				try {
					responsibleNode.insertEntry(entryToInsert);
				} catch (RemoteException e) { 
					e.printStackTrace();
				}
				inserted = true;
			} catch (Exception ex) {
				continue;
			}
		}
	}
	
	public void removeEntry(MyValue entryToRemove) {

		// remove entry from repository
		this.entries.remove(entryToRemove);

		if(replicateData) {
			try {					
				getSuccessor().removeReplica(entryToRemove);
				if(getPredecessor() != null) {
					getPredecessor().removeReplica(entryToRemove);
				}
			} catch (RemoteException e) {
				successorDiedMakeReconnect(0);
				removeEntry(entryToRemove);
			}
		}
	}
	
	public void removeData(MyValue entryToDelete) {

		// check parameters
		if (entryToDelete.getData() == null) {
			throw new NullPointerException(
					"Neither parameter may have value null!");
		}

		boolean removed = false;
		while (!removed) {

			// find successor of id
			IChordNode responsibleNode;
			responsibleNode = findSuccessor(entryToDelete.getId());

			
			// invoke removeEntry method
			try {
				try {
					responsibleNode.removeEntry(entryToDelete);
				} catch (RemoteException e) { 
					e.printStackTrace();
				}
				removed = true;
			} catch (Exception e1) {
				continue;
			}
		}
	}

	public void leave() throws RemoteException {
		//network was not created yet.
		if(getSuccessor().getIdentifier() == getIdentifier() && getPredecessor() == null) {
			return;
		}
		
		IChordNode successor = getSuccessor();
		IChordNode pred = getPredecessor();
		if (successor != null && pred != null) {
			try {
				successor.leavesNetwork(pred);
			} catch (RemoteException e) {
				successorDiedMakeReconnect(0);
				leave();
			}
		}
	}

	public Set<MyValue> query(long id) {
		Set<MyValue> result = null;

		boolean retrieved = false;
		while (!retrieved) {
			// find successor of id
			IChordNode responsibleNode = null;

			responsibleNode = findSuccessor(id);

			// invoke retrieveEntry method
			try {
				try {
					responsibleNode.queryData(id);
				} catch (RemoteException e) { 
					e.printStackTrace();
				}
				
				retrieved = true;
			} catch (Exception e1) {
				continue;
			}
		}
		
		return result;

	}
	
	public Set<MyValue> queryData(long id) {
		return this.entries.getEntries(id);
	}

	public Set<MyValue> migrateDataAfterJoin(IChordNode potentialPredecessor) throws RemoteException {
		Set<MyValue> copiedEntries = this.entries.getEntriesInInterval(potentialPredecessor.getIdentifier(), getIdentifier());
		this.entries.removeAll(copiedEntries);

		return copiedEntries;
	}

	@Override
	public void leavesNetwork(IChordNode newPredecessor) throws RemoteException {
		setPredecessor(newPredecessor);
		if(replicateData) {
			System.out.println("Replicating data to successor, after my former predecessor left.");
			//TODO: implement this!!!
		}
	}

	@Override
	public void addReplica(MyValue data) throws RemoteException {
		this.replicas.add(data);
	}

	@Override
	public void removeReplica(MyValue data) throws RemoteException {
		this.replicas.remove(data);
	}

	@Override
	public Entries getAllReplicas() throws RemoteException {
		return this.replicas;
	}

	@Override
	public void ping() throws RemoteException {
		// stays empty
	}
}
