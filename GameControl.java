package com.connect_4game;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GameControl implements Initializable {

	private static final int columns = 7;
	private static final int rows = 6;
	private static final int circle_dia = 80;
	private static final String disccolor1 = "#24303E";
	private static final String disccolor2 = "#4CAA88";

	private static String player_one = "player one";
	private static String player_two = "player two";

	private boolean isPlayerOneTurn = true;

	private Disc[][] insertedDiscsArray = new Disc[rows][columns];  // for structural changes

	@FXML
	public GridPane rootGridPane;

	@FXML
	public Pane insertedDiscsPane;

	@FXML
	public Label playerNameLabel;

	@FXML
	public TextField PlayerOneField, PlayerTwoField;

	@FXML
	public Button SetNames;

	private boolean isAllowedToInsert = true;    // flag to avoid same color disc being added.

	public void createPlayground() {

		SetNames.setOnAction(event -> {
			player_one = PlayerOneField.getText();
			player_two = PlayerTwoField.getText();
		});

		Shape rectanglewithHoles = createGamesStructuresGrdid();

		rootGridPane.add(rectanglewithHoles, 0, 1);

		List<Rectangle> rectangleList = createClickableColumns();

		for (Rectangle rectangle : rectangleList) {
			rootGridPane.add(rectangle, 0, 1);
		}
	}

	private Shape createGamesStructuresGrdid(){

		Shape rectanglewithHoles = new Rectangle((columns+1) * circle_dia, (rows+1) * circle_dia);

		for(int row=0; row< rows; row++){

			for(int col=0; col < columns; col++){
				Circle circle = new Circle();
				circle.setRadius(circle_dia / 2);
				circle.setCenterX(circle_dia / 2);
				circle.setCenterY(circle_dia / 2);
				circle.setSmooth(true);

				circle.setTranslateX(col * (circle_dia+5) + circle_dia / 4);
				circle.setTranslateY(row * (circle_dia+5) + circle_dia / 4);

				rectanglewithHoles = Shape.subtract(rectanglewithHoles, circle);
			}
		}

		rectanglewithHoles.setFill(Color.WHITE);

		return rectanglewithHoles;
	}

	private List<Rectangle> createClickableColumns(){

		List<Rectangle> rectangleList = new ArrayList<>();

		for(int col=0; col<columns; col++) {

			Rectangle rectangle = new Rectangle(circle_dia, (rows + 1) * circle_dia);
			rectangle.setFill(Color.TRANSPARENT);
			rectangle.setTranslateX(col * (circle_dia + 5) + circle_dia / 4);

			rectangle.setOnMouseEntered(event -> rectangle.setFill(Color.valueOf("#eeeeee26")));
			rectangle.setOnMouseExited(event -> rectangle.setFill(Color.TRANSPARENT));

			final int column = col;
			rectangle.setOnMouseClicked(event -> {
				if(isAllowedToInsert) {
					isAllowedToInsert = false;     // when the disc is being dropped then no more disc will be inserted
					insertedDisc(new Disc(isPlayerOneTurn), column);
				}
			});

			rectangleList.add(rectangle);
		}
			return rectangleList;
		}

	private void insertedDisc(Disc disc, int column) {

		int row = rows - 1;
		while ((row >= 0)) {

			if(getDiscPresent(row, column) == null)
				break;

			row--;
		}

		if(row < 0)                // it is full, we cannot insert anymore disc
			return;

		insertedDiscsArray[row][column] = disc;
		insertedDiscsPane.getChildren().add(disc);

		disc.setTranslateX(column * (circle_dia + 5) + circle_dia / 4);

		int currentRow = row;
		TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5), disc);
		translateTransition.setToY(row * (circle_dia + 5) + circle_dia / 4);
		translateTransition.setOnFinished(event -> {

			isAllowedToInsert = true;    // finally, when disc is dropped allow next player to insert disc
			if(gameEnded(currentRow, column)){
				gameOver();
				return;
			}

			isPlayerOneTurn = !isPlayerOneTurn;
			playerNameLabel.setText(isPlayerOneTurn? player_one : player_two);
		});


		translateTransition.play();
	}

	private boolean gameEnded(int row, int column) {

		// vertical points A small example:

		List<Point2D> verticalPoints = IntStream.rangeClosed(row - 3, row + 3)    // range of row values = 0,1,2,3,4,5
				                        .mapToObj(r -> new Point2D(r, column))  // 0,3  1,3  2,3  3,3  4,3  5,3 -- > point2D x,y
				                        .collect(Collectors.toList());

		List<Point2D> horizontalPoints = IntStream.rangeClosed(column - 3, column + 3)
				.mapToObj(col -> new Point2D(row, col))
				.collect(Collectors.toList());

		Point2D startPoint1 = new Point2D(row - 3, column + 3);
		List<Point2D> diagonalPoints1 = IntStream.rangeClosed(0, 6)
				.mapToObj(i -> startPoint1.add(i, -i))
				.collect(Collectors.toList());

		Point2D startPoint2 = new Point2D(row - 3, column - 3);
		List<Point2D> diagonalPoints2 = IntStream.rangeClosed(0, 6)
				.mapToObj(i -> startPoint2.add(i, i))
				.collect(Collectors.toList());

		boolean isEnded = checkCombinations(verticalPoints) || checkCombinations(horizontalPoints)
				|| checkCombinations(diagonalPoints1) || checkCombinations(diagonalPoints2);

		return isEnded;
	}

	private boolean checkCombinations(List<Point2D> points) {

		int chain = 0;

		for (Point2D point: points) {

			int rowIndexForArray = (int) point.getX();
			int columnIndexForArray = (int) point.getY();

			Disc disc = getDiscPresent(rowIndexForArray, columnIndexForArray);

			if(disc != null && disc.isPlayerOneMove == isPlayerOneTurn){

				chain++;
				if(chain == 4){
					return true;
				}
			}
			else{
				chain = 0;
			}
		}
		return false;
	}

	private Disc getDiscPresent(int row, int column){     // to prevent ArrayIndexOutofBoundException

		if( row >= rows || row < 0 || column >=columns || column < 0)   // if row or column index is invalid
			return null;

		return insertedDiscsArray[row][column];
	}
	private void gameOver(){
        String winner = isPlayerOneTurn ? player_one : player_two;
		System.out.println("winner is: "+ winner);

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("connect four");
		alert.setHeaderText("The winner is "+ winner);
		alert.setContentText("want to play again ?");

		ButtonType yesBtn = new ButtonType("yes");
		ButtonType noBtn = new ButtonType("no, exit game");
		alert.getButtonTypes().setAll(yesBtn, noBtn);

		Platform.runLater(() -> {   // helps us to resolve IllegalAtateException.

			Optional<ButtonType> btnClicked = alert.showAndWait();
			if(btnClicked.isPresent() && btnClicked.get() == yesBtn) {
				resetGame();
			}
				else{
					// .. user chose no.. so exit game
					System.exit(0);
				}
		});
	}

	public  void resetGame() {

		insertedDiscsPane.getChildren().clear();    // remove all inserted disc from pane

		for (int row = 0; row < insertedDiscsArray.length; row++) {  // structurally, make all elements
			for (int col = 0; col < insertedDiscsArray.length; col++) {
				insertedDiscsArray[row][col] = null;
			}
		}
		isPlayerOneTurn = true; // let player start the game
		playerNameLabel.setText(player_one);

		createPlayground();  // prepare a fresh playground
	}

	private static class Disc extends Circle{

		private final boolean isPlayerOneMove;

		public Disc(boolean isPlayerOneMove){

			this.isPlayerOneMove = isPlayerOneMove;
			setRadius(circle_dia / 2);
			setCenterX(circle_dia/2);
			setCenterY(circle_dia/2);
			setFill(isPlayerOneMove? Color.valueOf(disccolor1):Color.valueOf(disccolor2));
		}
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

	}
}












