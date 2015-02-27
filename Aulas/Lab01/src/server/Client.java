package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Client {
	public static void main(String args[]) throws Exception {
		if (args.length != 4) {
			System.out.println("Usage: java Client <host_name> <port_number> <oper> <opnd>*");
			System.exit(0);
		}
		
		int port;
		InetAddress address;
		DatagramSocket socket = null;
		DatagramPacket packet;
		byte[] sendBuf = new byte[256];
		
		DatagramSocket socket = new DatagramSocket();
		
		byte[] buf = new byte[256];
		InetAddress address = InetAddress.getByName(args[0]);
		DatagramPacket packet = new DatagramPacket(buf, buf.length, 
		                                address, 4445);
		socket.send(packet);
		
		packet = new DatagramPacket(buf, buf.length);
		socket.receive(packet);
		String received = new String(packet.getData(), 0, packet.getLength());
		System.out.println("Quote of the Moment: " + received);
		
	}
}
