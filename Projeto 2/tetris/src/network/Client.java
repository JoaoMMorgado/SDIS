package network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Vector;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Client {

	private static String URL;

	private static final byte[] ENCRYPTKEY = new String("MelhorFraseSempr")
			.getBytes();

	public Client(String ip) {
		URL = "http://" + ip + ":80/server";
	}

	public boolean sendMessage(String message, String username)
			throws Exception {
		if (sendPostPut(
				"newmessage&username=" + username + "&" + "message=" + message,
				"post").equals("SUCCESS")) {
			return true;
		}
		return false;
	}

	public String getChatMessages() throws Exception {
		StringBuffer messages = sendGet("messages");

		messages.deleteCharAt(0);
		messages.deleteCharAt(messages.length() - 1);

		String formatedMessages = "";
		if (!messages.toString().equalsIgnoreCase("[]")) {
			String tokens[] = messages.toString().split(", ");

			for (int i = 0; i < tokens.length; i++) {
				formatedMessages += tokens[i] + "\n";
			}
		}

		return formatedMessages;
	}

	public boolean register(String username, String password,
			String passwordCheck) throws Exception {
		String response = new String();
		if (password.equals(passwordCheck))
			response = sendPostPut("register&username="
					+ username + "&password=" + encryptPassword(password),
					"put");

		if (!response.equals("SUCCESS"))
			return false;
		else
			return true;
	}

	public boolean login(String username, String password) throws Exception {
		if (sendPostPut(
				"login&username=" + username + "&" + "password="
						+ encryptPassword(password), "post").equals("SUCCESS")) {
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

	public String getMyIp(String username) throws Exception {
		return sendGet("IP=" + username).toString();
	}

	public boolean updateScore(String username, int score) throws Exception {
		if (sendPostPut(
				"score&username=" + username + "&" + "score="
						+ Integer.toString(score), "post").equals("SUCCESS")) {
			return true;
		}
		return false;
	}

	public Vector<String[]> topScores() throws Exception {

		Vector<String[]> top = new Vector<String[]>();

		StringBuffer login = sendGet("get_score");

		if (!login.toString().equalsIgnoreCase("{}")) {
			login.deleteCharAt(0);
			login.deleteCharAt(login.length() - 1);
			String[] tempUsers = login.toString().split(", ");

			for (int i = 0; i < tempUsers.length; i++) {
				String array[] = new String[2];
				array[0] = tempUsers[i].split("=")[0];
				array[1] = tempUsers[i].split("=")[1];
				top.add(array);
			}
		}
		return top;
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
		System.out.println(decrypt(response.toString()));

		return new StringBuffer(decrypt(response.toString()));
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
		String encryptedMessage = encrypt(message);

		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(encryptedMessage);
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
		System.out.println(decrypt(response.toString()));

		return decrypt(response.toString());
	}

	private String encryptPassword(String password)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md = MessageDigest.getInstance("SHA");
		md.update(password.getBytes("UTF-8"));
		byte buffer[] = md.digest();
		return new BASE64Encoder().encode(buffer);
	}

	/**
	 * from:
	 * http://www.code2learn.com/2011/06/encryption-and-decryption-of-data-
	 * using.html at: 28/05/2015
	 * 
	 * @return
	 * @throws Exception
	 */
	private static Key generateKey() throws Exception {
		Key key = new SecretKeySpec(ENCRYPTKEY, "AES");
		return key;
	}

	private static String encrypt(String Data) throws Exception {
		Key key = generateKey();
		Cipher c = Cipher.getInstance("AES");
		c.init(Cipher.ENCRYPT_MODE, key);
		byte[] encVal = c.doFinal(Data.getBytes());
		String encryptedValue = new BASE64Encoder().encode(encVal);
		return encryptedValue;
	}

	private static String decrypt(String encryptedData) throws Exception {
		Key key = generateKey();
		Cipher c = Cipher.getInstance("AES");
		c.init(Cipher.DECRYPT_MODE, key);
		byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
		byte[] decValue = c.doFinal(decordedValue);
		String decryptedValue = new String(decValue);
		return decryptedValue;
	}
}
