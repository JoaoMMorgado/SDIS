package connections;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * classe para enviar cenas
 * 
 * @author Paulo
 *
 */
public class UDP {

	String config[];
	int type;
	MulticastSocket socket;

	public UDP(String config[], int type) throws IOException {
		this.config = config;
		this.type = type;
		socket = new MulticastSocket();
		socket.setTimeToLive(1);
	}

	public void sendMessage(String message) throws IOException {

		byte[] buffer = message.getBytes();

		InetAddress address = InetAddress.getByName(config[type]);
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length,
				address, Integer.parseInt(config[type + 1]));

		socket.send(packet);
	}

	public void sendMessage(byte[] buffer) throws IOException {
		InetAddress address = InetAddress.getByName(config[type]);
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length,
				address, Integer.parseInt(config[type + 1]));

		socket.send(packet);
	}

	public void close() {
		socket.close();
	}

	public DatagramSocket getSocket() {
		return socket;
	}

	public void setSocket(MulticastSocket socket) {
		this.socket = socket;
	}
}
