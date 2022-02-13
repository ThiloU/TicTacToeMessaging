package encryptedTicTacToe;

import java.io.*;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.text.BadLocationException;

public class Chat {
	Socket connection;
	Crypt crypt;
	Gui gui;
	TicTacToe tic;
	String ownUsername;
	String strangerUsername;

	public Chat(Socket connection, Crypt crypt, Gui gui, String ownUsername, TicTacToe tic) {
		this.connection = connection;
		this.crypt = crypt;
		this.gui = gui;
		this.ownUsername = ownUsername;
		this.tic = tic;
	}

	public void startMessageListener() throws IOException, NoSuchAlgorithmException, InvalidKeyException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		System.out.println("Chatverbindung wurde hergestellt");
		new Crypt();

		Thread messageListener = new Thread() {
			public void run() {
				try {
					while (true) {
						String msg = receiveEncryptedMessage();
						tic.handleCommands(msg, false);
						gui.printMessage(msg, false);

					}
				} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| IllegalBlockSizeException | BadPaddingException | ClassNotFoundException | IOException e) {
					gui.setStatus("FEHLER", "Verbindung zum Gesprächspartner unterbrochen");
					gui.showMessage("Verbindung zum Gesprächspartner unterbrochen");
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};

		messageListener.start();
	}

		
	public String receiveEncryptedMessage() throws IOException, InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException {
		byte[] msgBytes = receiveBytes();
		String msg = crypt.decryptMessage(msgBytes);
		return msg;
	}

	public void sendEncryptedMessage(String msg) throws IOException, InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		byte[] msgBytes = crypt.encryptMessage(msg);
		sendBytes(msgBytes);
	}

	public PublicKey receivePublicKey() throws IOException, ClassNotFoundException {
		ObjectInputStream dis = new ObjectInputStream(connection.getInputStream()); // creating inputStream to
																					// read data
		PublicKey msg = (PublicKey) dis.readObject();
		return msg;
	}

	public void sendPublicKey(PublicKey msg) throws IOException {
		ObjectOutputStream d = new ObjectOutputStream(connection.getOutputStream()); // create outputStream to send
																						// messsages on
		d.writeObject(msg);
		d.flush(); // Flushing out internal buffers
	}

	public byte[] receiveBytes() throws IOException, ClassNotFoundException {
		ObjectInputStream dis = new ObjectInputStream(connection.getInputStream()); // creating inputStream to
																					// read data
		byte[] msg = (byte[]) dis.readObject();
		return msg;
	}

	public void sendBytes(byte[] msg) throws IOException {
		ObjectOutputStream d = new ObjectOutputStream(connection.getOutputStream()); // create outputStream to send
																						// messsages on
		d.writeObject(msg);
		d.flush(); // Flushing out internal buffers
	}

	public void exchangeUsernames(boolean sendFirst)
			throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException, IOException, ClassNotFoundException {
		if (sendFirst) {
			sendEncryptedMessage(ownUsername);
			strangerUsername = receiveEncryptedMessage();
		} else {
			strangerUsername = receiveEncryptedMessage();
			sendEncryptedMessage(ownUsername);
		}
		gui.setStrangerUsername(strangerUsername);

	}
}
