package GUI;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.JTextField;
import javax.swing.JButton;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TetrisLauncher extends JFrame {

	private static final long serialVersionUID = 7539395895367651551L;
	private JPanel contentPane;
	private JTextField textFieldIp;
	private JPanel panel;
	private JPanel panel_1;
	private JPanel panel_2;
	private JButton btnStartGame;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TetrisLauncher frame = new TetrisLauncher();
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
	 * @throws IOException
	 */
	public TetrisLauncher() throws IOException {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 350, 125);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JLabel lblInputServerIp = new JLabel("Input Server IP");
		lblInputServerIp.setHorizontalAlignment(SwingConstants.CENTER);
		lblInputServerIp.setFont(new Font("Tahoma", Font.PLAIN, 16));
		contentPane.add(lblInputServerIp, BorderLayout.NORTH);

		textFieldIp = new JTextField();
		textFieldIp.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(textFieldIp, BorderLayout.CENTER);
		textFieldIp.setColumns(10);

		File file = new File("configuration.txt");
		if (file.exists()) {
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			String line = bufferedReader.readLine();
			bufferedReader.close();
			textFieldIp.setText(line);
		}

		panel = new JPanel();
		contentPane.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new GridLayout(1, 3, 0, 0));

		panel_1 = new JPanel();
		panel.add(panel_1);

		btnStartGame = new JButton("Start Game");
		panel.add(btnStartGame);
		btnStartGame.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String ip = textFieldIp.getText();

				String expr = "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
				Pattern pattern = Pattern.compile(expr);

				Matcher matcher = pattern.matcher(ip);

				// STORE IP
				try {
					PrintWriter writer = new PrintWriter("configuration.txt",
							"UTF-8");
					writer.write(textFieldIp.getText());
					writer.close();
				} catch (FileNotFoundException | UnsupportedEncodingException e1) {
					e1.printStackTrace();
				}

				if (matcher.matches()) {
					MainWindow game = new MainWindow(ip);
					game.setLocationRelativeTo(null);
					game.setVisible(true);
					
					setVisible(false);
					// menuMusic.play();
				} else
					JOptionPane.showMessageDialog(null, "Wrong Format");
			}
		});

		panel_2 = new JPanel();
		panel.add(panel_2);
	}

}
