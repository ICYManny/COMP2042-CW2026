package com.comp2042.view;

import com.comp2042.model.GameListener;
import com.comp2042.model.coin.Coin;
import com.comp2042.util.ResourceLoader;

import javafx.scene.media.AudioClip;

/**
 * Plays the coin sound when the snake eats a coin, by listening to the game model
 * (Observer pattern). The sound file was bundled with the original game but never played.
 * <p>
 * If audio cannot be initialised (for example on a machine without a sound device) the
 * game carries on silently instead of crashing.
 */
public class SoundEffects implements GameListener {

    private static final String COIN_SOUND = "/views/res/coinSound.wav";
    private static final double VOLUME = 0.5;

    private final AudioClip coinSound;

    /** Loads the sound effects. */
    public SoundEffects() {
        coinSound = load(COIN_SOUND);
    }

    private static AudioClip load(String path) {
        try {
            AudioClip clip = new AudioClip(ResourceLoader.externalForm(path));
            clip.setVolume(VOLUME);
            return clip;
        } catch (RuntimeException exception) {
            System.err.println("Sound disabled: " + exception.getMessage());
            return null;
        }
    }

    /**
     * Plays the coin sound.
     *
     * @param coin the coin that was eaten
     */
    @Override
    public void onCoinEaten(Coin coin) {
        if (coinSound != null) {
            coinSound.play();
        }
    }
}
