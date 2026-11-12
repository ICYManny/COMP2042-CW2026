package com.comp2042.util;

import java.net.URL;

/**
 * Finds files bundled inside the application (images, sounds, stylesheets, FXML, levels).
 * <p>
 * All lookups go through the class path with absolute paths such as
 * {@code "/views/res/redcoin.png"}, so the game never depends on the current working
 * directory or on where the project is stored on disk. A missing file fails immediately
 * with a clear message instead of silently drawing nothing.
 */
public final class ResourceLoader {

    private ResourceLoader() {
    }

    /**
     * Returns the URL of a bundled resource.
     *
     * @param path absolute class path location, starting with "/"
     * @return the resource URL
     * @throws IllegalStateException if the resource is not bundled with the game
     */
    public static URL url(String path) {
        URL url = ResourceLoader.class.getResource(path);
        if (url == null) {
            throw new IllegalStateException("Missing game resource: " + path);
        }
        return url;
    }

    /**
     * Returns the URL of a bundled resource as a string, the form JavaFX expects for
     * images, stylesheets and audio clips.
     *
     * @param path absolute class path location, starting with "/"
     * @return the resource URL as text
     * @throws IllegalStateException if the resource is not bundled with the game
     */
    public static String externalForm(String path) {
        return url(path).toExternalForm();
    }
}
