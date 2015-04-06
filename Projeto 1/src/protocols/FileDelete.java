/**
 * 
 */
package protocols;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JTextArea;

import connections.Multicast;
import connections.UDP;

/**
 * @author joanabeleza
 *
 */
public class FileDelete {

	private String config[];
	private JTextArea logsOut;
	private Multicast MC;

	/**
	 * 
	 */
	public FileDelete(Multicast MC, String config[], JTextArea logsOut)
			throws NumberFormatException, IOException {
		this.config = config;
		this.logsOut = logsOut;
		this.MC = MC;
	}

	public void start() throws NumberFormatException, IOException,
			InterruptedException {

		while (true) {
			try {
				UDP udp = new UDP(config, 0);
				udp.sendMessage("confirmation from MDR to MC");
				udp.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String msgHeader(String fileID, int repDegree, int protocolVersion,
			int chunkNo) {
		String msgType = "DELETE";
		String version = Integer.toString(protocolVersion);

		return msgType + " " + version + ".0" + " " + fileID + " " + "\r\n";
	}

	/**
	 * Makes a request to backup
	 * 
	 * @param path
	 * @param protocolVersion 
	 * @throws IOException
	 */
	public void delete(String path, int protocolVersion) throws IOException {
		// enviar chunks para o MDR e juntar ficheiro
		UDP udp = new UDP(config, 4);
		udp.sendMessage(path);
		udp.close();
		
		//nº do chunk a ser apagado
		int chunkNum = 0; 
		int chunkMax = 1000; //nº maximo de chunks da file guardados

		do {
			String pathChunk = path + Integer.toString(chunkNum);
			Path pathDelete = Paths.get(pathChunk);
			Files.deleteIfExists(pathDelete);
			
			chunkNum++;
			
		} while(chunkNum < chunkMax);
		// depois de enviado e' preciso verficar se foi recebida a mensagem...
		 String message = MC.getMessage();
		 logsOut.append("MC received: " + message + "\n");
	}

}
