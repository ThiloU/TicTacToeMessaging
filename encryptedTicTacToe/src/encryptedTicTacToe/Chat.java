package encryptedTicTacToe;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Chat {
	public void main(Socket connection) throws IOException {
		System.out.println("Chatverbindung wurde hergestellt");

		new Thread(() -> { // lambda expression to start a thread
			try {
				while (true) {
					DataInputStream dis = new DataInputStream(connection.getInputStream()); // creating inputStream to
																							// read data
					String str = (String) dis.readUTF(); // extract message from response
					System.out.println(">>> " + str);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
		
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);
		while (true) {
			String msg = scanner.nextLine();
			DataOutputStream d = new DataOutputStream(connection.getOutputStream()); // create outputStream to send
																						// messsages on
			d.writeUTF(msg); // Message to be displayed
			d.flush(); // Flushing out internal buffers
		}
	}
}
