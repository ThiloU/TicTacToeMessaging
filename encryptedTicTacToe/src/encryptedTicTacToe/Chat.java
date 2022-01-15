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
	public void main(Socket connection, Crypt crypt) throws IOException, NoSuchAlgorithmException, InvalidKeyException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		System.out.println("Chatverbindung wurde hergestellt");
		new Crypt();
		new Thread(() -> { // lambda expression to start a thread
			try {
				while (true) {
					DataInputStream dis = new DataInputStream(connection.getInputStream()); // creating inputStream to
																							// read data
					String str = (String) dis.readUTF(); // extract message from response
					System.out.println(">>> " + crypt.decryptMessage(str));

				}
			} catch (IOException | InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException
					| IllegalBlockSizeException | BadPaddingException e) {
				e.printStackTrace();
			}
		}).start();

		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		while (true) {
			String msg = scanner.nextLine();
			DataOutputStream d = new DataOutputStream(connection.getOutputStream()); // create outputStream to send
																						// messsages on
			d.writeUTF(crypt.encryptMessage(msg)); // Message to be displayed
			d.flush(); // Flushing out internal buffers
		}
	}

	public String receiveMessage(Socket connection) throws IOException {
		DataInputStream dis = new DataInputStream(connection.getInputStream()); // creating inputStream to
		// read data
		String str = (String) dis.readUTF(); // extract message from response
//		dis.close();
		return str;
	}

	public void sendMessage(Socket connection, String msg) throws IOException {
		DataOutputStream d = new DataOutputStream(connection.getOutputStream()); // create outputStream to send
		// messsages on
		d.writeUTF(msg); // Message to be displayed
		d.flush(); // Flushing out internal buffers
//		d.close();
	}

	public PublicKey receivePublicKey(Socket connection) throws IOException, ClassNotFoundException {
		ObjectInputStream dis = new ObjectInputStream(connection.getInputStream()); // creating inputStream to
																					// read data
		PublicKey msg = (PublicKey) dis.readObject();
		System.out.printf("receiveMessage: %s\n", msg);
//		dis.close();
		return msg;
	}

	public void sendPublicKey(Socket connection, PublicKey msg) throws IOException {
		ObjectOutputStream d = new ObjectOutputStream(connection.getOutputStream()); // create outputStream to send
																						// messsages on
		System.out.printf("sendMessage: %s\n", msg);
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
