/**
 * 
 */
package protocols;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import connections.UDP;
import fileManager.SavedFile;

/**
 * @author joanabeleza
 *
 */
public class FileDelete {

	private String config[];
	private JTextArea logsOut;
	private JComboBox<String> backupList;

	/**
	 * @param backupList
	 */
	public FileDelete(String config[], JTextArea logsOut,
			JComboBox<String> backupList) {
		this.config = config;
		this.logsOut = logsOut;
		this.backupList = backupList;
	}

	public void startDelete(String[] tokens) {
		String fileID = tokens[2];

		boolean noMoreChunks = Protocols.getFileManager().deleteAllChunks(
				fileID);

		if (noMoreChunks)
			JOptionPane.showMessageDialog(null, "File deleted!");

	}

	public String msgDelete(String fileID, int protocolVersion) {
		String msgType = "DELETE";
		String version = Integer.toString(protocolVersion);

		return msgType + " " + version + ".0" + " " + fileID + " " + "\r\n";
	}

	/**
	 * Makes a request to delete
	 * 
	 * @param path
	 * @param protocolVersion
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void delete(int selectedIndex, int protocolVersion)
			throws IOException, InterruptedException {
		UDP udp = new UDP(config, 0);
		SavedFile savedFile = Protocols.getFileManager().getSavedFileAtIndex(
				selectedIndex);
		String message = msgDelete(savedFile.getFileID(), protocolVersion);
		logsOut.append(message + "\n");
		int i = 0;
		while (i < 5) {
			udp.sendMessage(message.getBytes(StandardCharsets.ISO_8859_1));
			Thread.sleep(100);
			i++;
		}
		udp.close();

		if (Protocols.getFileManager().removeSavedFile(savedFile.getFileID())) {
			backupList.removeItemAt(selectedIndex);
			backupList.revalidate();
		}
	}

}
