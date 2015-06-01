package server;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import server.Server.Handler;

import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.URL;

public class ServerLauncher extends JFrame {

	private static final long serialVersionUID = -2694330204559467028L;
	private JPanel contentPane;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerLauncher frame = new ServerLauncher();
					frame.setVisible(true);
					frame.addWindowListener(new WindowAdapter() {
						@Override
						public void windowClosing(java.awt.event.WindowEvent e) {
							try {
								FileOutputStream fileOut = new FileOutputStream(
										"database.db");
								ObjectOutputStream out = new ObjectOutputStream(
										fileOut);
								out.writeObject(Server.getHandler());
								out.close();
								fileOut.close();
								System.exit(0);
							} catch (IOException e1) {
								e1.printStackTrace();
							}

						}
					});

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 * 
	 * @throws Exception
	 */
	public ServerLauncher() throws Exception {

		Server server = new Server();

		Handler handler;
		File file = new File("database.db");
		if (file.exists()) {
			FileInputStream input = new FileInputStream("database.db");
			ObjectInputStream objectInput = new ObjectInputStream(input);
			handler = (Handler) objectInput.readObject();
			objectInput.close();
		} else
			handler = new Handler();

		server.setHandler(handler);
		server.startServer();

		setBounds(100, 100, 450, 145);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JLabel lblHttpServerInfo = new JLabel("HTTP SERVER INFO");
		lblHttpServerInfo.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblHttpServerInfo, BorderLayout.NORTH);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new GridLayout(3, 2, 0, 0));

		JLabel lblInternalIp = new JLabel("Internal IP");
		lblInternalIp.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblInternalIp);

		JLabel lblIntIP = new JLabel(InetAddress.getLocalHost().toString());
		panel.add(lblIntIP);

		JLabel lblExternalIp = new JLabel("External IP");
		lblExternalIp.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblExternalIp);

		URL whatismyip = new URL("http://checkip.amazonaws.com");
		BufferedReader in = new BufferedReader(new InputStreamReader(
		                whatismyip.openStream()));
		String ip = in.readLine();
		JLabel lblExtIp = new JLabel(ip);
		panel.add(lblExtIp);

		JLabel lblHttpPort = new JLabel("HTTP Port");
		lblHttpPort.setHorizontalAlignment(SwingConstants.CENTER);
		panel.add(lblHttpPort);

		JLabel lblPort = new JLabel("80");
		lblPort.setHorizontalAlignment(SwingConstants.LEFT);
		panel.add(lblPort);

		JLabel lblServerIsAlready = new JLabel("Server is running");
		lblServerIsAlready.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblServerIsAlready, BorderLayout.SOUTH);
	}

}
