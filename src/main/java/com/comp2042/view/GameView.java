package com.comp2042.view;

import com.comp2042.util.ResourceLoader;
import com.comp2042.model.Direction;
import com.comp2042.model.Game;
import com.comp2042.model.Obstacle;
import com.comp2042.model.Snake;
import com.comp2042.model.coin.Coin;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameView {

    private static final String LARGE_LABEL_STYLE = "-fx-font-size: 50;";
    private static final String LARGE_SCORE_STYLE = "-fx-font-size: 35;";
    private static final String SMALL_LABEL_STYLE = "-fx-font-size: 15;";
    private static final String HEAD_SPRITE = "snakehead";
    private static final String BODY_SPRITE = "snakebody";
    private static final String TURN_SPRITE = "snaketurnleft";
    private static final String TAIL_SPRITE = "snaketail";
    private static final double VERTICAL_ANGLE = 0;
    private static final double HORIZONTAL_ANGLE = 270;

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
    private final SpriteManager sprites = new SpriteManager();

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
        String css = ResourceLoader.externalForm("/views/styling.css");
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

        gc.drawImage(sprites.get(coin.getSpriteKey()), (coin.getPosition().x * game.getCellSize()) * fullScale, (coin.getPosition().y * game.getCellSize()) * fullScale, game.getCellSize() * fullScale - 3 * fullScale, game.getCellSize() * fullScale - 3 * fullScale);

        drawBody(snake);
        drawHead(snake);

        gc.setFill(obstacle.getColor());
        for (Point point : obstacle.getObstacles()) {
            gc.fillRect(point.x * game.getCellSize() * fullScale, point.y * game.getCellSize() * fullScale, game.getCellSize() * fullScale, game.getCellSize() * fullScale);
        }
    }



    private void drawHead(Snake snake) {
        double size = game.getCellSize() * fullScale;
        Point head = snake.getHead();
        drawRotated(sprites.get(HEAD_SPRITE), head.x * size, head.y * size, size, snake.getDirection().getAngle());
    }

    /**
     * Draws every body segment, choosing a straight, corner or tail sprite from the
     * positions of the neighbouring segments. Working this out from the snake itself
     * replaces the old turnArray, which remembered turns separately and was never
     * cleared on reset, leaving stray corner sprites behind.
     */
    private void drawBody(Snake snake) {
        double size = game.getCellSize() * fullScale;
        List<Point> segments = new ArrayList<>();
        segments.add(snake.getHead());
        List<Point> body = snake.getBody();
        Collections.reverse(body);
        segments.addAll(body);

        for (int i = 1; i < segments.size(); i++) {
            Point segment = segments.get(i);
            Direction towardsHead = directionBetween(segment, segments.get(i - 1));
            String sprite;
            double angle;
            if (i == segments.size() - 1) {
                sprite = TAIL_SPRITE;
                angle = towardsHead.getAngle();
            } else {
                Direction towardsTail = directionBetween(segment, segments.get(i + 1));
                if (towardsHead.isOpposite(towardsTail)) {
                    sprite = BODY_SPRITE;
                    angle = towardsHead.getDx() == 0 ? VERTICAL_ANGLE : HORIZONTAL_ANGLE;
                } else {
                    sprite = TURN_SPRITE;
                    angle = cornerAngle(towardsHead, towardsTail);
                }
            }
            drawRotated(sprites.get(sprite), segment.x * size, segment.y * size, size, angle);
        }
    }

    /**
     * The corner sprite, unrotated, joins the bottom and left edges of a cell. Each
     * quarter turn clockwise moves the joined edges round by one side.
     */
    private double cornerAngle(Direction a, Direction b) {
        if (isPair(a, b, Direction.DOWN, Direction.LEFT)) {
            return 0;
        } else if (isPair(a, b, Direction.LEFT, Direction.UP)) {
            return 90;
        } else if (isPair(a, b, Direction.UP, Direction.RIGHT)) {
            return 180;
        }
        return 270;
    }

    private boolean isPair(Direction a, Direction b, Direction first, Direction second) {
        return (a == first && b == second) || (a == second && b == first);
    }

    /**
     * Direction from one segment to an adjacent one. Neighbours more than one cell apart
     * are touching through a wrapped border, so the step is reversed.
     */
    private Direction directionBetween(Point from, Point to) {
        int dx = Integer.signum(to.x - from.x);
        int dy = Integer.signum(to.y - from.y);
        if (Math.abs(to.x - from.x) > 1) {
            dx = -dx;
        }
        if (Math.abs(to.y - from.y) > 1) {
            dy = -dy;
        }
        for (Direction d : Direction.values()) {
            if (d.getDx() == dx && d.getDy() == dy) {
                return d;
            }
        }
        return Direction.UP;
    }

    /**
     * Draws an image rotated about its centre by transforming the canvas, instead of
     * rotating an ImageView and taking a snapshot for every segment on every frame.
     */
    private void drawRotated(Image image, double x, double y, double size, double angle) {
        gc.save();
        gc.translate(x + size / 2, y + size / 2);
        gc.rotate(angle);
        gc.drawImage(image, -size / 2, -size / 2, size, size);
        gc.restore();
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
