package main;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {
	public static void main(String args[]) throws Exception {
		if (args.length < 4 || args.length > 5) {
			System.out
					.println("Usage: java client <svc_name> <svc_port> <oper> <opnd> *");
			System.exit(0);
		}

		System.out.println("\t============\n\t===CLIENT===\n\t============\n");

		Socket clientSocket = new Socket(args[0], Integer.parseInt(args[1]));
		DataOutputStream outToServer = new DataOutputStream(
				clientSocket.getOutputStream());

		// message
		String msg = args[2].toUpperCase() + " " + args[3];
		if (args[2].toUpperCase().equals("REGISTER"))
			msg += " " + args[4].toUpperCase();
		System.out.println("Message sent: " + msg);

		outToServer.writeBytes(msg + "\n");

		BufferedReader fromServer = new BufferedReader(new InputStreamReader(
				clientSocket.getInputStream()));
		String received = fromServer.readLine();

		System.out.println("\nServer Response: " + received);

		fromServer.close();
		outToServer.close();
		clientSocket.close();

	}
}
