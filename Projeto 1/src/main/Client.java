package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Client {
	public static void main(String args[]) throws IOException {

		int udpPort, multicastPort;
		String multicastAdress;
		String config[] = new String[3];

		if (args.length != 3 && !readConfigurations(config)) {
			System.out
					.println("java Server <srvc_port> <mcast_addr> <mcast_port>\nwhere:\n\t<srvc_port> is the port number where the server provides the service\n\t<mcast_addr> is the IP address of the multicast group used by the server to advertise its service.\n\t<mcast_port> is the multicast group port number used by the server to advertise its service.");
			System.exit(-1);
		} else
			storeConfigurations(args);

		System.out.println(config);

	}

	public static void storeConfigurations(String args[]) throws IOException {
		FileWriter fileWriter = new FileWriter("configuration.txt");

		BufferedWriter writer = new BufferedWriter(fileWriter);

		writer.write(args[0] + "\n");
		writer.write(args[1] + "\n");
		writer.write(args[2] + "\n");

		writer.close();
	}

	public static boolean readConfigurations(String args[]) throws IOException {
		String fileName = "configuration.txt";

		String line = null;

		FileReader fileReader;
		try {
			fileReader = new FileReader(fileName);
		} catch (FileNotFoundException e1) {
			return false;
		}
		BufferedReader bufferedReader = new BufferedReader(fileReader);

		int i = 0;

		try {
			while ((line = bufferedReader.readLine()) != null) {
				args[i] = line;
				i++;
			}
		} catch (IOException e) {
			return false;
		}

		bufferedReader.close();

		if (i <= 3)
			return false;

		return true;
	}
}
