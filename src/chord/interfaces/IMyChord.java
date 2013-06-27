package chord.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import chord.data.Node;

public interface IMyChord extends Remote {
	public Node findSuccessor(long id) throws RemoteException;
	public Node findPredecessor(long id) throws RemoteException;
	public Node closestPrecedingFinger(long id) throws RemoteException;
	public Node notify(Node node) throws RemoteException;
	public void notifyPredecessor(Node node) throws RemoteException;
}
