package chord.interfaces;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IChordGraphView extends Remote {
	public void registerChordNode(IChordNode node) throws RemoteException;
}
