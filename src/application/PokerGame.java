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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.RadioButton;
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
	private int numberOfCardsInADeck = 54; // this will be the number of cards in the deck which changes with wildcards
	private int currentPositionInDeck = 0; // this will keep track of which card we need to deal next
	private int numberOfDecks = 1; // integer to keep track of the number of decks being used
	private int playerWalletAmount = 200; // how much the player has to bet
	private int currentAvatar = 0; // This will hold the index of the current avatar
	private Button drawButton = new Button("Draw"); // This button will be used to draw new cards
	private Button forfeitButton = new Button("Forfeit"); // This button allows the player to forfeit the hand and the wager
	private Button dealButton = new Button("Deal"); // button to set the bet and deal the cards
	private HBox cardsBox = new HBox(); // This layout holds the images of the cards for display horizontally
	private HBox wagerBox = new HBox(); // this box is used to manage wagers placed
	private Label playerWalletLabel = new Label(); // this will show the current amount left to bet
	private Label alertLabel = new Label(" "); // tells the player what they won with
	private Label instructionLabel = new Label(" "); // This will hold general instructions for the player
	private GameMechanics gameMechanics = new GameMechanics(); // this variable will handle the overall mechanics of the gameStatus
	private ImageView avatarImageView = new ImageView(); // this is the ImageView for the user's avatar
	private TextField wagerTextField = new TextField(); // this will allow the user to place custom wagers
	RadioButton oneDeck, twoDecks; // radio buttons for the number of decks to be played with

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

		VBox layoutBox = createLayoutBox(); // this is the layout arranged as we like

		Scene scene = new Scene(layoutBox); // This is the scene to place onto the stage for display

		primaryStage.setTitle("Poker Hand");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();

	}
	
	
	/**
	 * creates the layout we see on screen
	 * @return
	 */
	private VBox createLayoutBox() {
		//gameMechanics.populateDeck();
		
		HBox upperLayoutBox = createUpperLayoutBox();
		
		createCardsBox();
		HBox instructionBox = createInstructionBox();
		
		VBox layoutBox = new VBox(); // This layout holds the button and 5 card ImageViews vertically
		layoutBox.setPadding(new Insets(0, 5, 0, 5));
		layoutBox.setSpacing(5);
		layoutBox.getChildren().addAll(upperLayoutBox, cardsBox, instructionBox);
		
		return layoutBox;
		
	}
	
	public HBox createInstructionBox() {
		
		instructionLabel.setPadding(new Insets(5,0,5,0));
		instructionLabel.setFont(Font.font(18));
		
		HBox instructionBox = new HBox();
		instructionBox.setAlignment(Pos.CENTER);
		instructionBox.getChildren().add(instructionLabel);
		
		return instructionBox;
	}
	
	/**
	 * This creates the area above the cards that manages controls and displays the avatar
	 * @return
	 */
	public HBox createUpperLayoutBox() {
		
		drawButton.setOnMouseClicked(e -> {
			gameMechanics.drawCards();
			drawButton.setDisable(true);
			forfeitButton.setDisable(true);
			dealButton.setDisable(false);
			oneDeck.setDisable(false);
			twoDecks.setDisable(false);
			instructionLabel.setText(" ");
		});
		drawButton.setDisable(true);
		
		forfeitButton.setOnMouseClicked(e -> {
			flipCards();
			alertLabel.setText("Player Forfeits");
			drawButton.setDisable(true);
			forfeitButton.setDisable(true);
			dealButton.setDisable(false);
			oneDeck.setDisable(false);
			twoDecks.setDisable(false);
			instructionLabel.setText(" ");
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
			oneDeck.setDisable(true);
			twoDecks.setDisable(true);
			alertLabel.setText(" ");
			instructionLabel.setText("Select Cards to Discard");
		});
				
		HBox placeBetsBox = new HBox(); // this box handles the betting
		placeBetsBox.setSpacing(10);
		placeBetsBox.setAlignment(Pos.BASELINE_CENTER);
		placeBetsBox.getChildren().addAll(wagerBox, dealButton);
		placeBetsBox.setVisible(true);

		playerWalletLabel.setText("Wallet $" + playerWalletAmount);
		playerWalletLabel.setPadding(new Insets(0, 15, 0, 10));
		playerWalletLabel.setFont(Font.font(12));

		HBox numberOfDecksBox = createNumberOfDecksBox();
		
		HBox menuBox = new HBox(); // this is the HBox to hold the menu and button items
		menuBox.setSpacing(5);
		menuBox.getChildren().addAll(drawButton, forfeitButton, playerWalletLabel, numberOfDecksBox);

		alertLabel.setFont(Font.font(12));
		alertLabel.setPadding(new Insets(0, 0, 0, 10));

		HBox alertBox = new HBox(); // this HBox is just used to show what the person won with
		alertBox.setSpacing(5);
		alertBox.getChildren().add(alertLabel);				
			
		VBox menuAlertBox = new VBox(); // This VBox holds the menu and alert HBoxs
		menuAlertBox.setSpacing(5);
		menuAlertBox.getChildren().addAll(menuBox, placeBetsBox, alertBox);
		
		manageAvatars();
		
		HBox upperLayoutBox = new HBox();  //To allow for the avatar Image, the upper section is now laid out in an HBox
		upperLayoutBox.setSpacing(5);
		upperLayoutBox.getChildren().addAll(avatarImageView, menuAlertBox);
		
		return upperLayoutBox;		
		
	}
	
	/**
	 * This method will handle the avatars and click event to change them
	 */
	private void manageAvatars() {
		
		avatarImageView.setImage(changeAvatarImage(true));
		avatarImageView.setOnMouseClicked(e -> {
			avatarImageView.setImage(changeAvatarImage(false));
		});
		
	}
	
	private Image changeAvatarImage(boolean isInitial) {
		
		String[] avatarNames = {"ghost_skull.png", "anime_man.png", "archer.png", "bunny.png", "goblin.png", 
				"maze_man.png", "penguin.png", "snowman.png", "steampunk_woman.png", "sugar_skull.png", 
				"vampire_smiley.png", "witch.jpg"};  // this array holds all the file names for the avatars
		if (!isInitial) {
			if (currentAvatar == 11) {
				currentAvatar = 0;
			} else {
				currentAvatar ++;
			}
		}		
		String fileName = avatarNames[currentAvatar];
		String fullFileName = "file:" + System.getProperty("user.dir") + File.separator + "resources"
				+ File.separator + "player_avatars" + File.separator + fileName;// This creates the full file name with path
																		        // for the image in a format readable by any
																		        // system
		return new Image(fullFileName, 60, 60, true, true);
		
	}
	
	/**
	 * This method is used to select the number of decks a person wishes to use for 
	 * their current game
	 * 
	 * @return
	 */
	private HBox createNumberOfDecksBox() {
		
		oneDeck = new RadioButton("1 Deck");
		twoDecks = new RadioButton("2 Decks");
		
		ToggleGroup numberOfDecksGroup = new ToggleGroup(); // toggle group the radio buttons belong to
		oneDeck.setToggleGroup(numberOfDecksGroup);
		twoDecks.setToggleGroup(numberOfDecksGroup);
		oneDeck.setSelected(true);

		oneDeck.setFont(Font.font(12));
		twoDecks.setFont(Font.font(12));

		HBox numberOfDecksToggleBox = new HBox(); // HBox to hold the label and radio buttons
		numberOfDecksToggleBox.setSpacing(5);
		numberOfDecksToggleBox.getChildren().addAll(oneDeck, twoDecks);

		return numberOfDecksToggleBox;
	}
	
	
	/**
	 * This creates the area for the cards
	 */
	public void createCardsBox() {
		cardsBox.setSpacing(5);
		for (int i = 0; i < 5; i++) {
			playerCards.add(new PlayerCard(0));
			cardsBox.getChildren().add(playerCards.get(i));
		}
	}

	/**
	 * This method is used to set up betting for the game. It returns and HBox which
	 * can be placed with the rest of the menus
	 * 
	 * @return
	 */
	private HBox createBettingBox() {
		
		wagerTextField.textProperty().addListener(new ChangeListener<String>() {
			/**
			 * this method came from stack overflow
			 * https://stackoverflow.com/questions/7555564/what-is-the-recommended-way-to-make-a-numeric-textfield-in-javafx
			 */
			@Override
			public void changed(ObservableValue<? extends String> observableValue, 
					String oldValue, String newValue) {
				if (!newValue.matches("\\d*")) {
					wagerTextField.setText(newValue.replaceAll("[^\\d]", ""));
				}
				
			}
		});
		
		Button betOneButton = new Button("Bet 1");
		betOneButton.setOnMouseClicked(e -> {
			wagerTextField.setText("1");
		});
		
		Button betAllButton = new Button("Bet All");
		betAllButton.setOnMouseClicked(e -> {
			wagerTextField.setText(String.valueOf(playerWalletAmount));
		});

		Label wagerLabel = new Label("Wager: "); // label to describe the toggle group
		wagerLabel.setFont(Font.font(12));

		HBox betToggleBox = new HBox(); // HBox to hold the label and radio buttons
		betToggleBox.setSpacing(5);
		betToggleBox.getChildren().addAll(wagerLabel, wagerTextField, betOneButton, betAllButton);

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
		private StackPane cardPane;
		private boolean primaryCardBack = true;
		private boolean isDiscard = false;

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
			this.getChildren().addAll(cardPane);
			
			cardImageView.setOnMouseClicked(e -> {
				if (cardImageView.isVisible()) {
					flipCardFromFrontToBack();
				}
			});
			cardBackImageView.setOnMouseClicked(e -> {
				if (cardBackImageView.isVisible() && cardNumber != 0) {
					flipCardFromBackToFront();
				}
			});
			
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
		 * This method will flip the card from it's front to it's back
		 */
		public void flipCardFromFrontToBack() {
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
			isDiscard = true;
		}
		
		/**
		 * This flips the card from it's back to its front
		 */
		public void flipCardFromBackToFront() {
			RotateTransition rotator = new RotateTransition(Duration.millis(250), cardPane);
			rotator.setAxis(Rotate.Y_AXIS);
			rotator.fromAngleProperty();
			rotator.setFromAngle(180);
			rotator.setToAngle(270);
			rotator.setInterpolator(Interpolator.LINEAR);
			rotator.setOnFinished(e1 -> {
				cardImageView.setVisible(true);
				cardBackImageView.setVisible(false);
			});
			rotator.play();

			rotator = new RotateTransition(Duration.millis(750), cardPane);
			rotator.setDelay(Duration.millis(250));
			rotator.setAxis(Rotate.Y_AXIS);
			rotator.setFromAngle(270);
			rotator.setToAngle(360);
			rotator.setInterpolator(Interpolator.LINEAR);
			rotator.play();
			isDiscard = false;
		}

		/**
		 * This method swaps the card for the new drawn card with animations
		 * @param newCardNumber
		 */
		public void swapCard(int newCardNumber) {

			TranslateTransition discardTransition = new TranslateTransition(Duration.millis(500), cardPane);
			discardTransition.setToY(200 + cardPane.getLayoutY());
			discardTransition.play();

			TranslateTransition drawTransition = new TranslateTransition(Duration.millis(1000), cardPane);
			drawTransition.setDelay(Duration.millis(500));
			drawTransition.setFromY(-200);
			drawTransition.setToY(cardPane.getLayoutY());
			drawTransition.play();

			RotateTransition rotator = new RotateTransition(Duration.millis(250), cardPane);
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

			rotator = new RotateTransition(Duration.millis(750), cardPane);
			rotator.setDelay(Duration.millis(250));
			rotator.setAxis(Rotate.Y_AXIS);
			rotator.setFromAngle(270);
			rotator.setToAngle(360);
			rotator.setInterpolator(Interpolator.LINEAR);
			rotator.play();
			isDiscard = false;

		}

		/**
		 * @return the cardImageView
		 */
		public ImageView getCardImageView() {
			return cardImageView;
		}

		/**
		 * @return the cardBackImageView
		 */
		public ImageView getCardBackImageView() {
			return cardBackImageView;
		}

		/**
		 * @return the isDiscard
		 */
		public boolean isDiscard() {
			return isDiscard;
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
		
		int wager = 0; // this variable will be used to manage the wager per hand

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
			wager = getWager();// used to store the user's wager
			playerWalletAmount = playerWalletAmount - wager;
			playerWalletLabel.setText("Wallet $" + playerWalletAmount);
			
		}

		/**
		 * This method manages the bet after the draw and winning has been determined
		 */
		private void manageBet(int[] cardsAfterDraw) {
			String winLossString = isWon(cardsAfterDraw); // gets the type of win from isWon for display
			switch(winLossString) {
				case "Five of a Kind":
					wager = wager * 500;
					break;
				case "Royal Flush":
					wager = wager * 250;
					break;
				case "Straight Flush":
					wager = wager * 50;
					break;
				case "Four of a Kind":
					wager = wager * 25;
					break;
				case "Flush":
					wager = wager * 9;
					break;
				case "Straight":
					wager = wager * 6;
					break;
				case "Full House":
					wager = wager * 4;
					break;
				case "Three of a Kind":
					wager = wager * 3;
					break;
				case "Two Pair":
					wager = wager * 2;
					break;
				case "":
					wager = 0;
					break;
				
			};
			
			if (wager != 0) {
				playerWalletAmount = playerWalletAmount + wager;
				playerWalletLabel.setText("Wallet $" + playerWalletAmount);
				alertLabel.setText("You won with " + winLossString);
			} else {
				if (playerWalletAmount > 0) {
					alertLabel.setText("You lost");
				}  else {
					alertLabel.setText("Game Over");
					dealButton.setDisable(true);
				}
			}
			wagerTextField.setText("");
			wager = 0;
			
			
			

		}
		
		/**
		 * method to extract the user's wager from the radio buttons
		 * @return
		 */
		private int getWager() {
			if (wagerTextField.getText() != null && !wagerTextField.getText().equals("")) {
				wager = Integer.valueOf(wagerTextField.getText());// holds the value of the wager to make certain it's a valid wager
				if (wager > playerWalletAmount) {
					wager = playerWalletAmount;
					wagerTextField.setText(String.valueOf(playerWalletAmount));
				}
			} else {
				wager = 1;
				wagerTextField.setText("1");
			}
			return wager;
		}

		/**
		 * method to check to see if the hand is a winning hand
		 * 
		 * @return
		 */
		private String isWon(int[] cardNumberOrder) {

			int numberOfWilds = 0;  // holds the number of wilds in the player's hand
			Integer[] cardValues = new Integer[5]; // this holds just the card numeric values regardless of suit
			boolean[] handResults = new boolean[8]; // this holds whether the hand contains a certain winning hand or not
			
			for (int i = 0; i < 5; i++) {
				if (cardNumberOrder[i] > 52) {
					cardValues[i] = cardNumberOrder[i]; 
					numberOfWilds ++;
				} else {
					cardValues[i] = convertToActualNumber(cardNumberOrder[i]);
				}
			}
			
			cardNumberOrderSort(cardNumberOrder);
			sort(cardValues);
			if (numberOfWilds > 0) {
				handResults[0] = isFiveOfAKind(cardValues, numberOfWilds);
			}
			
			handResults[1] = isFlush(cardNumberOrder, numberOfWilds);
			handResults[2] = isStraight(cardValues, numberOfWilds);
			handResults[3] = handResults[2] && ((cardValues[0] == 1 && cardValues[1] >= 10) || cardValues[0] >= 10);
			checkForMatching(cardValues, numberOfWilds, handResults);
			
			if(handResults[0]) {
				return "Five of a Kind";
			} else if(handResults[1] && handResults[3]) {
				return "Royal Flush";
			} else if (handResults[1] && handResults[2]) {
				return "Straight Flush";
			} else if (handResults[4]) {
				return "Four of a Kind";
			} else if (handResults[5]) {
				return "Full House";
			} else if (handResults[1]) {
				return "Flush";
			} else if (handResults[2]) {
				return "Straight";
			} else if (handResults[6]) {
				return "Three of a Kind";
			} else if (handResults[7]) {
				return "Two Pair";
			} else {
				return "";
			}
		

		}
		
		/**
		 * This goes through the deck to see if the hand is five of a kind
		 * @param cardNumberOrder
		 * @param numberOfWilds
		 * @return
		 */
		private boolean isFiveOfAKind(Integer[] cardNumberOrder, int numberOfWilds) {
			
			if (numberOfWilds < 4) {
				for (int i = 0; i < 5 - numberOfWilds; i++) {
					if (cardNumberOrder[0] != cardNumberOrder[i]) {
						return false;
					}
				}
			}
			
			return true;
			
		}

		/**
		 * This checks if all the cards are the same suit
		 * 
		 * @param cardNumberOrder
		 * @return
		 */
		private boolean isFlush(int[] cardNumberOrder, int numberOfWilds) {

			int suitSet = cardNumberOrder[0] / 13; // this gets the numeric value of the suit of the first card to set
													// upper and lower bounds
			int lowerBound = (13 * suitSet) + 1; // is the numberic value of the ace of the suit the first card is
			int upperBound = lowerBound + 12; // numeric value of the king of suit the first card is in			

			for (int i = 1; i < 5 - numberOfWilds; i++) {
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
		private boolean isStraight(Integer[] cardValues, int numberOfWilds) {
			
			if (cardValues[0] == 1) {
				if (cardValues[4 - numberOfWilds] > 5 && cardValues[1] < 10) {
					return false;
				} else {
					return !anyRepeats(cardValues, numberOfWilds);
				}
			} else if ((cardValues[4 - numberOfWilds] - cardValues[0]) > 5 ) {
				return false;
			} else {
				return !anyRepeats(cardValues, numberOfWilds);
			}
			
		}
		
		/**
		 * This checks to see if any card in the array repeats itself
		 * @param cardValues
		 * @param numberOfWilds
		 * @return
		 */
		private boolean anyRepeats(Integer[] cardValues, int numberOfWilds) {
			for (int i = 0; i < 4 - numberOfWilds; i++) {
				if (cardValues[i] == cardValues[i + 1]) {
					return true;
				}
			}
			return false;
		}

		/**
		 * This method checks for four of a kind, three of a kind, etc.  Basically all the 
		 * winning hands that deal with repeated cards.  The handResults array will be used
		 * in the calling method what winning hand type occurred if one did
		 * @param cardValues
		 * @param numberOfWilds
		 * @param handResults
		 */
		private void checkForMatching(Integer[] cardValues, int numberOfWilds, boolean[] handResults) {
			
			int[] indexOfRepeats = new int[5];
			int firstRepeatedNumber = 0;
			int secondRepeatedNumber = 0;
			
			for (int i = 0; i < 4 - numberOfWilds; i++) {
				for (int j = i + 1; j < 5 - numberOfWilds; j++) {
					if ((indexOfRepeats[i] != -1) && (cardValues[i] == cardValues[j])) {
						indexOfRepeats[i]++;
						indexOfRepeats[j] = -1; 
					}
				}
			}
			
			for (int i = 0; i < 5 - numberOfWilds; i++) {
				if (indexOfRepeats[i] > 0) {
					if (firstRepeatedNumber == 0) {
						firstRepeatedNumber = indexOfRepeats[i] + 1;
					} else {
						secondRepeatedNumber = indexOfRepeats[i] + 1;
					}
				}
			}
			
			if ((firstRepeatedNumber == 4) || (firstRepeatedNumber == 3 && numberOfWilds == 1) ||
					(firstRepeatedNumber == 2 && numberOfWilds == 2) || (numberOfWilds ==  3)) {
				handResults[4] = true;
			} else if ((firstRepeatedNumber == 2 && secondRepeatedNumber == 3) || (firstRepeatedNumber == 3 && secondRepeatedNumber == 2) ||
					(firstRepeatedNumber == 2 && secondRepeatedNumber == 2 && numberOfWilds == 1)) {
				handResults[5] = true;
			} else if ((firstRepeatedNumber == 3) || (firstRepeatedNumber == 2 && numberOfWilds == 1) ||
					(numberOfWilds == 2)) {
				handResults[6] = true;
			} else if (firstRepeatedNumber == 2 && secondRepeatedNumber == 2) {
				handResults[7] = true;
			}
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
			for (int i = 0; i < numberOfDecks; i++) {
				for (int j = 0; j < numberOfCardsInADeck; j++) {
					deckOrder.add(j + 1);
				}
			}

		}

		/**
		 * This method will handle what happens when the draw button is pressed
		 */
		private void drawCards() {
			int[] cardsAfterDraw = new int[5];
			for (int i = 0; i < 5; i++) {
				if (playerCards.get(i).isDiscard()/*!playerCards.get(i).getHoldButton().isSelected()*/) {
					cardsAfterDraw[i] = deckOrder.get(currentPositionInDeck);
					playerCards.get(i).swapCard(cardsAfterDraw[i]);
					currentPositionInDeck++;
				} else {
					cardsAfterDraw[i] = playerCards.get(i).getCardNumber();
					//playerCards.get(i).getHoldButton().setSelected(false);
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
			if ((deckOrder.size() - currentPositionInDeck) >= (deckOrder.size() * .25) && 
					currentPositionInDeck != 0 && 
					((numberOfDecks == 1 && oneDeck.isSelected()) || (numberOfDecks == 2 && twoDecks.isSelected()))) {
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
			if (oneDeck.isSelected()) {
				numberOfDecks = 1;
			} else {
				numberOfDecks = 2;
			}
			populateDeck();
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
			int card1AbsoluteCardNumber = card1 % numberOfCardsInADeck; // numeric value of card1 as it's position in the
																		// deck (1 - last card number in deck)
			if (card1AbsoluteCardNumber == 0) {
				card1AbsoluteCardNumber = numberOfCardsInADeck;
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

			int card2AbsoluteCardNumber = card2 % numberOfCardsInADeck; // numeric value of card2 as it's position in the
																		// deck (1 - last card number in deck)
			if (card2AbsoluteCardNumber == 0) {
				card2AbsoluteCardNumber = numberOfCardsInADeck;
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
		
		/**
		 * This method takes a generic ArrayList and
		 * sorts it.
		 * @param <E>
		 * @param list
		 */
		public <E extends Comparable<E>> void sort(E[] list) {
			
			E currentMin; // holds the current minimum value in the ArrayList
			int currentMinIndex; // holds the index for the currentMin value
			
			for (int i = 0; i < list.length - 1; i++) {
				currentMin = list[i];
				currentMinIndex = i;
				for(int j = i + 1; j < list.length; j++) {
					if (currentMin.compareTo(list[j]) > 0) {
						currentMin = list[j];
						currentMinIndex = j;
					}
					
				}
				
				if (currentMinIndex != i) {
					list[currentMinIndex] = list[i];
					list[i] = currentMin;
				}
			}
			
		}

	}

}
