package encryptedTicTacToe;

import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

public class Crypt {

	SecretKey aesKey;
	PrivateKey privateKey;

	public Crypt() throws NoSuchAlgorithmException, InvalidKeyException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException {

	}

	public void sendSecureConnectionKey(Socket connection, Chat chat)
			throws IOException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException, ClassNotFoundException {
		PublicKey publicKey = chat.receivePublicKey(connection); // receive public rsa key from other client

		KeyGenerator generator = KeyGenerator.getInstance("AES"); // generate aes key to later encrypt all messages with
		generator.init(128); // The AES key size in number of bits
		aesKey = generator.generateKey();
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.PUBLIC_KEY, publicKey);
		byte[] aesKeyEncrypted = cipher.doFinal(aesKey.getEncoded()); // encrypt the aes key with the public rsa-key
																		// from other client
		chat.sendBytes(connection, aesKeyEncrypted); // send encrypted aes-key back to other client
		System.out.println("AES-Key generated is: " + aesKey);
	}

	public void receiveSecureConnectionKey(Socket connection, Chat chat)
			throws NoSuchAlgorithmException, IOException, IllegalBlockSizeException, BadPaddingException,
			InvalidKeyException, NoSuchPaddingException, ClassNotFoundException {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA"); // assymetric encryption keys, used to
																					// send the AES-key
		keyPairGenerator.initialize(2048);
		KeyPair keyPair = keyPairGenerator.genKeyPair();
		privateKey = keyPair.getPrivate();
		PublicKey publicKey = keyPair.getPublic();
		chat.sendPublicKey(connection, publicKey); // send rsa key in string format to other
													// client for him to encrypt the AES-key
		byte[] aesKeyEncrypted = chat.receiveBytes(connection); // receive encrypted aes-key
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.PRIVATE_KEY, privateKey);
		byte[] decryptedAesKeyBytes = cipher.doFinal(aesKeyEncrypted);// decrypt the aes-key sent from the other client
		aesKey = new SecretKeySpec(decryptedAesKeyBytes, "AES"); // recover the aes-key from bytes

	}

	public String encryptMessage(String msg) throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		Cipher encryptCipher = Cipher.getInstance("AES"); // initialise encryptor
		encryptCipher.init(Cipher.ENCRYPT_MODE, aesKey);
		byte[] encryptedMessageBytes = encryptCipher.doFinal(msg.getBytes(StandardCharsets.UTF_8)); // encrypt message
																									// after converting
																									// to bytes
		return Base64.getEncoder().encodeToString(encryptedMessageBytes); // convert message back to string and return
																			// it
	}

	public String decryptMessage(String msg) throws InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
		Cipher decryptCipher = Cipher.getInstance("AES"); // initialise decryptor
		decryptCipher.init(Cipher.DECRYPT_MODE, aesKey);
		byte[] decryptedMessageBytes = decryptCipher.doFinal(Base64.getDecoder().decode(msg)); // decrypt message after
																								// converting to base64
		return new String(decryptedMessageBytes, StandardCharsets.UTF_8); // convert message back to string and return
																			// it
	}
}
