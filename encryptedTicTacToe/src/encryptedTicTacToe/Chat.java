package encryptedTicTacToe;

import java.io.*;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class Chat {
	Socket connection;
	Crypt crypt;
	Gui gui;
	
	public Chat(Socket connection, Crypt crypt, Gui gui) {
		this.connection = connection;
		this.crypt = crypt;
		this.gui = gui;
	}
	
	
	public void main() throws IOException, NoSuchAlgorithmException,
			InvalidKeyException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		System.out.println("Chatverbindung wurde hergestellt");
		new Crypt();

		Thread messageListener = new Thread() {
			public void run() {
				try {
					while (true) {
						String msg = receiveEncryptedMessage();
						gui.printMessage(msg, "Nutzer 2");
						System.out.println(">>>" + msg);

					}
				} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| IllegalBlockSizeException | BadPaddingException | ClassNotFoundException | IOException e) {
					gui.setStatus("ERROR", "Verbindung zum Gesprächspartner unterbrochen");
				}
			}
		};

		messageListener.start();

		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		while (true) {
			String msg = scanner.nextLine();
			sendEncryptedMessage(msg);
		}
	}

	public String receiveEncryptedMessage()
			throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException, ClassNotFoundException {
		byte[] msgBytes = receiveBytes();
		String msg = crypt.decryptMessage(msgBytes);
		return msg;
	}

	public void sendEncryptedMessage(String msg)
			throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException {
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
}
