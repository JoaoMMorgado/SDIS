package main;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server {

	public static void main(String args[]) throws Exception {
		if (args.length != 1) {
			System.out.println("Usage: java Server <srvc_port>");
			System.exit(0);
		}

		System.out.println("\t============\n\t===SERVER===\n\t============\n");

		MainSystem sistema = new MainSystem();

		Thread startTCP = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					tcp(args, sistema);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});

		startTCP.start();
	}

	public static String consultSystem(String message, MainSystem sistema) {
		String tokens[] = message.split(" ");

		String result = "ERROR";

		if (tokens[0].equals("REGISTER")) {

			Matcher m = Pattern.compile("(([A-Z]|\\d){2}-){2}(([A-Z]|\\d)){2}")
					.matcher(tokens[2].toUpperCase());
			if (m.matches()) {
				int temp = sistema.register(tokens[2].toUpperCase(), tokens[1]);
				result = Integer.toString(temp);
			}
		} else if (tokens[0].equals("LOOKUP"))
			result = sistema.lookup(tokens[1].toUpperCase());

		System.out.println("\tResult: " + result);
		
		return result;
	}

	public static void tcp(String args[], MainSystem sistema)
			throws IOException {
		int port = Integer.parseInt(args[0]);

		ServerSocket server = new ServerSocket(port); // receber pedidos de
														// conex�o no porto dado

		boolean stop = true;
		while (stop) {
			Socket socket = server.accept(); // transfer�ncia de dados

			BufferedReader fromClient = new BufferedReader(
					new InputStreamReader(socket.getInputStream()));
			String msg = fromClient.readLine();
			System.out.println("Message Received: " + msg);

			String result = consultSystem(msg, sistema);

			// send response to client
			DataOutputStream outToClient = new DataOutputStream(
					socket.getOutputStream());
			outToClient.writeBytes(result + "\n");

			outToClient.close();
			fromClient.close();
			socket.close();
		}
		server.close();
	}
}
