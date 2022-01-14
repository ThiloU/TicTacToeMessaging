package encryptedTicTacToe;

import java.io.*;
import java.net.*;

public class Client {
	Socket soc;
	public Socket connect(String ip, int port) throws IOException {

		// Creating Socket class object and
		// initializing Socket
		soc = new Socket(ip, port);
		return soc;
	}
	
	
	public void disconnect() throws IOException {
		// Closing socket
		soc.close();
	}
}
