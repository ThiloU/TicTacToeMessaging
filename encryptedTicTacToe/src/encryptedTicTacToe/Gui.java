package encryptedTicTacToe;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class Gui {
	JLabel status = new JLabel();
	JTextPane textPane = new JTextPane();
	StyledDocument doc = textPane.getStyledDocument();

	SimpleAttributeSet left = new SimpleAttributeSet();
	SimpleAttributeSet right = new SimpleAttributeSet();

	final JButton[] ticTacToeButton = new javax.swing.JButton[9];

	Chat chat;
	TicTacToe tic;

	boolean setupDone = false;
	String mode;
	String ip;
	int port;
	String username;
	String strangerUsername;

	public void setTicTacToe(TicTacToe tic) {
		this.tic = tic;
	}

	public void setStrangerUsername(String strangerUsername) {
		this.strangerUsername = strangerUsername;
	}

	public void setChat(Chat chat) {
		this.chat = chat;
	}

	public void setStatus(String prefix, String msg) {
		status.setText(prefix + ": " + msg);
	}

	public void setStatus(String msg) { // Overloading to get optional parameter "prefix"
		setStatus("Status", msg);
	}

	public void showMessage(String msg) {

		JOptionPane.showMessageDialog(new JFrame(), msg);
	}

	public void showNonBlockingMessage(String msg) {
		JOptionPane pane = new JOptionPane(msg);
		JDialog dialog = pane.createDialog(pane, "Message");
		dialog.setModal(false); // this says not to block background components
		dialog.setVisible(true);
	}

	public void printMessage(String message, boolean selfIsAuthor) throws BadLocationException {

		tic.handleCommands(message, selfIsAuthor);

		if (selfIsAuthor) {
			String data = message + " [" + new SimpleDateFormat("HH:mm").format(new Date()) + "]";
			doc.insertString(doc.getLength(), "\n" + data, right);
			doc.setParagraphAttributes(doc.getLength(), 1, right, false);
		} else { // if the other client is the author
			String data = "[" + new SimpleDateFormat("HH:mm").format(new Date()) + "] " + strangerUsername + ": "
					+ message;
			doc.insertString(doc.getLength(), "\n" + data, left);
			doc.setParagraphAttributes(doc.getLength(), 1, left, false);
		}
	}

	public void printTicTacToe(String[] moves) {
		for (int i = 0; i < 9; i++) {
			ticTacToeButton[i].setText(moves[i]);
		}
	}

	public void setup() {
		final JFrame setupWindow = new JFrame("Setup"); // create setup window
		setupWindow.setLayout(new GridLayout(3, 1));
		setupWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setupWindow.setSize(300, 300);

		// make button group to select usage mode
		JRadioButton serverButton = new JRadioButton("Server", true);
		final JRadioButton clientButton = new JRadioButton("Client");

		final JTextField ipAddressInput = new JTextField(10); // initialize input field for ip-address
		final JLabel ipAddressTooltip = new JLabel("IP-Addresse des Servers: ");
		ipAddressInput.setVisible(false); // make it invisible by default
		ipAddressTooltip.setVisible(false);
		ipAddressInput.setToolTipText("IP-Addresse des Servers");
		ipAddressInput.setText("localhost");

		final JTextField portInput = new JTextField(10);
		final JLabel portTooltip = new JLabel("Serverport");
		portInput.setToolTipText("Port des Servers");
		portInput.setText("6666");

		final ButtonGroup bGroup = new ButtonGroup();
		bGroup.add(serverButton);
		bGroup.add(clientButton);

		JPanel usageModeSelector = new JPanel(); // add all items to a panel
		usageModeSelector.setLayout(new GridLayout(3, 2));
		usageModeSelector.add(serverButton);
		usageModeSelector.add(clientButton);

		usageModeSelector.add(ipAddressTooltip);
		usageModeSelector.add(ipAddressInput);

		usageModeSelector.add(portTooltip);
		usageModeSelector.add(portInput);
		usageModeSelector.setBorder(BorderFactory.createTitledBorder("Modus auswählen"));

		final JTextField userNameInput = new JTextField(10); // make an input for username
		userNameInput.setToolTipText("Nutzername");
		JLabel invisibleSpacer = new JLabel();
		invisibleSpacer.setVisible(false);

		JPanel userNameSelection = new JPanel();
		userNameSelection.setLayout(new GridLayout(3, 2));

		userNameSelection.add(invisibleSpacer);
		userNameSelection.add(invisibleSpacer);
		userNameSelection.add(new JLabel("Benutzername: "));
		userNameSelection.add(userNameInput);
		userNameSelection.add(invisibleSpacer);
		userNameSelection.add(invisibleSpacer);

		JButton doneButton = new JButton("Chat starten");

		// add all panels to the actual window
		setupWindow.getContentPane().add(usageModeSelector);
		setupWindow.getContentPane().add(userNameSelection);
		setupWindow.getContentPane().add(doneButton);

		clientButton.addActionListener(new ActionListener() { // make input field visible if client mode is selected
			@Override
			public void actionPerformed(ActionEvent e) {
				ipAddressTooltip.setVisible(true);
				ipAddressInput.setVisible(true);
			}
		});

		serverButton.addActionListener(new ActionListener() { // make input field invisible if server mode is selected
			@Override
			public void actionPerformed(ActionEvent e) {
				ipAddressTooltip.setVisible(false);
				ipAddressInput.setVisible(false);
			}
		});

		doneButton.addActionListener(new ActionListener() { // return all values to main class
			@Override
			public void actionPerformed(ActionEvent e) {
				// check if all input field are filled out (if needed)
				if (!((ipAddressInput.getText().isEmpty() && clientButton.isSelected())
						|| userNameInput.getText().isEmpty() || portInput.getText().isEmpty())) {
					String usageMode;
					if (clientButton.isSelected()) {
						usageMode = "c";
					} else {
						usageMode = "s";
					}
					System.out.println("Mode: " + usageMode);
					System.out.println("Server-address: " + ipAddressInput.getText());
					System.out.println("Server-port: " + portInput.getText());
					System.out.println("Username: " + userNameInput.getText());
					mode = usageMode; // write out all the data to be read later
					ip = ipAddressInput.getText();
					port = Integer.parseInt(portInput.getText());
					username = userNameInput.getText();
					setupDone = true;
					setupWindow.dispose();
				} else {
					JOptionPane.showMessageDialog(setupWindow, "Fehler: Alle Felder müssen ausgefüllt sein!");
					System.out.println("Test");
				}
			}
		});

		setupWindow.setVisible(true);
		// Also TODO: Amogus
	}

	public void main() {
		JFrame frame = new JFrame("TicTacToeMessaging");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600, 400);

		// Creating the MenuBar and adding components
		JMenuBar mb = new JMenuBar();
		mb.add(status);
		setStatus("Starte Applikation...");

		// Creating the panel at bottom and adding components
		JPanel panel = new JPanel(); // the panel is not visible in output
		final JCheckBox showTicTacToe = new JCheckBox("TicTacToe zeigen");
		JLabel label = new JLabel("Nachricht: ");
		final JTextField tf = new JTextField(10); // set length of input field to 10
		JButton send = new JButton("Senden");
		panel.add(label); // Components Added using Flow Layout
		panel.add(tf);
		panel.add(send);
		panel.add(showTicTacToe);

		// set up textpane for displaying messages
		textPane.setEditable(false);
		// define stuff so that messages can later be aligned right or left (used in
		// printMessage())
		StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
		StyleConstants.setAlignment(right, StyleConstants.ALIGN_RIGHT);

		JButton resetButton = new JButton("Für Reset abstimmen");
		final JPanel ticTacToeContent = new JPanel();
		ticTacToeContent.setVisible(false);
		ticTacToeContent.setLayout(new GridBagLayout());
		final JPanel ticTacToeBoard = new JPanel(new GridLayout(3, 3));
		
		GridBagConstraints c1 = new GridBagConstraints();
		c1.gridy = 0;
		c1.weighty = 0.8;
		c1.weightx = 1;
		c1.fill = GridBagConstraints.BOTH;
		ticTacToeContent.add(ticTacToeBoard, c1);
		
		c1.gridy = 1;
		c1.weighty = 0.2;
		c1.weightx = 1;
		c1.fill = GridBagConstraints.HORIZONTAL;
		ticTacToeContent.add(resetButton, c1);
		ticTacToeContent.setBorder(BorderFactory.createTitledBorder("TicTacToe"));

		ActionListener ticTacToeListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) { // if a tictactoe button is pressed, send it as a message
				for (int i = 0; i < 9; i++) {
					if (e.getSource() == ticTacToeButton[i]) {
						try {
							printMessage("/set " + i, true);
							chat.sendEncryptedMessage("/set " + i);
						} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
								| IllegalBlockSizeException | BadPaddingException | IOException
								| BadLocationException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}

		};

		for (int i = 0; i < 9; i++) {
			ticTacToeButton[i] = new javax.swing.JButton("");
			ticTacToeButton[i].setPreferredSize(new Dimension(40, 40));
			ticTacToeButton[i].addActionListener(ticTacToeListener);
			ticTacToeBoard.add(ticTacToeButton[i]);
		}

		JScrollPane scrollPane = new JScrollPane(textPane);
		scrollPane.setAutoscrolls(true);

		frame.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		// Adding Components to the frame.

//		frame.getContentPane().add(BorderLayout.SOUTH, panel);
//		frame.getContentPane().add(BorderLayout.NORTH, mb);

		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		frame.add(mb, c);

		c.weightx = 0.7;
		c.weighty = 1;
		c.gridy = 1;
		c.fill = GridBagConstraints.BOTH;

		frame.add(scrollPane, c);

		c.weightx = 0.3;
		c.weighty = 0;
		c.gridy = 1;
		c.fill = GridBagConstraints.BOTH;

		frame.add(ticTacToeContent, c);

		c.weightx = 0;
		c.weighty = 0;
		c.gridy = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		frame.add(panel, c);

		frame.setVisible(true);

		send.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) { // send a message
				try {
					if (!tf.getText().trim().isEmpty()) { // if the string contains something other than spaces
						chat.sendEncryptedMessage(tf.getText());
						printMessage(tf.getText(), true);
						tf.setText("");
					}

				} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| IllegalBlockSizeException | BadPaddingException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (BadLocationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		});

		showTicTacToe.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (showTicTacToe.isSelected()) {
					ticTacToeContent.setVisible(true);
				} else {
					ticTacToeContent.setVisible(false);
				}
			}

		});

		resetButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					printMessage("/resetgame", true);
					chat.sendEncryptedMessage("/resetgame");
				} catch (BadLocationException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| IllegalBlockSizeException | BadPaddingException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		});
	}
}
