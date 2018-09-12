/*
	UDP Server
*/
package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import common.MessageInfo;

public class UDPServer {

	private DatagramSocket recvSoc;
	private int totalMessages = 0;
	private List<Integer> receivedMessages;

	private void run() {
		int				pacSize;
		byte[]			pacData;
		DatagramPacket 	pac;

		// Receive the messages and process them
		while(true) {
			try {
				pacSize = 256;
				pacData = new byte[pacSize];
				pac = new DatagramPacket(pacData, pacSize);
				recvSoc.setSoTimeout(30000);
				recvSoc.receive(pac);
				totalMessages++;
				String pacDataStr = new String(pac.getData(), 0, pac.getLength());
				processMessage(pacDataStr);
			} catch (IOException e) {
				e.printStackTrace();
				break;
			} catch (Exception e) {
				System.out.print("Message Info: " + e.getMessage());
				break;
			}
		}
		System.out.println("Closing Server Socket..");
		recvSoc.close();
	}

	public void processMessage(String data) throws Exception {
		// Use the data to construct a new MessageInfo object
		MessageInfo msg = new MessageInfo(data);

		// On receipt of first message, initialise the receive buffer
		if (totalMessages == 1) {
			receivedMessages = new ArrayList<Integer>();
		}

		// Log receipt of the message
		receivedMessages.add(msg.messageNum);
		System.out.println("received msg = " + msg.toString());

		// If this is the last expected message, then identify
		// any missing messages
		if (msg.totalMessages == msg.messageNum) {
			System.out.println("Total messages received = " + receivedMessages.size());

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
			receivedMessages.clear();
		}

	}


	public UDPServer(int rp) {
		// Initialise UDP socket for receiving data
		try {
			recvSoc = new DatagramSocket(rp);
		} catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		}
		
		System.out.println("UDPServer ready");
	}

	public static void main(String args[]) {
		int	recvPort;

		if (args.length < 1) {
			System.err.println("Arguments required: recv port");
			System.exit(-1);
		}
		recvPort = Integer.parseInt(args[0]);

		// Construct Server object and start it by calling run().
		UDPServer server = new UDPServer(recvPort);
		server.run();
	}

}
