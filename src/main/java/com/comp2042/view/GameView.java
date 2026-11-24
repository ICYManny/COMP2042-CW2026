package com.comp2042.view;

import com.comp2042.model.Direction;
import com.comp2042.model.Effect;
import com.comp2042.model.GameModel;
import com.comp2042.model.GameState;
import com.comp2042.model.Position;
import com.comp2042.model.Snake;
import com.comp2042.model.WallMode;
import com.comp2042.model.coin.Coin;
import com.comp2042.model.level.Level;
import com.comp2042.util.ResourceLoader;

import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.stage.Screen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Draws a {@link GameModel} onto a canvas, with the score and end-of-game labels on top.
 * The view only reads the model; it never changes game state. It is redrawn every frame
 * of the game loop, and draws the snake between cells for smooth movement.
 */
public class GameView {

    private static final Color BACKGROUND_COLOR = Color.web("feffe4");
    private static final Color OBSTACLE_COLOR = Color.BLACK;
    private static final Color LEVEL_WALL_COLOR = Color.web("6d4c41");
    private static final Color LEVEL_WALL_HIGHLIGHT = Color.web("8d6e63");
    /** Inset of the lighter centre of a level wall block, as a fraction of a cell. */
    private static final double WALL_BEVEL = 0.18;
    private static final Color WALL_COLOR = Color.web("8b1e1e");
    /** Thickness of a solid wall as a fraction of a cell. */
    private static final double WALL_THICKNESS = 0.15;
    private static final int LARGE_GRID_HEIGHT = 50;
    private static final double SMALL_CELL_SIZE = 10;
    private static final double LARGE_CELL_SIZE = 20;
    private static final double COIN_MARGIN = 3;

    /** Height of the status bar under the board, in pixels. */
    private static final double HUD_HEIGHT = 44;
    private static final String HUD_STYLE = "-fx-background-color: #2e3b2f;";

    private static final String HEAD_SPRITE = "snakehead";
    private static final Color BODY_OUTLINE_COLOR = Color.BLACK;
    private static final Color BODY_COLOR = Color.rgb(0, 128, 0);
    private static final Color BODY_STRIPE_COLOR = Color.rgb(0, 255, 0);
    /** Widths as a fraction of a cell, matching the original body sprite. */
    private static final double BODY_OUTLINE_WIDTH = 0.70;
    private static final double BODY_WIDTH = 0.58;
    private static final double BODY_STRIPE_WIDTH = 0.08;
    /** Points further apart than this (in cells) are on opposite sides of a wrapped border. */
    private static final double MAX_JOINT_GAP = 1.01;
    private static final double GHOST_ALPHA = 0.45;

    private final GameModel model;
    private final SpriteManager sprites = new SpriteManager();
    private final double baseCellSize;
    private double cellSize;

    private final BorderPane root = new BorderPane();
    private final StackPane board = new StackPane();
    private final Canvas canvas;
    private final GraphicsContext gc;
    private final Label scoreLabel = new Label();
    private final Label effectsLabel = new Label();
    private final OverlayPane overlay = new OverlayPane();
    private GameState shownState;
    private int shownScore = -1;
    private int shownLevel = -1;

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
        scoreLabel.setId("scoreLabel");
        effectsLabel.setId("effectsLabel");
        board.getChildren().addAll(canvas, overlay.getNode());
        StackPane.setAlignment(overlay.getNode(), Pos.CENTER);

        // the status bar sits under the board so text never hides the snake or coins
        VBox hud = new VBox(2, scoreLabel, effectsLabel);
        hud.setAlignment(Pos.CENTER);
        hud.setStyle(HUD_STYLE);
        hud.setPrefHeight(HUD_HEIGHT);
        hud.setMinSize(0, HUD_HEIGHT);

        root.setCenter(board);
        root.setBottom(hud);
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

    /** @return window height needed to show the whole grid and the status bar */
    public double getPreferredHeight() {
        return boardHeight() + HUD_HEIGHT;
    }

    private double boardWidth() {
        return model.getWidth() * baseCellSize;
    }

    private double boardHeight() {
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
            double xScale = Screen.getPrimary().getBounds().getWidth() / boardWidth();
            double yScale = (Screen.getPrimary().getBounds().getHeight() - HUD_HEIGHT) / boardHeight();
            scale = Math.min(xScale, yScale);
        }
        cellSize = baseCellSize * scale;
        canvas.setWidth(boardWidth() * scale);
        canvas.setHeight(boardHeight() * scale);
        render();
    }

    /** Redraws the board and updates the labels from the current model state. */
    public void render() {
        gc.setFill(BACKGROUND_COLOR);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        drawCoin(model.getCoin());
        drawSnake(model.getSnake(), model.getStepProgress());
        drawObstacles();
        drawWalls();

        scoreLabel.setText(describeProgress());
        effectsLabel.setText(describeEffects());
        updateOverlay();
    }

    /** @return active effects and their remaining whole seconds, e.g. "Ghost 4s   x2 Points 9s" */
    private String describeEffects() {
        StringBuilder text = new StringBuilder();
        for (Map.Entry<Effect, Double> entry : model.getActiveEffects().entrySet()) {
            if (!text.isEmpty()) {
                text.append("   ");
            }
            text.append(entry.getKey().getDisplayName()).append(' ').append((int) Math.ceil(entry.getValue())).append('s');
        }
        return text.toString();
    }

    /** @return e.g. "Level 2/5   Score: 45   Coins: 3/10", or just the score in an endless game */
    private String describeProgress() {
        Level level = model.getLevel();
        if (level.isEndless()) {
            return "Score: " + model.getScore();
        }
        return "Level " + level.number() + "/" + model.getLevelCount() + "   Score: " + model.getScore()
                + "   Coins: " + model.getCoinsThisLevel() + "/" + level.coinsToClear();
    }

    /** Updates the overlay only when the state, score or level changed, not on every frame. */
    private void updateOverlay() {
        if (model.getState() != shownState || model.getScore() != shownScore
                || model.getLevel().number() != shownLevel) {
            shownState = model.getState();
            shownScore = model.getScore();
            shownLevel = model.getLevel().number();
            overlay.show(model);
        }
    }

    /**
     * Sets what the overlay buttons do.
     *
     * @param onRetry     starts a new game
     * @param onResume    resumes a paused game
     * @param onNextLevel starts the next level
     * @param onMenu      returns to the main menu
     */
    public void setOverlayActions(Runnable onRetry, Runnable onResume, Runnable onNextLevel, Runnable onMenu) {
        overlay.setActions(onRetry, onResume, onNextLevel, onMenu);
    }

    private void drawCoin(Coin coin) {
        if (coin == null) {
            return;
        }
        Position p = coin.getPosition();
        double size = cellSize - COIN_MARGIN * (cellSize / baseCellSize);
        gc.drawImage(sprites.get(coin.getSpriteKey()), p.x() * cellSize, p.y() * cellSize, size, size);
    }

    /**
     * Draws a red line along every solid edge, so the player can see which walls kill and
     * which ones wrap round. The lines disappear while a wall-passing effect is active.
     */
    private void drawWalls() {
        WallMode walls = model.getWallMode();
        double thickness = Math.max(2, cellSize * WALL_THICKNESS);
        double width = model.getWidth() * cellSize;
        double height = model.getHeight() * cellSize;
        gc.setFill(WALL_COLOR);
        if (!walls.wrapsVertically()) {
            gc.fillRect(0, 0, width, thickness);
            gc.fillRect(0, height - thickness, width, thickness);
        }
        if (!walls.wrapsHorizontally()) {
            gc.fillRect(0, 0, thickness, height);
            gc.fillRect(width - thickness, 0, thickness, height);
        }
    }

    /** Level walls are drawn as brown blocks, obstacles spawned by the silver coin as black squares. */
    private void drawObstacles() {
        double inset = cellSize * WALL_BEVEL;
        for (Position p : model.getObstacles().getWalls()) {
            gc.setFill(LEVEL_WALL_COLOR);
            gc.fillRect(p.x() * cellSize, p.y() * cellSize, cellSize, cellSize);
            gc.setFill(LEVEL_WALL_HIGHLIGHT);
            gc.fillRect(p.x() * cellSize + inset, p.y() * cellSize + inset, cellSize - 2 * inset, cellSize - 2 * inset);
        }
        gc.setFill(OBSTACLE_COLOR);
        for (Position p : model.getObstacles().getSpawned()) {
            gc.fillRect(p.x() * cellSize, p.y() * cellSize, cellSize, cellSize);
        }
    }

    /**
     * Draws the snake part way between cells so it glides instead of jumping.
     * <p>
     * The head is drawn {@code progress} of the way from the cell it came from to the cell
     * it is in, and the tail the same fraction of the way from where it was to where it is.
     * Every segment in between is a joint of one continuous stroke, so corners stay smooth
     * and the snake keeps a constant length while it moves.
     */
    private void drawSnake(Snake snake, double progress) {
        List<Position> segments = snake.getSegments();
        Direction heading = snake.getDirection();
        Position head = segments.get(0);

        List<double[]> path = new ArrayList<>();
        double[] headPoint = interpolate(head.step(heading.opposite()), head, progress);
        path.add(headPoint);
        for (int i = 1; i < segments.size(); i++) {
            Position segment = segments.get(i);
            path.add(new double[] {segment.x(), segment.y()});
        }
        Position tail = snake.getTail();
        Position previousTail = snake.getPreviousTail();
        if (!previousTail.equals(tail)) {
            Position tailFrom = tail.step(directionBetween(tail, previousTail));
            path.add(interpolate(tailFrom, tail, progress));
        }

        // a ghost snake is drawn see-through
        gc.setGlobalAlpha(model.isEffectActive(Effect.GHOST) ? GHOST_ALPHA : 1);
        strokeBody(path, BODY_OUTLINE_WIDTH, BODY_OUTLINE_COLOR);
        strokeBody(path, BODY_WIDTH, BODY_COLOR);
        strokeBody(path, BODY_STRIPE_WIDTH, BODY_STRIPE_COLOR);
        drawRotated(sprites.get(HEAD_SPRITE), headPoint[0], headPoint[1], heading.getAngle());
        gc.setGlobalAlpha(1);
    }

    /**
     * Linear interpolation between two cells. Positions are not wrapped, so a snake
     * entering from the opposite edge slides in from just outside the board.
     */
    private double[] interpolate(Position from, Position to, double progress) {
        return new double[] {
            from.x() + (to.x() - from.x()) * progress,
            from.y() + (to.y() - from.y()) * progress
        };
    }

    /**
     * Strokes a path through cell centres. Where two consecutive points are more than one
     * cell apart the snake has wrapped through a border, so the line is broken there
     * rather than drawn across the whole board.
     */
    private void strokeBody(List<double[]> path, double widthInCells, Color color) {
        gc.setStroke(color);
        gc.setFill(color);
        gc.setLineWidth(widthInCells * cellSize);
        gc.setLineCap(StrokeLineCap.ROUND);
        gc.setLineJoin(StrokeLineJoin.ROUND);
        int runStart = 0;
        for (int i = 1; i <= path.size(); i++) {
            boolean endOfRun = i == path.size() || gap(path.get(i - 1), path.get(i)) > MAX_JOINT_GAP;
            if (endOfRun) {
                strokeRun(path, runStart, i, widthInCells);
                runStart = i;
            }
        }
    }

    private void strokeRun(List<double[]> path, int from, int to, double widthInCells) {
        if (to - from == 1) {
            double diameter = widthInCells * cellSize;
            double[] p = path.get(from);
            gc.fillOval(toPixel(p[0]) - diameter / 2, toPixel(p[1]) - diameter / 2, diameter, diameter);
            return;
        }
        gc.beginPath();
        gc.moveTo(toPixel(path.get(from)[0]), toPixel(path.get(from)[1]));
        for (int i = from + 1; i < to; i++) {
            gc.lineTo(toPixel(path.get(i)[0]), toPixel(path.get(i)[1]));
        }
        gc.stroke();
    }

    private double gap(double[] a, double[] b) {
        return Math.abs(a[0] - b[0]) + Math.abs(a[1] - b[1]);
    }

    /** Converts a cell coordinate to the pixel at the centre of that cell. */
    private double toPixel(double cell) {
        return cell * cellSize + cellSize / 2;
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

    /** Draws a sprite centred on a (possibly fractional) cell, rotated by transforming the canvas. */
    private void drawRotated(Image image, double cellX, double cellY, double angle) {
        double half = cellSize / 2;
        gc.save();
        gc.translate(toPixel(cellX), toPixel(cellY));
        gc.rotate(angle);
        gc.drawImage(image, -half, -half, cellSize, cellSize);
        gc.restore();
    }
}
