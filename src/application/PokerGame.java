/**
 * Names:  Andrew Hoffman, Chase Revia, Robert Elmore, Ralph E. Beard IV
 * Course #:  1174
 * Date:  
 * Assignment Name: Group Project Poker
 */

package application;
	
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.transform.Rotate;


public class PokerGame extends Application {
	
	private ArrayList<Integer> deckOrder = new ArrayList<>(); // this holds the order of the cards to deal
	private ArrayList<PlayerCard> playerCards = new ArrayList<>(); // this holds the 5 cards the player has
	private int currentPositionInDeck = 0; // this will keep track of which card we need to deal next
	private int numberOfDecks = 1; // integer to keep track of the number of decks being used
	private int numberOfCardsInDeck = 52; // this will be the number of cards in the deck which changes with wildcards
	private int playerWalletAmount = 200; // how much the player has to bet
	private Button drawButton = new Button("Draw"); // This button will be used to draw new cards
	private Button forfeitButton = new Button("Forfeit"); // This button allows the player to forfeit the hand and the wager
	private Button dealButton = new Button("Deal"); // button to set the bet and deal the cards
	private HBox cardsBox = new HBox(); // This layout holds the images of the cards for display horizontally
	private HBox wagerBox = new HBox(); // this box is used to manage wagers placed
	private Label playerWalletLabel = new Label(); // this will show the current amount left to bet
	private Label alertLabel = new Label(" "); // tells the player what they won with
	private GameMechanics gameMechanics = new GameMechanics(); // this variable will handle the overall mechanics of the gameStatus

	/**
	 * Main method to start the program and just ensure that the start method is
	 * called
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);

	}

	/**
	 * The overrriden method that is the start of processing visual elements.
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {

		gameMechanics.populateDeck();
		
		drawButton.setOnMouseClicked(e -> {
			gameMechanics.drawCards();
			drawButton.setDisable(true);
			forfeitButton.setDisable(true);
			dealButton.setDisable(false);
		});
		drawButton.setDisable(true);
		
		forfeitButton.setOnMouseClicked(e -> {
			flipCards();
			alertLabel.setText("Player Forfeits");
			drawButton.setDisable(true);
			forfeitButton.setDisable(true);
			dealButton.setDisable(false);
		});
		forfeitButton.setDisable(true);
		
		wagerBox = createBettingBox();
		
		dealButton.setOnMouseClicked(e -> {
			gameMechanics.manageBet();
			gameMechanics.newHand();
			cardsBox.setVisible(true);
			drawButton.setDisable(false);
			forfeitButton.setDisable(false);
			dealButton.setDisable(true);
			alertLabel.setText(" ");
		});
				
		HBox placeBetsBox = new HBox(); // this box handles the betting
		placeBetsBox.setSpacing(10);
		placeBetsBox.setAlignment(Pos.BASELINE_CENTER);
		placeBetsBox.getChildren().addAll(wagerBox, dealButton);
		placeBetsBox.setVisible(true);

		playerWalletLabel.setText("Wallet $" + playerWalletAmount);
		playerWalletLabel.setPadding(new Insets(0, 15, 0, 10));
		playerWalletLabel.setFont(Font.font(12));

		HBox menuBox = new HBox(); // this is the HBox to hold the menu and button items
		menuBox.setSpacing(5);
		menuBox.getChildren().addAll(drawButton, forfeitButton, playerWalletLabel);

		alertLabel.setFont(Font.font(12));
		alertLabel.setPadding(new Insets(0, 0, 0, 10));

		HBox alertBox = new HBox(); // this HBox is just used to show what the person won with
		alertBox.setSpacing(5);
		alertBox.getChildren().add(alertLabel);		

		cardsBox.setSpacing(5);
		for (int i = 0; i < 5; i++) {
			playerCards.add(new PlayerCard(0));
			cardsBox.getChildren().add(playerCards.get(i));
		}
			
		VBox menuAlertBox = new VBox(); // This VBox holds the menu and alert HBoxs
		menuAlertBox.setSpacing(5);
		menuAlertBox.getChildren().addAll(menuBox, placeBetsBox, alertBox);

		VBox layoutBox = new VBox(); // This layout holds the button and 5 card ImageViews vertically
		layoutBox.setPadding(new Insets(0, 5, 0, 5));
		layoutBox.setSpacing(5);
		layoutBox.getChildren().addAll(menuAlertBox, cardsBox);

		Scene scene = new Scene(layoutBox); // This is the scene to place onto the stage for display

		primaryStage.setTitle("Poker Hand");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();

	}

	/**
	 * This method is used to set up betting for the game. It returns and HBox which
	 * can be placed with the rest of the menus
	 * 
	 * @return
	 */
	private HBox createBettingBox() {
		RadioButton oneDollar, tenDollars, hundredDollars; // radio buttons for the amount of the bet for the hand
		oneDollar = new RadioButton("$1");
		tenDollars = new RadioButton("$10");
		hundredDollars = new RadioButton("$100");

		ToggleGroup bettingGroup = new ToggleGroup(); // toggle group the radio buttons belong to
		oneDollar.setToggleGroup(bettingGroup);
		tenDollars.setToggleGroup(bettingGroup);
		hundredDollars.setToggleGroup(bettingGroup);
		oneDollar.setSelected(true);

		oneDollar.setFont(Font.font(12));
		tenDollars.setFont(Font.font(12));
		hundredDollars.setFont(Font.font(12));

		Label wagerLabel = new Label("Wager: "); // label to describe the toggle group
		wagerLabel.setFont(Font.font(12));

		HBox betToggleBox = new HBox(); // HBox to hold the label and radio buttons
		betToggleBox.setSpacing(5);
		betToggleBox.getChildren().addAll(wagerLabel, oneDollar, tenDollars, hundredDollars);

		return betToggleBox;
	}
	
	/**
	 * This method just flips the cards to their backs without animations
	 */
	private void flipCards( ) {
		for (int i = 0; i < 5; i++) {
			playerCards.get(i).flipCard();
		}
	}

	/*************************************************************************/

	/**
	 * Inner class to create properties for the displayed card
	 *
	 */
	class PlayerCard extends VBox {

		private int cardNumber;
		private Image cardImage, cardBackImage;
		private ImageView cardImageView = new ImageView();
		private ImageView cardBackImageView = new ImageView();
		private ToggleButton holdButton = new ToggleButton("Hold");
		private StackPane cardPane;
		private boolean primaryCardBack = true;
		
		/**
		 * The is the constructor for creating the card.
		 * 
		 * @param cardNumber
		 */
		public PlayerCard(int cardNumber) {
			this.cardNumber = cardNumber;
			createCard();
		}

		/**
		 * This method does the heavy lifting for creating and setting up the VBox
		 */
		private void createCard() {
			this.setAlignment(Pos.CENTER);
			this.setSpacing(5);
			this.setPadding(new Insets(0, 5, 0, 5));
			if (cardNumber != 0) {
				setCardNumber(cardNumber);
			} else {
				cardImageView.setVisible(false);
			}
			setBackImage();
			flipCard();
			cardPane = new StackPane();
			cardPane.getChildren().addAll(cardBackImageView, cardImageView);
			this.getChildren().addAll(cardPane, holdButton);
		}

		/**
		 * This method creates and sets the back Images and ImageViews to be used by the
		 * program to help keep on redundantly creating an image
		 */
		private void setBackImage() {
			String fileName = "", fullFileName = "";
			if (primaryCardBack) {
				fileName = "b1fv.png";
				fullFileName = "file:" + System.getProperty("user.dir") + File.separator + "resources" + File.separator
						+ "cards" + File.separator + fileName;// This creates the full file name with path for the
																// reverse image for the card.

			} else {
				fileName = "b2fv.png";
				fullFileName = "file:" + System.getProperty("user.dir") + File.separator + "resources" + File.separator
						+ "cards" + File.separator + fileName;// This creates the full file name with path for the
																// reverse image for the card.
			}
			cardBackImage = new Image(fullFileName, 100, 133, true, true);
			cardBackImageView.setImage(cardBackImage);
		}

		/**
		 * @return the cardNumber
		 */
		public int getCardNumber() {
			return cardNumber;
		}

		/**
		 * @param cardNumber the cardNumber to set
		 */
		public void setCardNumber(int cardNumber) {
			this.cardNumber = cardNumber;
			int imageNumber = cardNumber % 52;
			if (imageNumber == 0) {
				imageNumber = (cardNumber / 52) * 13;
			}
			String fileName = imageNumber + ".png";
			String fullFileName = "file:" + System.getProperty("user.dir") + File.separator + "resources"
					+ File.separator + "cards" + File.separator + fileName;// This creates the full file name with path
																			// for the image in a format readable by any
																			// system
			cardImage = new Image(fullFileName, 100, 133, true, true);
			cardImageView.setImage(cardImage);
			cardImageView.setVisible(true);
			cardBackImageView.setVisible(false);
		}
		
		/**
		 * flips the card over to just show the back of the card
		 */
		public void flipCard( ) {
			cardImageView.setVisible(false);
			cardBackImageView.setVisible(true);
		}

		/**
		 * This method swaps the card for the new drawn card with animations
		 * @param newCardNumber
		 */
		public void swapCard(int newCardNumber) {
			RotateTransition rotator = new RotateTransition(Duration.millis(250), cardPane);
			rotator.setAxis(Rotate.Y_AXIS);
			rotator.setFromAngle(0);
			rotator.setToAngle(90);
			rotator.setInterpolator(Interpolator.LINEAR);
			rotator.setOnFinished(e -> {
				cardImageView.setVisible(false);
				cardBackImageView.setVisible(true);
			});
			rotator.play();
			rotator = new RotateTransition(Duration.millis(500), cardPane);
			rotator.setDelay(Duration.millis(250));
			rotator.setAxis(Rotate.Y_AXIS);
			rotator.setFromAngle(90);
			rotator.setToAngle(180);
			rotator.setInterpolator(Interpolator.LINEAR);
			rotator.play();

			TranslateTransition discardTransition = new TranslateTransition(Duration.millis(750), cardPane);
			discardTransition.setDelay(Duration.millis(500));
			discardTransition.setToY(200 + cardPane.getLayoutY());
			discardTransition.play();

			TranslateTransition drawTransition = new TranslateTransition(Duration.millis(1750), cardPane);
			drawTransition.setDelay(Duration.millis(1250));
			drawTransition.setFromY(-200);
			drawTransition.setToY(cardPane.getLayoutY());
			drawTransition.play();

			rotator = new RotateTransition(Duration.millis(1000), cardPane);
			rotator.setDelay(Duration.millis(750));
			rotator.setAxis(Rotate.Y_AXIS);
			rotator.fromAngleProperty();
			rotator.setFromAngle(180);
			rotator.setToAngle(270);
			rotator.setInterpolator(Interpolator.LINEAR);
			rotator.setOnFinished(e1 -> {
				cardNumber = newCardNumber;
				String fileName = newCardNumber + ".png";
				String fullFileName = "file:" + System.getProperty("user.dir") + File.separator + "resources"
						+ File.separator + "cards" + File.separator + fileName;// This creates the full file name with
																				// path for the image in a format
																				// readable by any system
				Image cardImage = new Image(fullFileName, 100, 133, true, true);
				cardImageView.setImage(cardImage);
				cardImageView.setVisible(true);
				cardBackImageView.setVisible(false);
			});
			rotator.play();

			rotator = new RotateTransition(Duration.millis(1750), cardPane);
			rotator.setDelay(Duration.millis(1250));
			rotator.setAxis(Rotate.Y_AXIS);
			rotator.setFromAngle(270);
			rotator.setToAngle(360);
			rotator.setInterpolator(Interpolator.LINEAR);
			rotator.play();

		}

		/**
		 * @return the holdButton
		 */
		public ToggleButton getHoldButton() {
			return holdButton;
		}

	}

	/*************************************************************************/

	/**
	 * This inner class is designed to handle the overall game mechanics.  These are things
	 * such as win/lose, shuffling the deck, etc.  It's not really intended to deal with
	 * visuals, just the process.
	 *
	 */
	class GameMechanics {

		/**
		 * This method takes a generic ArrayList and 
		 * randomly shuffles the items in it.
		 * @param <E>
		 * @param list
		 */
		private <E> void shuffle(ArrayList<E> list) {
			
			Random random = new Random(); // random variable to enable scrambling indexes
			for (int i = list.size(); i > 1; i--) {
				int swappedIndex = random.nextInt(i); // holder for the random index to be swapped with
				if (swappedIndex != i - 1) {
					E tempObject = list.get(i - 1); // temp object to hold the contents in the list to be swapped
					list.set(i - 1, list.get(swappedIndex));
					list.set(swappedIndex, tempObject);
				}
			}
		}
		
		/**
		 * This method manages the bet and sets the current amount the user has to bet.
		 */
		private void manageBet() {
			int wager = getWager();// used to store the user's wager
			playerWalletAmount = playerWalletAmount - wager;
			playerWalletLabel.setText("Wallet $" + playerWalletAmount);
			
		}

		/**
		 * This method manages the bet after the draw and winning has been determined
		 */
		private void manageBet(int[] cardsAfterDraw) {
			int wager = getWager();// used to store the user's wager
			String winLossString = isWon(cardsAfterDraw); // gets the type of win from isWon for display
			if (!winLossString.equals("")) {
				wager = wager * 2;
				playerWalletAmount = playerWalletAmount + wager;
				playerWalletLabel.setText("Wallet $" + playerWalletAmount);
				alertLabel.setText("You won with " + winLossString);
			} else {
				alertLabel.setText("You lost");
			}

		}
		
		/**
		 * method to extract the user's wager from the radio buttons
		 * @return
		 */
		private int getWager() {
			int wager = 0; // used to keep track of the amount of the wager
			if (((RadioButton) wagerBox.getChildren().get(1)).isSelected()) {
				wager = 1;
			} else if (((RadioButton) wagerBox.getChildren().get(2)).isSelected()) {
				wager = 10;
			} else if (((RadioButton) wagerBox.getChildren().get(3)).isSelected()) {
				wager = 100;
			}
			
			return wager;
		}

		/**
		 * method to check to see if the hand is a winning hand
		 * 
		 * @return
		 */
		private String isWon(int[] cardNumberOrder) {

			cardNumberOrderSort(cardNumberOrder);
			if (isFlush(cardNumberOrder)) {
				if (isStraight(cardNumberOrder)) {
					int tempFirstNumber = convertToActualNumber(cardNumberOrder[0]); // value of first card to see if
																						// it's an ace
					int tempFinalNumber = convertToActualNumber(cardNumberOrder[4]); // value of last card to see if
																						// it's a king
					if (tempFirstNumber == 1 && tempFinalNumber == 13) {
						return "Royal Flush";
					}
					return "Straight Flush";
				}
				return "Flush";
			} else if (isStraight(cardNumberOrder)) {
				return "Straight";
			} else {
				return checkForMatching(cardNumberOrder);
			}

		}

		/**
		 * This checks if all the cards are the same suit
		 * 
		 * @param cardNumberOrder
		 * @return
		 */
		private boolean isFlush(int[] cardNumberOrder) {

			int suitSet = cardNumberOrder[0] / 13; // this gets the numeric value of the suit of the first card to set
													// upper and lower bounds
			int lowerBound = (13 * suitSet) + 1; // is the numberic value of the ace of the suit the first card is
			int upperBound = lowerBound + 12; // numeric value of the king of suit the first card is in

			for (int i = 1; i < 5; i++) {
				if (cardNumberOrder[i] < lowerBound || cardNumberOrder[i] > upperBound)
					return false;
			}
			return true;
		}

		/**
		 * This checks to see if the cards are in order
		 * 
		 * @param cardNumberOrder
		 * @return
		 */
		private boolean isStraight(int[] cardNumberOrder) {

			for (int i = 1; i < 4; i++) {
				int tempLowerCard = convertToActualNumber(cardNumberOrder[i]); // value of the first card to compare
				int tempUpperCard = convertToActualNumber(cardNumberOrder[i + 1]); // value of next card to compare
				if (tempLowerCard != (tempUpperCard - 1)) {
					return false;
				}
			}
			int tempFirstNumber = convertToActualNumber(cardNumberOrder[0]); // value of first card in the hand to
																				// compare to the second card or last
																				// depending on if it's an ace or not
			int tempSecondNumber = convertToActualNumber(cardNumberOrder[1]); // value of second card to compare with
																				// the first
			int tempFinalNumber = convertToActualNumber(cardNumberOrder[4]); // value of last card to compare with the
																				// first if the first is an ace
			if ((tempFirstNumber != 1) && (tempFirstNumber == (tempSecondNumber - 1))) {
				return true;
			} else if ((tempFirstNumber == 1) && (tempSecondNumber == 2) || (tempFinalNumber == 13)) {
				return true;
			}

			return false;
		}

		private String checkForMatching(int[] cardNumberOrder) {
			for (int i = 0; i < 5; i++) {
				cardNumberOrder[i] = convertToActualNumber(cardNumberOrder[i]);
			}

			// check for 4 of a kind
			if ((cardNumberOrder[0] == cardNumberOrder[1] && cardNumberOrder[0] == cardNumberOrder[2]
					&& cardNumberOrder[0] == cardNumberOrder[3])
					|| (cardNumberOrder[1] == cardNumberOrder[2] && cardNumberOrder[1] == cardNumberOrder[3]
							&& cardNumberOrder[1] == cardNumberOrder[4])) {
				return "Four of a Kind";
			}
			// check for 3 of a kind
			if (cardNumberOrder[0] == cardNumberOrder[1] && cardNumberOrder[0] == cardNumberOrder[2]) {
				// check for full house
				if (cardNumberOrder[3] == cardNumberOrder[4]) {
					return "Full House";
				}
				return "Three of a Kind";
			} else if (cardNumberOrder[2] == cardNumberOrder[3] && cardNumberOrder[2] == cardNumberOrder[4]) {
				// check for full house
				if (cardNumberOrder[0] == cardNumberOrder[1]) {
					return "Full House";
				}
				return "Three of a Kind";
			} else if (cardNumberOrder[1] == cardNumberOrder[2] && cardNumberOrder[1] == cardNumberOrder[3]) {
				return "Three of a Kind";
			}
			// check for a pair
			if (cardNumberOrder[0] == cardNumberOrder[1]) {
				if (cardNumberOrder[2] == cardNumberOrder[3] || cardNumberOrder[3] == cardNumberOrder[4]) {
					return "Two Pair";
				}
				return "A Pair";
			} else if (cardNumberOrder[1] == cardNumberOrder[2]) {
				if (cardNumberOrder[3] == cardNumberOrder[4]) {
					return "Two Pair";
				}
				return "A Pair";
			} else if (cardNumberOrder[2] == cardNumberOrder[3] || cardNumberOrder[3] == cardNumberOrder[4]) {
				return "A Pair";
			}

			return "";
		}

		/**
		 * converts the card number from a number in the deck to a number in a suit (1 -
		 * 13)
		 * 
		 * @param cardNumber
		 * @return
		 */
		private int convertToActualNumber(int cardNumber) {
			int actualCardValue = cardNumber % 13; // numeric value of the card in the suit (1 - 13)
			if (actualCardValue == 0) {
				actualCardValue = 13;
			}

			return actualCardValue;
		}

		/**
		 * this resets the deck(s) and the array that holds the ordering
		 */
		private void populateDeck() {
			deckOrder = new ArrayList<>();
			for (int i = 0; i < numberOfCardsInDeck * numberOfDecks; i++) {
				deckOrder.add(i + 1);
			}

		}

		/**
		 * This method will handle what happens when the draw button is pressed
		 */
		private void drawCards() {
			int[] cardsAfterDraw = new int[5];
			for (int i = 0; i < 5; i++) {
				if (!playerCards.get(i).getHoldButton().isSelected()) {
					cardsAfterDraw[i] = deckOrder.get(currentPositionInDeck);
					playerCards.get(i).swapCard(cardsAfterDraw[i]);
					currentPositionInDeck++;
				} else {
					cardsAfterDraw[i] = playerCards.get(i).getCardNumber();
					playerCards.get(i).getHoldButton().setSelected(false);
				}
			}
			manageBet(cardsAfterDraw);
		}

		/**
		 * This method suffles the deck of cards array producing a random order for the
		 * numbers. It then creates the full file name for the 5 cards based on the
		 * numbers in the first 5 places in the array. Finally it creates the Image from
		 * the file and sets the ImageView
		 */
		private void newHand() {
			if ((deckOrder.size() - currentPositionInDeck) >= 11 && currentPositionInDeck != 0) {
				int[] cardNumberOrder = new int[5]; // and array to hold just the card numbers in the hand for sorting
													// and processing
				for (int i = 0; i < 5; i++) {
					cardNumberOrder[i] = deckOrder.get(currentPositionInDeck);
					currentPositionInDeck++;
				}
				cardNumberOrderSort(cardNumberOrder);
				for (int i = 0; i < 5; i++) {
					playerCards.get(i).setCardNumber(cardNumberOrder[i]);
				}

			} else {
				newGame();
			}
		}

		/**
		 * method to reset the deck for a new game. This shuffles the cards and resets
		 * the currentPositionInDeck to 0 to start everything from scratch.
		 */
		private void newGame() {
			currentPositionInDeck = 0;
			shuffle(deckOrder);
			int[] cardNumberOrder = new int[5]; // and array to hold just the card numbers in the hand for sorting and
												// processing
			for (int i = 0; i < 5; i++) {
				cardNumberOrder[i] = deckOrder.get(i);
				currentPositionInDeck++;
			}
			cardNumberOrderSort(cardNumberOrder);
			for (int i = 0; i < 5; i++) {
				if (playerCards.size() == 5) {
					playerCards.get(i).setCardNumber(cardNumberOrder[i]);
				} else {
					playerCards.add(new PlayerCard(cardNumberOrder[i]));
				}
			}
		}

		/**
		 * This sorts an array of 5 integers of card positions in the deck to organize
		 * them in a logical poker order
		 * 
		 * @param cardNumberOrder
		 */
		private void cardNumberOrderSort(int[] cardNumberOrder) {
			int currentMin, currentMinIndex; // variables to store the current minimum for comparing and the index value
												// of that number

			for (int i = 0; i < 4; i++) {
				currentMin = cardNumberOrder[i];
				currentMinIndex = i;
				for (int j = i + 1; j < 5; j++) {
					if (cardNumberCompare(currentMin, cardNumberOrder[j]) > 0) {
						currentMin = cardNumberOrder[j];
						currentMinIndex = j;
					}
				}
				if (currentMinIndex != i) {
					cardNumberOrder[currentMinIndex] = cardNumberOrder[i];
					cardNumberOrder[i] = currentMin;
				}

			}
		}

		/**
		 * This compares two card numbers to get which one is greater or less than the
		 * other based off of a poker order
		 * 
		 * @param card1
		 * @param card2
		 * @return
		 */
		private int cardNumberCompare(int card1, int card2) {
			int card1AbsoluteCardNumber = card1 % numberOfCardsInDeck; // numeric value of card1 as it's position in the
																		// deck (1 - last card number in deck)
			if (card1AbsoluteCardNumber == 0) {
				card1AbsoluteCardNumber = numberOfCardsInDeck;
			}
			int card1ActualCardNumber; // value of card1 in it's suit (1 - 13)
			if (card1AbsoluteCardNumber / 13 == 4) {
				card1ActualCardNumber = 13 + card1AbsoluteCardNumber % 13;
			} else {
				card1ActualCardNumber = card1AbsoluteCardNumber % 13;
				if (card1ActualCardNumber == 0) {
					card1ActualCardNumber = 13;
				}
			}

			int card2AbsoluteCardNumber = card2 % numberOfCardsInDeck; // numeric value of card2 as it's position in the
																		// deck (1 - last card number in deck)
			if (card2AbsoluteCardNumber == 0) {
				card2AbsoluteCardNumber = numberOfCardsInDeck;
			}
			int card2ActualCardNumber; // value of card2 in it's suit (1 - 13)
			if (card2AbsoluteCardNumber / 13 == 4) {
				card2ActualCardNumber = 13 + card2AbsoluteCardNumber % 13;
			} else {
				card2ActualCardNumber = card2AbsoluteCardNumber % 13;
				if (card2ActualCardNumber == 0) {
					card2ActualCardNumber = 13;
				}
			}

			if (card1ActualCardNumber < card2ActualCardNumber)
				return -1;
			else if (card1ActualCardNumber > card2ActualCardNumber)
				return 1;
			else {
				if (card1AbsoluteCardNumber < card2AbsoluteCardNumber)
					return -1;
				else if (card1AbsoluteCardNumber > card2AbsoluteCardNumber)
					return 1;
				else
					return 0;
			}

		}

	}

}