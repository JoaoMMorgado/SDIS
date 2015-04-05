package protocols;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.swing.JTextArea;

import connections.Multicast;

public class Protocols {

//	private String config[];
//	private JTextArea logsOut;
	
	private ChunkBackup chunkBackup;
	private FileRestore fileRestore;
	private Multicast MC;
	
	

	public Protocols(String[] config, JTextArea logsOut)
			throws NumberFormatException, IOException {
		// this.config = config;
		// this.logsOut = logsOut;
		MC = new Multicast(config, 0);
		chunkBackup = new ChunkBackup(MC, config, logsOut);
		fileRestore = new FileRestore(MC, config, logsOut);
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
		// CHUNK BACKUP

		Thread ChunckBackup = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					chunkBackup.start();
				} catch (NumberFormatException | IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		ChunckBackup.start();

		// CHUNK RESTORE

		Thread FileRestore = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					fileRestore.start();
				} catch (NumberFormatException | IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		FileRestore.start();
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

	public void backup(String path, int replicationDegree, int protocolVersion) throws IOException, InterruptedException {
		chunkBackup.backup(path, replicationDegree, protocolVersion);
	}

	public void restore(String file, int protocolVersion) throws IOException {
		fileRestore.restore(file, protocolVersion);
	}

	public void delete() {
		// TODO Auto-generated method stub

	}

	public void claimSpace() {
		// TODO Auto-generated method stub

	}

}
