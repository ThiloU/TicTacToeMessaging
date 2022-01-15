package encryptedTicTacToe;

import java.io.*;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class EncryptedTicTacToe {

	static int port = 6666;
	static Socket connection;

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeyException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, ClassNotFoundException {

		Chat chat = new Chat();
		Crypt crypt = new Crypt();
		System.out.print("s = Server\nc = Client\nModus: ");
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in); // create scanner objet
		String modeString = scanner.nextLine(); // read user input

		if (modeString.equals("s")) {
			// start server
			System.out.println("Versuche, einen Server zu starten...");
			try {
				Server server = new Server();
				server.start(port);
				System.out.println("Server gestartet!\nWarte auf Verbindung mit Client...");
				connection = server.waitForClientConnection();
				System.out.println("Client verbunden!");
				crypt.receiveSecureConnectionKey(connection, chat);
				chat.main(connection, crypt);
			} catch (java.net.BindException e) {
				System.out.println("ERROR: Es läuft bereits ein Server");
			}

		} else if (modeString.equals("c")) {
			// start client
			System.out.print("Gib die Addresse des Servers ein (leer lassen für localhost): ");
			String ip = scanner.nextLine();
			// serverIp.close();
			if (ip.equals("")) {
				ip = "localhost";
			}
			System.out.println("Versuche, Verbindung mit dem Server herzustellen...");
			try {
				Client client = new Client();
				connection = client.connect(ip, port);
				System.out.println("Verbindung hergestellt!");
				crypt.sendSecureConnectionKey(connection, chat);
				chat.main(connection, crypt);
			} catch (java.net.ConnectException | java.net.UnknownHostException e) {
				System.out.println("ERROR: Kein Server gefunden");
			}

		} else {
			System.out.println("ERROR: Ungültige Eingabe");
		}
	}
}
