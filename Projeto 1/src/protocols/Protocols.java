package protocols;

import java.io.IOException;

import javax.swing.JTextArea;

import connections.Multicast;
import connections.UDP;

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

	public void backup(String path) throws IOException {
		chunkBackup.backup(path);
	}

	public void restore(String file) throws IOException {
		fileRestore.restore(file);
	}

	public void delete() {
		// TODO Auto-generated method stub

	}

	public void claimSpace() {
		// TODO Auto-generated method stub

	}

}
