package main;

import java.io.IOException;

public class Client {
	public static void main(String args[]) throws IOException {

		Config config = new Config(args);

		if (!config.readConfigurations() && args.length != 3) {
			System.out
					.println("java Server <srvc_port> <mcast_addr> <mcast_port>\nwhere:\n\t<srvc_port> is the port number where the server provides the service\n\t<mcast_addr> is the IP address of the multicast group used by the server to advertise its service.\n\t<mcast_port> is the multicast group port number used by the server to advertise its service.");
			System.exit(-1);
		} else if (args.length == 3)
			config.storeConfigurations();
	}
}
