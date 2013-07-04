package chord.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

import chord.data.MyValue;

public interface IMyChord extends Remote {
	public IChordNode findSuccessor(long id) throws RemoteException;
	public IChordNode findPredecessor(long id) throws RemoteException;
	public IChordNode closestPrecedingFinger(long id) throws RemoteException;
	public void notify(IChordNode node) throws RemoteException;
	public void notifyPredecessor(IChordNode node) throws RemoteException;
	public void insertData(MyValue data, boolean createReplicas) throws RemoteException;
	public void insertData(Set<MyValue> data, boolean createReplicas) throws RemoteException;
	public void removeData(MyValue data, boolean deleteReplicas) throws RemoteException;
	public void leave() throws RemoteException;
	public Set<MyValue> query(long id) throws RemoteException;
	public Set<MyValue> migrateDataAfterJoin(IChordNode potentialPredecessor) throws RemoteException;
	public IChordNode getRemoteChordNodeObject() throws RemoteException;
}
