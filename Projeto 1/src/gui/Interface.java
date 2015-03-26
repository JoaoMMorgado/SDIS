package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

public class Interface extends JFrame {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Interface frame = new Interface();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Interface() {
		setTitle("Dropbox LAN version");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 620, 500);
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);
		
		JLabel configLabel = new JLabel("Configura\u00E7\u00F5es");
		configLabel.setHorizontalAlignment(SwingConstants.CENTER);
		configLabel.setFont(new Font("Tahoma", Font.PLAIN, 20));
		configLabel.setBounds(0, 0, 600, 30);
		getContentPane().add(configLabel);
		
		JPanel IPdefinitions = new JPanel();
		IPdefinitions.setBorder(null);
		IPdefinitions.setBounds(0, 30, 600, 105);
		getContentPane().add(IPdefinitions);
		IPdefinitions.setLayout(null);
		
		JLabel lblMulticastControlChannel = new JLabel("Multicast control channel");
		lblMulticastControlChannel.setBounds(5, 36, 200, 34);
		lblMulticastControlChannel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		IPdefinitions.add(lblMulticastControlChannel);
		
		JLabel lblMulticastDataChannel = new JLabel("Multicast backup channel IP");
		lblMulticastDataChannel.setBounds(5, 71, 200, 34);
		lblMulticastDataChannel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		IPdefinitions.add(lblMulticastDataChannel);
		
		JLabel lblMulticastRestoreChannel = new JLabel("Multicast restore channel IP");
		lblMulticastRestoreChannel.setBounds(5, 1, 200, 34);
		lblMulticastRestoreChannel.setFont(new Font("Tahoma", Font.PLAIN, 16));
		IPdefinitions.add(lblMulticastRestoreChannel);
		
		JSpinner mc11 = new JSpinner();
		mc11.setFont(new Font("Tahoma", Font.PLAIN, 14));
		mc11.setBounds(208, 7, 50, 25);
		mc11.setModel(new SpinnerNumberModel(224, 224, 255, 1));
		IPdefinitions.add(mc11);
		
		JSpinner mc12 = new JSpinner();
		mc12.setModel(new SpinnerNumberModel(0, 0, 255, 1));
		mc12.setFont(new Font("Tahoma", Font.PLAIN, 15));
		mc12.setBounds(267, 7, 50, 25);
		IPdefinitions.add(mc12);
		
		JSpinner mc13 = new JSpinner();
		mc13.setModel(new SpinnerNumberModel(0, 0, 255, 1));
		mc13.setFont(new Font("Tahoma", Font.PLAIN, 15));
		mc13.setBounds(327, 7, 50, 25);
		IPdefinitions.add(mc13);
		
		JSpinner mc14 = new JSpinner();
		mc14.setModel(new SpinnerNumberModel(0, 0, 255, 1));
		mc14.setFont(new Font("Tahoma", Font.PLAIN, 15));
		mc14.setBounds(389, 7, 50, 25);
		IPdefinitions.add(mc14);
		
		JSpinner mdb21 = new JSpinner();
		mdb21.setFont(new Font("Tahoma", Font.PLAIN, 15));
		mdb21.setBounds(208, 39, 50, 25);
		mdb21.setModel(new SpinnerNumberModel(224, 224, 255, 1));
		IPdefinitions.add(mdb21);
		
		JSpinner mdb22 = new JSpinner();
		mdb22.setModel(new SpinnerNumberModel(0, 0, 255, 1));
		mdb22.setFont(new Font("Tahoma", Font.PLAIN, 15));
		mdb22.setBounds(267, 39, 50, 25);
		IPdefinitions.add(mdb22);
		
		JSpinner mdb23 = new JSpinner();
		mdb23.setModel(new SpinnerNumberModel(0, 0, 255, 1));
		mdb23.setFont(new Font("Tahoma", Font.PLAIN, 15));
		mdb23.setBounds(327, 39, 50, 25);
		IPdefinitions.add(mdb23);
		
		JSpinner mdb24 = new JSpinner();
		mdb24.setModel(new SpinnerNumberModel(0, 0, 255, 1));
		mdb24.setFont(new Font("Tahoma", Font.PLAIN, 15));
		mdb24.setBounds(389, 39, 50, 25);
		IPdefinitions.add(mdb24);
		
		JSpinner mdr31 = new JSpinner();
		mdr31.setModel(new SpinnerNumberModel(224, 224, 255, 1));
		mdr31.setFont(new Font("Tahoma", Font.PLAIN, 15));
		mdr31.setBounds(208, 71, 50, 25);
		IPdefinitions.add(mdr31);
		
		JSpinner mdr32 = new JSpinner();
		mdr32.setModel(new SpinnerNumberModel(0, 0, 255, 1));
		mdr32.setFont(new Font("Tahoma", Font.PLAIN, 15));
		mdr32.setBounds(267, 71, 50, 25);
		IPdefinitions.add(mdr32);
		
		JSpinner mdr33 = new JSpinner();
		mdr33.setModel(new SpinnerNumberModel(0, 0, 255, 1));
		mdr33.setFont(new Font("Tahoma", Font.PLAIN, 15));
		mdr33.setBounds(327, 71, 50, 25);
		IPdefinitions.add(mdr33);
		
		JSpinner mdr34 = new JSpinner();
		mdr34.setFont(new Font("Tahoma", Font.PLAIN, 15));
		mdr34.setBounds(389, 71, 50, 25);
		mdr34.setModel(new SpinnerNumberModel(0, 0, 255, 1));
		IPdefinitions.add(mdr34);
		
		JLabel lblPort = new JLabel("Port 1");
		lblPort.setBounds(467, 1, 51, 34);
		lblPort.setFont(new Font("Tahoma", Font.PLAIN, 16));
		IPdefinitions.add(lblPort);
		
		JLabel lblPort_1 = new JLabel("Port 2");
		lblPort_1.setBounds(467, 36, 51, 34);
		lblPort_1.setFont(new Font("Tahoma", Font.PLAIN, 16));
		IPdefinitions.add(lblPort_1);
		
		JLabel lblPort_2 = new JLabel("Port 3");
		lblPort_2.setBounds(467, 71, 51, 34);
		lblPort_2.setFont(new Font("Tahoma", Font.PLAIN, 16));
		IPdefinitions.add(lblPort_2);
		
		JSpinner mcPort = new JSpinner();
		mcPort.setModel(new SpinnerNumberModel(1024, 1024, 65000, 1));
		mcPort.setFont(new Font("Tahoma", Font.PLAIN, 15));
		mcPort.setBounds(518, 7, 70, 25);
		IPdefinitions.add(mcPort);
		
		JSpinner mdbPort = new JSpinner();
		mdbPort.setFont(new Font("Tahoma", Font.PLAIN, 15));
		mdbPort.setModel(new SpinnerNumberModel(1024, 1024, 65000, 1));
		mdbPort.setBounds(518, 39, 70, 25);
		IPdefinitions.add(mdbPort);
		
		JSpinner mdrPort = new JSpinner();
		mdrPort.setModel(new SpinnerNumberModel(1024, 1024, 65000, 1));
		mdrPort.setFont(new Font("Tahoma", Font.PLAIN, 15));
		mdrPort.setBounds(518, 71, 70, 25);
		IPdefinitions.add(mdrPort);
	}
}
