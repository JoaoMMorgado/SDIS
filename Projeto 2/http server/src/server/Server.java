package server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class Server {

	static int HTTP_PORT = 80;

	public static void main(String args[]) throws Exception {

		HttpServer httpServer = HttpServer.create(new InetSocketAddress(
				InetAddress.getLocalHost(), HTTP_PORT), 0);
		httpServer.createContext("/server", new Handler());
		httpServer.setExecutor(null);
		httpServer.start();

	}

	static class Handler implements HttpHandler {

		private HashMap<String, String> users = new HashMap<String, String>();
		private HashMap<String, String> loggedUsers = new HashMap<String, String>();

		public void handle(HttpExchange httpExchange) throws IOException {

			if (httpExchange.getRequestMethod().equalsIgnoreCase("GET"))
				parseGetRequest(httpExchange);
			else if (httpExchange.getRequestMethod().equalsIgnoreCase("PUT"))
				parsePutRequest(httpExchange);
			else if (httpExchange.getRequestMethod().equalsIgnoreCase("POST"))
				parsePostRequest(httpExchange);
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
				throws IOException {

			// verificar se o content que chegou veio em termos analisando o
			// tamanho do body com o header.

			String request = getRequestString(httpExchange.getRequestBody());

			if (postRequest(request, httpExchange.getRemoteAddress()
					.getAddress().getHostAddress()))
				sendResponse(httpExchange, "SUCCESS", 200);
			else
				sendResponse(httpExchange, "SERVER ERROR", 200);
			httpExchange.close();
		}

		private boolean postRequest(String request, String ip) {
			String tokens[] = request.split("&");
			if (tokens[0].toUpperCase().equals("LOGIN"))
				return login(tokens, ip);
			else if (tokens[0].toUpperCase().equals("LOGOUT"))
				return logout(tokens);
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
					System.out.println("Utilizador logado: " + username + ", " + ip);
					return true;
				}

			return false;
		}

		private void parsePutRequest(HttpExchange httpExchange)
				throws IOException {
			String request = getRequestString(httpExchange.getRequestBody());

			if (putRequest(request))
				sendResponse(httpExchange, "SUCCESS", 200);
			else
				sendResponse(httpExchange, "SERVER ERROR", 200);
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

		private void parseGetRequest(HttpExchange httpExchange) {

			String response = "";

			URI uri = httpExchange.getRequestURI();

			String type = uri.toString().split("&")[1];

			if (type.toUpperCase().equals("LOGGED_USERS"))
				response = loggedUsers.toString();
			else if (type.split("=")[0].toUpperCase().equals("IP")) {
				response = getMyPublicIp(type.split("=")[1]);
			}

			sendResponse(httpExchange, response, 200);
			httpExchange.close();
		}

		private String getMyPublicIp(String username) {
			return loggedUsers.get(username);
		}

		private boolean sendResponse(HttpExchange httpExchange,
				String response, int code) {
			try {
				Headers responseHeaders = httpExchange.getResponseHeaders();
				responseHeaders.set("Content-Type", "aplication/json");
				httpExchange.sendResponseHeaders(code,
						response.getBytes().length);
				httpExchange.getResponseBody().write(response.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}

			return true;
		}
	}
}