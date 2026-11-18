package com.comp2042.model;

/**
 * The difficulty levels offered in the main menu. Each sets the snake's speed and the
 * rule for the board's edges.
 */
public enum Difficulty {
    /** Slow, and every wall wraps round. */
    EASY("Easy", 6, WallMode.WRAP_ALL, "Slow. Go through any wall."),
    /** Faster, and only the side walls wrap round. */
    MEDIUM("Medium", 9, WallMode.WRAP_SIDES, "Faster. Go through the side walls only."),
    /** Fastest, and every wall is solid. */
    HARD("Hard", 12, WallMode.SOLID, "Fastest. Every wall is deadly.");

    private final String displayName;
    private final double speed;
    private final WallMode wallMode;
    private final String description;

    Difficulty(String displayName, double speed, WallMode wallMode, String description) {
        this.displayName = displayName;
        this.speed = speed;
        this.wallMode = wallMode;
        this.description = description;
    }

    /** @return snake moves per second */
    public double getSpeed() {
        return speed;
    }

    /** @return what happens at the edges of the board */
    public WallMode getWallMode() {
        return wallMode;
    }

    /** @return a one-line explanation shown in the menu */
    public String getDescription() {
        return description;
    }

    /** @return the name shown in the menu */
    @Override
    public String toString() {
        return displayName;
    }
}
