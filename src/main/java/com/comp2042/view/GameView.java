package com.comp2042.view;

import com.comp2042.controller.MainMenuController;
import com.comp2042.model.Direction;
import com.comp2042.model.Game;
import com.comp2042.model.Obstacle;
import com.comp2042.model.Snake;
import com.comp2042.model.coin.Coin;

import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.awt.*;

public class GameView {

    private static final String LARGE_LABEL_STYLE = "-fx-font-size: 50;";
    private static final String LARGE_SCORE_STYLE = "-fx-font-size: 35;";
    private static final String SMALL_LABEL_STYLE = "-fx-font-size: 15;";

    private Canvas gridCanvas;
    private GraphicsContext gc;

    private Label gameOverLabel;
    private Label gameWonLabel;
    private Label scoreLabel;
    private boolean gameOverVis = false;
    private boolean gameWonVis = false;

    private Game game;
    private double initX;
    private double initY;
    private double fullScale = 1;
    private boolean fullsize = false;
    private int[][] turnArray = new int[MainMenuController.getxValue()][MainMenuController.getyValue()];

    public GameView(Game game) {
        this.game = game;
    }

    public void makeScene(Scene scene, Snake snake, Coin coin, Obstacle obstacle) {
        Scene gameScene = scene;
        gridCanvas = new Canvas(gameScene.getWidth(), gameScene.getHeight());
        initX = scene.getWidth();
        initY = scene.getHeight();
        gc = gridCanvas.getGraphicsContext2D();
        drawGrid(coin, snake, obstacle);

        gameOverLabel = new Label("GAME OVER");
        gameOverLabel.setId("gameOverLabel");
        String css = "resources/views/styling.css";
        gameOverLabel.getStylesheets().add(css);
        gameOverLabel.setVisible(false);

        gameWonLabel = new Label("YOU WON!");
        gameWonLabel.setId("gameWonLabel");
        gameWonLabel.getStylesheets().add(css);
        gameWonLabel.setVisible(false);

        scoreLabel = new Label("Score: " + game.getScore());
        scoreLabel.setId("scoreLabel");
        scoreLabel.getStylesheets().add(css);
        scoreLabel.setFont(new Font("Arial", 20));
        scoreLabel.setVisible(true);
    }

    public void scaleToFullscreen(boolean fullScreen, Stage primaryStage, Obstacle obstacle, Snake snake, Coin coin) {
        primaryStage.setFullScreen(fullScreen);

        double xScaling = Screen.getPrimary().getVisualBounds().getHeight() / initY;
        double yScaling = Screen.getPrimary().getVisualBounds().getWidth() / initX;

        if (fullScreen && !fullsize) {
            if (xScaling < yScaling) {
                fullScale = xScaling;
            } else {
                fullScale = yScaling;
            }
            gridCanvas.setWidth(gridCanvas.getWidth() * fullScale);
            gridCanvas.setHeight(gridCanvas.getHeight() * fullScale);
            fullsize = true;
        } else if (!fullScreen) {
            fullScale = 1;
            gridCanvas.setWidth(initX);
            gridCanvas.setHeight(initY);
            fullsize = false;
        }

        gameOverLabel.setFont(new Font("Arial", 25 * fullScale));
        gameWonLabel.setFont(new Font("Arial", 25 * fullScale));
        gameOverLabel.setStyle(fullScreen ? LARGE_LABEL_STYLE : SMALL_LABEL_STYLE);
        gameWonLabel.setStyle(fullScreen ? LARGE_LABEL_STYLE : SMALL_LABEL_STYLE);
        scoreLabel.setStyle(fullScreen ? LARGE_SCORE_STYLE : SMALL_LABEL_STYLE);
        drawGrid(coin, snake, obstacle);
    }

    public void drawGrid(Coin coin, Snake snake, Obstacle obstacle) {
        gc.setFill(game.getColor());
        gc.fillRect(0, 0, gridCanvas.getWidth(), gridCanvas.getHeight());

        gc.drawImage(coin.getImage(), (coin.getPosition().x * game.getCellSize()) * fullScale, (coin.getPosition().y * game.getCellSize()) * fullScale, game.getCellSize() * fullScale - 3 * fullScale, game.getCellSize() * fullScale - 3 * fullScale);

        int bodyIndex = 0;
        for (Point point : snake.getBody()) {
            drawBody(snake, point, bodyIndex);
            bodyIndex++;
        }

        drawHead(snake);

        gc.setFill(obstacle.getColor());
        for (Point point : obstacle.getObstacles()) {
            gc.fillRect(point.x * game.getCellSize() * fullScale, point.y * game.getCellSize() * fullScale, game.getCellSize() * fullScale, game.getCellSize() * fullScale);
        }
    }



    private void drawHead(Snake snake) {
        snake.getImageViewHead().setRotate(snake.getDirection().getAngle());
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        Image rotatedImage = snake.getImageViewHead().snapshot(params, null);
        gc.drawImage(rotatedImage, (snake.getHead().x * game.getCellSize()) * fullScale, (snake.getHead().y * game.getCellSize()) * fullScale, game.getCellSize() * fullScale, game.getCellSize() * fullScale);
    }

    private void drawBody(Snake snake, Point point, int index) {
        Image rotatedImage;
        if (snake.getBody().size() <= 1) {
            snake.getImageViewTail().setRotate(snake.getDirection().getAngle());
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            rotatedImage = snake.getImageViewTail().snapshot(params, null);
        } else if (index == 0) {
            int rotateTail;
            if (snake.getBody().get(1).getX() == snake.getBody().get(0).getX() && snake.getBody().get(1).getY() == snake.getBody().get(0).getY() - 1) {
                rotateTail = 0;
            } else if (snake.getBody().get(1).getX() == snake.getBody().get(0).getX() && snake.getBody().get(1).getY() == snake.getBody().get(0).getY() + 1) {
                rotateTail = 180;
            } else if (snake.getBody().get(1).getX() == snake.getBody().get(0).getX() - 1 && snake.getBody().get(1).getY() == snake.getBody().get(0).getY()) {
                rotateTail = 270;
            } else {
                rotateTail = 90;
            }
            snake.getImageViewTail().setRotate(rotateTail);
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            rotatedImage = snake.getImageViewTail().snapshot(params, null);
        } else if (index > 0 && (turnArray[(int) snake.getBody().get(index).getX()][(int) snake.getBody().get(index).getY()] != 0)) {
            snake.getImageViewTurn().setRotate(90 * turnArray[(int) snake.getBody().get(index).getX()][(int) snake.getBody().get(index).getY()]);
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            rotatedImage = snake.getImageViewTurn().snapshot(params, null);
        } else {
            snake.getImageViewBody().setRotate(snake.getBodyDir(index));
            SnapshotParameters params = new SnapshotParameters();
            params.setFill(Color.TRANSPARENT);
            rotatedImage = snake.getImageViewBody().snapshot(params, null);
        }

        if (turnArray[(int) snake.getBody().get(0).getX()][(int) snake.getBody().get(0).getY()] != 0) {
            turnArray[(int) snake.getBody().get(0).getX()][(int) snake.getBody().get(0).getY()] = 0;
        }

        gc.drawImage(rotatedImage, (point.x * game.getCellSize()) * fullScale, (point.y * game.getCellSize()) * fullScale, game.getCellSize() * fullScale, game.getCellSize() * fullScale);
    }

    public void turnDir(Snake snake, Direction last, Direction current) {
        int turn;
        if ((last == Direction.LEFT && current == Direction.UP) || (last == Direction.DOWN && current == Direction.RIGHT)) {
            turn = 2;
        } else if ((last == Direction.LEFT && current == Direction.DOWN) || (last == Direction.UP && current == Direction.RIGHT)) {
            turn = 3;
        } else if ((last == Direction.RIGHT && current == Direction.UP) || (last == Direction.DOWN && current == Direction.LEFT)) {
            turn = 1;
        } else if ((last == Direction.UP && current == Direction.LEFT) || (last == Direction.RIGHT && current == Direction.DOWN)) {
            turn = 4;
        } else {
            turn = 0;
        }
        turnArray[(int) snake.getHead().getX()][(int) snake.getHead().getY()] = turn;
    }

    public void gameOver(Snake snake) {
        if (snake.isDead() && !gameOverVis) {
            gameOverVis = true;
            gameOverLabel.setVisible(true);
        } else if (!snake.isDead() && gameOverVis) {
            gameOverLabel.setVisible(false);
            gameOverVis = false;
        }
    }

    public void gameWon() {
        if (game.gameWon() && !gameWonVis) {
            gameWonVis = true;
            gameWonLabel.setVisible(true);
        } else if (!game.gameWon() && gameWonVis) {
            gameWonVis = false;
            gameWonLabel.setVisible(false);
        }
    }

    public void updateScoreLabel() {
        scoreLabel.setText("Score: " + game.getScore());
    }

    public Canvas getCanvas() {
        return gridCanvas;
    }

    public GraphicsContext getGraphicsContext() {
        return gc;
    }

    public Label getGameOverLabel() {
        return gameOverLabel;
    }

    public Label getGameWonLabel() {
        return gameWonLabel;
    }

    public Label getScoreLabel() {
        return scoreLabel;
    }

}
