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
}
