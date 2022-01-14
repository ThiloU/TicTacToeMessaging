package encryptedTicTacToe;

import java.io.*;
import java.net.*;

public class Server {
	ServerSocket ss;
	Socket soc;
	
	public void start(int port) throws IOException {

		// Creating an object of ServerSocket class
		// in the main() method for socket connection
		ss = new ServerSocket(port);

		// Lastly close the socket using standard close
		// method to release memory resources
	}

	
	public void stop() throws IOException {
		// Close the socket using standard close
		// method to release memory resources
		ss.close();
	}

	
	public Socket waitForClientConnection() throws IOException {
		soc = ss.accept();
		return soc;
	}
}
