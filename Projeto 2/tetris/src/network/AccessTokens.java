package network;

import java.io.Serializable;

public class AccessTokens implements Serializable {
	
	private static final long serialVersionUID = -3715552941033913985L;
	
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
