package connections;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * 
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
		InetAddress address = InetAddress.getByName(config[type]);
		socket.joinGroup(address);
		socket.setLoopbackMode(true); //com isto o proprio pc nao recebe as mensagens que envia para o grupo de multicast
	}

	public String getMessage() throws IOException {
		int size = 64 * 1000; //este valor deve ser o maximo do udp. porque 64000 é o tamanho dos dados. sem header
		byte[] buffer = new byte[size];
		DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);
		socket.receive(inPacket);
		String message = new String(buffer, 0, inPacket.getLength());
		return message;
	}
	
	public byte[] getByteMessage() throws IOException {
		int size = 64 * 1000;
		byte[] buffer = new byte[size];
		DatagramPacket inPacket = new DatagramPacket(buffer, buffer.length);
		socket.receive(inPacket);
		return inPacket.getData();
	}

	public MulticastSocket getSocket() {
		return socket;
	}

	public void setSocket(MulticastSocket socket) {
		this.socket = socket;
	}
}
