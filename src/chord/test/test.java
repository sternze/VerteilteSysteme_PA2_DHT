package chord.test;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.util.Enumeration;

import chord.data.ContactManager;
import chord.data.MyValue;
import chord.data.Node;
import chord.interfaces.IMyChord;

public class test {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}
	
	public static void insertData() {
		Node node = new Node("192.168.1.102", 8000, "PA2_MyKV", 3);
		IMyChord contact = ContactManager.get(node);
		
		MyValue data = new MyValue(new String("hallo").getBytes(), 3);
		MyValue data1 = new MyValue(new String("du").getBytes(), 3);
		MyValue data2 = new MyValue(new String("da").getBytes(), 3);
		try {
			contact.insertData(data, true);
			contact.insertData(data1, true);
			contact.insertData(data2, true);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void listNetworkInterfacesWithIPs() {
		Enumeration<NetworkInterface> ni;
		try {
			ni = NetworkInterface.getNetworkInterfaces();
			
			while (ni.hasMoreElements()) {
				NetworkInterface n = ni.nextElement();
			    Enumeration<InetAddress> addresses = n.getInetAddresses();
			    String IP = "";
			    while (addresses.hasMoreElements()){
			        InetAddress current_addr = addresses.nextElement();
			        if (current_addr.isLoopbackAddress() || !(current_addr instanceof Inet4Address))
			        	continue;
			        IP = current_addr.getHostAddress();
			    }
			    
			    System.out.println(n.getName() + " " + IP);
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
