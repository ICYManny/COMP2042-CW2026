package com.comp2042.model.level;

import com.comp2042.model.Position;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Reads level layouts from text files bundled with the game ({@code /levels/level1.txt},
 * {@code level2.txt}, ...).
 * <p>
 * File format: optional comment lines starting with {@code #} followed by a space, then
 * <pre>
 * name: Pillars
 * coins: 10
 * speed: 1.05
 * ....................   (one row per line, '#' = wall, '.' = empty)
 * </pre>
 * Keeping levels in data files means a new level is a new text file, with no code change.
 */
public final class LevelLoader {

    /** Width and height of every level, in cells. */
    public static final int LEVEL_SIZE = 20;

    private static final String LEVEL_PATH = "/levels/level%d.txt";
    private static final char WALL = '#';
    private static final char EMPTY = '.';
    /** Cells around the snake's start that must stay clear: the head, its body and 3 cells ahead. */
    private static final int START_CLEARANCE_AHEAD = 3;

    private LevelLoader() {
    }

    /**
     * Loads level1.txt, level2.txt, ... until the next file does not exist.
     *
     * @return the levels in order
     * @throws IllegalStateException if no level files are bundled, or one is invalid
     */
    public static List<Level> loadDefaultLevels() {
        List<Level> levels = new ArrayList<>();
        for (int number = 1; ; number++) {
            InputStream in = LevelLoader.class.getResourceAsStream(String.format(LEVEL_PATH, number));
            if (in == null) {
                break;
            }
            levels.add(parse(number, readLines(in)));
        }
        if (levels.isEmpty()) {
            throw new IllegalStateException("No level files found in /levels");
        }
        return levels;
    }

    private static List<String> readLines(InputStream in) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            return reader.lines().toList();
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    /**
     * Turns the lines of a level file into a {@link Level}.
     *
     * @param number the level's number
     * @param lines  the file's lines
     * @return the parsed level
     * @throws IllegalArgumentException if the file is malformed, explaining what is wrong
     */
    public static Level parse(int number, List<String> lines) {
        List<String> content = new ArrayList<>();
        for (String line : lines) {
            if (!line.startsWith("# ") && !line.isBlank()) {
                content.add(line.stripTrailing());
            }
        }
        if (content.size() != 3 + LEVEL_SIZE) {
            throw new IllegalArgumentException("Level " + number + ": expected 3 header lines and "
                    + LEVEL_SIZE + " rows, found " + content.size() + " lines");
        }
        String name = header(number, content.get(0), "name");
        int coins = parseNumber(number, header(number, content.get(1), "coins"));
        double speed;
        try {
            speed = Double.parseDouble(header(number, content.get(2), "speed"));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Level " + number + ": speed is not a number");
        }
        Set<Position> walls = parseGrid(number, content.subList(3, content.size()));
        checkStartIsClear(number, walls);
        return new Level(number, name, coins, speed, walls);
    }

    private static String header(int number, String line, String key) {
        String prefix = key + ":";
        if (!line.startsWith(prefix)) {
            throw new IllegalArgumentException("Level " + number + ": expected '" + prefix + "' but found '" + line + "'");
        }
        return line.substring(prefix.length()).trim();
    }

    private static int parseNumber(int number, String text) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("Level " + number + ": coins is not a whole number");
        }
    }

    private static Set<Position> parseGrid(int number, List<String> rows) {
        Set<Position> walls = new HashSet<>();
        for (int y = 0; y < rows.size(); y++) {
            String row = rows.get(y);
            if (row.length() != LEVEL_SIZE) {
                throw new IllegalArgumentException("Level " + number + ": row " + (y + 1) + " has "
                        + row.length() + " cells, expected " + LEVEL_SIZE);
            }
            for (int x = 0; x < row.length(); x++) {
                char cell = row.charAt(x);
                if (cell == WALL) {
                    walls.add(new Position(x, y));
                } else if (cell != EMPTY) {
                    throw new IllegalArgumentException("Level " + number + ": unknown character '" + cell
                            + "' in row " + (y + 1));
                }
            }
        }
        return walls;
    }

    /** The snake starts in the centre facing left, so those cells must not be walls. */
    private static void checkStartIsClear(int number, Set<Position> walls) {
        int centre = LEVEL_SIZE / 2;
        for (int x = centre - START_CLEARANCE_AHEAD; x <= centre + 1; x++) {
            if (walls.contains(new Position(x, centre))) {
                throw new IllegalArgumentException("Level " + number + ": wall at (" + x + ", " + centre
                        + ") blocks the snake's starting position");
            }
        }
    }
}
