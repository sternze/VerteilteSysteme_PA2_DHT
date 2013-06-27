package chord.data;

import java.io.Serializable;
import java.math.BigInteger;

public class FingerTableEntry implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int index;
	private long start;
	private long intervalLowerBound;
	private long intervalUpperBound;
	private Node owner;
	private Node node;
	private Node successor;
	private Node predecessor;
	
	public FingerTableEntry(int index, Node owner) { 
		this.index = index;
		this.owner = owner;
		this.start = calculateStart(this.index);
		this.intervalLowerBound = this.start;
		this.intervalUpperBound = calculateStart(this.index + 1);
	}

	private long calculateStart(int index) {
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

	public Node getNode() {
		return node;
	}
	
	public void setNode(Node node) {
		this.node = node;
		this.successor = this.node.getSuccessor();
		this.predecessor = this.node.getPredecessor();
	}
	
	public Node getOwner() {
		return this.owner;
	}

	public Node getSuccessor() {
		return successor;
	}

	public Node getPredecessor() {
		return predecessor;
	}
}
