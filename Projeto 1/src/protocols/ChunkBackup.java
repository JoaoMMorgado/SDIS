package protocols;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JTextArea;

import connections.*;
import fileManager.Chunk;
import fileManager.SavedFile;

public class ChunkBackup {

	private String config[];
	private JTextArea logsOut;
	private JComboBox<String> backupList;
	private Multicast MDB;
	private Vector<String> storedMessages;

	public ChunkBackup(String config[], JTextArea logsOut,
			JComboBox<String> backupList) throws IOException {

		this.config = config;
		this.logsOut = logsOut;
		this.backupList = backupList;

		MDB = new Multicast(config, 2);
		storedMessages = new Vector<String>();
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

		Random rand = new Random();

		while (true) {
			try {
				byte[] message = MDB.getByteMessage();

				BufferedReader input = new BufferedReader(
						new InputStreamReader(new ByteArrayInputStream(message)));
				String header = input.readLine();

				logsOut.append(header + "\n");
				String tokens[] = header.split(" ");

				if (tokens[0].equals("PUTCHUNK")) {

					byte[] dataTemp = new byte[message.length - header.length()
							+ 4];
					for (int i = header.length() + 4, j = 0; i < message.length; i++, j++)
						dataTemp[j] = message[i];

					byte data[] = Protocols.trim(dataTemp);

					if (data.length < Protocols.getFileManager()
							.getSpaceAvailable()) {

						File newFile = new File(tokens[2] + "\\" + "file"
								+ ".part" + tokens[3]);

						if (!newFile.exists() && !newFile.isDirectory()) {

							newFile.getParentFile().mkdirs();
							newFile.createNewFile();

							FileOutputStream output = new FileOutputStream(
									newFile);
							output.write(data);
							output.flush();
							output.close();

							Protocols.getFileManager().decreaseAvailableSpace(
									data.length);

							Chunk chunk = new Chunk(tokens[2],
									Integer.parseInt(tokens[3]),
									Integer.parseInt(tokens[4]), data.length);
							Protocols.getFileManager().addChunk(chunk);

						}
					}

					Thread.sleep(rand.nextInt(401));

					UDP udp = new UDP(config, 0);
					String response = msgStored(tokens[2], tokens[1], tokens[3]);
					udp.sendMessage(response);
					udp.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void processStoredOtherPeers() {

	}

	public String msgHeader(String fileID, int repDegree, int protocolVersion,
			int chunkNo) {
		String msgType = "PUTCHUNK";
		String version = Integer.toString(protocolVersion);

		String chunkNumber = Integer.toString(chunkNo);
		String replicationDegree = Integer.toString(repDegree);

		return msgType + " " + version + ".0" + " " + fileID + " "
				+ chunkNumber + " " + replicationDegree + " " + "\r\n" + "\r\n";
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

			byte[] headerByte = header.getBytes(StandardCharsets.ISO_8859_1);
			byte[] message = Protocols.merge(headerByte, chunkData);

			// udp.sendMessage(message);

			while (!stop && waitTime <= 31500) {
				udp.sendMessage(message);
				stop = waitForStoredMessages(waitTime, fileID, repDegree);
				waitTime *= 2;
			}

			chunkNum++;
			totalSizeRead += sizeRead;
			stop = false;
			waitTime = 500;

		} while (fileSize > totalSizeRead);

		SavedFile savedFile = new SavedFile(path, file.getName(), fileID,
				chunkNum);
		Protocols.getFileManager().addSavedFile(savedFile);
		backupList.addItem(path);

		inputStream.close();
		udp.close();
	}

	public void addStoredMessage(String message) {
		storedMessages.add(message);
	}

	private boolean waitForStoredMessages(long waitTime, String fileID,
			int repDegree) throws IOException, InterruptedException {
		int currentRepDegree = 0;
		long end = System.currentTimeMillis() + waitTime;
		int i = 0;
		while (System.currentTimeMillis() <= end) {

			if (i < storedMessages.size()) {
				String[] tokens = storedMessages.get(i).split(" ");
				// falta verificar se nao vem do mesmo ip -> guardar ip
				// juntamente com a string... era "facil" mas sem hipotese de
				// testar
				if (tokens[0].equals("STORED") && tokens[2].equals(fileID))
					currentRepDegree++;
				i++;
			}
			Thread.sleep(50);
		}
		storedMessages.clear();
		return currentRepDegree >= repDegree;
	}

}
