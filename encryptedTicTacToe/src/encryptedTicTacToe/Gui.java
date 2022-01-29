package encryptedTicTacToe;

import java.awt.BorderLayout;
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

	
	Chat chat;

	boolean setupDone = false;
	String mode;
	String ip;
	String username;

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

	public void printMessage(String message, String author) throws BadLocationException {
		if (author.equals(username)) {		// if this client is the sender
			String data = message + " [" + new SimpleDateFormat("HH:mm").format(new Date()) + "]";
			doc.insertString(doc.getLength(), "\n" + data, right );
			doc.setParagraphAttributes(doc.getLength(), 1, right, false);
		} else {			// if the other client is the author
			String data = "[" + new SimpleDateFormat("HH:mm").format(new Date()) + "] " + author + ": " + message;
			doc.insertString(doc.getLength(), "\n" + data, left );
			doc.setParagraphAttributes(doc.getLength(), 1, left, false);
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

		final ButtonGroup bGroup = new ButtonGroup();
		bGroup.add(serverButton);
		bGroup.add(clientButton);

		JPanel usageModeSelector = new JPanel(); // add all items to a panel
		usageModeSelector.setLayout(new GridLayout(2, 2));
		usageModeSelector.add(serverButton);
		usageModeSelector.add(clientButton);


		usageModeSelector.add(ipAddressTooltip);
		usageModeSelector.add(ipAddressInput);
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
						|| userNameInput.getText().isEmpty())) {
					String usageMode;
					if (clientButton.isSelected()) {
						usageMode = "c";
					} else {
						usageMode = "s";
					}
					System.out.println("Mode: " + usageMode);
					System.out.println("Server-address: " + ipAddressInput.getText());
					System.out.println("Username: " + userNameInput.getText());
					mode = usageMode; // write out all the data to be read later
					ip = ipAddressInput.getText();
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
		frame.setSize(400, 400);

		// Creating the MenuBar and adding components
		JMenuBar mb = new JMenuBar();
		mb.add(status);
		setStatus("Starte Applikation...");

		// Creating the panel at bottom and adding components
		JPanel panel = new JPanel(); // the panel is not visible in output
		JLabel label = new JLabel("Nachricht: ");
		final JTextField tf = new JTextField(10); // set length of input field to 10
		JButton send = new JButton("Senden");
		panel.add(label); // Components Added using Flow Layout
		panel.add(tf);
		panel.add(send);

		// set up textpane for displaying messages
		textPane.setEditable(false);
		
		// define stuff so that messages can later be aligned right or left (used in printMessage())
		StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);

		StyleConstants.setAlignment(right, StyleConstants.ALIGN_RIGHT);
				
				

		// Adding Components to the frame.
		frame.getContentPane().add(BorderLayout.SOUTH, panel);
		frame.getContentPane().add(BorderLayout.NORTH, mb);
		frame.getContentPane().add(BorderLayout.CENTER, textPane);
		frame.setVisible(true);

		send.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) { // send a message
				try {
					chat.sendEncryptedMessage(tf.getText());
					printMessage(tf.getText(), username);
					tf.setText("");

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
	}
}
