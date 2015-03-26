package protocols;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

class FileChunk {

	public static void splitFile(String filename) throws IOException {
		
		File f = new File(filename);
		int partCounter = 1;

		int sizeOfFiles = 1000 * 64;// 1MB
		byte[] buffer = new byte[sizeOfFiles];

		try (BufferedInputStream bis = new BufferedInputStream(
				new FileInputStream(f))) {

			String name = f.getName();

			int tmp = 0;
			while ((tmp = bis.read(buffer)) > 0) {
				
				//em vez de escrever para o ficheiro escrever para a stream do socket
				File newFile = new File(f.getParent(), name + "."
						+ String.format("%03d", partCounter++));
				try (FileOutputStream out = new FileOutputStream(newFile)) {
					out.write(buffer, 0, tmp);
				}
			}
		}
	}
	
	public static void main(String[] args) throws IOException {
		splitFile("cenas.txt");
	}
}
