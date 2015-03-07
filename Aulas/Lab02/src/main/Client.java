package main;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Client {
	public static void main(String args[]) throws Exception {
		if (args.length < 4 || args.length > 5) {
			System.out
					.println("Usage: java client <mcast_addr> <mcast_port> <oper> <opnd> *");
			System.exit(0);
		}

		System.out.println("\t============\n\t===CLIENT===\n\t============\n");

		// get server address
		int port = Integer.parseInt(args[1]);
		MulticastSocket multisocket = new MulticastSocket(port);
		InetAddress address = InetAddress.getByName(args[0]);
		multisocket.joinGroup(address);

		byte[] inBuf = new byte[256];
		DatagramPacket inPacket = new DatagramPacket(inBuf, inBuf.length);
		multisocket.receive(inPacket);
		String initialmsg = new String(inBuf, 0, inPacket.getLength());
		System.out.println("From " + inPacket.getAddress() + " Messsage: "
				+ initialmsg);

		// message
		String msg = args[2].toUpperCase() + " " + args[3];
		if (args[2].toUpperCase().equals("REGISTER"))
			msg += " " + args[4].toUpperCase();

		byte[] sendBuf = msg.getBytes();

		// send response
		int uniPort = Integer.parseInt(initialmsg.substring(0, 4));

		DatagramSocket unisocket = new DatagramSocket();
		DatagramPacket packet = new DatagramPacket(sendBuf, sendBuf.length,
				inPacket.getAddress(), uniPort);
		unisocket.send(packet);

		// get response
		byte[] receiveBuf = new byte[sendBuf.length];
		packet = new DatagramPacket(receiveBuf, receiveBuf.length);
		unisocket.receive(packet);

		String received = new String(packet.getData(), 0, packet.getLength());
		// String msg = packet.toString();
		System.out.println("\n\nServer Response: " + received);

		multisocket.close();
		unisocket.close();

	}
}
