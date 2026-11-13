package com.comp2042.view;

import com.comp2042.util.ResourceLoader;

import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

/**
 * Loads and caches the game's sprite images.
 * <p>
 * Model classes only expose a sprite key (for example {@code "redcoin"}); this class turns
 * the key into an {@link Image} the first time it is needed and reuses it afterwards.
 * Previously every new coin decoded its own copy of the image, and model classes depended
 * on JavaFX image classes, which made them impossible to unit test without a display.
 */
public class SpriteManager {

    private static final String SPRITE_FOLDER = "/views/res/";
    private static final String SPRITE_EXTENSION = ".png";

    private final Map<String, Image> cache = new HashMap<>();

    /**
     * Returns the sprite for a key, loading it on first use.
     *
     * @param key file name of the sprite without folder or extension, e.g. "snakehead"
     * @return the cached image
     * @throws IllegalStateException if no sprite with that key is bundled with the game
     */
    public Image get(String key) {
        return cache.computeIfAbsent(key,
                k -> new Image(ResourceLoader.externalForm(SPRITE_FOLDER + k + SPRITE_EXTENSION)));
    }
}
