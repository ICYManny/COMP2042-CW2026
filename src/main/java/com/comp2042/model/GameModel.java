package com.comp2042.model;

import com.comp2042.model.coin.BasicCoin;
import com.comp2042.model.coin.BlueCoin;
import com.comp2042.model.coin.Coin;
import com.comp2042.model.coin.GoldCoin;
import com.comp2042.model.coin.RainbowCoin;
import com.comp2042.model.coin.SilverCoin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Holds the whole state of one game and applies its rules: moving the snake, walls,
 * obstacles, eating coins, scoring, winning and losing.
 * <p>
 * Previously these rules were split between Snake (collisions), Game (score) and the
 * Timeline lambda inside GameController (eating, spawning coins, power-up effects). Keeping
 * them in one model class means the controller only forwards input and asks for a step,
 * the view only draws, and the rules can be unit tested without JavaFX.
 */
public class GameModel {

    /** The snake starts with two segments, so two cells can never hold a coin. */
    private static final int STARTING_LENGTH = 2;
    private static final int SPECIAL_COIN_ODDS = 12;
    private static final int OBSTACLE_DENSITY = 20;
    private static final int MIN_NEW_OBSTACLES = 3;

    private final GameSettings settings;
    private final Random random;
    private final Obstacles obstacles;
    private final List<GameListener> listeners = new ArrayList<>();
    private Snake snake;
    private Coin coin;
    private int score;
    private int coinsEaten;
    private int maxCoins;
    private double speed;
    private boolean ignoreBorders;
    private boolean dead;

    /**
     * Creates a new game, ready to play.
     *
     * @param settings the options chosen in the main menu
     */
    public GameModel(GameSettings settings) {
        this(settings, new Random());
    }

    /**
     * Creates a new game using a given random generator, so tests can make coin and
     * obstacle placement repeatable.
     *
     * @param settings the options chosen in the main menu
     * @param random   source of randomness
     */
    public GameModel(GameSettings settings, Random random) {
        this.settings = settings;
        this.random = random;
        this.obstacles = new Obstacles(settings.width(), settings.height());
        reset();
    }

    /**
     * Registers a listener to be told about coins eaten and the game ending.
     *
     * @param listener the listener to add
     */
    public void addListener(GameListener listener) {
        listeners.add(listener);
    }

    /** Puts the game back to its starting state. */
    public void reset() {
        obstacles.clear();
        snake = new Snake(new Position(settings.width() / 2, settings.height() / 2), Direction.LEFT, STARTING_LENGTH);
        coin = new BasicCoin(settings.width(), settings.height(), snake, obstacles);
        score = 0;
        coinsEaten = 0;
        maxCoins = settings.width() * settings.height() - STARTING_LENGTH;
        speed = settings.difficulty().getSpeed();
        ignoreBorders = false;
        dead = false;
    }

    /**
     * Asks the snake to turn. Ignored once the game has ended.
     *
     * @param direction requested direction
     * @return true if the turn was accepted
     */
    public boolean turn(Direction direction) {
        if (isOver()) {
            return false;
        }
        return snake.turn(direction);
    }

    /** Moves the snake one cell and applies the consequences. Does nothing once the game is over. */
    public void tick() {
        if (isOver()) {
            return;
        }
        Position next = snake.nextHead();
        if (!next.isInside(settings.width(), settings.height())) {
            if (!wrapsAtBorders()) {
                die();
                return;
            }
            next = next.wrap(settings.width(), settings.height());
        }
        boolean eating = next.equals(coin.getPosition());
        if (obstacles.contains(next) || snake.collidesWithBody(next, eating)) {
            die();
            return;
        }
        snake.moveTo(next, eating);
        if (eating) {
            eatCoin();
        }
    }

    private void die() {
        dead = true;
        listeners.forEach(listener -> listener.onGameOver(false));
    }

    private void eatCoin() {
        Coin eaten = coin;
        speed = settings.difficulty().getSpeed();
        ignoreBorders = false;
        coin.applyEffect(this);
        score += coin.getPoints();
        coinsEaten++;
        listeners.forEach(listener -> listener.onCoinEaten(eaten));
        if (isWon()) {
            listeners.forEach(listener -> listener.onGameOver(true));
        } else {
            coin = nextCoin();
        }
    }

    private Coin nextCoin() {
        int w = settings.width();
        int h = settings.height();
        if (!settings.powerUpsEnabled()) {
            return new BasicCoin(w, h, snake, obstacles);
        }
        int roll = random.nextInt(SPECIAL_COIN_ODDS);
        if (roll == 1) {
            return new GoldCoin(w, h, snake, obstacles);
        } else if (roll == 2) {
            return new RainbowCoin(w, h, snake, obstacles);
        } else if (roll == 3) {
            return new BlueCoin(w, h, snake, obstacles);
        } else if (roll == 4 && obstacles.count() == 0) {
            return new SilverCoin(w, h, snake, obstacles);
        }
        return new BasicCoin(w, h, snake, obstacles);
    }

    /**
     * Makes the snake faster until the next coin is eaten. Used by coin effects.
     *
     * @param bonus extra moves per second
     */
    public void boostSpeed(double bonus) {
        speed += bonus;
    }

    /**
     * Lets the snake wrap through the outer walls until the next coin is eaten.
     * Used by coin effects.
     *
     * @param ignore true to let the snake pass through walls
     */
    public void setIgnoreBorders(boolean ignore) {
        ignoreBorders = ignore;
    }

    /** Adds a random batch of obstacles to the board. Used by coin effects. */
    public void spawnObstacles() {
        obstacles.spawn(random.nextInt(settings.width() * settings.height() / OBSTACLE_DENSITY) + MIN_NEW_OBSTACLES,
                snake, coin);
        maxCoins -= obstacles.count() - 1;
    }

    private boolean wrapsAtBorders() {
        return ignoreBorders || settings.difficulty().wrapsAtBorders();
    }

    /** @return true if the player has won or lost */
    public boolean isOver() {
        return dead || isWon();
    }

    /** @return true if the snake has crashed */
    public boolean isDead() {
        return dead;
    }

    /** @return true if every free cell has been filled */
    public boolean isWon() {
        return coinsEaten >= maxCoins;
    }

    /** @return the snake */
    public Snake getSnake() {
        return snake;
    }

    /** @return the coin currently on the board */
    public Coin getCoin() {
        return coin;
    }

    /** @return the obstacles on the board */
    public Obstacles getObstacles() {
        return obstacles;
    }

    /** @return points scored so far */
    public int getScore() {
        return score;
    }

    /** @return current snake speed in moves per second */
    public double getSpeed() {
        return speed;
    }

    /** @return the settings this game was started with */
    public GameSettings getSettings() {
        return settings;
    }

    /** @return grid width in cells */
    public int getWidth() {
        return settings.width();
    }

    /** @return grid height in cells */
    public int getHeight() {
        return settings.height();
    }
}
