package protocols;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Config {

	private String config[];

	public Config() {
		setConfig(new String[6]);
		
		//default config
		config[0] = "224.0.0.2";
		config[1] = "4445";
		config[2] = "224.0.0.3";
		config[3] = "4446";
		config[4] = "224.0.0.4";
		config[5] = "4447";
	}

	public void storeConfigurations(String args[]) throws IOException {
		
		FileWriter fileWriter = new FileWriter("configuration.txt");

		BufferedWriter writer = new BufferedWriter(fileWriter);

		for (int i = 0; i < 6; i++) {
//			config[i] = args[i];
			writer.write(args[i] + "\n");
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
				getConfig()[i] = line;
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

	public String[] getConfig() {
		return config;
	}

	public void setConfig(String config[]) {
		this.config = config;
	}
}
