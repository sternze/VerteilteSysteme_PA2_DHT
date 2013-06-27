package chord.data;

import java.util.Date;

import chord.interfaces.IMyChord;

public class ChordContact {
	private IMyChord contact;
	private Date timestamp;
	
	public ChordContact(IMyChord contact) {
		this.contact = contact;
		this.timestamp = new Date();
	}
	
	public IMyChord getContact() {
		return contact;
	}
	
	public Date getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
}
