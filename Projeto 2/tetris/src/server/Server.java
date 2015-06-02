package server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.security.Key;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Server {

	private static int HTTP_PORT = 80;
	private static final byte[] ENCRYPTKEY = new String("MelhorFraseSempr")
			.getBytes();

	private static Handler handler;

	public static Handler getHandler() {
		return handler;
	}

	public void setHandler(Handler handler) {
		Server.handler = handler;
	}

	public void startServer() throws Exception {

		HttpServer httpServer = HttpServer.create(new InetSocketAddress(
				InetAddress.getLocalHost(), HTTP_PORT), 0);

		httpServer.createContext("/server", handler);
		httpServer.setExecutor(null);
		httpServer.start();
		
	}

	static class Handler implements HttpHandler, Serializable {

		private static final long serialVersionUID = -7926521198689979665L;

		private HashMap<String, String> users = new HashMap<String, String>();
		private HashMap<String, String> loggedUsers = new HashMap<String, String>();
		private HashMap<String, Integer> scores = new HashMap<String, Integer>();

		private LinkedList<String> messages = new LinkedList<String>();
		
		public void cleanLoggedUsers() {
			loggedUsers.clear();
		}

		public void handle(HttpExchange httpExchange) {
			try {
				if (httpExchange.getRequestMethod().equalsIgnoreCase("GET"))
					parseGetRequest(httpExchange);
				else if (httpExchange.getRequestMethod()
						.equalsIgnoreCase("PUT"))
					parsePutRequest(httpExchange);
				else if (httpExchange.getRequestMethod().equalsIgnoreCase(
						"POST"))
					parsePostRequest(httpExchange);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private String getRequestString(InputStream in) throws IOException {

			String request = null;
			try {
				ByteArrayOutputStream out = new ByteArrayOutputStream();
				byte buffer[] = new byte[4096];
				for (int n = in.read(buffer); n > 0; n = in.read(buffer)) {
					out.write(buffer, 0, n);
				}
				request = new String(out.toByteArray(), "UTF-8");
			} finally {
				in.close();
			}
			return request;
		}

		private void parsePostRequest(HttpExchange httpExchange)
				throws Exception {

			String request = decrypt(getRequestString(httpExchange
					.getRequestBody()));

			if (postRequest(request, httpExchange.getRemoteAddress()
					.getAddress().getHostAddress()))
				sendResponse(httpExchange, encrypt("SUCCESS"), 200);
			else
				sendResponse(httpExchange, encrypt("SERVER ERROR"), 200);
			httpExchange.close();
		}

		private boolean postRequest(String request, String ip) {
			String tokens[] = request.split("&");
			if (tokens[0].toUpperCase().equals("LOGIN"))
				return login(tokens, ip);
			else if (tokens[0].toUpperCase().equals("LOGOUT"))
				return logout(tokens);
			else if (tokens[0].toUpperCase().equals("SCORE"))
				return updateScore(tokens);
			else if (tokens[0].toUpperCase().equals("NEWMESSAGE"))
				return updateMessages(tokens);
			return false;
		}

		private boolean updateMessages(String[] tokens) {

			String username = tokens[1].substring(9);
			String message = tokens[2].substring(8);

			if (messages.size() < 50)
				return messages.add(username + ": " + message);
			else {
				messages.removeFirst();
				return messages.add(username + ": " + message);
			}
		}

		private boolean updateScore(String[] tokens) {

			String username = tokens[1].substring(9);
			String score = tokens[2].substring(6);
			int newScore = Integer.parseInt(score);

			if (!scores.containsKey(username)) {
				scores.put(username, newScore);
			} else {
				int oldScore = scores.get(username);
				if (newScore > oldScore)
					scores.put(username, oldScore);
			}

			return true;
		}

		private boolean logout(String[] tokens) {
			loggedUsers.remove(tokens[1].substring(9));
			System.out.println("Utilizador saiu: " + tokens[1].substring(9));
			return true;
		}

		private boolean login(String[] tokens, String ip) {

			String username = tokens[1].substring(9);
			String password = tokens[2].substring(9);

			if (users.containsKey(username))
				if (users.get(username).equals(password)) {
					loggedUsers.put(username, ip);
					System.out.println("Utilizador logado: " + username + ", "
							+ ip);
					return true;
				}

			return false;
		}

		private void parsePutRequest(HttpExchange httpExchange)
				throws Exception {
			String request = decrypt(getRequestString(httpExchange
					.getRequestBody()));

			if (putRequest(request))
				sendResponse(httpExchange, encrypt("SUCCESS"), 200);
			else
				sendResponse(httpExchange, encrypt("SERVER ERROR"), 200);
			httpExchange.close();
		}

		private boolean putRequest(String request) {

			String tokens[] = request.split("&");
			if (tokens[0].toUpperCase().equals("register".toUpperCase()))
				return addNewUser(tokens);
			return true;
		}

		private boolean addNewUser(String[] tokens) {
			String username = tokens[1].substring(9);
			String password = tokens[2].substring(9);

			if (!users.containsKey(username)) {
				users.put(username, password);
				System.out.println("Utilizador registado: " + username);
				return true;
			}
			return false;
		}

		private void parseGetRequest(HttpExchange httpExchange)
				throws Exception {

			String response = "";

			URI uri = httpExchange.getRequestURI();

			String type = uri.toString().split("&")[1];

			if (type.toUpperCase().equals("LOGGED_USERS"))
				response = loggedUsers.toString();
			else if (type.split("=")[0].toUpperCase().equals("IP")) {
				response = getMyPublicIp(type.split("=")[1]);
			} else if (type.split("=")[0].toUpperCase().equals("GET_SCORE")) {
				TreeMap<String, Integer> sortedMap = SortByValue(scores);
				response = sortedMap.toString();
			} else if (type.split("=")[0].toUpperCase().equals("MESSAGES")) {
				response = messages.toString();
			}

			sendResponse(httpExchange, encrypt(response), 200);
			httpExchange.close();
		}

		private String getMyPublicIp(String username) {
			return loggedUsers.get(username);
		}

		private boolean sendResponse(HttpExchange httpExchange,
				String response, int code) {
			try {
				Headers responseHeaders = httpExchange.getResponseHeaders();
				responseHeaders.set("Content-Type",
						"application/x-www-form-urlencoded");
				httpExchange.sendResponseHeaders(code,
						response.getBytes().length);
				httpExchange.getResponseBody().write(response.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

			return true;
		}

		/**
		 * http://www.programcreek.com/2013/03/java-sort-map-by-value/
		 */
		class ValueComparator implements Comparator<String> {

			Map<String, Integer> map;

			public ValueComparator(Map<String, Integer> base) {
				this.map = base;
			}

			public int compare(String a, String b) {
				if (map.get(a) >= map.get(b)) {
					return -1;
				} else {
					return 1;
				}
			}
		}

		public TreeMap<String, Integer> SortByValue(HashMap<String, Integer> map) {
			ValueComparator vc = new ValueComparator(map);
			TreeMap<String, Integer> sortedMap = new TreeMap<String, Integer>(
					vc);
			sortedMap.putAll(map);
			return sortedMap;
		}
	}

	/**
	 * from:
	 * http://www.code2learn.com/2011/06/encryption-and-decryption-of-data-
	 * using.html at: 28/05/2015
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