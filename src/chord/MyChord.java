package chord;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;

import chord.data.ChordNode;
import chord.data.Node;
import chord.gui.ChordGraphView;
import chord.interfaces.IChordGraphView;
import chord.interfaces.IMyChord;
import chord.utils.MyChordUtils;

public class MyChord extends UnicastRemoteObject implements IMyChord {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final int MIN_PORT_NUMBER = 8000;
	private static final int MAX_PORT_NUMBER = 10000;
	private static final String DEFAULT_SERVICE_NAME = "PA2_MyKV";
	public static final int KEYLENGTH = 3;
	
	private static ChordNode me;
	private static IChordGraphView graphViewContact = null;
	
	private static int port;
	private static String ServiceName = "";
	private static String ConnectionURI = "";
	private static String GraphViewConnectionURI = "";
	private static long manualId = -1;
	
	protected MyChord() throws RemoteException {
		super();
	}
	
	public static void main(String[] args) {
		
		if(args.length >= 1 && !args[0].equals("${ServiceName}")) {
			ServiceName = args[0];
		} else {
			ServiceName = DEFAULT_SERVICE_NAME;
		}
		
		if (args.length >= 4 && !args[3].equals("${manualID}")) {
			manualId = Long.parseLong(args[3]);
		}
		
		Registry reg = establishRegistry(); 

		try {
			MyChord myChord = new MyChord();
			reg.rebind(ServiceName, myChord);       // Bind object
			
			System.out.println(new Date() + " Registered with registry");
			
			if (manualId == -1) {
				me = new ChordNode(MyChordUtils.getIPAdressOfNic("net4"), port, ServiceName, KEYLENGTH);
			} else {
				me = new ChordNode(manualId, MyChordUtils.getIPAdressOfNic("net4"), port, ServiceName, KEYLENGTH);
			}
			
			if (args.length >= 2 && !args[1].equals("${GraphViewIP:Port}")) {
				GraphViewConnectionURI = args[1];
			} else {
				GraphViewConnectionURI = MyChordUtils.getIPAdressOfNic("net4") + ":" + ChordGraphView.GRAPH_VIEW_PORT;
			}
			
			if (args.length >= 3 && !args[2].equals("${NodeIP:Port}") && !args[2].equals("1:1")) {
				ConnectionURI = args[2];
							
				me.join(new Node(ConnectionURI.split(":")[0], Integer.parseInt(ConnectionURI.split(":")[1]), ServiceName, KEYLENGTH));      	
			} else {
				me.create();
			}
			
			Thread stabilize = new Thread() {
				
				@Override
				public void run() {
					System.out.println(new Date() + " stabilize thread started");
					
					while (true) {
						me.stabilize();
						
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
			};
			
			Thread fixFingers = new Thread() {
				
				@Override
				public void run() {
					System.out.println(new Date() + " fixFingers thread started");
					
					while (true) {
						me.fixFingers();
						
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			};
			
			Thread printStatus = new Thread() {
				
				@Override
				public void run() {
					System.out.println(new Date() + " printStatus thread started");
					
					while (true) {
						me.printStatus();
						
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			};
			
			Thread sendStatus = new Thread() {
				
				@Override
				public void run() {
					System.out.println(new Date() + " sendStatus thread started");
					
					while (true) {
						if (graphViewContact == null) {
							try {
								graphViewContact = (IChordGraphView) Naming.lookup("rmi://" + GraphViewConnectionURI + "/" + ChordGraphView.GRAPH_VIEW_SERVICE_NAME);
							} catch (MalformedURLException | RemoteException | NotBoundException e) { 
								e.printStackTrace();
							}
						}
						
						try {
							graphViewContact.pushStatus(me);
						} catch (RemoteException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			};
			
			stabilize.start();
			fixFingers.start();
			printStatus.start();
			sendStatus.start();
			
		} catch (RemoteException e) {
			System.out.println(new Date() + " Error: " + e);
		}
	}
	
	/**
	 * Establishes the Remote Registry
	 * @return established Registry
	 */
	private static Registry establishRegistry()  {
		Registry reg = null;
		
		try {
			for (port = MIN_PORT_NUMBER; port <= MAX_PORT_NUMBER && !MyChordUtils.IsPortAvailable(port, MIN_PORT_NUMBER, MAX_PORT_NUMBER); port++);
			
			System.out.println(new Date() + " using port " + port);
			
			reg = LocateRegistry.createRegistry(port);
		} catch (IllegalArgumentException ae) {
			System.out.println(new Date() + " No ports avilable");
			System.exit(0);
		} catch (RemoteException e) {
			try {
				reg = LocateRegistry.getRegistry(); 
			} catch (RemoteException e2) {
				System.out.println(new Date() + " Registry could not be established" + e);
				System.exit(0);
			}
		}
		
		System.out.println(new Date() + " Registry established"); 
		
		return reg;
	}

	@Override
	public Node findSuccessor(long id) throws RemoteException {
		return me.findSuccessor(id);
	}

	@Override
	public Node findPredecessor(long id) throws RemoteException {
		return me.findPredecessor(id);
	}

	@Override
	public Node closestPrecedingFinger(long id) throws RemoteException {
		return me.closestPrecedingFinger(id);
	}
	
	@Override
	public Node notify(Node node) throws RemoteException {
		return me.notify(node);
	}

	@Override
	public void notifyPredecessor(Node node) throws RemoteException {
		me.notifyPredecessor(node);
	}
}
