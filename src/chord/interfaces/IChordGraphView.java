package chord.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import chord.data.ChordNode;

public interface IChordGraphView extends Remote {
	public void registerChordNode(IChordNode node) throws RemoteException;
}
