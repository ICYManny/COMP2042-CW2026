package com.comp2042.controller;

import com.comp2042.app.Main;
import com.comp2042.app.ViewManager;
import com.comp2042.model.Difficulty;
import com.comp2042.model.GameModel;
import com.comp2042.model.GameSettings;
import com.comp2042.view.GameView;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainMenuController {

    private static final int GRID_WIDTH = 20;
    private static final int GRID_HEIGHT = 20;
    private static final boolean POWER_UPS = true;
    private static final Difficulty DIFFICULTY = Difficulty.NORMAL;

    private final ViewManager viewManager = Main.getViewManager();

    public void handleStart() {
        viewManager.initGameStage();
        Stage gameStage = viewManager.getGameStage();

        GameModel model = new GameModel(new GameSettings(GRID_WIDTH, GRID_HEIGHT, POWER_UPS, DIFFICULTY));
        GameView view = new GameView(model);
        GameController controller = new GameController(model, view, gameStage);
        Scene gameScene = new Scene(view.getRoot(), view.getPreferredWidth(), view.getPreferredHeight());
        gameScene.setOnKeyPressed(controller::handle);

        gameStage.setScene(gameScene);
        viewManager.getPrimaryStage().close();
        gameStage.show();
    }

    public ViewManager getViewManager() {
        return viewManager;
    }
}
