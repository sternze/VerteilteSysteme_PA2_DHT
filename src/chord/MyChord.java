package chord;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Date;
import java.util.Set;

import chord.data.ChordNode;
import chord.data.MyValue;
import chord.gui.ChordGraphView;
import chord.interfaces.IChordGraphView;
import chord.interfaces.IChordNode;
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
	public static final int KEYLENGTH = 60;
	
	private static ChordNode me;
	private static IChordGraphView graphViewContact = null;
	
	private static int port;
	private static String ServiceName = "";
	private static String ConnectionURI = "";
	private static String GraphViewConnectionURI = "";
	private static long manualId = -1;
	private static boolean Task3 = false;
	
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

		if (args.length >= 5 && !args[4].equals("${replicateData}")) {
			Task3 = Boolean.parseBoolean(args[4]);
		}
		
		Registry reg = establishRegistry(); 

		try {
			MyChord myChord = new MyChord();
			reg.rebind(ServiceName, myChord);       // Bind object
			
			System.out.println(new Date() + " Registered with registry");
			
			if (manualId == -1) {
				me = new ChordNode(MyChordUtils.getIPAdressOfNic("net4"), port, ServiceName, KEYLENGTH, Task3);
			} else {
				me = new ChordNode(manualId, MyChordUtils.getIPAdressOfNic("net4"), port, ServiceName, KEYLENGTH, Task3);
			}
			
			if (args.length >= 2 && !args[1].equals("${GraphViewIP:Port}")) {
				GraphViewConnectionURI = args[1];

				if (graphViewContact == null) {
					try {
						graphViewContact = (IChordGraphView) Naming.lookup("rmi://" + GraphViewConnectionURI + "/" + ChordGraphView.GRAPH_VIEW_SERVICE_NAME);
					} catch (MalformedURLException | RemoteException | NotBoundException e) { 
						e.printStackTrace();
					}
				}

				graphViewContact.registerChordNode(me);
			} else {
				//GraphViewConnectionURI = MyChordUtils.getIPAdressOfNic("net4") + ":" + ChordGraphView.GRAPH_VIEW_PORT;
			}
			
			if (args.length >= 3 && !args[2].equals("${NodeIP:Port}") && !args[2].equals("1:1")) {
				ConnectionURI = args[2];
							
				me.join(new ChordNode(ConnectionURI.split(":")[0], Integer.parseInt(ConnectionURI.split(":")[1]), ServiceName, KEYLENGTH, Task3));      	
			} else {
				me.create();
			}
			
			Thread stabilize = new Thread() {
				
				@Override
				public void run() {
					System.out.println(new Date() + " stabilize thread started");
					
					while (true) {
						try {
							me.stabilize();
							
							System.gc();
							Thread.sleep(1000);
						} catch (InterruptedException | RemoteException e) {
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
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			};
			
//			Thread printStatus = new Thread() {
//				
//				@Override
//				public void run() {
//					System.out.println(new Date() + " printStatus thread started");
//					
//					while (true) {
//						
//						try {
//							me.printStatus();
//							Thread.sleep(5000);
//						} catch (InterruptedException | RemoteException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//					}
//				}
//			};
					
			Thread successorMaintainer = new Thread() {
				
				@Override
				public void run() {
					System.out.println(new Date() + " maintaining successor list");
					
					while (true) {
						
						try {
							if(me.getSuccessor() != null) {
								ChordNode.successors[1] = me.getSuccessor().getSuccessor();
								ChordNode.successors[2] = me.getSuccessor().getSuccessor().getSuccessor();
//								System.out.println("first succ: " + me.getSuccessor().getIdentifier());
//								System.out.println("second succ: " + me.getSuccessor().getSuccessor().getIdentifier());
//								System.out.println("third succ: " + me.getSuccessor().getSuccessor().getSuccessor().getIdentifier());
							}
							Thread.sleep(5000);
						} catch (InterruptedException | RemoteException e) {
							// stays empty
						}
					}
				}
			};
			
			
			stabilize.start();
			fixFingers.start();
			successorMaintainer.start();
			//printStatus.start();
			
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
	public void insertData(MyValue data) throws RemoteException {
		me.insertData(data);
	}

	@Override
	public void removeData(MyValue data) throws RemoteException {
		me.removeData(data);
	}

	@Override
	public void leave() throws RemoteException {
		me.leave();
	}

	@Override
	public Set<MyValue> query(long id) throws RemoteException {
		return me.query(id);
	}


	@Override
	public void insertData(Set<MyValue> data) throws RemoteException {
		// TODO Auto-generated method stub
		for(MyValue dat : data) {
			me.insertData(dat);
		}
	}

	@Override
	public IChordNode getRemoteChordNodeObject() throws RemoteException {
		return me;
	}
}
