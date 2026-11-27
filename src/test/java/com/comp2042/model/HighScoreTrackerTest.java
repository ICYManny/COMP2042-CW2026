package com.comp2042.model;

import com.comp2042.model.coin.BasicCoin;
import com.comp2042.model.coin.GoldCoin;
import com.comp2042.model.score.HighScoreTracker;
import com.comp2042.model.score.HighScores;
import com.comp2042.model.score.InMemoryScoreStore;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** HighScoreTracker lives in model.score; this test sits in model so it can place coins exactly. */
class HighScoreTrackerTest {

    private HighScores highScores;
    private GameModel game;
    private HighScoreTracker tracker;

    @BeforeEach
    void setUp() {
        highScores = new HighScores(new InMemoryScoreStore());
        highScores.submit(GameMode.ENDLESS, Difficulty.HARD, 20);
        game = new GameModel(new GameSettings(20, 20, false, Difficulty.HARD, 1, GameMode.ENDLESS), new Random(3));
        tracker = new HighScoreTracker(game, highScores);
        game.addListener(tracker);
    }

    /** Each gold coin is worth 15 points. */
    private void eatGoldCoins(int count) {
        for (int i = 0; i < count; i++) {
            game.placeCoin(new GoldCoin(game.getSnake().nextHead()));
            game.tick();
        }
    }

    private void crash() {
        game.placeCoin(new BasicCoin(new Position(0, 0)));
        while (!game.isOver()) {
            game.tick();
        }
    }

    @Test
    void showsTheSavedBestBeforeAnyPoints() {
        assertEquals(20, tracker.getBest());
        assertFalse(tracker.isNewBest());
    }

    @Test
    void bestFollowsTheScoreOnceItIsBeaten() {
        eatGoldCoins(2);
        assertEquals(30, tracker.getBest());
        assertTrue(tracker.isNewBest());
    }

    @Test
    void crashingWithAHigherScoreSavesIt() {
        eatGoldCoins(2);
        crash();
        assertEquals(30, highScores.best(GameMode.ENDLESS, Difficulty.HARD));
    }

    @Test
    void crashingWithALowerScoreKeepsTheOldBest() {
        eatGoldCoins(1);
        crash();
        assertEquals(20, highScores.best(GameMode.ENDLESS, Difficulty.HARD));
        assertFalse(tracker.isNewBest());
    }

    @Test
    void finishRunSavesAGameLeftPartWay() {
        eatGoldCoins(3);
        tracker.finishRun();
        tracker.finishRun();
        assertEquals(45, highScores.best(GameMode.ENDLESS, Difficulty.HARD));
    }

    @Test
    void newRunComparesAgainstTheUpdatedBest() {
        eatGoldCoins(2);
        crash();
        game.reset();
        tracker.startRun();
        eatGoldCoins(2);
        assertFalse(tracker.isNewBest());
    }
}
