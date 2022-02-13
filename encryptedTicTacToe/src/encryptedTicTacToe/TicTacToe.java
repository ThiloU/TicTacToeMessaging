package encryptedTicTacToe;

import java.util.concurrent.ThreadLocalRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class TicTacToe {

	Gui gui;
	Chat chat;

	String[] ticTacToeField = new String[9]; // create array to store up to 9 moves

	int[][] winConditions = { { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 }, { 0, 3, 6 }, { 1, 4, 7 }, { 2, 5, 8 }, { 0, 4, 8 },
			{ 2, 4, 6 } };

	String ownSymbol;
	String strangerSymbol;
	boolean didFirstMove;
	boolean currentlyMyMove;
	boolean gameEnded = false;
	boolean selfWantsReset = false;
	boolean strangerWantsReset = false;
	boolean firstPlayerAlreadyDetermined = false;
	int moveCounter = 0;

	public TicTacToe(Gui gui) {
		this.gui = gui;
		for (int i = 0; i < 9; i++) {
			ticTacToeField[i] = " ";
		}
	}

	public void setChat(Chat chat) {
		this.chat = chat;
	}

	public void startGame() throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException,
			IllegalBlockSizeException, BadPaddingException, IOException {
		if (gui.mode.equals("s") && !firstPlayerAlreadyDetermined) { // if this user is the server (if he is privileged)
			if (ThreadLocalRandom.current().nextInt(0, 2) == 0) { // pick random integer between 0 and 1 to
																	// determine starting player
				didFirstMove = true; // self is starting
				currentlyMyMove = true;
				ownSymbol = "X";
				strangerSymbol = "O";
				gui.showNonBlockingMessage("Du darfst nun den ersten Zug machen");

				chat.sendEncryptedMessage("/startinggame sender");
			} else { // stranger is starting
				didFirstMove = false;
				currentlyMyMove = false;
				ownSymbol = "O";
				strangerSymbol = "X";
				gui.showNonBlockingMessage("Der Gegner macht nun den ersten Zug");
				chat.sendEncryptedMessage("/startinggame receiver");
			}

			firstPlayerAlreadyDetermined = true;
			gui.setGameButtonText("reset");
		} else if (!gui.mode.equals("s") && !firstPlayerAlreadyDetermined) {
			gui.showNonBlockingMessage("Nur der Server-Nutzer darf ein TicTacToe Spiel starten");
		} else {
			gui.showNonBlockingMessage("Es läuft bereits ein Spiel");
		}
	}

	public boolean handleCommands(String msg, boolean selfSentCommand)
			throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
			BadPaddingException, IOException {
		String[] split = msg.split(" ");
		boolean msgWasCommand = false;
		if (!split[0].startsWith("/")) { // if message is not a command, do nothing
			return msgWasCommand;
		}

		System.out.println("Received command: " + split[0]);
		if (split.length > 1) {
			System.out.println("Command argument: " + split[1]);
		}
		System.out.println("Command came from self? " + selfSentCommand);

		if (split[0].equals("/set") && split.length > 1) {
			msgWasCommand = true;
			Integer currentMove = Integer.valueOf(split[1]); // extract number from command argument

			if (currentMove > 8 || currentMove < 0 || !firstPlayerAlreadyDetermined
					|| !ticTacToeField[currentMove].equals(" ") || gameEnded) {
				return msgWasCommand; // if something isn't right, do nothing
			}

			System.out.println("\n");

			if (currentlyMyMove && selfSentCommand) { // check if its my turn
				chat.sendEncryptedMessage(msg);
				ticTacToeField[currentMove] = ownSymbol;
				moveCounter++;
				currentlyMyMove = !currentlyMyMove;

			} else if(!currentlyMyMove && !selfSentCommand){
				ticTacToeField[currentMove] = strangerSymbol;
				moveCounter++;
				currentlyMyMove = !currentlyMyMove;
			}


			gui.printTicTacToe(ticTacToeField);

			for (int winCondition = 0; winCondition < 8; winCondition++) {
				String[] row = new String[3];
				for (int winConditionPart = 0; winConditionPart < 3; winConditionPart++) {
					int coordinate = winConditions[winCondition][winConditionPart]; // get current coordinate for
																					// possible win-condition
					String symbol = ticTacToeField[coordinate]; // get the symbol at that place
					row[winConditionPart] = symbol; // store all three symbols together
				}

				if (row[0].equals(row[1]) && row[0].equals(row[2]) && !row[0].equals(" ")) { // if all symbols are
																								// equal: someone has
																								// won
					gameEnded = true;
					if (row[0].equals(ownSymbol)) {
						gui.showNonBlockingMessage(gui.username + " hat gewonnen!");
						return msgWasCommand;
					} else if (row[0].equals(strangerSymbol)) {
						gui.showNonBlockingMessage(gui.strangerUsername + " hat gewonnen!");
						return msgWasCommand;
					}
				}
			}

			if (moveCounter == 9) { // game has ended and noone has won
				gameEnded = true;
				System.out.println("Game has ended in a draw");
			}

		} else if (split[0].equals("/resetgame"))

		{
			if (selfSentCommand) {
				selfWantsReset = true;
			} else {
				strangerWantsReset = true;
				System.out.println("Stranger wants reset");
				gui.setGameButtonText("Auch für Reset abstimmen");
			}

			if (selfWantsReset && strangerWantsReset) {
				resetTicTacToe();
			}

		} else if (split[0].equals("/startinggame") && split.length > 1 && !selfSentCommand) {
			if (split[1].equals("receiver")) {
				gui.showNonBlockingMessage("Ein Spiel wurde gestartet, du darfst anfangen");
				didFirstMove = true;
				currentlyMyMove = true;
				ownSymbol = "X";
				strangerSymbol = "O";
			} else if (split[1].equals("sender")) {
				gui.showNonBlockingMessage("Ein Spiel wurde gestartet, der Gegner fängt an");
				didFirstMove = false;
				currentlyMyMove = false;
				ownSymbol = "O";
				strangerSymbol = "X";
			}
			gui.setGameButtonText("reset");

			firstPlayerAlreadyDetermined = true;
		}

		return msgWasCommand;

	}

	public void resetTicTacToe() { // reset all variables to their default values
		for (int i = 0; i < 9; i++) {
			ticTacToeField[i] = " ";
		}

		gameEnded = false;
		selfWantsReset = false;
		strangerWantsReset = false;
		firstPlayerAlreadyDetermined = false;
		moveCounter = 0;
		gui.setGameButtonText("start");

		gui.printTicTacToe(ticTacToeField);
	}
}
