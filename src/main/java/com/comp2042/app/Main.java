package com.comp2042.app;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * JavaFX application class. Hands the window JavaFX creates to a {@link ViewManager},
 * which shows the main menu.
 */
public class Main extends Application {

    /**
     * Called by JavaFX when the application starts.
     *
     * @param primaryStage the main window created by JavaFX
     */
    @Override
    public void start(Stage primaryStage) {
        new ViewManager(primaryStage).showMainMenu();
    }

    /**
     * Launches JavaFX.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
