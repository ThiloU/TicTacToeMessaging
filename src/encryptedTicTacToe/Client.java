package encryptedTicTacToe;

import java.io.*;
import java.net.*;

/**
 * This class contains methods to connect and disconnect a client from a server.
 * 
 */
public class Client {
	Socket soc;
	/**
	 * Connects the client to a given server IP.
	 * 
	 * @param ip The server IP
	 * @param port The server port
	 * @return The socket the connection is taking place on
	 * @throws IOException
	 */
	public Socket connect(String ip, int port) throws IOException {

		// Creating Socket class object and
		// initializing Socket
		soc = new Socket(ip, port);
		return soc;
	}
	
	/**
	 * Disconnects the client from the server.
	 * 
	 * @throws IOException
	 */
	public void disconnect() throws IOException {
		// Closing socket
		soc.close();
	}
}
