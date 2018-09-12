/*
	RMI Client
*/
package rmi;

import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

import common.MessageInfo;

public class RMIClient {

	public static void main(String[] args) {

		RMIServerI iRMIServer = null;

		if (args.length < 2){
			System.out.println("Needs 2 arguments: ServerHostName/IPAddress, TotalMessageCount");
			System.exit(-1);
		}

		String urlServer = new String("rmi://" + args[0] + "/RMIServer");
		int numMessages = Integer.parseInt(args[1]);


		// Initialise Security Manager
		// if (System.getSecurityManager() == null) {
		// 	System.setSecurityManager(new SecurityManager());
		// }

		// Bind to RMIServer
		try {
			Registry reg = LocateRegistry.getRegistry(4200);
			iRMIServer = (RMIServerI) reg.lookup(urlServer);
		} catch (Exception e) {
			System.err.println("RMIClient exception: ");
			e.printStackTrace();
		}

		// Attempt to send messages the specified number of times
		int messageCount = 1;
		while (messageCount <= numMessages) {
			try {
				MessageInfo message = new MessageInfo(numMessages, messageCount);
				iRMIServer.receiveMessage(message);
				System.out.println("sent msg = " + message.toString());
				messageCount++;
			}	catch (RemoteException e) {
				System.err.println("RMIClient exception on receiveMessage(..): ");
				e.printStackTrace();
			}
		}
	}
}
