# COMP2042 Snake Game – Maintenance and Extension

**Student:** Immanuel Chieng Yi
**IDE / Java:** IntelliJ IDEA, JDK 25 (the project compiles for Java 21, so any JDK 21 or newer works)

## GitHub
https://github.com/ICYManny/COMP2042-CW2026.git

---

## Compilation Instructions

The project uses Maven with the Maven Wrapper, so Maven does not need to be installed. JavaFX 25 and JUnit 5 are downloaded automatically on the first build (internet needed once).

**Requirements:** JDK 21 or newer, with `JAVA_HOME` pointing at it.

### From a terminal (project root)
| Task | Windows | macOS / Linux |
|---|---|---|
| Run the game | `mvnw.cmd javafx:run` | `./mvnw javafx:run` |
| Run the unit tests | `mvnw.cmd test` | `./mvnw test` |
| Build a runnable jar | `mvnw.cmd clean package` | `./mvnw clean package` |
| Run the jar | `java -jar target/snake-javafx.jar` | same |
| Generate Javadoc | `mvnw.cmd javadoc:javadoc` (output in `target/reports/apidocs`) | `./mvnw javadoc:javadoc` |

### From IntelliJ IDEA
1. **File → Open** and choose the folder that contains `pom.xml`. Trust the project and let Maven import it.
2. **File → Project Structure → Project:** set the SDK to JDK 21 or newer.
3. Open the **Maven** tool window → *Plugins* → *javafx* → double-click **javafx:run**.
   (Running `Main` directly also works if IntelliJ adds JavaFX to the module path. `javafx:run` always works.)
4. To run the tests, right-click `src/test/java` → **Run 'All Tests'**.

There are no hard-coded file paths. All images, sounds, the stylesheet, the FXML and the level files are loaded from the class path through `util/ResourceLoader`.

### Controls
| Key | Action |
|---|---|
| W A S D | Steer (the first press starts the game) |
| P | Pause / resume |
| Enter | Continue: next level, resume, or retry after game over |
| R | Restart |
| + / - | Enter / leave fullscreen |
| Esc | Back to the main menu |

---

## Implemented and Working Properly

### Extensions (new features)
1. **Game-over screen with Retry and Main Menu buttons.** `OverlayPane` appears over the board for every non-playing state: *Ready* (start prompt, with the level name and coin target), *Paused* (Resume / Menu), *Level Clear* (Next / Menu), *Game Over* and *You Won* (Retry / Menu). Enter does the same as the main button.
2. **Smooth movement at 24 FPS.** The game logic and the drawing are separated. `GameLoop` draws 24 frames per second, while `GameModel.update()` moves the snake on a fixed time step based on its speed. The view draws the head and tail part of the way between cells, and the body as one continuous rounded stroke, so the snake glides and its corners stay joined. Up to two turns can be queued, so quick key presses are never lost.
3. **Difficulty levels chosen in the main menu.**
   - *Easy*: slow (6 moves/s) and every wall wraps round.
   - *Medium*: faster (9 moves/s) and only the side walls wrap.
   - *Hard*: fastest (12 moves/s) and every wall kills.

   Solid walls are drawn in red, so the player can see which edges are deadly.
4. **Power-ups toggle** (main menu check box). Four timed power-up coins:
   - *Slow* (purple): 60% speed for 8 s
   - *Shrink* (pink): 3 tail segments removed, score kept
   - *Ghost* (blue): pass through walls and obstacles for 6 s, snake drawn see-through
   - *x2 Points* (green): double points for 10 s

   The status bar shows each active power-up with its remaining seconds. The original coins are kept: Gold is a 15-point bonus, Rainbow is a 5-second speed-up hazard, and Silver scatters obstacles. With the toggle off, only basic coins appear.
5. **WASD controls.** All keys are defined in one `KeyBindings` table.
6. **Five levels** (*Open Field, Pillars, Corridors, Rooms, Maze*), each with its own wall layout, a coin target and a speed multiplier. Clearing a level shows *Level Clear*, and the next level keeps the score. Clearing level 5 wins the game. Levels are plain text files in `src/main/resources/levels/`, so adding one needs no code change. The starting level can be chosen in the main menu.
7. **Endless mode and high scores.** The menu has a *Mode* choice: *Levels* or *Endless*. Endless is one open board with no target: survive as long as you can for the highest score. Best scores are saved per mode and difficulty (with `java.util.prefs`, so no file paths) and survive closing the game. Every game shows a score bar on top with the current score, the mode and difficulty, and the best score, which rises live once beaten. The menu shows the best score of every mode for the selected difficulty, and Game Over announces a new best. A score is also saved if you restart or leave a game part way.
8. **Extras:** the coin sound (bundled with the original game but never played) now plays when a coin is eaten. The menu remembers your last choices. The score and timers sit in a status bar under the board instead of covering it.

### Maintenance (refactoring and bug fixes)
See *Modified Java Classes* and *Bug fixes* below. The model is covered by **166 JUnit 5 tests** (`mvnw test`).

---

## Implemented but Not Working Properly
None known at the time of submission.

## Features Not Implemented
- **Remappable controls in the UI.** `KeyBindings.bind()` supports rebinding, but there is no settings screen for it.

---

## Bug fixes (existing bugs found and fixed)
| Bug | Where | Fix |
|---|---|---|
| RainbowCoin could never spawn (two branches tested `ran == 1`) | `GameController.newCoin` → now `CoinFactory` | Fixed the branch, then replaced it with a weighted factory. Regression test `CoinFactoryTest.everyCoinTypeCanAppear` |
| Pressing DOWN while moving UP reversed the snake into itself (`valKey` compared DOWN with DOWN) | `GameController.valKey` → now `Snake.turn` | Turns are checked against the opposite direction. Tests in `SnakeTest` |
| Two quick key presses within one step could reverse the snake | `Snake.turn` | Each turn is checked against the direction the snake will be facing. A queue keeps both presses |
| Obstacles could spawn off the grid (`nextInt(x) + 1`), never in column 0, on top of each other, or right in front of the head | `Obstacle.newObstacles` → `Obstacles.spawn` | Uses `FreeCellFinder`, a set (no duplicates) and a 3-cell safe zone around the head |
| The game froze when the board was almost full (coin placement looped forever looking for a free cell) | `Coin` constructor | `FreeCellFinder` returns empty when there is no free cell, and the game is then won. Test `fillingTheBoardWinsInsteadOfFreezing` |
| In wrap mode the snake could wrap onto an obstacle and survive | `Snake.move` | The obstacle check happens after wrapping. Test `wrappingOntoAnObstacleKillsTheSnake` |
| Snake sprites were scaled twice in fullscreen (`fullScale * fullScale`) | `GameView.drawHead/drawBody` | Scaled once; the renderer was later rewritten |
| Fullscreen never triggered on most keyboards (`+` arrives as `EQUALS`) | `GameController.handle` | Accepts `=`, `+` and numpad keys. The board now also follows the window's fullscreen property |
| Stray corner sprites stayed on the board after a reset (`turnArray` was never cleared) | `GameView` | `turnArray` removed. Corners are worked out from neighbouring segments |
| Escape left the old game's Timeline running, and the static key field carried the old direction into the next game | `GameController` | The loop is stopped and the state is per-instance (no statics) |
| `./mvnw` failed on every machine (`distributionUrl` pointed to a non-existent `apache-maven-3-bin.zip`; script not executable) | `.mvn/wrapper/maven-wrapper.properties`, `mvnw` | Pinned to Maven 3.9.11 and set the executable bit |
| The packaged jar did not start ("JavaFX runtime components are missing") | `pom.xml` | Added the `Launcher` entry point for the shaded jar |
| Each coin decoded a 1254×1254 PNG every time it spawned | coin classes | Images are cached in `SpriteManager` and resized to 64×64 |

---

## New Java Classes
| Class | Location | Purpose |
|---|---|---|
| `Launcher` | `app` | Plain entry point so the runnable jar starts |
| `GameAction` | `controller` | Enum of non-steering commands (pause, restart, fullscreen, menu, continue) |
| `KeyBindings` | `controller` | Maps keys to directions and actions (WASD) |
| `GameLoop` | `controller` | 24 FPS frame timer that passes the elapsed time to the controller |
| `Position` | `model` | Immutable grid coordinate (record), replaces `java.awt.Point` |
| `Direction` | `model` | Enum with offset, sprite angle and opposite, replaces `'U'/'D'/'L'/'R'` |
| `Difficulty` | `model` | Easy / Medium / Hard: speed, wall rule, menu text |
| `WallMode` | `model` | Wall rule strategy: WRAP_ALL, WRAP_SIDES, SOLID |
| `GameSettings` | `model` | Immutable menu choices (record) |
| `GameMode` | `model` | Levels or Endless |
| `GameState` | `model` | READY, PLAYING, PAUSED, LEVEL_COMPLETE, GAME_OVER, WON |
| `GameListener` | `model` | Observer interface for coin eaten / level complete / game over |
| `FreeCellFinder` | `model` | Picks a random free cell; injectable `Random` |
| `Effect`, `ActiveEffects` | `model` | Timed power-up effects and their timers |
| `BasicCoin` | `model.coin` | The plain red coin (the old concrete `Coin`) |
| `SlowCoin`, `ShrinkCoin`, `DoublePointsCoin` | `model.coin` | New power-up coins |
| `CoinFactory` | `model.coin` | Weighted factory that chooses the next coin |
| `Level` | `model.level` | A level's walls, coin target and speed (record) |
| `LevelLoader` | `model.level` | Reads and validates `levels/levelN.txt` |
| `HighScores` | `model.score` | Best score per mode and difficulty |
| `HighScoreTracker` | `model.score` | Follows one game against the best; saves on game over (a `GameListener`) |
| `ScoreStore`, `PreferencesScoreStore`, `InMemoryScoreStore` | `model.score` | Where scores are kept: saved per user, or in memory for tests |
| `SpriteManager` | `view` | Loads and caches sprites by key |
| `OverlayPane` | `view` | Ready / Paused / Level Clear / Game Over / Won panel with buttons |
| `SoundEffects` | `view` | Plays the coin sound (a `GameListener`) |
| `ResourceLoader` | `util` | Class-path resource lookup with clear errors |

## Modified Java Classes
| Original class | Now | Changes and reasons |
|---|---|---|
| (all) | packages `app`, `controller`, `model`, `model.coin`, `model.level`, `view`, `util` | Everything was in one flat package. It is now grouped by MVC responsibility |
| `Main` | `app.Main` | Passes the stage JavaFX provides to `ViewManager`. Removed the static `getViewManager()` global |
| `ViewManager` | `app.ViewManager` | One window reused for menu and game (it used to ignore the JavaFX stage and open two). It injects itself into the menu controller through an FXML controller factory, builds games, loads levels once, remembers the last settings, and no longer swallows FXML errors |
| `MainMenuController` | `controller.MainMenuController` | Only reads menu choices (difficulty, power-ups, start level) and asks `ViewManager` to start a game. Game construction moved out. Added Quit |
| `GameController` | `controller.GameController` | Holds no game rules. Keys go through `KeyBindings`, time comes from `GameLoop`. Removed dead code (`calculateBonusScore`, `legacyScoreMultiplier`, `unusedObstacleCount`, unused imports and fields) and the public static key fields |
| `Game` | `model.GameModel` + `model.GameSettings` | Split state and rules from configuration. `GameModel` owns all rules (movement, walls, collisions, coins, scoring, effects, levels, states) so they are unit-testable without JavaFX. View concerns (colour, cell size) moved to `GameView` |
| `Snake` | `model.Snake` | Now only the body: a `Deque<Position>` from head to tail, a turn queue and the previous tail for smooth drawing. Collision rules moved to `GameModel`. Removed the image fields, the unused colours and the four duplicated move cases |
| `Obstacle` | `model.Obstacles` | Renamed (it holds all obstacles). Uses sets so duplicates are impossible, and keeps level walls separate from spawned obstacles |
| `Coin` | `model.coin.Coin` (abstract) | Places nothing itself, holds no image, and has an overridable `applyEffect()` (Open/Closed) instead of the controller's `getClass()` chain |
| `GoldCoin`, `RainbowCoin`, `SilverCoin` | `model.coin` | Implement `getPoints()`, `getSpriteKey()` and their own `applyEffect()` |
| `BlueCoin` | `model.coin.GhostCoin` | Renamed. Now a timed Ghost effect (walls and obstacles) instead of "ignore borders until the next coin" |
| `GameView` | `view.GameView` | Reads the model only. Rotated sprites are drawn with canvas transforms (no snapshot per segment per frame). Smooth interpolated snake, red solid walls, brown level walls, status bar, see-through Ghost |

**Resources:** flattened `resources/resources/views` to `resources/views`. Deleted unused `SnakeLogo.png`, `green_button04.png` and `kenvector_future.ttf`, and the body/corner/tail sprites replaced by the smooth renderer. Resized the coins and the icon (assets went from 5.0 MB to about 0.3 MB). New sprites for the Slow, Shrink and x2 coins (recoloured from the original red coin). New `levels/level1-5.txt`. Redesigned `MainMenu.fxml` and updated `styling.css`.

### Design principles and patterns
- **MVC:** the model has no JavaFX dependency, the view only reads it, and the controller only translates input and time.
- **Single Responsibility:** for example `Snake` (body), `FreeCellFinder` (placement), `CoinFactory` (choice), `SpriteManager` (images), `KeyBindings` (controls).
- **Open/Closed:** new coins, levels, wall rules or key bindings are added without editing existing logic.
- **Dependency injection:** `HighScores` depends on the `ScoreStore` interface, so tests never touch real saved scores; `ViewManager` goes into controllers, a `Random` into `GameModel` (repeatable tests), and an exit `Runnable` into `GameController`.
- **Patterns:** *Factory* (`CoinFactory`), *Observer* (`GameListener` → `SoundEffects`), *Strategy* (`WallMode` per `Difficulty`), *State* enum (`GameState`), plus a fixed-time-step game loop.

### Testing
166 JUnit 5 tests in `src/test/java` cover normal cases, edge cases and error conditions for every model class and for `KeyBindings`. They include regression tests for the bugs above and a flood-fill test proving every level is completable.

---

## Unexpected Problems
- **Broken Maven wrapper:** the provided `distributionUrl` did not exist, so `mvnw` could only work where Maven was already installed. I found this while checking that the project builds on another machine.
- **Infinite loop when the board fills up:** coin placement used "pick random cells until one is free". I found it while writing a test for winning on a full board, and replaced it with `FreeCellFinder`.
- **Speed and smoothness are linked:** raising the frame rate in the original design would have made the snake faster, because it moved once per frame. I had to separate the game step from drawing (fixed time step plus interpolation) and handle interpolation across wrapped borders (the stroke is broken where the snake wraps).
- **Buttons swallowing keys:** after adding buttons to the game screen, JavaFX focus navigation would consume Tab, Space and Enter. I made the buttons non-focusable, moved key handling to a scene event filter, and moved Restart from Tab to R.
- **Labels covering the board:** the original labels were stacked on the canvas and hid the top and bottom rows, so I moved them into a status bar under the board.
- **Making the model testable:** the model classes created JavaFX `Image`s, which cannot be created in plain unit tests. Moving images into `SpriteManager` was needed before writing tests.

## Documentation
- `Design.pdf`: class diagram (source in `docs/design.mmd`)
- `Javadoc/`: generated API documentation
