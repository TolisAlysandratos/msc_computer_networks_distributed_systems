/*
 RMI Server
*/
package rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.rmi.registry.Registry;


import common.*;

public class RMIServer extends UnicastRemoteObject implements RMIServerI {

	private int totalMessages = 0;
	private List<Integer> receivedMessages;

	public RMIServer() throws RemoteException {
		super();
	}

	public void receiveMessage(MessageInfo msg) throws RemoteException {
		totalMessages++;

		// On receipt of first message, initialise the receive buffer
		if (totalMessages == 1) {
			receivedMessages = new ArrayList<Integer>();
		}

		// Log receipt of the message
		receivedMessages.add(msg.messageNum);
		System.out.println("received = " + msg.toString());

		// If this is the last expected message, then identify
		//        any missing messages
		if (msg.totalMessages == msg.messageNum) {
			System.out.println("Total received = " + receivedMessages.size());
			List<Integer> missingMessages = new ArrayList<Integer>();
			for (int i = 1, j = 0; i <= receivedMessages.size(); i++) {
				if (i != receivedMessages.get(j)) {
					missingMessages.add(i);
				} else { j++; }
			}
			if (missingMessages.size() > 0) {
				for (int i = 0; i < missingMessages.size(); i++) {
					System.out.println("missed msg num: " + missingMessages.get(i));
				}
			}
			if (missingMessages.size() == 0) {
				System.out.println("There are no missing messages");
			}
		}
	}


	public static void main(String[] args) {

		RMIServer rmis = null;

		// Initialise Security Manager
		// if (System.getSecurityManager() == null) {
		// 	System.setSecurityManager(new SecurityManager());
		// }


		// Instantiate the server class
		try {
			rmis = new RMIServer();
		} catch (RemoteException e) {
			System.err.println("RMIServer exception 1: ");
			e.printStackTrace();
		}

		// Bind to RMI registry
		rebindServer("rmi://remotehost/RMIServer", rmis);
	}


	protected static void rebindServer(String serverURL, RMIServer server) {

		// Start / find the registry
		try {

			RMIServerI serverI = server;
			RMIServerI stub = null;

			if (UnicastRemoteObject.unexportObject(serverI, true)) {
				stub = (RMIServerI) UnicastRemoteObject.exportObject(serverI, 0);
				System.out.println("RMIServer object exported");
			}
			LocateRegistry.createRegistry(4200);
			Registry reg = LocateRegistry.getRegistry(4200);

			// Rebind the server to the registry (rebind replaces any existing servers bound to the serverURL)
			reg.rebind(serverURL, stub);
		} catch (Exception e) {
			System.err.println("RMIServer exception 2: ");
			e.printStackTrace();
		}

		System.out.println("RMIServer bound");
	}
}
