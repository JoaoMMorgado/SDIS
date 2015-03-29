package protocols;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
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
				byte[] message = MDB.getByteMessage();

				// separar todos os campos da mensagem e criar o novo ficheiro
				// com isso
				
				BufferedReader input = new BufferedReader(new InputStreamReader(
						new ByteArrayInputStream(message)));
				String header = input.readLine();
				logsOut.append(header + "\n");
				//fazer parse do header
				
				//ir buscar o resto dos dados e meter para um ficheiro (y) yey
				
				
				Thread.sleep(rand.nextInt(401));

				UDP udp = new UDP(config, 0);
				udp.sendMessage("confirmation from MDB to MC");
				udp.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public String fileID(File file) {

		MessageDigest hash = null;
		try {
			hash = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		String fileName = file.getName();
		long lastModified = file.lastModified();
		String tmp = fileName + " " + Integer.toString((int) lastModified)
				+ " " + file.length();

		hash.update(tmp.getBytes());

		byte[] hashed = hash.digest();

		return hashed.toString();

	}

	public String msgHeader(String fileID, int repDegree, int protocolVersion,
			int chunkNo) {
		String msgType = "PUTCHUNK";
		String version = Integer.toString(protocolVersion);

		String chunkNumber = Integer.toString(chunkNo);
		String replicationDegree = Integer.toString(repDegree);

		return msgType + " " + version + ".0" + " " + fileID + " "
				+ chunkNumber + " " + replicationDegree + " " + "\r\n";/*0xD 0xA;*/
	}

	/**
	 * Makes a request to backup
	 * 
	 * @param path
	 * @throws IOException
	 */
	public void backup(String path, int repDegree, int protocolVersion)
			throws IOException {
		// dividir ficheiro e enviar chunks para o MDB

		File file = new File(path);
		String fileID = fileID(file);
		long fileSize = file.length();

		long totalSizeRead = 0;

		int chunkNum = 0;
		BufferedInputStream inputStream = new BufferedInputStream(
				new FileInputStream(file));

		UDP udp = new UDP(config, 2);
		byte[] chunkData;

		boolean stop = false;
		do {
			// enviar pedido putchunk
			String header = msgHeader(fileID, repDegree, protocolVersion,
					chunkNum);

			if (fileSize - totalSizeRead < 64000)
				chunkData = new byte[(int) ((int) fileSize - totalSizeRead)];
			else
				chunkData = new byte[64000];
			
			System.out.println(chunkData.length);

			int sizeRead = inputStream.read(chunkData, 0, chunkData.length);

			byte[] headerByte = header.getBytes();
			byte[] message = merge(headerByte, chunkData);
			udp.sendMessage(message);

			// recebe confirmação -> se ok, continuar, senao duplicar tempo
			// merdas do genero
			String confirmation = MC.getMessage();
			logsOut.append(confirmation + "\n");
			chunkNum++;
			totalSizeRead += sizeRead;

		} while (fileSize > totalSizeRead);

		udp.close();
	}

	private byte[] merge(byte[] array1, byte[] array2) {
		int length = array1.length + array2.length;
		byte[] result = new byte[length];
		System.arraycopy(array1, 0, result, 0, array1.length);
		System.arraycopy(array2, 0, result, array1.length, array2.length);
		return result;
	}
}
