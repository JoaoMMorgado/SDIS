package network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

public class Client {

	private static String URL = "http://172.30.40.240:80/server";

	public boolean register(String username, String password,
			String passwordCheck) throws Exception {
		String response = new String();
		if (password.equals(passwordCheck))
			response = sendPostPut("register&username=" + username
					+ "&password=" + password, "put");

		if (!response.equals("SUCCESS"))
			return false;
		else
			return true;
	}

	public boolean login(String username, String password) throws Exception {
		if (sendPostPut(
				"login&username=" + username + "&" + "password=" + password,
				"post").equals("SUCCESS")) {
			return true;
		}
		return false;
	}
	
	public void logout(String username) throws Exception {
		sendPostPut("logout&username=" + username, "post");
	}

	public Vector<String[]> getCurrentUsers() throws Exception {

		Vector<String[]> users = new Vector<String[]>();
		
		StringBuffer login = sendGet("logged_users");
		
		if (!login.toString().equalsIgnoreCase("{}")) {
			login.deleteCharAt(0);
			login.deleteCharAt(login.length() - 1);
			String[] tempUsers = login.toString().split(", ");

			for (int i = 0; i < tempUsers.length; i++) {
				String array[] = new String[2];
				array[0] = tempUsers[i].split("=")[0];
				array[1] = tempUsers[i].split("=")[1];
				users.add(array);
			}
		}
		return users;
	}
	
	public String getMyIp (String username) throws Exception {
		return sendGet("IP=" + username).toString();
	}

	// HTTP GET request
	private StringBuffer sendGet(String type) throws Exception {

		URL obj = new URL(URL + "&" + type);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + URL);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		System.out.println(response.toString());

		return response;
	}

	// HTTP POST request
	private String sendPostPut(String message, String type) throws Exception {

		URL obj = new URL(URL);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// add request header
		con.setRequestMethod(type.toUpperCase());
		con.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		con.setRequestProperty("Content-Length",
				"" + Integer.toString(message.getBytes().length));

		// Send request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(message);
		wr.flush();
		wr.close();

		int responseCode = con.getResponseCode();
		System.out.println("\nSending request to URL : " + URL);
		System.out.println("Post parameters : " + message);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		System.out.println(response.toString());

		return response.toString();
	}
}
