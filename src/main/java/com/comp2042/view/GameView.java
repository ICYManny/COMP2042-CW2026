package com.comp2042.view;

import com.comp2042.model.Direction;
import com.comp2042.model.GameModel;
import com.comp2042.model.Position;
import com.comp2042.model.coin.Coin;
import com.comp2042.util.ResourceLoader;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;

import java.util.List;

/**
 * Draws a {@link GameModel} onto a canvas, with the score and end-of-game labels on top.
 * The view only reads the model; it never changes game state.
 */
public class GameView {

    private static final Color BACKGROUND_COLOR = Color.web("feffe4");
    private static final Color OBSTACLE_COLOR = Color.BLACK;
    private static final int LARGE_GRID_HEIGHT = 50;
    private static final double SMALL_CELL_SIZE = 10;
    private static final double LARGE_CELL_SIZE = 20;
    private static final double COIN_MARGIN = 3;

    private static final String LARGE_LABEL_STYLE = "-fx-font-size: 50;";
    private static final String LARGE_SCORE_STYLE = "-fx-font-size: 35;";
    private static final String SMALL_LABEL_STYLE = "-fx-font-size: 15;";

    private static final String HEAD_SPRITE = "snakehead";
    private static final String BODY_SPRITE = "snakebody";
    private static final String TURN_SPRITE = "snaketurnleft";
    private static final String TAIL_SPRITE = "snaketail";
    private static final double VERTICAL_ANGLE = 0;
    private static final double HORIZONTAL_ANGLE = 270;

    private final GameModel model;
    private final SpriteManager sprites = new SpriteManager();
    private final double baseCellSize;
    private double cellSize;

    private final StackPane root = new StackPane();
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final Label gameOverLabel = new Label("GAME OVER");
    private final Label gameWonLabel = new Label("YOU WON!");
    private final Label scoreLabel = new Label();

    /**
     * Builds the game screen for a model.
     *
     * @param model the game to draw
     */
    public GameView(GameModel model) {
        this.model = model;
        baseCellSize = model.getHeight() > LARGE_GRID_HEIGHT ? SMALL_CELL_SIZE : LARGE_CELL_SIZE;
        cellSize = baseCellSize;
        canvas = new Canvas(getPreferredWidth(), getPreferredHeight());
        gc = canvas.getGraphicsContext2D();

        String css = ResourceLoader.externalForm("/views/styling.css");
        root.getStylesheets().add(css);
        gameOverLabel.setId("gameOverLabel");
        gameWonLabel.setId("gameWonLabel");
        scoreLabel.setId("scoreLabel");
        root.getChildren().addAll(canvas, gameOverLabel, gameWonLabel, scoreLabel);
        StackPane.setAlignment(gameOverLabel, Pos.TOP_CENTER);
        StackPane.setAlignment(gameWonLabel, Pos.TOP_CENTER);
        StackPane.setAlignment(scoreLabel, Pos.BOTTOM_CENTER);
        render();
    }

    /** @return the node to place in the game scene */
    public Parent getRoot() {
        return root;
    }

    /** @return window width needed to show the whole grid */
    public double getPreferredWidth() {
        return model.getWidth() * baseCellSize;
    }

    /** @return window height needed to show the whole grid */
    public double getPreferredHeight() {
        return model.getHeight() * baseCellSize;
    }

    /**
     * Resizes the board to fill the screen, or returns it to its normal size.
     *
     * @param fullScreen true when the window is going fullscreen
     */
    public void setFullScreen(boolean fullScreen) {
        double scale = 1;
        if (fullScreen) {
            double xScale = Screen.getPrimary().getVisualBounds().getWidth() / getPreferredWidth();
            double yScale = Screen.getPrimary().getVisualBounds().getHeight() / getPreferredHeight();
            scale = Math.min(xScale, yScale);
        }
        cellSize = baseCellSize * scale;
        canvas.setWidth(getPreferredWidth() * scale);
        canvas.setHeight(getPreferredHeight() * scale);
        gameOverLabel.setStyle(fullScreen ? LARGE_LABEL_STYLE : SMALL_LABEL_STYLE);
        gameWonLabel.setStyle(fullScreen ? LARGE_LABEL_STYLE : SMALL_LABEL_STYLE);
        scoreLabel.setStyle(fullScreen ? LARGE_SCORE_STYLE : SMALL_LABEL_STYLE);
        render();
    }

    /** Redraws the board and updates the labels from the current model state. */
    public void render() {
        gc.setFill(BACKGROUND_COLOR);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        drawCoin(model.getCoin());
        drawSnake(model.getSnake().getSegments(), model.getSnake().getDirection());
        drawObstacles();

        scoreLabel.setText("Score: " + model.getScore());
        gameOverLabel.setVisible(model.isDead());
        gameWonLabel.setVisible(model.isWon());
    }

    private void drawCoin(Coin coin) {
        Position p = coin.getPosition();
        double size = cellSize - COIN_MARGIN * (cellSize / baseCellSize);
        gc.drawImage(sprites.get(coin.getSpriteKey()), p.x() * cellSize, p.y() * cellSize, size, size);
    }

    private void drawObstacles() {
        gc.setFill(OBSTACLE_COLOR);
        for (Position p : model.getObstacles().getPositions()) {
            gc.fillRect(p.x() * cellSize, p.y() * cellSize, cellSize, cellSize);
        }
    }

    /**
     * Draws the snake, choosing a straight, corner or tail sprite for each segment from the
     * positions of its neighbours.
     */
    private void drawSnake(List<Position> segments, Direction heading) {
        for (int i = 1; i < segments.size(); i++) {
            Position segment = segments.get(i);
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
            drawRotated(sprites.get(sprite), segment, angle);
        }
        drawRotated(sprites.get(HEAD_SPRITE), segments.get(0), heading.getAngle());
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
    private Direction directionBetween(Position from, Position to) {
        int dx = Integer.signum(to.x() - from.x());
        int dy = Integer.signum(to.y() - from.y());
        if (Math.abs(to.x() - from.x()) > 1) {
            dx = -dx;
        }
        if (Math.abs(to.y() - from.y()) > 1) {
            dy = -dy;
        }
        for (Direction d : Direction.values()) {
            if (d.getDx() == dx && d.getDy() == dy) {
                return d;
            }
        }
        return Direction.UP;
    }

    /** Draws an image rotated about the centre of a cell by transforming the canvas. */
    private void drawRotated(Image image, Position cell, double angle) {
        double half = cellSize / 2;
        gc.save();
        gc.translate(cell.x() * cellSize + half, cell.y() * cellSize + half);
        gc.rotate(angle);
        gc.drawImage(image, -half, -half, cellSize, cellSize);
        gc.restore();
    }
}
