package fileManager;

public class Chunk {
	
	String fileID;
	int chunkNo;
	int replicationDegree;
	int currentReplicationDegree;
	
	public Chunk (String fileID, int chunkNo, int replicationDegree) {
		this.fileID = fileID;
		this.chunkNo = chunkNo;
		this.replicationDegree = replicationDegree;
	}

	public String getFileID() {
		return fileID;
	}

	public int getChunkNo() {
		return chunkNo;
	}

	public int getReplicationDegree() {
		return replicationDegree;
	}

	public int getCurrentReplicationDegree() {
		return currentReplicationDegree;
	}
	
}
