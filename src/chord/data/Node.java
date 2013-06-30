package chord.data;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Node implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String ip;
	private String serviceName;
	private int port;
	private int keySize;
	private long identifier;
	private Node successor;
	private Node predecessor;
	
	public Node(String ip, int port, String serviceName, int keySize) {
		this.ip = ip;
		this.port = port;
		this.serviceName = serviceName;
		this.keySize = keySize;
		this.identifier = calculateChordId(ip + ":" + port);
	}
	
	public Node(ChordNode cn) {
		this(cn, 0);
	}
	
	// i wird verwendet um sicherzustellen, dass nicht mehr als zwei ebenen tief kopiert wird.
	private Node(ChordNode cn, int i) {
		this.ip = cn.getIp();
		this.port = cn.getPort();
		this.identifier = cn.getIdentifier();
		this.serviceName = cn.getServiceName();
		this.keySize = cn.getKeySize();
		if(cn.getSuccessor() != null) {
			// diese bedingung dient dazu, dass nicht immer rekursiv versucht wird die
			if(i < 1) {
				if(cn.getSuccessor() instanceof ChordNode) {
					this.successor = new Node((ChordNode)cn.getSuccessor(), i+1);
				} else {
					this.successor = cn.getSuccessor();
				}
			}
		} else {
			this.successor = this;
		}
		
		if(cn.getPredecessor() != null) {
			if(cn.getPredecessor() instanceof ChordNode) {
				this.predecessor = new Node((ChordNode)cn.getPredecessor(), i+1);
			} else {
				this.predecessor = cn.getPredecessor();
			}
		} else {
			this.predecessor = null;
		}
	}
	
	public Node getSuccessor() {
		return successor;
	}

	public void setSuccessor(Node successor) {
		this.successor = successor;
	}

	public Node getPredecessor() {
		return predecessor;
	}

	public void setPredecessor(Node predecessor) {
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
		Node other = (Node) obj;
		if (identifier != other.identifier)
			return false;
		if (ip == null) {
			if (other.ip != null)
				return false;
		} else if (!ip.equals(other.ip))
			return false;
		if (port != other.port)
			return false;
		return true;
	}
	
	
}
