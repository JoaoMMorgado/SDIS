package fileManager;

import java.io.Serializable;

public class Chunk implements Serializable {

	private static final long serialVersionUID = -7821027837975141236L;

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
