package chord.data;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import chord.interfaces.IMyChord;

public class ContactManager {

	public static final ContactManager contactManager = new ContactManager();
	
	private static HashMap<Long, ChordContact> contacts;
	
	private ContactManager() { 
		contacts = new HashMap<Long, ChordContact>();
		
		Thread manageContacts = new Thread() {

			@Override
			public void run() {
				while (true) {
					synchronized (contacts) {
						Date now = new Date();
						ArrayList<ChordContact> toRemove = new ArrayList<ChordContact>();
						
						for (ChordContact contact : contacts.values()) {
							if (now.getTime() - contact.getTimestamp().getTime() >= 5000) {
								toRemove.add(contact);
							}
						}
						
						for (ChordContact contact : toRemove) {
							contacts.remove(contact);
						}
						
						System.out.println(new Date() + " removed " + toRemove.size() + " contacts, left: " + contacts.size());
					}
					
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		
		manageContacts.start();
	}
	
	public static boolean contains(long id) {
		return contacts.containsKey(id);
	}
	
	public static IMyChord get(Node node) {
		if (node == null) {
			return null;
		}
		
		IMyChord contact = null;
		
		if (!contains(node.getIdentifier())) {
			try {
				contact = (IMyChord) Naming.lookup("rmi://" + node.getIp() + ":" + node.getPort() + "/" + node.getServiceName());
				
				contacts.put(node.getIdentifier(), new ChordContact(contact));
			} catch (MalformedURLException | RemoteException | NotBoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			contact = contacts.get(node.getIdentifier()).getContact();
		}
		
		contacts.get(node.getIdentifier()).setTimestamp(new Date());
		
		return contact;
	}
}
