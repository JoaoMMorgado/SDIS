package database;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;


public class Database {



	  public void login(String username,String password) throws Exception {
	    URL url = new URL("http://www.java2s.com");
	    URLConnection conn = url.openConnection();
	    conn.setDoOutput(true);
	    OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

	    writer.write("username="+1+"&password="+password);
	    writer.flush();
	    String line;
	    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	    while ((line = reader.readLine()) != null) {
	      System.out.println(line);
	    }
	    writer.close();
	    reader.close();

	  }
	}