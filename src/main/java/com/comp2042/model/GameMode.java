package com.comp2042.model;

/**
 * The two ways to play, chosen in the main menu.
 */
public enum GameMode {
    /** Play through the bundled levels, each with walls and a coin target. */
    LEVELS("Levels", "Clear each level's coin target to move on."),
    /** One open board with no target: survive as long as you can for the highest score. */
    ENDLESS("Endless", "No levels, no target. Go for the highest score.");

    private final String displayName;
    private final String description;

    GameMode(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    /** {@return a one-line explanation shown in the menu} */
    public String getDescription() {
        return description;
    }

    /** {@return the name shown in the menu} */
    @Override
    public String toString() {
        return displayName;
    }
}
