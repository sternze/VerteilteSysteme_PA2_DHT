package chord.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

import chord.data.MyValue;
import chord.data.Node;

public interface IMyChord extends Remote {
	public Node findSuccessor(long id) throws RemoteException;
	public Node findPredecessor(long id) throws RemoteException;
	public Node closestPrecedingFinger(long id) throws RemoteException;
	public void notify(Node node) throws RemoteException;
	public void notifyPredecessor(Node node) throws RemoteException;
	public void insertData(MyValue data, boolean createReplicas) throws RemoteException;
	public void insertData(Set<MyValue> data, boolean createReplicas) throws RemoteException;
	public void insertEntry_ChordInternal(MyValue data, boolean createReplicas) throws RemoteException;
	public void removeData(MyValue data, boolean deleteReplicas) throws RemoteException;
	public void removeEntry_ChordInternal(MyValue data, boolean deleteReplicas) throws RemoteException;
	public void leavesNetwork(Node newPredecessor) throws RemoteException;
	public void leave() throws RemoteException;
	public Set<MyValue> query(long id) throws RemoteException;
	public Set<MyValue> queryData_ChordInternal(long id) throws RemoteException;
	public Set<MyValue> migrateDataAfterJoin(Node potentialPredecessor) throws RemoteException;
	public Node getCurrentPredecessor() throws RemoteException;
	public Node getCurrentSuccessor() throws RemoteException;
}
