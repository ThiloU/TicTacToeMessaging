package encryptedTicTacToe;

import java.io.*;
import java.net.*;

/**
 *This class contains methods to start/stop the server and wait for a client to connect.
 *
 */
public class Server {
	ServerSocket ss;
	Socket soc;
	
	/**
	 * Starts the server on a given port.
	 * 
	 * @param port The port to start the server on
	 * @throws IOException
	 */
	public void start(int port) throws IOException {

		// Creating an object of ServerSocket class
		// in the main() method for socket connection
		ss = new ServerSocket(port);

		// Lastly close the socket using standard close
		// method to release memory resources
	}

	/**
	 * Stops the server.
	 * 
	 * @throws IOException
	 */
	public void stop() throws IOException {
		// Close the socket using standard close
		// method to release memory resources
		ss.close();
	}

	/**
	 * Waits for a client to connect to the server.
	 * 
	 * @return The socket containing the connection information
	 * @throws IOException
	 */
	public Socket waitForClientConnection() throws IOException {
		soc = ss.accept();
		return soc;
	}
}
