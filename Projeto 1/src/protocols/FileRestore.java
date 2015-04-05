package protocols;

import java.io.File;
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

	public FileRestore(Multicast MC, String config[], JTextArea logsOut)
			throws NumberFormatException, IOException {
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

	public String msgHeader(String fileID, int protocolVersion, int chunkNo) {
		String msgType = "GETCHUNK";
		String version = Integer.toString(protocolVersion);

		String chunkNumber = Integer.toString(chunkNo);

		return msgType + " " + version + ".0" + " " + fileID + " "
				+ chunkNumber + " " + "\r\n";
	}

	public String msgChunk(String fileID, String protocolVersion, String chunkNo) {
		String msgType = "CHUNK";

		return msgType + " " + protocolVersion + " " + fileID + " " + chunkNo
				+ " " + "\r\n" + "\r\n";
	}

	/**
	 * Makes a request to backup
	 * 
	 * @param path
	 * @param protocolVersion
	 * @throws IOException
	 */
	public void restore(String path, int protocolVersion) throws IOException {

		File file = new File(path);
		String fileID = Protocols.fileID(file);
		long fileSize = file.length();

		//ir buscar a "database" o numero de chunks de file
		int chunkNum = 1000; // mudar para numero de chunks a mandar para que o
								// ficheiro esteja completo

		// enviar chunks para o MDR e juntar ficheiro
		UDP udp = new UDP(config, 4);

		do {
			String header = msgHeader(fileID, protocolVersion, chunkNum);

			byte[] message = header.getBytes();
			System.out.println(message.length);
			udp.sendMessage(message);
		} while (chunkNum > 0);

		// depois de enviado e' preciso verficar se foi recebida a mensagem...
		String message = MC.getMessage();
		logsOut.append("MC received: " + message + "\n");
	}
}
