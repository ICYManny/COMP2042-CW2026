package com.comp2042.model;

import com.comp2042.model.coin.BasicCoin;
import com.comp2042.model.coin.Coin;
import com.comp2042.model.coin.CoinFactory;
import com.comp2042.model.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

    private static final int STARTING_LENGTH = 2;
    /** New obstacles never appear closer than this many steps to the head. */
    private static final int SAFE_DISTANCE = 3;
    private static final int OBSTACLE_DENSITY = 20;
    private static final int MIN_NEW_OBSTACLES = 3;
    /** Longest frame gap simulated at once, so a stall (e.g. dragging the window) cannot teleport the snake. */
    private static final double MAX_FRAME_TIME = 0.25;
    private static final double SLOW_FACTOR = 0.6;
    private static final double SPEED_BOOST_FACTOR = 1.5;
    private static final int DOUBLE_POINTS_FACTOR = 2;

    private final GameSettings settings;
    private final Random random;
    private final Obstacles obstacles = new Obstacles();
    private final FreeCellFinder freeCells;
    private final CoinFactory coinFactory;
    private final List<GameListener> listeners = new ArrayList<>();
    private final List<Level> levels;
    private int levelIndex;
    private int coinsThisLevel;
    private Snake snake;
    private Coin coin;
    private int score;
    private int coinsEaten;
    private final ActiveEffects effects = new ActiveEffects();
    private double timeSinceStep;
    private GameState state;

    /**
     * Creates an endless game (no walls, no level target).
     *
     * @param settings the options chosen in the main menu
     */
    public GameModel(GameSettings settings) {
        this(settings, new Random(), List.of(Level.endless()));
    }

    /**
     * Creates an endless game using a given random generator, so tests can make coin and
     * obstacle placement repeatable.
     *
     * @param settings the options chosen in the main menu
     * @param random   source of randomness
     */
    public GameModel(GameSettings settings, Random random) {
        this(settings, random, List.of(Level.endless()));
    }

    /**
     * Creates a game played through a list of levels.
     *
     * @param settings the options chosen in the main menu
     * @param levels   the levels, in order
     */
    public GameModel(GameSettings settings, List<Level> levels) {
        this(settings, new Random(), levels);
    }

    /**
     * Creates a game played through a list of levels with a given random generator.
     *
     * @param settings the options chosen in the main menu
     * @param random   source of randomness
     * @param levels   the levels, in order
     * @throws IllegalArgumentException if there are no levels, the start level does not
     *                                  exist, or a level's walls do not fit on the grid
     */
    public GameModel(GameSettings settings, Random random, List<Level> levels) {
        if (levels.isEmpty()) {
            throw new IllegalArgumentException("At least one level is needed");
        }
        if (settings.startLevel() > levels.size()) {
            throw new IllegalArgumentException("Start level " + settings.startLevel() + " does not exist");
        }
        for (Level level : levels) {
            for (Position wall : level.walls()) {
                if (!wall.isInside(settings.width(), settings.height())) {
                    throw new IllegalArgumentException(level + " does not fit on a "
                            + settings.width() + "x" + settings.height() + " grid");
                }
            }
        }
        this.settings = settings;
        this.random = random;
        this.levels = List.copyOf(levels);
        this.freeCells = new FreeCellFinder(settings.width(), settings.height(), random);
        this.coinFactory = new CoinFactory(random, settings.powerUpsEnabled());
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

    /** Puts the game back to its starting state: the starting level, with no score. */
    public void reset() {
        levelIndex = settings.startLevel() - 1;
        score = 0;
        coinsEaten = 0;
        loadLevel();
    }

    /** Moves on to the next level after one has been completed. Does nothing in other states. */
    public void nextLevel() {
        if (state == GameState.LEVEL_COMPLETE) {
            levelIndex++;
            loadLevel();
        }
    }

    /** Sets up the current level: its walls, a fresh snake and a first coin. The score is kept. */
    private void loadLevel() {
        obstacles.clear();
        obstacles.setWalls(getLevel().walls());
        snake = new Snake(new Position(settings.width() / 2, settings.height() / 2), Direction.LEFT, STARTING_LENGTH);
        coinsThisLevel = 0;
        effects.clear();
        timeSinceStep = 0;
        state = GameState.READY;
        coin = new BasicCoin(freeCells.find(this::isOccupied).orElseThrow());
    }

    /** Starts the game from READY, or resumes it from PAUSED. Does nothing in other states. */
    public void start() {
        if (state == GameState.READY || state == GameState.PAUSED) {
            state = GameState.PLAYING;
        }
    }

    /** Pauses a running game, or resumes a paused one. Does nothing in other states. */
    public void togglePause() {
        if (state == GameState.PLAYING) {
            state = GameState.PAUSED;
        } else if (state == GameState.PAUSED) {
            state = GameState.PLAYING;
        }
    }

    /**
     * Asks the snake to turn. Ignored once the game has ended or a level is complete.
     *
     * @param direction requested direction
     * @return true if the turn was accepted
     */
    public boolean turn(Direction direction) {
        if (!canMove()) {
            return false;
        }
        return snake.turn(direction);
    }

    /** {@return false once the game has ended or while a completed level is waiting} */
    private boolean canMove() {
        return !isOver() && state != GameState.LEVEL_COMPLETE;
    }

    /**
     * Advances the game by real time. The snake moves one cell every {@code 1 / speed}
     * seconds however often this is called, so drawing can run at a different rate from
     * the game logic (fixed time step).
     *
     * @param elapsedSeconds time since the previous update
     */
    public void update(double elapsedSeconds) {
        if (state != GameState.PLAYING || elapsedSeconds <= 0) {
            return;
        }
        double elapsed = Math.min(elapsedSeconds, MAX_FRAME_TIME);
        effects.update(elapsed);
        timeSinceStep += elapsed;
        while (state == GameState.PLAYING && timeSinceStep >= 1.0 / getSpeed()) {
            timeSinceStep -= 1.0 / getSpeed();
            tick();
        }
    }

    /**
     * How far the snake has travelled from its last cell towards its next one, used to
     * draw it between cells.
     *
     * @return a value from 0 (just arrived) to 1 (about to move); 1 once the game is over
     */
    public double getStepProgress() {
        if (isOver()) {
            return 1;
        }
        return Math.min(1, timeSinceStep * getSpeed());
    }

    /**
     * Moves the snake one cell and applies the consequences. Does nothing once the game is
     * over or while waiting for the next level.
     */
    public void tick() {
        if (!canMove()) {
            return;
        }
        Optional<Position> landing = getWallMode().resolve(snake.nextHead(), settings.width(), settings.height());
        if (landing.isEmpty()) {
            die();
            return;
        }
        Position next = landing.get();
        boolean eating = coin != null && next.equals(coin.getPosition());
        boolean hitsObstacle = obstacles.contains(next) && !effects.isActive(Effect.GHOST);
        if (hitsObstacle || snake.collidesWithBody(next, eating)) {
            die();
            return;
        }
        snake.moveTo(next, eating);
        if (eating) {
            eatCoin();
        }
    }

    private void die() {
        state = GameState.GAME_OVER;
        listeners.forEach(listener -> listener.onGameOver(false));
    }

    private void eatCoin() {
        Coin eaten = coin;
        coin.applyEffect(this);
        int multiplier = effects.isActive(Effect.DOUBLE_POINTS) ? DOUBLE_POINTS_FACTOR : 1;
        score += coin.getPoints() * multiplier;
        coinsEaten++;
        coinsThisLevel++;
        listeners.forEach(listener -> listener.onCoinEaten(eaten));
        if (!getLevel().isEndless() && coinsThisLevel >= getLevel().coinsToClear()) {
            completeLevel();
            return;
        }
        Optional<Position> cell = freeCells.find(this::isOccupied);
        if (cell.isEmpty()) {
            coin = null;
            state = GameState.WON;
            listeners.forEach(listener -> listener.onGameOver(true));
        } else {
            coin = coinFactory.create(cell.get(), obstacles.spawnedCount() > 0);
        }
    }

    private void completeLevel() {
        coin = null;
        if (hasNextLevel()) {
            state = GameState.LEVEL_COMPLETE;
            Level cleared = getLevel();
            listeners.forEach(listener -> listener.onLevelComplete(cleared));
        } else {
            state = GameState.WON;
            listeners.forEach(listener -> listener.onGameOver(true));
        }
    }

    /**
     * Returns true if the snake or an obstacle is on that cell.
     *
     * @param cell a grid cell
     * @return true if the snake or an obstacle is on that cell
     */
    private boolean isOccupied(Position cell) {
        return snake.occupies(cell) || obstacles.contains(cell);
    }

    /**
     * Replaces the coin on the board. Package-private so unit tests can put a coin exactly
     * where they need it instead of relying on random placement.
     *
     * @param newCoin the coin to place
     */
    void placeCoin(Coin newCoin) {
        coin = newCoin;
    }

    /**
     * Starts a timed effect, or restarts it if already active. Used by coin effects.
     *
     * @param effect the effect to start
     */
    public void activateEffect(Effect effect) {
        effects.activate(effect);
    }

    /**
     * Makes the snake shorter without changing the score. Used by coin effects.
     *
     * @param amount number of tail segments to remove
     */
    public void shrinkSnake(int amount) {
        snake.shrink(amount);
    }

    /**
     * Returns true if the effect is currently active.
     *
     * @param effect an effect
     * @return true if the effect is currently active
     */
    public boolean isEffectActive(Effect effect) {
        return effects.isActive(effect);
    }

    /** {@return every active effect and its remaining seconds, read-only} */
    public Map<Effect, Double> getActiveEffects() {
        return effects.getAll();
    }

    /** Adds a random batch of obstacles to the board. Used by coin effects. */
    public void spawnObstacles() {
        int amount = random.nextInt(settings.width() * settings.height() / OBSTACLE_DENSITY) + MIN_NEW_OBSTACLES;
        Position head = snake.getHead();
        obstacles.spawn(amount, freeCells, cell -> isOccupied(cell)
                || (coin != null && cell.equals(coin.getPosition()))
                || head.distanceTo(cell) < SAFE_DISTANCE);
    }

    /**
     * {@return the edge rule currently in force: the difficulty's rule, or wrapping on every
     *         side while a wall-passing effect is active}
     */
    public WallMode getWallMode() {
        return effects.isActive(Effect.GHOST) ? WallMode.WRAP_ALL : settings.difficulty().getWallMode();
    }

    /** {@return the current stage of the game} */
    public GameState getState() {
        return state;
    }

    /** {@return true if the player has won or lost} */
    public boolean isOver() {
        return state.isFinished();
    }

    /** {@return true if the snake has crashed} */
    public boolean isDead() {
        return state == GameState.GAME_OVER;
    }

    /** {@return true if the snake has filled every free cell} */
    public boolean isWon() {
        return state == GameState.WON;
    }

    /** {@return number of coins eaten so far} */
    public int getCoinsEaten() {
        return coinsEaten;
    }

    /** {@return the snake} */
    public Snake getSnake() {
        return snake;
    }

    /** {@return the coin currently on the board, or null once the game is won} */
    public Coin getCoin() {
        return coin;
    }

    /** {@return the obstacles on the board} */
    public Obstacles getObstacles() {
        return obstacles;
    }

    /** {@return points scored so far} */
    public int getScore() {
        return score;
    }

    /**
     * {@return current snake speed in moves per second: the difficulty's speed, times the
     *         level's speed multiplier, adjusted by any active Slow or Speed effect}
     */
    public double getSpeed() {
        double speed = settings.difficulty().getSpeed() * getLevel().speedMultiplier();
        if (effects.isActive(Effect.SLOW)) {
            speed *= SLOW_FACTOR;
        }
        if (effects.isActive(Effect.SPEED_BOOST)) {
            speed *= SPEED_BOOST_FACTOR;
        }
        return speed;
    }

    /** {@return the level being played} */
    public Level getLevel() {
        return levels.get(levelIndex);
    }

    /** {@return the number of levels in this game} */
    public int getLevelCount() {
        return levels.size();
    }

    /** {@return true if another level follows the current one} */
    public boolean hasNextLevel() {
        return levelIndex < levels.size() - 1;
    }

    /** {@return coins eaten on the current level} */
    public int getCoinsThisLevel() {
        return coinsThisLevel;
    }

    /** {@return the settings this game was started with} */
    public GameSettings getSettings() {
        return settings;
    }

    /** {@return grid width in cells} */
    public int getWidth() {
        return settings.width();
    }

    /** {@return grid height in cells} */
    public int getHeight() {
        return settings.height();
    }
}
