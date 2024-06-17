package com.connect_4game;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class GameApplication extends Application {

	private GameControl control;

	@Override
	public void start(Stage primaryStage) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("game.fxml"));
		GridPane rootGridPane = loader.load();

		loader.getController();

		control = loader.getController();
		control.createPlayground();


		MenuBar menuBar = createMenu();
		menuBar.prefWidthProperty().bind(primaryStage.widthProperty());
		//menuBar.prefHeightProperty().bind(primaryStage.heightProperty());

		Pane menuPane = (Pane) rootGridPane.getChildren().get(0);
		menuPane.getChildren().add(menuBar);

		Scene scene = new Scene(rootGridPane);

		primaryStage.setScene(scene);
		primaryStage.setTitle("connect four");
		primaryStage.setResizable(false);
		primaryStage.show();
	}

	private MenuBar createMenu(){

		// file menu
		Menu fileMenu = new Menu("file");

		MenuItem newGame = new MenuItem("new game");
		newGame.setOnAction(event -> control.resetGame());

		MenuItem resetGame = new MenuItem("reset game");
		resetGame.setOnAction(event -> control.resetGame());

		SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
		MenuItem exitGame= new MenuItem("exit game");
		exitGame.setOnAction(event -> exitGame());

		fileMenu.getItems().addAll(newGame, resetGame, separatorMenuItem, exitGame);

		// help menu
		Menu helpMenu = new Menu("help");

		MenuItem aboutGame = new MenuItem("about connect4");
		aboutGame.setOnAction(event -> aboutConnect4());

		SeparatorMenuItem separator = new SeparatorMenuItem();
		MenuItem aboutMe = new MenuItem("about me");
		aboutMe.setOnAction(event -> aboutMe());

		helpMenu.getItems().addAll(aboutGame, separator, aboutMe);

		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().addAll(fileMenu, helpMenu);

		return menuBar;
	}

	private void aboutMe() {
		Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
		alert2.setTitle("who is the developer");
		alert2.setHeaderText("Himanshu gulia");
		alert2.setContentText("i love to play this game");

		alert2.show();
	}

	private void aboutConnect4() {
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle("about connect four");
		alert.setHeaderText("how tp play?");
		alert.setContentText("connect four is a two player game");

		alert.show();
	}

	private void exitGame() {
		Platform.exit();
		System.exit(0);
	}

	public static void main(String[] args) {
		launch();
	}
}









