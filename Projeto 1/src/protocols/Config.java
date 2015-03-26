package protocols;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Config {

	protected String config[];
	private String args[];

	public Config(String args[]) {
		this.args = args;
		config = new String[6];
		args = new String[6];
	}

	public void storeConfigurations() throws IOException {
		FileWriter fileWriter = new FileWriter("configuration.txt");

		BufferedWriter writer = new BufferedWriter(fileWriter);

		for (int i = 0; i < 6; i++) {
			writer.write(args[i] + "\n");
			config[i] = args[i];
		}

		writer.close();
	}

	public boolean readConfigurations() throws IOException {
		String fileName = "configuration.txt";

		String line = null;

		FileReader fileReader;
		try {
			fileReader = new FileReader(fileName);
		} catch (FileNotFoundException e1) {
			return false;
		}
		BufferedReader bufferedReader = new BufferedReader(fileReader);

		int i = 0;

		try {
			while ((line = bufferedReader.readLine()) != null) {
				config[i] = line;
				i++;
				if (i == 6)
					break;
			}
		} catch (IOException e) {
			return false;
		}

		bufferedReader.close();

		if (i < 6)
			return false;

		return true;
	}
}
