package main;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {
	public static void main(String args[]) throws Exception {
		if (args.length != 3) {
			// "Usage: java Client <host_name> <port_number> <oper> <opnd>*"
			System.out
					.println("Usage: java Client <host_name> <port_number> <msg>");
			System.exit(0);
		}

		System.out.println("===Cliente===\n\thostname: " + args[0]
				+ "\n\tPorta: " + args[1] + "\n\tMensagem: " + args[2]);
		
		int port = Integer.parseInt(args[1]);

		DatagramSocket socket = new DatagramSocket();
		byte[] sendBuf = args[2].getBytes();
		InetAddress address = InetAddress.getByName(args[0]);

		//send response
		DatagramPacket packet = new DatagramPacket(sendBuf, sendBuf.length,
				address, port);
		socket.send(packet);

		// get response
		byte[] receiveBuf = new byte[sendBuf.length];
		packet = new DatagramPacket(receiveBuf, receiveBuf.length);
		socket.receive(packet);

		String received = new String(packet.getData(), 0, packet.getLength());
		// String msg = packet.toString();
		System.out.println("\n\nResposta: " + received);

		socket.close();

	}
}
