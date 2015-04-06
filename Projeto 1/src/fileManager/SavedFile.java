package fileManager;

public class SavedFile {
	private String fileName;
	private String fileID;
	private int numberChunks;

	public SavedFile(String fileName, String fileID, int numberChunks) {
		this.fileName = fileName;
		this.fileID = fileID;
		this.numberChunks = numberChunks;
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
}
