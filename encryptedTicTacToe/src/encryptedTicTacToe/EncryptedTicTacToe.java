package encryptedTicTacToe;

import java.io.*;
import java.net.*;
import java.util.Scanner;

public class EncryptedTicTacToe {

	static int port = 6666;
	static Socket connection;

	public static void main(String[] args) throws IOException {

		System.out.print("s = Server\nc = Client\nModus: ");
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
			} catch (java.net.ConnectException | java.net.UnknownHostException e) {
				System.out.println("ERROR: Kein Server gefunden");
			}
		} else {
			System.out.println("ERROR: Ungültige Eingabe");
		}

		System.out.print("Nachicht eingeben (leer lassen um auf Nachrichten zu warten): ");
		String msg = scanner.nextLine();
		if (msg.equals("")) {
			DataInputStream dis = new DataInputStream(connection.getInputStream()); // creating inputStream to read data from
			while (true) {
				String str = (String) dis.readUTF(); // extract message from response
				System.out.println("Nachricht: " + str);
			}
		} else {
			DataOutputStream d = new DataOutputStream(connection.getOutputStream()); // create outputStream to send messsages on
			d.writeUTF(msg);		// Message to be displayed
			d.flush();				// Flushing out internal buffers
		}
	}
}
