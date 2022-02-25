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

/**
*
* This class contains several methods relating to the chat-functionality.
*
*/

public class Chat {
	Socket connection;
	Crypt crypt;
	Gui gui;
	TicTacToe tic;
	String ownUsername;
	String strangerUsername;

	/**
	 * Class Constructor
	 * 
	 * @param connection
	 * @param crypt
	 * @param gui
	 * @param ownUsername
	 * @param tic
	 */
	public Chat(Socket connection, Crypt crypt, Gui gui, String ownUsername, TicTacToe tic) {
		this.connection = connection;
		this.crypt = crypt;
		this.gui = gui;
		this.ownUsername = ownUsername;
		this.tic = tic;
	}

	/**
	 * Starts the main message listener and distributes the incoming messages to the GUI and to the TicTacToe Command Handler.
	 * 
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws NoSuchPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * 
	 * 
	 */
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

		
	/**
	 * Waits for incoming bytes on the server socket, then decrypts the message.
	 * 
	 * @return Decrypted message as String
	 * @throws IOException
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws ClassNotFoundException
	 */
	public String receiveEncryptedMessage() throws IOException, InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException {
		byte[] msgBytes = receiveBytes();
		String msg = crypt.decryptMessage(msgBytes);
		return msg;
	}

	/**
	 * Encrypts a given string, then sends the encrypted message on the server socket as bytes.
	 * 
	 * @param msg String to send
	 * @throws IOException
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public void sendEncryptedMessage(String msg) throws IOException, InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		byte[] msgBytes = crypt.encryptMessage(msg);
		sendBytes(msgBytes);
	}

	/**
	 * Waits for an incoming public key and returns the key.
	 * 
	 * @return The public Key
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public PublicKey receivePublicKey() throws IOException, ClassNotFoundException {
		ObjectInputStream dis = new ObjectInputStream(connection.getInputStream()); // creating inputStream to
																					// read data
		PublicKey msg = (PublicKey) dis.readObject();
		return msg;
	}

	/**
	 * Sends a public key.
	 * 
	 * @param msg Public Key to send
	 * @throws IOException
	 */
	public void sendPublicKey(PublicKey msg) throws IOException {
		ObjectOutputStream d = new ObjectOutputStream(connection.getOutputStream()); // create outputStream to send
																						// messsages on
		d.writeObject(msg);
		d.flush(); // Flushing out internal buffers
	}

	/**
	 * Waits for incoming bytes and returns them. This is the basis for all other "receive" Methods.
	 * 
	 * @return The received byte array
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public byte[] receiveBytes() throws IOException, ClassNotFoundException {
		ObjectInputStream dis = new ObjectInputStream(connection.getInputStream()); // creating inputStream to
																					// read data
		byte[] msg = (byte[]) dis.readObject();
		return msg;
	}

	/**
	 * Sends bytes. This is the basis for all other "send" Methods.
	 * 
	 * @param msg Byte array to send
	 * @throws IOException
	 */
	public void sendBytes(byte[] msg) throws IOException {
		ObjectOutputStream d = new ObjectOutputStream(connection.getOutputStream()); // create outputStream to send
																						// messsages on
		d.writeObject(msg);
		d.flush(); // Flushing out internal buffers
	}

	/**
	 * Receives the username of the other user and sends own username to the other user.
	 * 
	 * @param sendFirst Send your username first
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
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
