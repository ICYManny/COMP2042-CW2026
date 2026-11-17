package com.comp2042.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.util.function.DoubleConsumer;

/**
 * Calls a frame callback {@value #FRAMES_PER_SECOND} times per second, passing the real
 * time since the previous frame.
 * <p>
 * The original game redrew only when the snake moved (8.5 to 13 times per second), so the
 * snake jumped from cell to cell. Drawing at a fixed frame rate, separate from the game's
 * step rate, lets the view draw the snake part way between cells.
 */
public class GameLoop {

    /** How many times per second the game is drawn. */
    public static final double FRAMES_PER_SECOND = 24;

    private static final double NANOS_PER_SECOND = 1_000_000_000.0;

    private final Timeline timeline;
    private final DoubleConsumer onFrame;
    private long lastFrameNanos = -1;

    /**
     * @param onFrame called every frame with the seconds elapsed since the previous frame
     */
    public GameLoop(DoubleConsumer onFrame) {
        this.onFrame = onFrame;
        timeline = new Timeline(new KeyFrame(Duration.millis(1000 / FRAMES_PER_SECOND), event -> frame()));
        timeline.setCycleCount(Animation.INDEFINITE);
    }

    private void frame() {
        long now = System.nanoTime();
        double elapsed = lastFrameNanos < 0 ? 0 : (now - lastFrameNanos) / NANOS_PER_SECOND;
        lastFrameNanos = now;
        onFrame.accept(elapsed);
    }

    /** Starts calling the frame callback. */
    public void start() {
        lastFrameNanos = -1;
        timeline.play();
    }

    /** Stops calling the frame callback. */
    public void stop() {
        timeline.stop();
    }
}
