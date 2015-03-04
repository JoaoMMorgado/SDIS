package main;

import java.io.IOException;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {

	public static void main(String args[]) throws Exception {
		if (args.length != 1) {
			System.out.println("Usage: java Server <port>");
			System.exit(0);
		}

		MainSystem sistema = new MainSystem();
		
		Thread startUnicast = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					unicast(args, sistema);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		Thread startMulticast = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					multicast(args, sistema);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		startMulticast.start();
		startUnicast.start();
	}

	public static void unicast(String args[], MainSystem sistema) throws IOException {
		int port = Integer.parseInt(args[0]);

		System.out.println("===Server===\n\tPort: " + port + "\n");
		DatagramSocket socket = new DatagramSocket(port);
		byte[] buffer = new byte[1024];

		boolean stop = true;
		while (stop) {
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
			socket.receive(packet);
			String msg = new String(packet.getData(), 0, packet.getLength());
			System.out.println("Message Received: " + msg);

			String tokens[] = msg.split(" ");

			String result = "-1";

			if (tokens[0].equals("REGISTER")) {

				Matcher m = Pattern.compile(
						"(([A-Z]|\\d){2}-){2}(([A-Z]|\\d)){2}").matcher(
						tokens[2].toUpperCase());
				if (m.matches()) {
					int temp = sistema.register(tokens[2].toUpperCase(),
							tokens[1]);
					result = Integer.toString(temp);
				}
			} else if (tokens[0].equals("LOOKUP"))
				result = sistema.lookup(tokens[1]);

			// run
			InetAddress address = packet.getAddress();
			int port2 = packet.getPort();
			byte[] msg2 = result.getBytes();
			packet = new DatagramPacket(msg2, msg2.length, address, port2);
			socket.send(packet);

		}
		socket.close();
	}
	public static void multicast (String args[], MainSystem sistema) throws InterruptedException {
		while(true) {
			System.out.println("Ola joana");
			Thread.sleep(1000);
		}
		
	}
}
