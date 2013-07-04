package chord.test;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Enumeration;

import chord.data.ChordNode;
import chord.data.MyValue;
import chord.interfaces.IChordNode;
import chord.interfaces.IMyChord;

public class test {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	}
	
	public static void insertData() {
		IChordNode node = null;
		IMyChord entryPoint = null;
		try {
			node = new ChordNode("192.168.1.102", 8000, "PA2_MyKV", 3);
			entryPoint = (IMyChord) Naming.lookup("rmi://" + node.getIp() + ":" + node.getPort() + "/" + node.getServiceName());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		MyValue data = new MyValue(new String("hallo").getBytes(), 3);
		MyValue data1 = new MyValue(new String("du").getBytes(), 3);
		MyValue data2 = new MyValue(new String("da").getBytes(), 3);
		try {
			entryPoint.insertData(data, true);
			entryPoint.insertData(data1, true);
			entryPoint.insertData(data2, true);
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
