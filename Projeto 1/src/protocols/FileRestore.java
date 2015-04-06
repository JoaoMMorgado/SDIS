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

import javax.swing.JTextArea;

import connections.Multicast;
import connections.UDP;
import fileManager.SavedFile;

public class FileRestore {

	private String config[];
	private JTextArea logsOut;
	private Multicast MDR;

	public FileRestore(String config[], JTextArea logsOut)
			throws NumberFormatException, IOException {

		this.config = config;
		this.logsOut = logsOut;

		MDR = new Multicast(config, 4);
	}

	public void sendChunks(String[] tokens) throws NumberFormatException,
			IOException, InterruptedException {

		Random rand = new Random();
		Thread.sleep(rand.nextInt(401));

		if (Protocols.getFileManager().checkIfChunkExists(tokens[2],
				Integer.parseInt(tokens[3]))) {

			UDP udp = new UDP(config, 4);

			String header = msgChunkHeader(tokens[2], tokens[3]);
			byte[] headerBytes = header.getBytes(StandardCharsets.ISO_8859_1);

			File file = new File(tokens[2] + "\\" + "file" + ".part"
					+ tokens[3]);
			BufferedInputStream inputStream = new BufferedInputStream(
					new FileInputStream(file));
			byte[] data = new byte[64 * 1024];
			inputStream.read(data, 0, data.length);
			
			byte[] chunk = Protocols.merge(headerBytes, Protocols.trim(data));

			udp.sendMessage(chunk);
			udp.close();
			inputStream.close();
		}
	}

	public String msgRequest(String fileID, int protocolVersion, int chunkNo) {
		String msgType = "GETCHUNK";
		String version = Integer.toString(protocolVersion);

		String chunkNumber = Integer.toString(chunkNo);

		return msgType + " " + version + ".0" + " " + fileID + " "
				+ chunkNumber + " " + "\r\n";
	}

	public String msgChunkHeader(String fileID, String chunkNo) {
		String msgType = "CHUNK";

		return msgType + " " + fileID + " " + chunkNo + " " + "\r\n" + "\r\n";
	}

	/**
	 * Makes a request to backup
	 * 
	 * @param selectedIndex
	 * @param protocolVersion
	 * @throws IOException
	 */
	public void restore(int selectedIndex, int protocolVersion)
			throws IOException {

		UDP udp = new UDP(config, 0);

		SavedFile savedFile = Protocols.getFileManager().getSavedFileAtIndex(
				selectedIndex);
		File restoredFile = new File(savedFile.getFileName());
		FileOutputStream output = new FileOutputStream(restoredFile);
		int chunkNo = 0;
		do {
			String getChunk = msgRequest(savedFile.getFileID(),
					protocolVersion, chunkNo);

			udp.sendMessage(getChunk.getBytes(StandardCharsets.ISO_8859_1));

			byte[] received = MDR.getByteMessage();

			BufferedReader input = new BufferedReader(new InputStreamReader(
					new ByteArrayInputStream(received)));
			String header = input.readLine();
			logsOut.append(header);

			byte[] dataTemp = new byte[received.length - header.length() + 4];
			
			for (int i = header.length() + 2, j = 0; i < received.length; i++, j++)
				dataTemp[j] = received[i];
			
			byte data[] = Protocols.trim(dataTemp);
			output.write(data);
			
			chunkNo++;
		} while (chunkNo < savedFile.getNumberChunks());

		output.flush();
		output.close();
		udp.close();
	}
}
