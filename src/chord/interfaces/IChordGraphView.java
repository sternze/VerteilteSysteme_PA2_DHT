package chord.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

import chord.data.ChordNode;

public interface IChordGraphView extends Remote {
	public void pushStatus(ChordNode node) throws RemoteException;
}
