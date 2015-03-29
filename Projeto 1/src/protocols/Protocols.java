package protocols;

import java.io.IOException;
import java.net.Socket;

import javax.swing.JTextArea;

import connections.Multicast;
import connections.UDP;

public class Protocols {

	private String config[];
	private JTextArea logsOut;
	
	public Protocols(String[] config, JTextArea logsOut) {
		this.config = config;
		this.logsOut = logsOut;
	}

	public void start() throws NumberFormatException, IOException {
		Multicast MC = new Multicast(config, 0);
		Thread MCsocket = new Thread (new Runnable() {
			
			@Override
			public void run() {
				while(true) {
					try {
						String message = MC.getMessage();
						logsOut.append(message + "\n");
						System.out.println("MC received: " + message);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		});
		MCsocket.start();
	}

	public void backup(String path) throws IOException {
		UDP udp = new UDP(config, 0);
		udp.sendMessage(path);
		udp.close();
	}

	public void restore() {
		// TODO Auto-generated method stub
		
	}

	public void delete() {
		// TODO Auto-generated method stub
		
	}

	public void claimSpace() {
		// TODO Auto-generated method stub
		
	}

}
