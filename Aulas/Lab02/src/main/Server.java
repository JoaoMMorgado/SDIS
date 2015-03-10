package main;

import java.io.IOException;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {

	public static void main(String args[]) throws Exception {
		if (args.length != 3) {
			System.out
					.println("Usage: java Server <srvc_port> <mcast_addr> <mcast_port>");
			System.exit(0);
		}

		System.out.println("\t============\n\t===SERVER===\n\t============\n");
		
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
					multicast(args);
				} catch (InterruptedException | IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		startMulticast.start();
		startUnicast.start();
	}

	public static void unicast(String args[], MainSystem sistema)
			throws IOException {
		int port = Integer.parseInt(args[0]);

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
				result = sistema.lookup(tokens[1].toUpperCase());

			System.out.println("\tResult: " + result);
			// run
			InetAddress address = packet.getAddress();
			int port2 = packet.getPort();
			byte[] msg2 = result.getBytes();
			packet = new DatagramPacket(msg2, msg2.length, address, port2);
			socket.send(packet);

		}
		socket.close();
	}

	public static void multicast(String args[])
			throws InterruptedException, IOException {

		int port = Integer.parseInt(args[2]);

		DatagramSocket socket = new DatagramSocket();
		String message = args[0] + "/ Welcome to the car database system! ";
		byte[] buffer = message.getBytes();

		boolean stop = true;
		while (stop) {
			// sending
			InetAddress address = InetAddress.getByName(args[1]);
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length,
					address, port);
			
			System.out.println("multicast: " + args[1] + " " + port + " : "
					+ InetAddress.getLocalHost() + " " + args[0]);

			socket.send(packet);

			Thread.sleep(1000);
		}
		socket.close();
	}
}
