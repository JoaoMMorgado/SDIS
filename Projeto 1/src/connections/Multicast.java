package connections;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * classe para receber cenas
 * 
 * @author Paulo
 *
 */
public class Multicast {

	String config[];
	MulticastSocket socket;

	public Multicast(String config[], int type) throws NumberFormatException, IOException {
		this.config = config;
		socket = new MulticastSocket(Integer.parseInt(config[type + 1]));
		socket.setLoopbackMode(true);
		InetAddress address = InetAddress.getByName(config[type]);
		socket.joinGroup(address);
	}

	public String getMessage() throws IOException {
		int size = 64 * 1000;
		byte[] buffer = new byte[size];
		DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);
		socket.receive(inPacket);
		String message = new String(buffer, 0, inPacket.getLength());

		return message;
	}

	public MulticastSocket getSocket() {
		return socket;
	}

	public void setSocket(MulticastSocket socket) {
		this.socket = socket;
	}
}
