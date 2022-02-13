package encryptedTicTacToe;

import java.io.*;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;


public class EncryptedTicTacToe {
	static Socket connection;

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, InvalidKeyException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, ClassNotFoundException, InterruptedException {
		
		
		Crypt crypt = new Crypt();
		Gui gui = new Gui();
		TicTacToe tic = new TicTacToe(gui);
		gui.setTicTacToe(tic);
		gui.setup();
		while(!gui.setupDone) {	// wait until setup is done by user
			Thread.sleep(100);
		}
		gui.main();
		
		String mode = gui.mode;
		String ip = gui.ip;
		int port = gui.port;
		String username = gui.username;

		if (mode.equals("s")) {
			// start server
			gui.setStatus("Versuche, einen Server zu starten...");
			try {
				Server server = new Server();
				server.start(port);
				gui.setStatus("Server gestartet! Warte auf Verbindung mit Client...");
				connection = server.waitForClientConnection();
				gui.setStatus("Client verbunden! Initiiere verschlüsselte Verbindung...");
				Chat chat = new Chat(connection, crypt, gui, username, tic);
				gui.setChat(chat);
				tic.setChat(chat);
				crypt.receiveSecureConnectionKey(chat);
				gui.setStatus("Verbindung ist verschlüsselt!");
				chat.exchangeUsernames(true);
				chat.startMessageListener();

			} catch (java.net.BindException e) {
				gui.showMessage("FEHLER: Es läuft bereits ein Server");
				System.exit(1);
			}

		} else if (mode.equals("c")) {
			// start client
			gui.setStatus("Versuche, Verbindung mit dem Server herzustellen...");
			try {
				Client client = new Client();
				connection = client.connect(ip, port);
				gui.setStatus("Verbindung hergestellt! Initiiere verschlüsselte Verbindung...");
				Chat chat = new Chat(connection, crypt, gui, username, tic);
				gui.setChat(chat);
				tic.setChat(chat);
				crypt.sendSecureConnectionKey(chat);
				gui.setStatus("Verbindung ist verschlüsselt!");
				chat.exchangeUsernames(false);
				chat.startMessageListener();
			} catch (ConnectException | UnknownHostException e) {
				gui.showMessage("FEHLER: Kein Server gefunden");
				System.exit(1);
			}

		} 
	}
}
