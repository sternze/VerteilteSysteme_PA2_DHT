package chord.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

import chord.data.Entries;
import chord.data.FingerTable;
import chord.data.MyValue;

public interface IChordNode extends Remote {
	public IChordNode getSuccessor() throws RemoteException;
	public IChordNode getPredecessor() throws RemoteException;
	public IChordNode closestPrecedingFinger(long id) throws RemoteException;
	public IChordNode findSuccessor(long id) throws RemoteException;
	public FingerTable getFingerTable() throws RemoteException;
	public int getKeySize() throws RemoteException;
	public long getIdentifier() throws RemoteException;
	public Entries getEntries() throws RemoteException;
	public void notifyPredecessor(IChordNode node) throws RemoteException;
	public Set<MyValue> migrateDataAfterJoin(IChordNode potentialPredecessor) throws RemoteException;
	public Set<MyValue> queryData(long id) throws RemoteException;
	public void leavesNetwork(IChordNode newPredecessor) throws RemoteException;
	public void removeEntry(MyValue data, boolean deleteReplicas) throws RemoteException;
	public void insertEntry(MyValue data, boolean createReplicas) throws RemoteException;
	public void notify(IChordNode chordNode) throws RemoteException;
	public String getIp() throws RemoteException;
	public int getPort() throws RemoteException;
	public String getServiceName() throws RemoteException;
	public String getIdAndEntryCount() throws RemoteException;
}
