package fileManager;

import java.util.Vector;

public class FileManager {

	private Vector<Chunk> chunks;
	private Vector<SavedFile> savedFiles;

	public FileManager() {
		chunks = new Vector<Chunk>();
		savedFiles = new Vector<SavedFile>();
	}

	public void addSavedFile(SavedFile savedFile) {
		savedFiles.add(savedFile);
	}

	public void addChunk(Chunk chunk) {
		chunks.add(chunk);
	}

	public boolean checkIfChunkExists(String fileID, int chunkNo) {

		for (int i = 0; i < chunks.size(); i++) {
			if (chunks.get(i).getChunkNo() == chunkNo
					&& chunks.get(i).getFileID().equals(fileID))
				return true;
		}

		return false;
	}
	
	public SavedFile getSavedFileAtIndex(int index) {
		return savedFiles.get(index);
	}

}
