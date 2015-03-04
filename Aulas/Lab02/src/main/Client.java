package main;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {
	public static void main(String args[]) throws Exception {
		if (args.length < 4 || args.length > 5) {
			System.out
					.println("Usage: java Client <host_name> <port_number> <oper> <opnd>*");
			System.exit(0);
		}

		System.out.println("===Client===\n\thostname: " + args[0]
				+ "\n\tPort: " + args[1] + "\n\tMessage: " + args[2]);

		int port = Integer.parseInt(args[1]);
		String oper = args[2].toUpperCase();

		String Owner = args[3];

		DatagramSocket socket = new DatagramSocket();

		String msg = oper + " " + Owner;

		if (oper.equals("REGISTER")) {
			String Plate = args[4].toUpperCase();
			msg += " " + Plate;
		}

		byte[] sendBuf = msg.getBytes();

		InetAddress address = InetAddress.getByName(args[0]);

		// send response
		DatagramPacket packet = new DatagramPacket(sendBuf, sendBuf.length,
				address, port);
		socket.send(packet);

		// get response
		byte[] receiveBuf = new byte[sendBuf.length];
		packet = new DatagramPacket(receiveBuf, receiveBuf.length);
		socket.receive(packet);

		String received = new String(packet.getData(), 0, packet.getLength());
		// String msg = packet.toString();
		System.out.println("\n\nServer Response: " + received);

		socket.close();

	}
}
