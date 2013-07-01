package chord.data;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import chord.interfaces.IMyChord;
import chord.utils.ChordUtils;

public class ChordNode extends Node implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private FingerTable fingerTable;
	
	private Entries entries;
	
	private int nextEntry = 0;
	
	// This list stores information about replicas.
	private List<Node> successors = new ArrayList<Node>();
	
	public ChordNode(String ip, int port, String serviceName, int keySize) {
		super(ip, port, serviceName, keySize);
		
		System.out.println(new Date() + " created me {");
		System.out.println(new Date() + " \t IP: " + getIp());
		System.out.println(new Date() + " \t Port: " + getPort());
		System.out.println(new Date() + " \t ServiceName: " + getServiceName());
		System.out.println(new Date() + " \t ChordIdentifier: " + getIdentifier());
		System.out.println(new Date() + " }");
		
		this.fingerTable = new FingerTable(this);
	}
	
	public ChordNode(long manualId, String ip, int port, String serviceName, int keySize) {
		super(ip, port, serviceName, keySize);
		setIdentifier(manualId);
		
		System.out.println(new Date() + " created me {");
		System.out.println(new Date() + " \t IP: " + getIp());
		System.out.println(new Date() + " \t Port: " + getPort());
		System.out.println(new Date() + " \t ServiceName: " + getServiceName());
		System.out.println(new Date() + " \t ChordIdentifier: " + getIdentifier());
		System.out.println(new Date() + " }");
		
		this.fingerTable = new FingerTable(this);
	}
	
	public void create() {
		setPredecessor(null);
		setSuccessor(new Node(this));
		
		System.out.println(new Date() + " i'm the first one");
	}
	
	public void join(Node nodeToJoin) {
		System.out.println(new Date() + " joining node: " + nodeToJoin.getIdentifier());
		
		setPredecessor(null);

		try {
			IMyChord contact = (IMyChord) Naming.lookup("rmi://" + nodeToJoin.getIp() + ":" + nodeToJoin.getPort() + "/" + nodeToJoin.getServiceName());
        	
			Node node = contact.findSuccessor(getIdentifier());
			setSuccessor(node);
			
			System.out.println(new Date() + " my successor is: " + node.getIdentifier());
			
        	contact = null;
		} catch (MalformedURLException | RemoteException | NotBoundException e) { 
			e.printStackTrace();
		}
	}
	
	public Node findSuccessor(long id) {
		Node predecessor = findPredecessor(id);
		Node retVal = getNodeOfOtherNode(predecessor, true);
		
		return retVal;
	}
	
	public Node findPredecessor(long id) {
		Node node = this;
		while (!ChordUtils.inRangeLeftOpenIntervall(id, node.getIdentifier(), node.getSuccessor().getIdentifier())) {
			if (node.getIdentifier() == getIdentifier()) {
				node = closestPrecedingFinger(id);
			} else {
				try {
					IMyChord contact = (IMyChord) Naming.lookup("rmi://" + node.getIp() + ":" + node.getPort() + "/" + node.getServiceName());
		        	
					node = contact.closestPrecedingFinger(id);
					
		        	contact = null;
				} catch (MalformedURLException | RemoteException | NotBoundException e) { 
					e.printStackTrace();
				}
			}
		}
		
		return node;
	}
	
	public Node closestPrecedingFinger(long id) {
		for (int i = fingerTable.size() - 1; i >= 0; i--) {
			if (fingerTable.get(i).getNode() != null && ChordUtils.inRangeOpenIntervall(fingerTable.get(i).getNode().getIdentifier(), getIdentifier(), id)) {
				return fingerTable.get(i).getNode();
			}
		}
		
		return this;
	}
	
	public void stabilize() {
		
		Node node = getNodeOfOtherNode(getSuccessor(), false);
		
		if (node != null) {
			if (ChordUtils.inRangeOpenIntervall(node.getIdentifier(), getIdentifier(), getSuccessor().getIdentifier())) {
				setSuccessor(node);
			}
		}
		
		if (getSuccessor() != null && getSuccessor().getIdentifier() != getIdentifier()) {
			Node successor = getSuccessor();
			
			try {
				IMyChord contact = (IMyChord) Naming.lookup("rmi://" + successor.getIp() + ":" + successor.getPort() + "/" + successor.getServiceName());
				
				contact.notify(new Node(this));
	      
	        	contact = null;
			} catch (MalformedURLException | RemoteException | NotBoundException e) { 
				e.printStackTrace();
			}
		}
	}
	
	// gets either the successor or the predecessor of the contact node.
	private Node getNodeOfOtherNode(Node contactNode, boolean isSuccessor) {
		Node ret = null;
		
		try {
			IMyChord contact = (IMyChord) Naming.lookup("rmi://" + contactNode.getIp() + ":" + contactNode.getPort() + "/" + contactNode.getServiceName());
        	
			if(isSuccessor) {
				ret = contact.getCurrentSuccessor();
			} else {
				ret = contact.getCurrentPredecessor();
			}
      
        	contact = null;
		} catch (MalformedURLException | RemoteException | NotBoundException e) { 
			e.printStackTrace();
		}
		
		return ret;
	}
	
	public void notify(Node node) {
		//System.out.println(new Date() + " got nitfication from: " + node.getIdentifier());
		
		if (getPredecessor() == null) {
			setPredecessor(node);
		} else if (ChordUtils.inRangeOpenIntervall(node.getIdentifier(), getPredecessor().getIdentifier(), getIdentifier())) {
			try {
				IMyChord contact = (IMyChord) Naming.lookup("rmi://" + getPredecessor().getIp() + ":" + getPredecessor().getPort() + "/" + getPredecessor().getServiceName());				
				
				setPredecessor(node);
				contact.notifyPredecessor(node);
	      
	        	contact = null;
			} catch (MalformedURLException | RemoteException | NotBoundException e) { 
				e.printStackTrace();
			}
		} else if(node.getIdentifier() == getPredecessor().getIdentifier()) {
			setPredecessor(node);
		}
		
	}
	
	public void notifyPredecessor(Node node) {
		if (getSuccessor() == null || ChordUtils.inRangeOpenIntervall(node.getIdentifier(), getIdentifier(), getSuccessor().getIdentifier())) {
			setSuccessor(node);
		}
	}
	
	public void fixFingers() {
		//System.out.println(new Date() + " (" + me.getIdentifier() + ") fix fingers: start(" + fingerTable.get(i).getStart() + ")");
		Node node = findSuccessor(fingerTable.get(nextEntry).getStart());
		
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
	
	public void printStatus() {
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
	public Node getSuccessor() {
		if (super.getSuccessor() == null) {
			super.setSuccessor(fingerTable.get(0).getNode());
		}
		
		return super.getSuccessor();
	}
	
	public void setSuccessor(Node node) {
		if(fingerTable != null) {
			fingerTable.get(0).setNode(node);
		}
		super.setSuccessor(node);
	}
	
	public Node getMe() {
		return this;
	}
	
	public FingerTable getFingerTable() {
		return fingerTable;
	}
	
	public List<Node> getSuccessors() {
		return this.successors;
	}
	
	public void insertEntry_ChordInternal(MyValue toInsert, boolean includeReplicas) {
		
		// add entry to local repository
		this.entries.add(toInsert);

		// create set containing this entry for insertion of replicates at all
		// nodes in successor list
		if(includeReplicas) {
			Set<MyValue> newEntries = new HashSet<MyValue>();
			newEntries.add(toInsert);
	
			// invoke insertReplicates method on all nodes in successor list
			final Set<MyValue> mustBeFinal = new HashSet<MyValue>(newEntries);
			for (final Node successor : this.getSuccessors()) {
				new Thread(new Runnable() {
					public void run() {
						try {
							// TODO: create RMI call "insertReplicas" on successor.
						} catch (Exception e) {
							// do nothing
						}
					}
				}).start();
			}
		}
	}
	
	public void insertData(MyValue entryToInsert, boolean createReplicas) {
		// check parameters
		if (entryToInsert.getData() == null) {
			throw new NullPointerException(
					"Neither parameter may have value null!");
		}

		boolean inserted = false;
		while (!inserted) {
			// find successor of id
			Node responsibleNode = this.findSuccessor(entryToInsert.getId());

			// invoke insertEntry method
			try {
				try {
					IMyChord contact = (IMyChord) Naming.lookup("rmi://" + responsibleNode.getIp() + ":" + responsibleNode.getPort() + "/" + responsibleNode.getServiceName());
		        	
					
					contact.insertEntry_ChordInternal(entryToInsert, createReplicas);
					
		      
		        	contact = null;
				} catch (MalformedURLException | RemoteException | NotBoundException e) { 
					e.printStackTrace();
				}
				inserted = true;
			} catch (Exception ex) {
				continue;
			}
		}
	}
}
