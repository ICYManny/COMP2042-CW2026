package com.comp2042;

/**
 * Plain entry point used by the runnable (shaded) jar.
 * <p>
 * When the main class of a jar extends {@link javafx.application.Application} and JavaFX
 * is on the class path, the JVM refuses to start with "JavaFX runtime components are
 * missing". Starting from a class that does not extend {@code Application} avoids this.
 */
public final class Launcher {

    private Launcher() {
    }

    /**
     * Starts the game.
     *
     * @param args command line arguments, passed straight to {@link Main#main(String[])}
     */
    public static void main(String[] args) {
        Main.main(args);
    }
}
