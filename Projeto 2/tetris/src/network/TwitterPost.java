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

	// TOKENS DA NOSSA APP NO TWITTER
	private String consumerKey = "xfjotua1FHVhWqq23DdRqfJom";
	private String consumerSecret = "OOZjuMWfXaXxLTqv3kbMt39iwTf6MTewIyvVEVTbkenl6iVjTR";

	private static final long serialVersionUID = 2203462030517552618L;
	private JPanel contentPane;
	private JTextField textField;

	private AccessTokens accessTokens;

	private Twitter twitter;

	private boolean tokensLoaded;

	public TwitterPost() throws IOException, ClassNotFoundException,
			TwitterException, URISyntaxException {

		TwitterFactory twitterFactory = new TwitterFactory();
		twitter = twitterFactory.getInstance();
		twitter.setOAuthConsumer(consumerKey, consumerSecret);

		File file = new File("twitter.tk");
		if (file.exists()) {
			FileInputStream input = new FileInputStream("twitter.tk");
			ObjectInputStream objectInput = new ObjectInputStream(input);
			accessTokens = (AccessTokens) objectInput.readObject();
			objectInput.close();

			tokensLoaded = true;

			twitter.setOAuthAccessToken(new AccessToken(accessTokens
					.getAccessToken(), accessTokens.getAccessTokenSecret()));
		} else {
			accessTokens = new AccessTokens();
			tokensLoaded = false;
		}
	}

	public void connectToTwitter(final int score) throws TwitterException,
			IOException, URISyntaxException {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 200, 121);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		setVisible(true);

		JLabel lblGetAcessTokens = new JLabel("Insert PIN");
		lblGetAcessTokens.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblGetAcessTokens, BorderLayout.NORTH);

		textField = new JTextField();
		textField.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(textField, BorderLayout.CENTER);
		textField.setColumns(10);

		final RequestToken requestToken = twitter.getOAuthRequestToken();
		Desktop.getDesktop()
				.browse(new URI(requestToken.getAuthorizationURL()));

		JButton btnRequest = new JButton("Confirm Acess");
		contentPane.add(btnRequest, BorderLayout.SOUTH);
		btnRequest.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String pin = textField.getText();

				if (pin.length() > 0) {
					try {
						AccessToken token = twitter.getOAuthAccessToken(
								requestToken, pin);

						accessTokens.setAccessToken(token.getToken());
						accessTokens.setAccessTokenSecret(token
								.getTokenSecret());

						twitter.setOAuthAccessToken(token);
						tokensLoaded = true;

						postTweet("I MADE " + score
								+ " POINTS AT TETRIS MASTER!!!! :D ");

					} catch (TwitterException e1) {
						if (401 == e1.getStatusCode()) {
							System.out
									.println("Unable to get the access token.");
						} else {
							e1.printStackTrace();
						}
					}

					try {
						FileOutputStream fileOut = new FileOutputStream(
								"twitter.tk");
						ObjectOutputStream out = new ObjectOutputStream(fileOut);
						out.writeObject(accessTokens);
						out.close();
						fileOut.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}

					dispose();
				}

			}
		});
	}

	public boolean postTweet(String message) {

		StatusUpdate statusUpdate = new StatusUpdate(message);
		try {
			twitter.updateStatus(statusUpdate);
		} catch (TwitterException e) {
			if (e.getStatusCode() == 403) {

				JOptionPane.showMessageDialog(null, "Cannot post to Twitter due to update limits by Twitter!");
				e.printStackTrace();
				return false;
			}
			else {
				JOptionPane.showMessageDialog(null, "Something went wrong!");
				e.printStackTrace();
				return false;
			}
		}
		JOptionPane.showMessageDialog(null, "Success!");
		return true;
	}

	public void postToTwitter(int score) {
		int reply = JOptionPane.showConfirmDialog(null,
				"Post score to Twitter?", "Twitter", JOptionPane.YES_NO_OPTION);

		if (reply == JOptionPane.YES_OPTION) {
			try {
				if (!tokensLoaded)
					connectToTwitter(score);
				else
					postTweet("I MADE " + score
							+ " POINTS AT TETRIS MASTER!!!! :D ");
			} catch (IOException | TwitterException | URISyntaxException e) {
				e.printStackTrace();
			}

		}

	}

}
