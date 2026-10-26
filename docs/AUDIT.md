# Snake3D — Repair, Test and Packaging Report

Date: 24 October 2026  
Target: Windows 11 x64 (build 26200)  
Result: **working, self-contained Windows desktop application**

## 1. Original project status

The project was a small, single-module JavaFX game, not a web app:

- **Language/runtime:** Java 17
- **UI/graphics:** JavaFX 17.0.16 (`javafx-controls`, `javafx-media`, `javafx-graphics`, Windows classifier)
- **Build:** Maven (`pom.xml`)
- **Architecture:** one large `Main.java` (about 2,800 lines) containing the scene, game loop, AI opponents, skins, audio/video, and UI; a separate `VillainSnakeAI.java` with BFS pathfinding; resources under `src/main/resources`
- **Data:** no database and no server. The only state is the selected skin and high score.
- **Existing desktop build:** `Snake3D-1.0.exe` (193,024,512 bytes), a valid jpackage/WiX Windows installer for the old 1.0 build, including a Java 17 runtime and JavaFX. It was inspected but not installed during the audit because it would deploy the old build over the repaired one.
- **Environment:** the machine initially had **no JDK, no Maven, and no WiX**, so a project-local toolchain was downloaded into `.tools/` (git-ignored): Temurin JDK 17.0.20.1, Maven 3.9.9, WiX 3.14.1.

The source compiled successfully once a JDK was available. There were no syntax errors, missing files, broken imports, missing assets, or hardcoded machine-specific paths in the original source. The app already ran; this was a repair and packaging job, not a rewrite.

## 2. Architecture and entry points

```
JavaFX Application (Main)
├── start(Stage)
│   ├── space canvas + starfield
│   ├── 3D playfield (Group): player snake, 3 villains, fruit, pickups, HUD
│   ├── AnimationTimer game loop
│   ├── buildSkinSelectorOverlay()     menu / home screen
│   └── stop/start, restart, home, exit buttons
├── VillainSnakeAI.getNextMove()       BFS first-step pathfinding (used by the villain logic)
└── Launcher.main()                    classpath-safe packaged entry point
```

JavaFX modules are supplied from `target/desktop-input` in development and bundled into the app image for the desktop build. `Launcher` exists because launching an `Application` subclass directly from a plain classpath jar makes the JDK print "JavaFX runtime components are missing".

## 3. Bugs found and fixed

Severity is based on user impact at the time of the audit.

| # | Severity | Problem | Fix |
|---|---|---|---|
| 1 | **High** | After a villain died and respawned (and after Restart), villains 1–3 were drawn with arbitrary offsets (`-75`, `-85`, `+500`, `+900`, `-200`, `+200`) that did not match their logical/collision coordinates. Villains 2 and 3 respawned off-screen; hits were detected at a different place from the visible snake. Reset had the same mismatch. | Removed the offsets and spawn all heads/bodies from the same clamped playfield coordinates used by movement and collision. Respawn and reset now agree. |
| 2 | **High** | Game over opened **two** dialogs: first a "new high score" dialog whose Restart button started the game, then a second "Game Over" dialog stacked on top of the restarted game. | Reworked `gameOver()` into one dialog showing score/high score with **Restart** and **Exit**; a guard prevents re-entry. Restart resumes the timer only after the dialog closes. |
| 3 | **High (UX)** | The six skin options were fixed at 160×140 with 60 px gaps and a 26 px font. At the default 800 px window the row was far wider than the window, clipping "Cosmic" and truncating labels. | Responsive one-row layout: widths are recalculated from the scene width, with styled toggle buttons and 16 px text. All six full labels fit at 1280, 800 and 640 widths. |
| 4 | **Medium** | High score was an in-memory field only, reset on every launch, and was not shown in the HUD. The HUD's initial text was "Player Score: 0" but changed to "Score: N" later. | High score is loaded/saved with Java Preferences and shown in the HUD ("Score: N    High Score: M"); the label is now consistent. |
| 5 | **Medium** | The golden-fruit respawn timer started when the app started, so fruit appeared/despawned behind the menu, while paused, and after game over. | The timer starts on Play/Restart, pauses on Home and game over, and resumes on return. |
| 6 | **Medium (performance)** | Rainbow and Cosmic skins allocated a new `PhongMaterial` for the head and **every body segment on every frame**, creating continuous garbage and GPU material churn. | Materials are cached per sphere (weak map) and recolored in place. |
| 7 | **Medium** | AI wrapping used incorrect modulo math (`(x-left) % (width-left)`) that never mapped to the full playfield and differed from player wrapping. Villain 3's fallback also used the wrong wrap helper. | `wrapX`/`wrapX1`/`wrapX2` now use the same clamped playfield bounds; the Villain 3 fallback uses its own wrappers. |
| 8 | **Medium** | Closing the window did not stop the game timer or dispose the game-over `MediaPlayer`, and did not flush the high score. | Added a close handler that stops the timer, disposes audio, and saves preferences. |
| 9 | **Low** | Click-spark confetti was added to the button's parent using layout coordinates, so sparks appeared in the wrong place (and could be clipped) for buttons inside the menu card. | Sparks are now unmanaged nodes in the overlay using `localToScene` coordinates. |
| 10 | **Low** | After pausing and then using Home → Play, the stop/start button could still read "Start" while the game ran. | Play/back-to-game set the button label to "Stop". |
| 11 | **Low** | No minimum window size; the 800×700 layout could be shrunk to unusable dimensions. | Stage minimum is 640×560; the menu adapts down to that size. |
| 12 | **Low** | Restart did not reset the elapsed-time epoch used by rainbow timing, and `gameOverShown` was never reset. | Both are reset in `resetSnake()`. |
| 13 | **Low** | `resetSnake()` could schedule no villain respawns but left pending timers running after game over; villains could reappear behind the dialog. | Game over stops all three respawn timers. |
| 14 | **Medium (build)** | `pom.xml` hardcoded the `win` JavaFX classifier, so the source only built on Windows; no test dependencies or test runner; jar had no classpath metadata; packaging relied on ad-hoc commands. | Added OS-activated JavaFX profiles, JUnit 5 + Surefire, jar manifest `Class-Path` and `Main-Class: Launcher`, dependency copying, and a reproducible build script. |
| 15 | **Medium (packaging)** | The jar's `Main-Class` was `Main`, which fails with "JavaFX runtime components are missing" when run from a classpath jar/app image. | Added `Launcher.java` that calls `Main.main` without extending `Application`; all jar/package metadata now points at it. |
| 16 | **Low** | No README, audit report, build script, `.gitignore` entries for `target/`, `dist/`, `.tools/`, or test output. | Added `README.md`, this report, `scripts/build-desktop.ps1`, and ignore rules. |
| 17 | **Low** | App/installer metadata was minimal ("Snake3D Installer", version 1.0, vendor "Unknown"). | jpackage now uses name Snake3D, version 1.0.0, vendor Snake3D, description, and the existing icon. |

### Code quality observations (not changed)

- `Main.java` is a very large single class, with duplicated import statements and several unused helpers. It was left intact because splitting it would be a rewrite with regression risk; the confirmed bugs were fixed in place.
- `VillainSnakeAI` correctly implements BFS; it was not changed. Tests now cover its behavior.
- The original installer and old build artifacts were preserved, not deleted.

## 4. Files changed or added

**Changed**

- `src/main/java/Main.java` — all gameplay/UI fixes above: responsive skins, game-over flow, high-score persistence, villain spawn coordinates, wrapping, golden-fruit timers, material caching, sparks, close handling, minimum window size, labels.
- `pom.xml` — version 1.0.0, cross-platform JavaFX profiles, JUnit 5, Surefire, jar manifest with classpath and `Launcher`, dependency copy into `target/desktop-input`, javafx-maven-plugin.
- `.gitignore` — build, toolchain, temp, IDE, and log exclusions.

**Added**

- `src/main/java/Launcher.java` — classpath-safe entry point.
- `src/test/java/VillainSnakeAITest.java` — five BFS tests.
- `src/test/java/UiSmokeHarness.java` — manual JavaFX end-to-end harness.
- `scripts/build-desktop.ps1` — reproducible Windows build.
- `README.md` and `docs/AUDIT.md`.

**Unchanged:** game assets (`bgvideo.mp4`, WAV files, PNGs, icon), `VillainSnakeAI.java`, and the old `Snake3D-1.0.exe`.

## 5. Verification performed

### 5.1 Build and unit tests

```text
mvn clean package
Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Test coverage added for `VillainSnakeAI`:

- moves straight toward a fruit on an empty grid,
- detours around a wall through the only gap,
- does not step into obstacles when the fruit is unreachable,
- returns the head position when completely trapped,
- handles a fruit on the same tile without throwing.

### 5.2 GUI smoke harness

`UiSmokeHarness` drives the real JavaFX scene without screen coordinates. It checks the menu, all six skins are inside the scene bounds, Play removes the menu, the HUD exists, Stop changes to Start, Start resumes, Home reopens the menu, Play replays, then invokes game over via the production method and clicks Restart while asserting exactly one dialog is open and the game resumes.

| Window | Result |
|---|---|
| 1280×820 (scene 1265×782) | PASS — layout, Play, Stop, Start, Home, replay |
| 800×700 (scene 785×662) | PASS — full flow including **one** game-over dialog and Restart |
| 640×560 (scene 625×522, stage minimum) | PASS — all six labels within bounds, full flow |

The harness also saved scene snapshots (`.tmp/ui-*.png`, ignored by git). The gameplay snapshot shows the player, three AI snakes, fruit, HUD, and the Stop/Restart/Exit bar rendering correctly.

### 5.3 Development mode

```text
mvn javafx:run
```

Launched a window titled "Snake3D" with the JavaFX modules on the module path and was stopped cleanly. A classpath launch (`java -jar snake3d.jar` from `target/desktop-input`) also worked.

### 5.4 Packaged app image

`dist/portable/Snake3D/Snake3D.exe` was started in a child environment with **`JAVA_HOME` unset and `PATH` reduced to Windows system folders**:

- process stayed alive and responsive,
- window title "Snake3D" appeared,
- the scene rendered the repaired menu,
- no system Java was used.

### 5.5 Portable ZIP

`dist/portable/Snake3D-1.0.0-windows-portable.zip` was extracted to a clean folder (362 files), started with the same stripped environment, and produced a "Snake3D" window. This verifies the actual deliverable ZIP, not just the unpacked app image.

### 5.6 Installer

`dist/installer/Snake3D-1.0.0.exe`:

- silent install to a test directory: exit code **0**,
- files installed: `Snake3D.exe`, `app/`, `runtime/`,
- installed app launched with no system Java and showed a "Snake3D" window,
- installed product appeared in Windows uninstall entries as **Snake3D 1.0.0**,
- silent uninstall: exit code **0**, install directory removed, registry entry count **0**, no Start Menu leftovers,
- installer metadata: ProductName "Snake3D Installer", ProductVersion 1.0.0, CompanyName "Snake3D", icon and description present.

The final install/uninstall cycle was performed against the final rebuilt artifacts.

## 6. Delivered artifacts

| Artifact | Exact path | Size |
|---|---|---|
| Windows installer | `C:\Users\ShahrukhKhan\Desktop\Snake3D\dist\installer\Snake3D-1.0.0.exe` | 143,665,152 bytes (~137 MB) |
| Portable ZIP | `C:\Users\ShahrukhKhan\Desktop\Snake3D\dist\portable\Snake3D-1.0.0-windows-portable.zip` | 141,517,360 bytes (~135 MB) |
| Portable app image | `C:\Users\ShahrukhKhan\Desktop\Snake3D\dist\portable\Snake3D\Snake3D.exe` | ~212 MB folder |
| Built jar | `C:\Users\ShahrukhKhan\Desktop\Snake3D\target\snake3d.jar` | ~72 MB |

SHA-256:

```text
02396B7AF6A9694A5976B6BBB1ADABA8DA26EFDCBFE24B275DAB4B8224BD82C5  Snake3D-1.0.0.exe
6576F4228C4A3E4DFB9469DBFD354156C337EC739CFE1D9C7FA78303C12D16F5  Snake3D-1.0.0-windows-portable.zip
```

The installer bundles the Java runtime and JavaFX, so no JDK/JRE is required on the target machine.

## 7. Not verified / remaining limitations

- **Not code-signed.** Windows SmartScreen may warn. This is a local build with no certificate.
- **Windows only.** The Maven build has Linux/macOS profiles, but only Windows packaging was produced and tested.
- **Second physical laptop not tested.** The bundle has no machine-specific paths and runs with system Java removed, which is strong evidence of portability, but a separate machine test was not possible here.
- **Audio playback was not verified by listening.** The JavaFX audio pipeline loaded without errors and the menu video rendered, but sound output was not confirmed aurally.
- **Game over was invoked directly** through the production method in the harness, not by waiting for an organic collision. The collision code calls the same method, but every collision geometry was not exhaustively simulated.
- **Long sessions, golden-fruit boosts, special pickups, and score progression through fruit collisions** were not exhaustively tested. The rendering and HUD were verified; logic beyond the pathfinding tests was reviewed rather than fully automated.
- The old `Snake3D-1.0.exe` remains in the project root as the original artifact; it is not the repaired build.
- The environment's display uses DPI/desktop composition that makes native window screenshots inconsistent when a window is forced to maximize. Layout was therefore verified by JavaFX scene bounds at 640, 800, and 1280 logical widths rather than by pixel screenshots alone.

## 7b. Final round — corrections and additions (26 October 2026)

| Change | Detail | Verified by |
|---|---|---|
| **Game Over window borderless** | The game-over stage is `StageStyle.UNDECORATED`, sized exactly to the card (560×480): no white OS title bar, close button, border or back window behind it. | FinalProbe: exactly 1 extra window, style `UNDECORATED`, window = card, frame `0.0×0.0`; Exit returns to the home page. |
| **Home music replaced** | The old menu jingle is gone; `Backgroundaudio.wav` in the project root is the home-page music (bundled into the jar by `pom.xml`, read from the root when running from source). Missing file = silent menu, no errors. Follows Sound On/Off and the volume slider. | FinalProbe: source = project-root `Backgroundaudio.wav`, playing on launch, stops on Sound Off, resumes on Sound On. |
| **Volume slider** | Settings → horizontal 0–100% slider under Sound: live volume for home music and game-over music, saved to preferences, position restored when the panel reopens. | FinalProbe: drag to 25% → volume 0.25 live on `AudioClip` + `MediaPlayer`, label `25%`, pref saved, thumb 50 px / 200 px after reopen. |
| **Auto-pause on focus loss** (new feature) | Alt-tabbing away pauses a running round through the stage `focusedProperty` (mirrors Stop, HUD → Start); focus back resumes exactly. Guarded against the game-over state. | FinalProbe: pause (running=false, HUD=Start, pausedByFocusLoss=true) then resume (running=true, HUD=Stop). |
| **Settings hover blink fix** | Inline button styles pin `-fx-font-size` and `-fx-padding`, so `:hover`/`:focused` CSS re-applies can no longer rebuild the font at Modena's inherited 12 px (the shrink-then-grow blink). | SettingsBlinkProbe `geomChanges=0`; HoverProbe PASS (0 problems). |
| **Beige arena particles** | 460 evenly spread dark-brown particles with an alpha floor; the dark theme's 200-star field is unchanged. | PerfProbe: play p50 16.1 ms, 0 frames over 33 ms. |

**Final test evidence:** `mvn clean package` → `Tests run: 5, Failures: 0`; HoverProbe **PASS** (0 problems); LeaderboardProbe **PASS**; PerfProbe **DONE** (0 visible-stutter frames, game-over freeze intact, Exit → home); VisualCheck **PASS** (screenshots of menu, settings, arena and game-over in both themes); FinalProbe **PASS** (13/13 checks).

## 8. Recommended next steps

1. Sign the installer with an Authenticode certificate before distributing widely.
2. Add tests around score increments, golden-fruit boosts, pickups, and collision geometry.
3. Split `Main` into game model, renderer, and menu classes when a larger feature is next requested.
4. Add macOS/Linux jpackage builds on those operating systems if cross-platform distribution is needed.
5. Consider adding a proper save file (not just preferences) if features such as levels or unlocks are added.
