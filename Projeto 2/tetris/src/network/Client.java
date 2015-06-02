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
import javax.swing.JOptionPane;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class Client {

	private static String URL;

	private static final byte[] ENCRYPTKEY = new String("MelhorFraseSempr")
			.getBytes();

	public Client(String ip) {
		URL = "http://" + ip + ":80/server";
	}

	public boolean sendScore(String username, int score, boolean multiplayer) {
		System.out.println("score&username=" + username + "&" + "score="
				+ Integer.toString(score) + "&singleplayer");
		if (multiplayer) {
			if (sendPostPut(
					"score&username=" + username + "&" + "score="
							+ Integer.toString(score) + "&multiplayer", "post")
					.equals("SUCCESS"))
				return true;
		} else {
			if (sendPostPut(
					"score&username=" + username + "&" + "score="
							+ Integer.toString(score) + "&singleplayer", "post")
					.equals("SUCCESS"))
				return true;
		}

		return false;
	}

	public String getScores(boolean multiplayer) {
		String scores = "";

		if (multiplayer) {
			StringBuffer message = sendGet("GET_MULT_SCORE");
			message.deleteCharAt(0);
			message.deleteCharAt(message.length() - 1);

			String[] tempScores = message.toString().split(", ");

			for (int i = 0; i < 10 && i < tempScores.length; i++) {
				String token[] = tempScores[i].split("=");
				scores += Integer.toString(i + 1) + ": " + token[0] + "  -  "
						+ token[1] + "\n";
			}
		} else {
			StringBuffer message = sendGet("GET_SINGLE_SCORE");
			message.deleteCharAt(0);
			message.deleteCharAt(message.length() - 1);

			String[] tempScores = message.toString().split(", ");

			for (int i = 0; i < 10 && i < tempScores.length; i++) {
				String token[] = tempScores[i].split("=");
				scores += Integer.toString(i + 1) + ": " + token[0] + "  -  "
						+ token[1] + "\n";
			}
		}

		return scores;
	}

	public boolean sendMessage(String message, String username) {
		if (sendPostPut(
				"newmessage&username=" + username + "&" + "message=" + message,
				"post").equals("SUCCESS")) {
			return true;
		}
		return false;
	}

	public String getChatMessages() {
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
			String passwordCheck) throws NoSuchAlgorithmException,
			UnsupportedEncodingException {
		String response = new String();
		if (password.equals(passwordCheck))
			response = sendPostPut("register&username=" + username
					+ "&password=" + encryptPassword(password), "put");

		if (!response.equals("SUCCESS"))
			return false;
		else
			return true;
	}

	public String login(String username, String password)
			throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String response = sendPostPut("login&username=" + username + "&"
				+ "password=" + encryptPassword(password), "post");

		return response;

	}

	public void logout(String username) {
		sendPostPut("logout&username=" + username, "post");
	}

	public Vector<String[]> getCurrentUsers() {

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

	public String getMyIp(String username) {
		return sendGet("IP=" + username).toString();
	}

	public boolean updateScore(String username, int score) {
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
	private StringBuffer sendGet(String type) {

		try {

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

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Server is not running! :(");
			System.err.println("server not running...");
		}
		return null;

	}

	// HTTP POST request
	private String sendPostPut(String message, String type) {
		try {
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

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Server is not running! :(");
			System.err.println("server not running...");
		}
		return "FAILED";
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
