package encryptedTicTacToe;

public class TicTacToe {

	Gui gui;

	String[] ticTacToeField = new String[9]; // create array to store up to 9 moves

	int[][] winConditions = { { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 }, { 0, 3, 6 }, { 1, 4, 7 }, { 2, 5, 8 }, { 0, 4, 8 },
			{ 2, 4, 6 } };

	String ownSymbol;
	String strangerSymbol;
	boolean firstMove = true;
	boolean didFirstMove;
	boolean gameEnded = false;
	boolean selfWantsReset = false;
	boolean strangerWantsReset = false;
	int moveCounter = 0;

	public TicTacToe(Gui gui) {
		this.gui = gui;
		for (int i = 0; i < 9; i++) {
			ticTacToeField[i] = " ";
		}
	}

	public boolean handleCommands(String msg, boolean selfSentCommand) {
		System.out.println(selfSentCommand);
		String[] split = msg.split(" ");
		boolean msgWasCommand = false;

		if (split[0].equals("/set") && split.length > 1) {
			msgWasCommand = true;
			System.out.println("Message was a set-command with argument " + split[1]);
			Integer currentMove = Integer.valueOf(split[1]); // extract number from command argument
			if (firstMove) { // set up stuff if it is the first move
				if (selfSentCommand) {
					didFirstMove = true;
					ownSymbol = "X";
					strangerSymbol = "O";
				} else {
					didFirstMove = false;
					ownSymbol = "O";
					strangerSymbol = "X";
				}
				firstMove = false;
			}

			if (!ticTacToeField[currentMove].equals(" ")) { // if current field is already taken, do nothing
				return msgWasCommand;
			}
			
			if (gameEnded) {		// if game has ended, do nothing
				return msgWasCommand;
			}
			System.out.println("\n");

			if ((moveCounter % 2 == 0 && didFirstMove) || (moveCounter % 2 == 1 && !didFirstMove)) { // check if its my
																										// turn
				if (selfSentCommand) {
					ticTacToeField[currentMove] = ownSymbol;
					moveCounter++;
				}

			} else if (!selfSentCommand) {
				if (!selfSentCommand) {
					ticTacToeField[currentMove] = strangerSymbol;
					moveCounter++;
				}
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

					if (row[0].equals(row[1]) && row[0].equals(row[2]) && !row[0].equals(" ")) { // if all symbols are equal: someone has won
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

				int value = ticTacToeField[currentMove].charAt(0);
				System.out.println(value);
				if (moveCounter == 9) { // game has ended and noone has won
					gameEnded = true;
					System.out.println("Game has ended in a draw");
				}
			
		} else if (split[0].equals("/resetgame")) {
			if(selfSentCommand) {
				selfWantsReset = true;
			} else {
				strangerWantsReset = true;
			}
			
			if(selfWantsReset && strangerWantsReset) {
				resetTicTacToe();
			}
		}

		return msgWasCommand;

	}
	
	public void resetTicTacToe() {		// reset all variables to their default values
		for (int i = 0; i < 9; i++) {
			ticTacToeField[i] = " ";
		}
		
		firstMove = true;
		gameEnded = false;
		selfWantsReset = false;
		strangerWantsReset = false;
		moveCounter = 0;
		
		gui.printTicTacToe(ticTacToeField);
	}
}
