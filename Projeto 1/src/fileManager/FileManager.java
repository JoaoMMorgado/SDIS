package fileManager;

import java.io.File;
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

	public boolean deleteAllChunks(String fileID) {

		for (int i = 0; i < chunks.size(); i++) {
			if (chunks.get(i).getFileID().equals(fileID)) {
				chunks.remove(i);
				i--;
			}
		}
		
		return deleteDirectory(new File(fileID));
	}

	/**
	 * From stack overflow: function to delete a folder and all files inside it
	 * http://stackoverflow.com/questions/3775694/deleting-folder-from-java
	 * 
	 * @param directory
	 * @return
	 */
	public boolean deleteDirectory(File directory) {
	    if(directory.exists()){
	        File[] files = directory.listFiles();
	        if(null!=files){
	            for(int i=0; i<files.length; i++) {
	                if(files[i].isDirectory()) {
	                    deleteDirectory(files[i]);
	                }
	                else {
	                    files[i].delete();
	                }
	            }
	        }
	    }
	    return(directory.delete());
	}

	public boolean removeSavedFile(String fileID) {
		for (int i = 0; i < savedFiles.size(); i++) {
			if(savedFiles.get(i).getFileID().equals(fileID)) {
				savedFiles.remove(i);
				return true;
			}
		}
		return false;
	}
	
}
