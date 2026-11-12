package com.comp2042.app;

import com.comp2042.util.ResourceLoader;
import com.comp2042.model.Game;
import com.comp2042.model.Snake;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ViewManager {

    private Stage primaryStage = new Stage();
    private AnchorPane root;

    private Stage gameStage;

    private Image snakeLogo = new Image(ResourceLoader.externalForm("/views/res/SnakeLogoWhite.png"));

    public ViewManager() {
    }

    public void initializeMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(ResourceLoader.url("/views/MainMenu.fxml"));
            root = loader.load();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        Scene mainScene = new Scene(root);

        primaryStage.setScene(mainScene);
        primaryStage.setResizable(false);
        primaryStage.getIcons().add(snakeLogo);
        primaryStage.setTitle("Snake Game");
        primaryStage.show();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void initGameStage() {
        gameStage = new Stage();
        gameStage.setTitle("Snake Game");
        gameStage.getIcons().add(snakeLogo);
        gameStage.setResizable(false);
    }

    public Stage getGameStage() {
        return gameStage;
    }
}
