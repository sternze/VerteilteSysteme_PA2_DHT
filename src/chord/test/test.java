package chord.test;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

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
