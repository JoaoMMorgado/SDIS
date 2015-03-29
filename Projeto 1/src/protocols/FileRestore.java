package protocols;

import java.io.IOException;
import java.util.Random;

import javax.swing.JTextArea;

import connections.Multicast;
import connections.UDP;

public class FileRestore {

	private String config[];
	private JTextArea logsOut;
	private Multicast MC;
	private Multicast MDR;

	public FileRestore(Multicast MC, String config[], JTextArea logsOut) throws NumberFormatException, IOException {
		this.config = config;
		this.logsOut = logsOut;
		this.MC = MC;

		MDR = new Multicast(config, 4);
	}

	public void start() throws NumberFormatException, IOException,
			InterruptedException {

		Random rand = new Random();

		while (true) {
			try {
				String message = MDR.getMessage();
				logsOut.append("MDR received: " + message + "\n");

				Thread.sleep(rand.nextInt(401));

				UDP udp = new UDP(config, 0);
				udp.sendMessage("confirmation from MDR to MC");
				udp.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Makes a request to backup
	 * 
	 * @param path
	 * @throws IOException
	 */
	public void restore(String path) throws IOException {
		// enviar chunks para o MDR e juntar ficheiro
		UDP udp = new UDP(config, 4);
		udp.sendMessage(path);
		udp.close();

		// depois de enviado � preciso verficar se foi recebida a mensagem...
		 String message = MC.getMessage();
		 logsOut.append("MC received: " + message + "\n");
	}

}
