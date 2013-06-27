package chord.data;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Date;

import chord.interfaces.IMyChord;
import chord.utils.ChordUtils;

public class ChordNode extends Node implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private FingerTable fingerTable;
	
	private int nextEntry = 0;
	
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
	
	public void create() {
		setPredecessor(null);
		setSuccessor(this);
		
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
		return predecessor.getSuccessor();
	}
	
	public Node findPredecessor(long id) {
		Node node = this;
		while (!ChordUtils.inRangeLeftOpenIntervall(id, node.getIdentifier(), node.getSuccessor().getIdentifier())) {
			try {
				IMyChord contact = (IMyChord) Naming.lookup("rmi://" + node.getIp() + ":" + node.getPort() + "/" + node.getServiceName());
	        	
				node = contact.closestPrecedingFinger(id);
				
	        	contact = null;
			} catch (MalformedURLException | RemoteException | NotBoundException e) { 
				e.printStackTrace();
			}
		}
		
		return  node;
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
		Node node = getSuccessor().getPredecessor();
		if (node != null) {
			if (ChordUtils.inRangeOpenIntervall(node.getIdentifier(), getIdentifier(), getSuccessor().getIdentifier())) {
				setSuccessor(node);
			}
		}
		
		/*if (fingerTable.get(0).getNode().getIdentifier() != getIdentifier()) {
			notify(this);
		} else if (fingerTable.get(0).getNode() != null) {*/
		if (getSuccessor() != null && getSuccessor().getIdentifier() != getIdentifier()) {
			Node successor = getSuccessor();
			
			try {
				IMyChord contact = (IMyChord) Naming.lookup("rmi://" + successor.getIp() + ":" + successor.getPort() + "/" + successor.getServiceName());
	        	
				contact.notify(this);
	      
	        	contact = null;
			} catch (MalformedURLException | RemoteException | NotBoundException e) { 
				e.printStackTrace();
			}
		}
	}
	
	public void notify(Node node) {
		//System.out.println(new Date() + " got nitfication from: " + node.getIdentifier());
		
		if (getPredecessor() == null) {
			setPredecessor(node);
		} else if (ChordUtils.inRangeOpenIntervall(node.getIdentifier(), getPredecessor().getIdentifier(), getIdentifier())) {
			try {
				IMyChord contact = (IMyChord) Naming.lookup("rmi://" + getPredecessor().getIp() + ":" + getPredecessor().getPort() + "/" + getPredecessor().getServiceName());
	        	
				contact.notifyPredecessor(node);
				setPredecessor(node);
	      
	        	contact = null;
			} catch (MalformedURLException | RemoteException | NotBoundException e) { 
				e.printStackTrace();
			}
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
		fingerTable.get(0).setNode(node);
		super.setSuccessor(node);
	}
	
	public Node getMe() {
		return this;
	}
	
	public FingerTable getFingerTable() {
		return fingerTable;
	}
}
