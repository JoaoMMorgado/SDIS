package main;

import java.net.*;

public class Server {
	public static void main(String args[]) throws Exception {
		if (args.length != 1) {
			System.out.println("Usage: java Server <port>");
			System.exit(0);
		}

		int port = Integer.parseInt(args[0]);

		System.out.println("===Servidor===\n\tPorta: " + port);
		DatagramSocket socket = new DatagramSocket(port);
		byte[] buffer = new byte[1024];

		boolean stop = true;
		while (stop) {
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			socket.receive(packet);
			String msg = new String(packet.getData(), 0, packet.getLength());
			System.out.println(msg);
			// run
			InetAddress address = packet.getAddress();
			int port2 = packet.getPort();
			byte[] msg2 = msg.getBytes();
			packet = new DatagramPacket(msg2, msg2.length, address, port2);
			socket.send(packet);

		}

		socket.close();
	}
}
