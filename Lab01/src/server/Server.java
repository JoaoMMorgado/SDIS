package server;

import java.net.*;

public class Server {
	public static void main(String args[]) throws Exception {
		if (args.length != 1) {
			System.out.println("Usage: java Server <port>");
			System.exit(0);
		}
		int port = Integer.parseInt(args[0]);
		System.out.println(port);
		byte[] buffer = new byte[1024];
		String s;
		DatagramSocket socket = new DatagramSocket(port);
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		socket.receive(packet);
		
		//run
		InetAddress address = packet.getAddress();
		int port2 = packet.getPort();
		packet = new DatagramPacket(buffer, buffer.length, address, port2);
		socket.send(packet);
	}
}
