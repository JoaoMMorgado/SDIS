package fileManager;

import java.io.Serializable;

public class SavedFile implements Serializable {

	private static final long serialVersionUID = 8728840304655952192L;

	private String fileName;
	private String path;
	private String fileID;
	private int numberChunks;

	public SavedFile(String path, String fileName, String fileID, int numberChunks) {
		this.fileName = fileName;
		this.fileID = fileID;
		this.numberChunks = numberChunks;
		this.path = path;
	}

	public int getNumberChunks() {
		return numberChunks;
	}

	public String getFileName() {
		return fileName;
	}

	public String getFileID() {
		return fileID;
	}
	
	public String getPath() {
		return path;
	}
}
