package main;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Server implements RemoteInterface {
	
	public static void main(String args[]) {

		if (args.length != 1) {
			System.out.println("Usage: java Server <remote_object_name>");
			System.exit(0);
		}

		try {
			//configuring properties not working... create registry to test aplication
			LocateRegistry.createRegistry(1099);
			Server obj = new Server();
			RemoteInterface stub = (RemoteInterface) UnicastRemoteObject
					.exportObject(obj, 0);
			Registry registry = LocateRegistry.getRegistry();
			registry.bind(args[0], stub);
			System.err.println("Server ready!\n");
		} catch (Exception e) {
			System.err.println("Server exception: " + e.toString());
			e.printStackTrace();
		}
	}

	public String consultSystem(String message) {
		System.out.println("Server received: " + message);
		
		String tokens[] = message.split(" ");
		System.out.println(tokens);
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
}