package network;

import java.io.Serializable;

public class AcessTokens implements Serializable {
	
	private static final long serialVersionUID = -3715552941033913985L;

	//TOKENS DA NOSSA APP NO TWITTER
	static String consumerKey = "UcJ9G3iIWXT3W8mxzPvfTgdKe";
	static String consumerSecret = "G21CKhTP5KfMIOeX6l6H2sO0kcKn3H090ftAsEnY93AJ0pvt8j";
	
	private String accessToken;
	private String accessTokenSecret;
	
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getAccessTokenSecret() {
		return accessTokenSecret;
	}
	public void setAccessTokenSecret(String accessTokenSecret) {
		this.accessTokenSecret = accessTokenSecret;
	}

}
