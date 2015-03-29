package protocols;

import java.io.IOException;
import java.util.Random;

import javax.swing.JTextArea;

import connections.Multicast;
import connections.UDP;

public class ChunkBackup {

	private String config[];
	private JTextArea logsOut;
	private Multicast MC;
	private Multicast MDB;

	public ChunkBackup(Multicast MC, String config[], JTextArea logsOut)
			throws IOException {
		this.MC = MC;
		this.config = config;
		this.logsOut = logsOut;
		MDB = new Multicast(config, 2);
	}

	/**
	 * Waits for incoming requests
	 * 
	 * @throws NumberFormatException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void start() throws NumberFormatException, IOException,
			InterruptedException {

		Random rand = new Random();

		while (true) {
			try {
				String message = MDB.getMessage();
				logsOut.append("MDB received: " + message + "\n");

				Thread.sleep(rand.nextInt(401));

				UDP udp = new UDP(config, 0);
				udp.sendMessage("confirmation from MDB to MC");
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
	public void backup(String path) throws IOException {
		// dividir ficheiro e enviar chunks para o MDB
		UDP udp = new UDP(config, 2);
		udp.sendMessage(path);
		udp.close();

		 String message = MC.getMessage();
		 logsOut.append(message + "\n");
		/*
		 * File file = new File(path); int chunkSize = 64 * 1000; byte[] buffer
		 * = new byte[chunkSize];
		 * 
		 * BufferedInputStream inputFile = new BufferedInputStream( new
		 * FileInputStream(file));
		 * 
		 * int sizeRead = 0; while ((sizeRead = inputFile.read(buffer)) > 0) {
		 * udp.sendMessage(buffer);
		 * 
		 * // depois de enviado � preciso verificar se foi recebida a //
		 * mensagem... String message = MC.getMessage();
		 * logsOut.append("MC received: " + message + "\n");
		 * 
		 * } // fechar conexao para o MDB depois de tudo realizado
		 * inputFile.close();
		 */

	}
}
