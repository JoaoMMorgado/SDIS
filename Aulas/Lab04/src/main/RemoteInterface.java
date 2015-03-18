package main;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface RemoteInterface extends Remote {
	MainSystem sistema = new MainSystem();

	String consultSystem(String message) throws RemoteException;
}