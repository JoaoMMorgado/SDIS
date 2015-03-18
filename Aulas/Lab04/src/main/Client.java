package main;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

	public static void main(String[] args) {

		if (args.length < 4 || args.length > 5) {
			System.out
					.println("Usage: java Client <host_name> <remote_object_name> <oper> <opnd>*");
			System.exit(0);
		}

		String host = args[0];
		String objectName = args[1];

		try {
			Registry registry = LocateRegistry.getRegistry(host, 1099);
			RemoteInterface stub = (RemoteInterface) registry
					.lookup(objectName);

			String response = "";
			if (args.length == 5) {
				response = stub.consultSystem(args[2].toUpperCase() + " "
						+ args[3] + " " + args[4].toUpperCase());
			} else {
				response = stub.consultSystem(args[2].toUpperCase() + " "
						+ args[3]);
			}

			System.out.println("Server response: " + response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}