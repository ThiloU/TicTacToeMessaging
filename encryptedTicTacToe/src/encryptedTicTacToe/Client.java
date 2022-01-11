package encryptedTicTacToe;

import java.io.*;
import java.net.*;

public class Client {
	public void main() {

		// Try block to check if exception occurs
		try {

			// Creating Socket class object and
			// initializing Socket
			Socket soc = new Socket("localhost", 6666);

			DataOutputStream d = new DataOutputStream(soc.getOutputStream());

			// Message to be displayed
			d.writeUTF("Hello GFG Readers!");

			// Flushing out internal buffers,
			// optimizing for better performance
			d.flush();

			// Closing the connections

			// Closing DataOutputStream
			d.close();
			// Closing socket
			soc.close();
		}

		// Catch block to handle exceptions
		catch (Exception e) {

			// Print the exception on the console
			System.out.println(e);
		}
	}

}