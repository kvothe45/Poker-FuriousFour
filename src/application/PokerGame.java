/**
 * Names:  Andrew Hoffman, Chase Revia, Robert Elmore, Ralph E. Beard IV
 * Course #:  1174
 * Date:  
 * Assignment Name: Group Project Poker
 */

package application;
	
import java.io.File;

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
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Rotate;


public class PokerGame extends Application {
	
	private Integer[] deckOfCardsPosition = new Integer[52];  //Will hold the position of the cards that will be used
	private PlayerCard[] playerCards = new PlayerCard[5]; // this is the array of visible cards for the player
	private int currentPositionInDeck = 0 ; // this will keep track of which card we need to deal next
	
	/**
	 * Main method to start the program and just ensure that 
	 * the start method is called
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);

	}

	/**
	 * The overrriden method that is the start of processing
	 * visual elements.
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		for (int i = 0; i < 52; i++) {
			deckOfCardsPosition[i] = i + 1; 
		}	
		
		Button drawButton = new Button("Draw"); // This button will be used to draw new cards
		drawButton.setOnMouseClicked(e -> {
			drawCards();
			if ((deckOfCardsPosition.length - currentPositionInDeck) < 5) {
				drawButton.setDisable(true);
			}	
		});
		
		Button newHandButton = new Button("New Hand"); // This is just a button that calls newHand method
		newHandButton.setOnMouseClicked(e -> {
			newHand();
			drawButton.setDisable(false);
		});
		
		HBox menuBox = new HBox();
		menuBox.setSpacing(5);
		menuBox.getChildren().addAll(drawButton, newHandButton);
		
		HBox cardsBox = new HBox(); //This layout holds the images of the cards for display horizontally
		cardsBox.setSpacing(5);
		Utilities.shuffle(deckOfCardsPosition);
		for(int i = 0; i < 5; i++) {
			playerCards[i] = new PlayerCard(deckOfCardsPosition[i]); 
			cardsBox.getChildren().add(playerCards[i]);
			currentPositionInDeck++;
		}
		
		VBox layoutBox = new VBox(); // This layout holds the button and 5 card ImageViews vertically
		layoutBox.setPadding(new Insets(0,5,0,5));
		layoutBox.setSpacing(5);
		layoutBox.getChildren().addAll(menuBox, cardsBox);
		
		Scene scene = new Scene(layoutBox);	// This is the scene to place onto the stage for display
		
		primaryStage.setTitle("Poker Hand");
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		primaryStage.show();
				
	}
	
	
	/**
	 * This method suffles the deck of cards array producing a random order for the numbers.
	 * It then creates the full file name for the 5 cards based on the numbers in the first
	 * 5 places in the array.  Finally it creates the Image from the file and sets the 
	 * ImageView
	 */
	private void newHand() {
		Utilities.shuffle(deckOfCardsPosition);
		for (int i = 0; i < 5; i++) {
			playerCards[i].setCardNumber(deckOfCardsPosition[i]);
			currentPositionInDeck++;
		}
	}
	
	/**
	 * This method will handle what happens when the draw button is pressed
	 */
	private void drawCards() {
		for (int i = 0; i < 5; i++) {
			if (!playerCards[i].getHoldButton().isSelected()) {
			
				playerCards[i].swapCard(deckOfCardsPosition[currentPositionInDeck]);
				currentPositionInDeck++;
			} else {
				playerCards[i].getHoldButton().setSelected(false);
			}
		}
	}
	
	/**
	 * Inner class to create properties for the displayed card
	 *
	 */
	public class PlayerCard extends VBox {
		
		private int cardNumber;
		private Image cardImage, backImage;
		private ImageView cardImageView = new ImageView();
		private ImageView backImageView = new ImageView();
		private ToggleButton holdButton = new ToggleButton("Hold");
		private StackPane cardPane;
		
		/**
		 * The is the constructor for creating the card.  It at least needs the 
		 * card number to display as a parameter
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
			this.setPadding(new Insets(0,5,0,5));
			String fileName = cardNumber + ".png";
			String fullFileName = "file:" + System.getProperty("user.dir") + 
					File.separator + "resources" + File.separator + "cards" + 
					File.separator +  fileName;// This creates the full file name with path for the image in a format readable by any system
			cardImage = new Image(fullFileName, 100, 133, true, true);
			fileName = "b2fv.png";
			fullFileName = "file:" + System.getProperty("user.dir") + 
					File.separator + "resources" + File.separator + "cards" + 
					File.separator +  fileName;// This creates the full file name with path for the reverse image for the card.
			backImage = new Image(fullFileName, 100, 133, true, true);
			backImageView.setImage(backImage);
			cardImageView.setImage(cardImage);
			cardPane = new StackPane();
			cardPane.getChildren().addAll(backImageView, cardImageView);
			backImageView.setVisible(false);
			this.getChildren().addAll(cardPane, holdButton);
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
			String fileName = cardNumber + ".png";
			String fullFileName = "file:" + System.getProperty("user.dir") + 
					File.separator + "resources" + File.separator + "cards" + 
					File.separator +  fileName;// This creates the full file name with path for the image in a format readable by any system
			cardImage = new Image(fullFileName, 100, 133, true, true);
			cardImageView.setImage(cardImage);
		}
		
		
		
		public void swapCard(int newCardNumber) {
			RotateTransition rotator = new RotateTransition(Duration.millis(250), cardPane);
			rotator.setAxis(Rotate.Y_AXIS);
			rotator.setFromAngle(0);
			rotator.setToAngle(90);
			rotator.setInterpolator(Interpolator.LINEAR);
			rotator.setOnFinished(e -> {
				cardImageView.setVisible(false);
				backImageView.setVisible(true);
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
				String fileName = newCardNumber + ".png";
				String fullFileName = "file:" + System.getProperty("user.dir") + 
						File.separator + "resources" + File.separator + "cards" + 
						File.separator +  fileName;// This creates the full file name with path for the image in a format readable by any system
				Image cardImage = new Image(fullFileName, 100, 133, true, true);
				cardImageView.setImage(cardImage);
				cardImageView.setVisible(true);
				backImageView.setVisible(false);
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
		 * @return the cardImage
		 */
		public Image getCardImage() {
			return cardImage;
		}

		/**
		 * @param cardImage the cardImage to set
		 */
		public void setCardImage(Image cardImage) {
			this.cardImage = cardImage;
		}

		/**
		 * @return the cardImageView
		 */
		public ImageView getCardImageView() {
			return cardImageView;
		}

		/**
		 * @param cardImageView the cardImageView to set
		 */
		public void setCardImageView(ImageView cardImageView) {
			this.cardImageView = cardImageView;
		}

		/**
		 * @return the holdButton
		 */
		public ToggleButton getHoldButton() {
			return holdButton;
		}

		/**
		 * @param holdButton the holdButton to set
		 */
		public void setHoldButton(ToggleButton holdButton) {
			this.holdButton = holdButton;
		}
		
		
		
		
	}
	

}