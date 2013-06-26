package chord.utils;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.util.Enumeration;

public class MyChordUtils {

	public static String getIPAdressOfNic(String NIC_Name) {
		String IP = null;
		try {
			NetworkInterface ni = NetworkInterface.getByName(NIC_Name);
			
		    Enumeration<InetAddress> addresses = ni.getInetAddresses();
		    while (addresses.hasMoreElements()){
		        InetAddress current_addr = addresses.nextElement();
		        if (current_addr.isLoopbackAddress() || !(current_addr instanceof Inet4Address))
		        	continue;
		        IP = current_addr.getHostAddress();
		    }
		} catch(Exception ex) { }
		
		return IP;
	}
	
	/**
	 * Checks to see if a specific port is available. (implementation from the Apache "camel" project)
	 *
	 * @param port the port to check for availability
	 */
	public static boolean IsPortAvailable(int port, int minPort, int maxPort) {
	    if (port < minPort || port > maxPort) {
	        throw new IllegalArgumentException("Invalid start port: " + port);
	    }

	    ServerSocket ss = null;
	    DatagramSocket ds = null;
	    try {
	        ss = new ServerSocket(port);
	        ss.setReuseAddress(true);
	        ds = new DatagramSocket(port);
	        ds.setReuseAddress(true);
	        return true;
	    } catch (IOException e) {
	    } finally {
	        if (ds != null) {
	            ds.close();
	        }

	        if (ss != null) {
	            try {
	                ss.close();
	            } catch (IOException e) {
	                /* should not be thrown */
	            }
	        }
	    }

	    return false;
	}
}
