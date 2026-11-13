package com.comp2042.app;

import com.comp2042.controller.GameController;
import com.comp2042.controller.MainMenuController;
import com.comp2042.model.GameModel;
import com.comp2042.model.GameSettings;
import com.comp2042.util.ResourceLoader;
import com.comp2042.view.GameView;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Switches the single game window between the main menu and a game.
 * <p>
 * Previously the menu and the game used two separate windows, the stage JavaFX passes
 * to {@code start()} was never used (a second one was created instead), and controllers
 * reached this class through a static {@code Main.getViewManager()}. Now one window is
 * reused and the manager is handed to the controllers that need it.
 */
public class ViewManager {

    private static final String TITLE = "Snake Game";
    private static final String MENU_FXML = "/views/MainMenu.fxml";
    private static final String ICON = "/views/res/SnakeLogoWhite.png";

    private final Stage stage;

    /**
     * @param stage the application window
     */
    public ViewManager(Stage stage) {
        this.stage = stage;
        stage.setTitle(TITLE);
        stage.getIcons().add(new Image(ResourceLoader.externalForm(ICON)));
        stage.setResizable(false);
    }

    /**
     * Shows the main menu.
     *
     * @throws IllegalStateException if the menu layout cannot be loaded
     */
    public void showMainMenu() {
        FXMLLoader loader = new FXMLLoader(ResourceLoader.url(MENU_FXML));
        loader.setControllerFactory(type -> new MainMenuController(this));
        Parent root;
        try {
            root = loader.load();
        } catch (IOException exception) {
            throw new IllegalStateException("Could not load the main menu", exception);
        }
        stage.setFullScreen(false);
        stage.setScene(new Scene(root));
        stage.sizeToScene();
        stage.show();
    }

    /**
     * Starts a new game with the given settings.
     *
     * @param settings options chosen in the menu
     */
    public void showGame(GameSettings settings) {
        GameModel model = new GameModel(settings);
        GameView view = new GameView(model);
        GameController controller = new GameController(model, view, stage, this::showMainMenu);
        Scene scene = new Scene(view.getRoot(), view.getPreferredWidth(), view.getPreferredHeight());
        scene.setOnKeyPressed(controller::handle);
        stage.setScene(scene);
        stage.sizeToScene();
    }
}
