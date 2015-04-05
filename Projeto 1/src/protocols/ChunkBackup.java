package protocols;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JTextArea;

import connections.*;

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
	 * Waits for incoming requests and stores the backup if not exists
	 * 
	 * @throws NumberFormatException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void start() throws NumberFormatException, IOException,
			InterruptedException {

		// falta processar todos os stored enviados pelos outros peers

		Random rand = new Random();

		while (true) {
			try {
				byte[] message = MDB.getByteMessage();

				BufferedReader input = new BufferedReader(
						new InputStreamReader(new ByteArrayInputStream(message)));
				String header = input.readLine();

				logsOut.append(header + "\n");
				String tokens[] = header.split(" ");

				if (!tokens[0].equals("PUTCHUNK"))
					return;

				byte[] dataTemp = new byte[message.length - header.length() + 2];
				for (int i = header.length() + 2, j = 0; i < message.length; i++, j++)
					dataTemp[j] = message[i];

				byte data[] = trim(dataTemp);

				File newFile = new File(tokens[2] + "\\" + "file" + ".part"
						+ tokens[3]);

				if (!newFile.exists() && !newFile.isDirectory()) {

					newFile.getParentFile().mkdirs();
					newFile.createNewFile();

					FileOutputStream output = new FileOutputStream(newFile);
					output.write(data);
					output.flush();
					output.close();
				}

				Thread.sleep(rand.nextInt(401));

				UDP udp = new UDP(config, 0);
				String response = msgStored(tokens[2], tokens[1], tokens[3]);
				udp.sendMessage(response);
				udp.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private byte[] trim(byte[] bytes) {
		int i = bytes.length - 1;
		while (i >= 0 && bytes[i] == 0) {
			--i;
		}

		return Arrays.copyOf(bytes, i + 1);
	}

	public String msgHeader(String fileID, int repDegree, int protocolVersion,
			int chunkNo) {
		String msgType = "PUTCHUNK";
		String version = Integer.toString(protocolVersion);

		String chunkNumber = Integer.toString(chunkNo);
		String replicationDegree = Integer.toString(repDegree);

		return msgType + " " + version + ".0" + " " + fileID + " "
				+ chunkNumber + " " + replicationDegree + " " + "\r\n";
	}

	public String msgStored(String fileID, String protocolVersion,
			String chunkNo) {
		String msgType = "STORED";

		return msgType + " " + protocolVersion + " " + fileID + " " + chunkNo
				+ " " + "\r\n" + "\r\n";
	}

	/**
	 * Makes a request to backup
	 * 
	 * @param path
	 * @param protocolVersion
	 * @param repDegree
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void backup(String path, int repDegree, int protocolVersion)
			throws IOException, InterruptedException {
		// dividir ficheiro e enviar chunks para o MDB

		File file = new File(path);
		String fileID = Protocols.fileID(file);
		long fileSize = file.length();

		long totalSizeRead = 0;

		int chunkNum = 0;
		BufferedInputStream inputStream = new BufferedInputStream(
				new FileInputStream(file));

		UDP udp = new UDP(config, 2);
		byte[] chunkData;

		boolean stop = false;
		long waitTime = 500;

		do {
			String header = msgHeader(fileID, repDegree, protocolVersion,
					chunkNum);

			if (fileSize - totalSizeRead < 64000)
				chunkData = new byte[(int) ((int) fileSize - totalSizeRead)];
			else
				chunkData = new byte[64000];

			int sizeRead = inputStream.read(chunkData, 0, chunkData.length);

			byte[] headerByte = header.getBytes();
			byte[] message = merge(headerByte, chunkData);

//			udp.sendMessage(message);
//			String confirmation = MC.getMessage();
//			logsOut.append(confirmation + "\n");

			int currentRepDegree = 0;
			while (!stop && waitTime <= 31.5 * 1000) {
				udp.sendMessage(message);
				stop = waitForStoredMessages(waitTime, fileID, repDegree,
						currentRepDegree);
				waitTime *= 2;
			}
			chunkNum++;
			totalSizeRead += sizeRead;

		} while (fileSize > totalSizeRead);

		inputStream.close();
		udp.close();
	}

	private boolean waitForStoredMessages(long waitTime, String fileID,
			int repDegree, int currentRepDegree) throws IOException {

		long end = System.currentTimeMillis() + waitTime;

		while (System.currentTimeMillis() <= end
				&& currentRepDegree < repDegree) {
			String confirmation = MC.getMessage();

			String[] tokens = confirmation.split(" ");

			if (tokens[0].equals("STORED") && tokens[2].equals(fileID))
				currentRepDegree++;
			logsOut.append(confirmation + "\n");

		}
		System.out.println("cenas3");
		return currentRepDegree >= repDegree;
	}

	private byte[] merge(byte[] array1, byte[] array2) {
		int length = array1.length + array2.length;
		byte[] result = new byte[length];
		System.arraycopy(array1, 0, result, 0, array1.length);
		System.arraycopy(array2, 0, result, array1.length, array2.length);
		return result;
	}
}
