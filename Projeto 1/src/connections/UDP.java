package connections;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * classe para enviar cenas
 * 
 * @author Paulo
 *
 */
public class UDP {

	String config[];
	int type;
	DatagramSocket socket;

	public UDP(String config[], int type) throws SocketException {
		this.config = config;
		this.type = type;
		socket = new DatagramSocket();
	}

	public void sendMessage(String message) throws IOException {
		byte[] buffer = message.getBytes();

		InetAddress address = InetAddress.getByName(config[type]);
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length,
				address, Integer.parseInt(config[type + 1]));

		socket.send(packet);
	}
	
	public void close() {
		socket.close();
	}

}
