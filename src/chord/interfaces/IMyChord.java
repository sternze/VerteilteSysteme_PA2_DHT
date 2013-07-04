package chord.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

import chord.data.MyValue;

public interface IMyChord extends Remote {
	public void insertData(MyValue data) throws RemoteException;
	public void insertData(Set<MyValue> data) throws RemoteException;
	public void removeData(MyValue data) throws RemoteException;
	public void leave() throws RemoteException;
	public Set<MyValue> query(long id) throws RemoteException;
	public IChordNode getRemoteChordNodeObject() throws RemoteException;
}
