package protocols;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.swing.JComboBox;
import javax.swing.JTextArea;

import connections.Multicast;
import fileManager.FileManager;

public class Protocols {

	private ChunkBackup chunkBackup;
	private FileRestore fileRestore;
	private FileDelete fileDelete;
	private Multicast MC;
	private JTextArea logsOut;

	private static FileManager fileManager;

	public Protocols(String[] config, JTextArea logsOut,
			JComboBox<String> backupList) throws NumberFormatException,
			IOException {
		this.logsOut = logsOut;

		fileManager = new FileManager();
		MC = new Multicast(config, 0);
		chunkBackup = new ChunkBackup(config, logsOut, backupList);
		fileRestore = new FileRestore(config, logsOut);
		fileDelete = new FileDelete(config, logsOut, backupList);
	}

	/**
	 * Starts all concurrent threads that is needed
	 * 
	 * @throws NumberFormatException
	 *             +
	 * @throws IOException
	 */
	public void start() throws NumberFormatException, IOException {

		// SUBPROTOCOLS
		// MC control
		Thread mc = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					String message = null;
					try {
						message = MC.getMessage();
					} catch (IOException e) {
						e.printStackTrace();
					}
					logsOut.append(message);
					startProtocol(message);
				}
			}
		});
		mc.start();

		// CHUNK BACKUP
		Thread ChunckBackup = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					chunkBackup.start();
				} catch (NumberFormatException | IOException
						| InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		ChunckBackup.start();
	}

	private void startProtocol(String message) {
		String tokens[] = message.split(" ");

		if (tokens[0].equals("STORED")) {
			// adicionar as mensagens para um vetor dentro do backup ou parecido
		} else if (tokens[0].equals("GETCHUNK")) {
			Thread restore = new Thread(new Runnable() {

				@Override
				public void run() {
					try {
						fileRestore.sendChunks(tokens);
					} catch (NumberFormatException | IOException
							| InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
			restore.start();
		} else if (tokens[0].equals("DELETE")) {
			Thread delete = new Thread(new Runnable() {

				@Override
				public void run() {
					fileDelete.startDelete(tokens);
				}
			});
			delete.start();
		}
	}

	public static String fileID(File file) {

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

	public static byte[] merge(byte[] array1, byte[] array2) {
		int length = array1.length + array2.length;
		byte[] result = new byte[length];
		System.arraycopy(array1, 0, result, 0, array1.length);
		System.arraycopy(array2, 0, result, array1.length, array2.length);
		return result;
	}

	public static byte[] trim(byte[] bytes) {
		int i = bytes.length - 1;
		while (i >= 0 && bytes[i] == 0) {
			--i;
		}

		return Arrays.copyOf(bytes, i + 1);
	}

	public void backup(String path, int replicationDegree, int protocolVersion)
			throws IOException, InterruptedException {
		chunkBackup.backup(path, replicationDegree, protocolVersion);
	}

	public void restore(int selectedIndex, int protocolVersion)
			throws IOException {
		fileRestore.restore(selectedIndex, protocolVersion);
	}

	public void delete(int index, int version) throws IOException, InterruptedException {
		fileDelete.delete(index, version);
	}

	public void claimSpace() {
		// TODO Auto-generated method stub

	}

	public static FileManager getFileManager() {
		return fileManager;
	}
	
	public void setFileManager(FileManager fileManager) {
		Protocols.fileManager = fileManager;
	}
}
