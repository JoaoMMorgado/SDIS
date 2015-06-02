package network;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
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

	private static final long serialVersionUID = 2203462030517552618L;
	private JPanel contentPane;
	private JTextField textField;

	private RequestToken requestToken;
	private Twitter twitter;

	private AcessTokens acessTokens;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TwitterPost frame = new TwitterPost();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * 
	 * @throws TwitterException
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public TwitterPost() {
		//load do ficheiro aqui if else
		acessTokens = new AcessTokens();

		if (acessTokens.getAccessToken().equals("")
				|| acessTokens.getAccessTokenSecret().equals(""))
			try {
				showDialog();
			} catch (TwitterException | IOException | URISyntaxException e) {
				e.printStackTrace();
			}
	}

	public void showDialog() throws TwitterException, IOException,
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
					JOptionPane.showMessageDialog(null, "monweoinw");
				}
			}
		});
	}

	public void setAcessTokens(AcessTokens acessTokens) {
		this.acessTokens = acessTokens;
	}

	private boolean getAcessTokens(String pin) {

		AccessToken accessToken = null;
		try {
			if (pin.length() > 0) {
				accessToken = twitter.getOAuthAccessToken(requestToken, pin);
				acessTokens.setAccessToken(accessToken.getToken());
				acessTokens.setAccessTokenSecret(accessToken.getTokenSecret());
			} else
				JOptionPane.showMessageDialog(null, "Enter valid pin");
		} catch (TwitterException e) {
			if (401 == e.getStatusCode()) {
				System.out.println("Unable to get the access token.");
			} else {
				e.printStackTrace();
			}
		}

		try {
			postToTwitter("cenas teste");
		} catch (TwitterException e) {
			e.printStackTrace();
		}

		return true;
	}

	public boolean postToTwitter(String message) throws TwitterException {

		StatusUpdate statusUpdate = new StatusUpdate(message);
		twitter.updateStatus(statusUpdate);

		return true;
	}

}
