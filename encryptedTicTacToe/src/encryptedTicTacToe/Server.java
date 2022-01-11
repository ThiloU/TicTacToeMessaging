package encryptedTicTacToe;

import java.io.*;
import java.net.*;

public class Server {
	// Main driver method
	public void main() {

		// Try block to check for exceptions
		try {

			// Creating an object of ServerSocket class
			// in the main() method for socket connection
			ServerSocket ss = new ServerSocket(6666);

			// Establishing a connection
			Socket soc = ss.accept();

			// Invoking input stream via getInputStream()
			// method by creating DataInputStream class
			// object
			DataInputStream dis = new DataInputStream(soc.getInputStream());

			String str = (String) dis.readUTF();

			// Display the string on the console
			System.out.println("message: " + str);

			// Lastly close the socket using standard close
			// method to release memory resources
			ss.close();
		}

		// Catch block to handle the exceptions
		catch (Exception e) {

			// Display the exception on the console
			System.out.println(e);
		}
	}

}
