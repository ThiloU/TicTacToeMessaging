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
	public void main(final Socket connection, final Crypt crypt) throws IOException, NoSuchAlgorithmException, InvalidKeyException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		System.out.println("Chatverbindung wurde hergestellt");
		new Crypt();
		
		Thread messageListener = new Thread() {
		    public void run() {
		    	try {
					while (true) {
						String msg = receiveEncryptedMessage(connection, crypt);
						System.out.println(">>>" + msg);

					}
				} catch (IOException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
						| IllegalBlockSizeException | BadPaddingException |  ClassNotFoundException e) {
					e.printStackTrace();
				}
		    }  
		};
		
		messageListener.start();

		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		while (true) {
			String msg = scanner.nextLine();
			sendEncryptedMessage(connection, crypt, msg);
		}
	}

	public String receiveEncryptedMessage(Socket connection, Crypt crypt) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException {
		byte[] msgBytes = receiveBytes(connection);
		String msg = crypt.decryptMessage(msgBytes);
		return msg;
	}

	public void sendEncryptedMessage(Socket connection, Crypt crypt, String msg) throws IOException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		byte[] msgBytes = crypt.encryptMessage(msg);
		sendBytes(connection, msgBytes);
	}

	public PublicKey receivePublicKey(Socket connection) throws IOException, ClassNotFoundException {
		ObjectInputStream dis = new ObjectInputStream(connection.getInputStream()); // creating inputStream to
																					// read data
		PublicKey msg = (PublicKey) dis.readObject();
//		dis.close();
		return msg;
	}

	public void sendPublicKey(Socket connection, PublicKey msg) throws IOException {
		ObjectOutputStream d = new ObjectOutputStream(connection.getOutputStream()); // create outputStream to send
																						// messsages on
		d.writeObject(msg);
		d.flush(); // Flushing out internal buffers
//		d.close();
	}

	public byte[] receiveBytes(Socket connection) throws IOException, ClassNotFoundException {
		ObjectInputStream dis = new ObjectInputStream(connection.getInputStream()); // creating inputStream to
																					// read data
		byte[] msg = (byte[]) dis.readObject();
//		dis.close();
		return msg;
	}

	public void sendBytes(Socket connection, byte[] msg) throws IOException {
		ObjectOutputStream d = new ObjectOutputStream(connection.getOutputStream()); // create outputStream to send
																						// messsages on
		d.writeObject(msg);
		d.flush(); // Flushing out internal buffers
//		d.close();
	}
}
