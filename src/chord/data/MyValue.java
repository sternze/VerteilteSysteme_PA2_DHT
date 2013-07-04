package chord.data;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MyValue implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2632374711647032471L;
	
	private long key;
	private byte[] data;
	private int keysize;
	
	
	public MyValue(int keysize) {
		this.keysize = keysize;
	}
	
	public MyValue(byte[] data, int keysize) {
		this.keysize = keysize;
		this.data = data;
		calculateKey();
	}
	
	public long getId() {
		return key;
	}

	public byte[] getData() {
		return data;
	}
	
	public void setData(byte[] data) {
		this.data = data;
	}
	
	private void calculateKey() {
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-1");
			byte[] bla = md.digest(data);
			
			
			long key = ByteBuffer.wrap(bla).getLong() % (long)Math.pow(2, keysize);

			if(key < 0) {
				key += (long)Math.pow(2, keysize);
			}
			
			this.key = key;
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} 
		//System.out.println("Key calculated and set");
	}
}
