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
		
		Crypt crypt = new Crypt();
		Gui gui = new Gui();
		gui.mainWindowStart();
		System.out.print("s = Server\nc = Client\nModus: ");
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in); // create scanner object
		String modeString = scanner.nextLine(); // read user input

		if (modeString.equals("s")) {
			// start server
			gui.setStatus("Versuche, einen Server zu starten...");
			try {
				Server server = new Server();
				server.start(port);
				gui.setStatus("Server gestartet! Warte auf Verbindung mit Client...");
				connection = server.waitForClientConnection();
				gui.setStatus("Client verbunden! Initiiere verschlüsselte Verbindung...");
				Chat chat = new Chat(connection, crypt, gui);
				gui.setChat(chat);
				crypt.receiveSecureConnectionKey(chat);
				gui.setStatus("Verbindung ist verschlüsselt!");
				chat.main();
			} catch (java.net.BindException e) {
				gui.setStatus("ERROR", "Es läuft bereits ein Server");
				System.exit(1);
			}

		} else if (modeString.equals("c")) {
			// start client
			System.out.print("Gib die Addresse des Servers ein (leer lassen für localhost): ");
			String ip = scanner.nextLine();
			if (ip.equals("")) {
				ip = "localhost";
			}
			gui.setStatus("Versuche, Verbindung mit dem Server herzustellen...");
			try {
				Client client = new Client();
				connection = client.connect(ip, port);
				gui.setStatus("Verbindung hergestellt! Initiiere verschlüsselte Verbindung...");
				Chat chat = new Chat(connection, crypt, gui);
				gui.setChat(chat);
				crypt.sendSecureConnectionKey(chat);
				gui.setStatus("Verbindung ist verschlüsselt!");
				chat.main();
			} catch (ConnectException | UnknownHostException e) {
				gui.setStatus("ERROR", "Kein Server gefunden");
				System.exit(1);
			}

		} else {
			gui.setStatus("ERROR", "Ungültige Eingabe");
			System.exit(2);
		}
	}
}
