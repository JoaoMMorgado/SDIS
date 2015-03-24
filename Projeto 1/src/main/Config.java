package main;

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
		config = new String[3];
		args = new String[3];
	}
	
	public void storeConfigurations() throws IOException {
		FileWriter fileWriter = new FileWriter("configuration.txt");

		BufferedWriter writer = new BufferedWriter(fileWriter);

		writer.write(args[0] + "\n");
		config[0] = args[0];
		writer.write(args[1] + "\n");
		config[1] = args[1];
		writer.write(args[2]);
		config[2] = args[2];
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
			}
		} catch (IOException e) {
			return false;
		}

		bufferedReader.close();

		if (i < 3)
			return false;
			
		return true;
	}
}
