package network;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import server.Server;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.SwingConstants;

public class TwitterPost extends JFrame {

	private static final long serialVersionUID = 2203462030517552618L;
	private JPanel contentPane;
	private JTextField textField;

	private RequestToken requestToken;
	private Twitter twitter;

	private AcessTokens acessTokens;

	private boolean connected;

	public TwitterPost() throws IOException, ClassNotFoundException,
			TwitterException, URISyntaxException {
		File file = new File("twitter.properties");
		if (file.exists()) {
			FileInputStream input = new FileInputStream("twitter.properties");
			ObjectInputStream objectInput = new ObjectInputStream(input);
			acessTokens = (AcessTokens) objectInput.readObject();
			objectInput.close();
			connected = true;
		} else {
			acessTokens = new AcessTokens();
			connected = false;
		}
	}

	public void connectToTwitter() throws TwitterException, IOException,
			URISyntaxException {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 200, 121);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JLabel lblGetAcessTokens = new JLabel("Insert PIN");
		lblGetAcessTokens.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblGetAcessTokens, BorderLayout.NORTH);

		textField = new JTextField();
		textField.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(textField, BorderLayout.CENTER);
		textField.setColumns(10);

		twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(AcessTokens.consumerKey,
				AcessTokens.consumerSecret);
		requestToken = twitter.getOAuthRequestToken();
		Desktop.getDesktop()
				.browse(new URI(requestToken.getAuthorizationURL()));

		JButton btnRequest = new JButton("Confirm Acess");
		contentPane.add(btnRequest, BorderLayout.SOUTH);
		btnRequest.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (getAcessTokens(textField.getText())) {
					JOptionPane.showMessageDialog(null, "Sucess!");
					FileOutputStream fileOut;
					try {
						fileOut = new FileOutputStream("database.db");
						ObjectOutputStream out = new ObjectOutputStream(fileOut);
						out.writeObject(Server.getHandler());
						out.close();
						fileOut.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}

				}
			}
		});
	}

	private boolean getAcessTokens(String pin) {

		AccessToken accessToken = null;
		try {
			if (pin.length() > 0) {
				accessToken = twitter.getOAuthAccessToken(requestToken, pin);
				acessTokens.setAccessToken(accessToken.getToken());
				acessTokens.setAccessTokenSecret(accessToken.getTokenSecret());
			} else
				JOptionPane.showMessageDialog(null, "Enter valid pin!");
		} catch (TwitterException e) {
			if (401 == e.getStatusCode()) {
				JOptionPane.showMessageDialog(null, "Unable to get the access token.");
			} else {
				e.printStackTrace();
			}
		}
		return true;
	}

	public boolean postToTwitter(String message) throws TwitterException {

		StatusUpdate statusUpdate = new StatusUpdate(message);
		twitter.updateStatus(statusUpdate);

		return true;
	}
	
	public boolean isConnected() {
		return connected;
	}

}
