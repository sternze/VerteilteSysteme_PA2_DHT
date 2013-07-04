package chord.data;

import java.io.Serializable;
import java.math.BigInteger;
import java.rmi.RemoteException;

import chord.interfaces.IChordNode;

public class FingerTableEntry implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int index;
	private long start;
	private long intervalLowerBound;
	private long intervalUpperBound;
	private IChordNode owner;
	private IChordNode node;
	
	public FingerTableEntry(int index, ChordNode chordNode) throws RemoteException { 
		this.index = index;
		this.owner = chordNode;
		this.start = calculateStart(this.index);
		this.intervalLowerBound = this.start;
		this.intervalUpperBound = calculateStart(this.index + 1);
	}

	private long calculateStart(int index) throws RemoteException{
		BigInteger modulus = new BigInteger("2").pow(owner.getKeySize());
		BigInteger offset = new BigInteger("2").pow(index);
		BigInteger start = new BigInteger(owner.getIdentifier() + "").add(offset).mod(modulus);

		return start.longValue();
	}
	
	public int getIndex() {
		return index;
	}
	
	public long getStart() {
		return start;
	}

	public long getIntervalLowerRange() {
		return intervalLowerBound;
	}

	public long getIntervalUpperRange() {
		return intervalUpperBound;
	}

	public IChordNode getNode() {
		return node;
	}
	
	public synchronized void setNode(IChordNode node) {
		this.node = node;
	}
	
	public IChordNode getOwner() {
		return this.owner;
	}

	public IChordNode getSuccessor() throws RemoteException {
		return this.node.getSuccessor();
	}

	public IChordNode getPredecessor() throws RemoteException {
		return this.node.getSuccessor();
	}
}
