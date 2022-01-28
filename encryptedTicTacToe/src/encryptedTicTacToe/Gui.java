package encryptedTicTacToe;

import java.awt.BorderLayout;
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

public class Gui {
	JLabel status = new JLabel();
	JTextArea ta = new JTextArea();
	Chat chat;
	
	public void setChat(Chat chat) {
		this.chat = chat;
	}
	public void setStatus(String prefix, String msg) {
		status.setText(prefix + ": " + msg);
	}

	public void setStatus(String msg) { // Overloading to get optional parameter "prefix"
		setStatus("Status", msg);
	}

	public void printMessage(String message, String author) {
		String data = "[" + new SimpleDateFormat("HH:mm").format(new Date()) + "] " + author + ": " + message;
		ta.setText(ta.getText() + "\n" + data);
	}

	public String[] setup(String mode) {
		// TODO: make a setup popup to get usage mode + connection details
		// Also TODO: Amogus
		return null;

	}

	public void mainWindowStart() {
		JFrame frame = new JFrame("TicTacToeMessaging");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 400);

		// Creating the MenuBar and adding components
		JMenuBar mb = new JMenuBar();
		mb.add(status);
		setStatus("Starte Applikation...");

		// Creating the panel at bottom and adding components
		JPanel panel = new JPanel(); // the panel is not visible in output
		JLabel label = new JLabel("Enter Text");
		final JTextField tf = new JTextField(10); // set length of input field to 10
		JButton send = new JButton("Send");
		panel.add(label); // Components Added using Flow Layout
		panel.add(tf);
		panel.add(send);

		// Text Area at the Center

		ta.setEditable(false);

		// Adding Components to the frame.
		frame.getContentPane().add(BorderLayout.SOUTH, panel);
		frame.getContentPane().add(BorderLayout.NORTH, mb);
		frame.getContentPane().add(BorderLayout.CENTER, ta);
		frame.setVisible(true);

		send.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					chat.sendEncryptedMessage(tf.getText());
					tf.setText("");
					
					
				} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| IllegalBlockSizeException | BadPaddingException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	}
}
