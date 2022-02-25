package encryptedTicTacToe;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

/**
 * This class contains methods used for message encryption and decryption
 * 
 */
public class Crypt {

	private SecretKey aesKey;
	private PrivateKey privateKey;

	public Crypt() {

	}
	
	/**
	 * Generates a symmetric AES encryption key and transmits it to the other client.
	 * For this, the method waits for the other user to send his public RSA key, then encrypts the AES key using RSA to send the AES key securely.
	 * 
	 * @param chat The chat component the communication is taking place on
	 * @throws IOException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws ClassNotFoundException
	 */
	public void sendSecureConnectionKey(Chat chat)
			throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException {
		PublicKey publicKey = chat.receivePublicKey(); // receive public rsa key from other client

		KeyGenerator generator = KeyGenerator.getInstance("AES"); // generate aes key to later encrypt all messages with
		generator.init(128); // The AES key size in number of bits
		aesKey = generator.generateKey();
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.PUBLIC_KEY, publicKey);
		byte[] aesKeyEncrypted = cipher.doFinal(aesKey.getEncoded()); // encrypt the aes key with the public rsa-key
																		// from other client
		chat.sendBytes(aesKeyEncrypted); // send encrypted aes-key back to other client
	}

	/**
	 * Receives the AES key from the other client.
	 * For this, a public RSA key is generated and sent to the other user so that he can encrypt and securely send the AES key.
	 * 
	 * @param chat The chat component the communication is taking place on
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws InvalidKeyException
	 * @throws NoSuchPaddingException
	 * @throws ClassNotFoundException
	 */
	public void receiveSecureConnectionKey(Chat chat)
			throws NoSuchAlgorithmException, IOException, IllegalBlockSizeException, BadPaddingException,
			InvalidKeyException, NoSuchPaddingException, ClassNotFoundException {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA"); // assymetric encryption keys, used to
																					// send the AES-key
		keyPairGenerator.initialize(2048);
		KeyPair keyPair = keyPairGenerator.genKeyPair();
		privateKey = keyPair.getPrivate();
		PublicKey publicKey = keyPair.getPublic();
		chat.sendPublicKey(publicKey); // send rsa key in string format to other
													// client for him to encrypt the AES-key
		byte[] aesKeyEncrypted = chat.receiveBytes(); // receive encrypted aes-key
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.PRIVATE_KEY, privateKey);
		byte[] decryptedAesKeyBytes = cipher.doFinal(aesKeyEncrypted);// decrypt the aes-key sent from the other client
		aesKey = new SecretKeySpec(decryptedAesKeyBytes, "AES"); // recover the aes-key from bytes

	}

	/**
	 * Encrypts a given message and converts it from String to Byte array using the previously exchanged cryptographic keys.
	 * 
	 * @param msg The String to encrypt
	 * @return The encrypted message as byte array
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public byte[] encryptMessage(String msg) throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		Cipher encryptCipher = Cipher.getInstance("AES"); // initialise encryptor
		encryptCipher.init(Cipher.ENCRYPT_MODE, aesKey);
		byte[] encryptedMessageBytes = encryptCipher.doFinal(msg.getBytes(StandardCharsets.UTF_8)); // encrypt message
																									// after converting
																									// to bytes
		return encryptedMessageBytes; // return encrypted message in bytes ready to be sent
	}

	/**
	 * Decrypts a given message and converts it from Byte array to String.
	 * 
	 * @param msg The message as Byte array
	 * @return The decrypted message as String
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public String decryptMessage(byte[] msg) throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		Cipher decryptCipher = Cipher.getInstance("AES"); // initialise decryptor
		decryptCipher.init(Cipher.DECRYPT_MODE, aesKey);
		byte[] decryptedMessageBytes = decryptCipher.doFinal(msg); // decrypt message after
																								// converting to base64
		return new String(decryptedMessageBytes, StandardCharsets.UTF_8); // convert message back to string and return
																			// it
	}
}
