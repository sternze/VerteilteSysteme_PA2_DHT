package chord.data;

import java.io.Serializable;
import java.util.ArrayList;

public class FingerTable implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private ArrayList<FingerTableEntry> fingerTable;
	
	public FingerTable(Node me) {
		this.fingerTable = new ArrayList<FingerTableEntry>();
		
		for (int i = 0; i < me.getKeySize(); i++) {
			this.fingerTable.add(new FingerTableEntry(i, me));
		}
	}
	
	public synchronized FingerTableEntry get(int index) {
		return this.fingerTable.get(index);
	}
	
	public int size() {
		return fingerTable.size();
	}
}
