# Snake3D

A 3D arcade snake game built with Java 17 and JavaFX. You steer a snake, eat fruit, grow, and compete against six AI-controlled snakes (three original opponents plus three new colourful ones). It includes six snake skins, sound effects, a **volume slider**, a video menu background with a **Dark / Beige** theme switch in **Settings**, a local **Top-5 Leaderboard**, golden-fruit speed boosts, auto-pause when the window loses focus, and a persistent high score.

This repository originally contained only the game source and an older installer. It has been inspected, repaired, tested, and rebuilt into the Windows desktop application described below.

## What you get

| Item | Location | Notes |
|---|---|---|
| **Windows installer** | [`dist/installer/Snake3D-1.0.0.exe`](dist/installer/Snake3D-1.0.0.exe) | Recommended. Self-contained, per-user install, no Java required. |
| **Portable ZIP** | [`dist/portable/Snake3D-1.0.0-windows-portable.zip`](dist/portable/Snake3D-1.0.0-windows-portable.zip) | Unzip and run `Snake3D.exe`. No installation or admin rights. |
| **Unpacked portable app** | [`dist/portable/Snake3D/Snake3D.exe`](dist/portable/Snake3D/Snake3D.exe) | Same app image; useful for testing. |
| Corrected source | `src/`, `pom.xml` | Java 17 / JavaFX 17.0.16. |
| Tests | `src/test/java/` | JUnit pathfinding tests plus a manual GUI smoke harness. |
| Build script | [`scripts/build-desktop.ps1`](scripts/build-desktop.ps1) | Tests, builds the jar, app image, ZIP and installer. |
| Audit report | [`docs/AUDIT.md`](docs/AUDIT.md) | All bugs found, fixes, and test evidence. |

The `Snake3D-1.0.exe` file in the project root is the **old original installer**. It is not the repaired build. Use the files in `dist/` instead.

## System requirements

**To install and play (recommended path):**

- 64-bit Windows 10 or Windows 11.
- No Java, JRE, JDK, database, or other runtime needs to be installed. The app ships its own Java runtime.
- About 250 MB of free disk space for the installed app (installer is ~137 MB).
- A working sound output device and GPU/driver capable of normal desktop graphics. JavaFX uses Direct3D and falls back to software rendering when needed.

**To build from source:**

- JDK 17 or newer (tested with Temurin 17.0.20.1).
- Apache Maven 3.9+ (tested with 3.9.9).
- Windows for the jpackage installer step.
- WiX Toolset 3.x (`candle.exe` and `light.exe`) for the `.exe` installer. The portable app-image does not need WiX.

The build ran on Windows 11 x64 (build 26200). The portable and installed app were verified without any system Java available.

## Install on this laptop

1. Open the project folder and go to `dist\installer`.
2. Double-click **`Snake3D-1.0.0.exe`**.
3. If Windows SmartScreen appears (the build is not code-signed), choose **More info → Run anyway** only if you trust this locally built file.
4. Follow the installer. It is a **per-user install**, so administrator rights are not required. By default it installs under your local app data folder and creates:
   - a **Snake3D** Start Menu group,
   - a **Snake3D** desktop shortcut.
5. Launch **Snake3D** from the Start Menu or desktop shortcut.

The first launch can take a few seconds while the menu video loads.

### Portable version (no installation)

1. Go to `dist\portable`.
2. Copy **`Snake3D-1.0.0-windows-portable.zip`** anywhere you like.
3. Right-click the ZIP → **Extract All**.
4. Open the extracted folder and run **`Snake3D.exe`**.

Do not run `Snake3D.exe` directly from inside the ZIP preview; extract it first so the `app` and `runtime` folders stay together. Keep the folder contents together; the executable needs its bundled runtime.

### Uninstall

Open **Settings → Apps → Installed apps → Snake3D → Uninstall**. This removes the program, shortcuts, and installer registration. The high-score preference is described below.

The portable version does not install anything: delete its extracted folder to remove it.

## How to play

- **Arrow keys** or **W A S D** steer the snake. The game wraps around the screen edges.
- The start menu lets you choose a skin with the mouse, or with **Left/Right**, then **Enter** or **Space** to play.
- **Stop / Start** pauses and resumes.
- Alt-tabbing away (or clicking another window) **pauses automatically** — the HUD turns into **Start** — and clicking back resumes exactly where you left off.
- **Restart** starts a new round.
- **Home page** opens the skin menu. **Back to Game** closes it; **Play** starts a fresh round.
- **Settings** (home page, top right) toggles the sound, sets the master **volume** with a 0–100% slider (applies live to music and effects and is remembered), switches between the **Dark** and **Beige** themes, and shows the developer credit.
- **Leaderboard** (home page, top left) shows the five best runs saved on this computer. A run that makes the board also gets a **TOP-5 FINISH** badge on the Game Over card (`#1` is celebrated as a new top score).
- **Exit** closes the application.
- Eat fruit to score. Every 10 fruits eaten by a snake increases that snake's speed.
- A **golden fruit** appears periodically and gives a 10-second speed boost.
- Eagle, rodent, and mongoose pickups shorten a snake.
- The HUD shows the current score and the best score. The best score is saved between launches.
- Avoid the six AI snakes (grey, maroon, brown, violet, turquoise and pink). Touching a villain head or body ends the round.
- When the round ends a compact **Game Over** card appears; the board freezes behind it. The Game Over window is **borderless** (no title bar, no frame) and sits edge-to-edge over the frozen board. **Restart** begins a completely fresh round, **Exit** returns to the home page.

## Data, backup, and moving to another laptop

The game has **no database, no server, no API keys, and no network access**. All assets are bundled.

The only saved data is:

- the **last selected skin**,
- the **high score**,
- the **Top-5 leaderboard** scores, and
- the **sound on/off**, **volume level**, and **Dark/Beige theme** settings.

They are stored through Java Preferences in the current Windows user profile, under the `SnakeGame` preference node (registry path: `HKEY_CURRENT_USER\Software\JavaSoft\Prefs\SnakeGame`). They are not stored in the project folder and are not included in the installer.

- **Backup:** export that registry key, or simply note the high score. There is no save game to back up.
- **Migrating:** copying the installer or portable folder to another laptop does **not** copy the high score. The game starts a new high score there. To carry it over, export the registry key on the old laptop and import it on the new one while signed in as the same Windows user, or set it again by playing.
- **Reset:** delete the `SnakeGame` preference node/key, or use `regedit` to remove `HKEY_CURRENT_USER\Software\JavaSoft\Prefs\SnakeGame`. The game will recreate it with defaults.

## Copying the app to another laptop

1. Copy **`dist\installer\Snake3D-1.0.0.exe`** to a USB drive, OneDrive/Google Drive, or any file-transfer method.
2. On the other 64-bit Windows 10/11 laptop, copy the file to its Desktop or Downloads folder.
3. Double-click it and follow the installer.
4. Launch Snake3D from the Start Menu.

No Java or other software needs to be installed on the destination laptop. The installer bundles its runtime.

For the portable version, copy the **ZIP**, extract it on the destination laptop, and run `Snake3D.exe`. No admin rights are needed.

### Security-signing note

The installers are not digitally signed. Windows SmartScreen or an antivirus product may show a warning for a newly built, unsigned executable. This is expected for a local build. Verify the SHA-256 checksums below before running, and only bypass a warning if you trust the file.

## Development

From the project root, with JDK 17 and Maven available:

```powershell
$env:JAVA_HOME = "C:\path\to\jdk-17"
mvn clean test
mvn javafx:run
```

`mvn javafx:run` launches the game with the JavaFX modules on the module path. It was verified during this repair.

To run the built jar from the classpath:

```powershell
mvn package
cd target\desktop-input
java -jar snake3d.jar
```

The jar manifest lists the JavaFX jars, so keep `snake3d.jar` next to the dependency jars in `target\desktop-input`.

Run the automated tests only:

```powershell
mvn test
```

Run the manual GUI smoke harness after `mvn test-compile`:

```powershell
java -cp "target/classes;target/desktop-input/*;target/test-classes" UiSmokeHarness
# Optional window sizes:
java -Dui.width=800 -Dui.height=700 -cp "target/classes;target/desktop-input/*;target/test-classes" UiSmokeHarness
```

The harness opens the real game, checks the menu, fires Play, Stop/Start, Home, replay, and a game-over/restart flow, and saves screenshots in `.tmp/`. It is not part of `mvn test` because it needs a desktop display.

## Build the installers

```powershell
powershell -ExecutionPolicy Bypass -File scripts\build-desktop.ps1
```

The script:

1. Finds the project-local JDK/Maven/WiX in `.tools` if present, otherwise uses `JAVA_HOME` and `PATH`.
2. Runs `mvn clean package` (compiles and runs the JUnit tests).
3. Copies the app jar and JavaFX jars to `target/desktop-input`.
4. Creates the app image at `dist/portable/Snake3D`.
5. Creates `dist/portable/Snake3D-1.0.0-windows-portable.zip`.
6. Creates the WiX-based installer at `dist/installer/Snake3D-1.0.0.exe`.
7. Prints the artifact paths.

If WiX is missing, the script still builds the portable output and warns that the installer step was skipped.

### Checksums of the delivered build

| File | SHA-256 |
|---|---|
| `dist/installer/Snake3D-1.0.0.exe` | `02396B7AF6A9694A5976B6BBB1ADABA8DA26EFDCBFE24B275DAB4B8224BD82C5` |
| `dist/portable/Snake3D-1.0.0-windows-portable.zip` | `6576F4228C4A3E4DFB9469DBFD354156C337EC739CFE1D9C7FA78303C12D16F5` |

Check a file before running it:

```powershell
Get-FileHash .\dist\installer\Snake3D-1.0.0.exe -Algorithm SHA256
```

## Troubleshooting

**Windows says "Windows protected your PC" / SmartScreen**
The installer is unsigned. Use **More info → Run anyway** only after verifying the checksum. SmartScreen warnings are not evidence of a broken build.

**The app does not start after installation**
Try the portable build to determine whether the problem is the install or the runtime. Make sure the `app` and `runtime` folders were not separated from `Snake3D.exe`. Check Windows Event Viewer → Windows Logs → Application for a Java/JavaFX error.

**No sound**
Check the Windows volume mixer and that an audio output device is connected, and make sure Settings → Sound is **On** with the volume slider above 0%. The game uses JavaFX audio; muted system audio affects it.

**No music on the home page**
The menu music comes from `Backgroundaudio.wav`. Running from source, the game reads it from the project root (drop the file there to change the music); the packaged build bundles the same file automatically. If the file is missing, the menu is silent — everything else still works.

**High score is missing**
The high score is per Windows user. Reinstalling or using the portable version on another user account does not carry it over. If the preference store was cleared, it resets.

**The window is too small or the menu is cut off**
The window is resizable; the layout adapts down to 640×560 (logical pixels). Maximize or resize the window if needed. The repair fixed the previous menu overflow at narrow widths.

**The game freezes on the menu video**
The menu uses an embedded 54 MB video. On very slow machines it can take a moment. You can still choose a skin and press **Play**.

**Antivirus blocks the installer**
This is an unsigned executable built locally. Verify the checksum, or build the app image from source and run the portable version. Do not disable antivirus protection globally.

**Reset the app**
Uninstall it and delete the `HKCU\Software\JavaSoft\Prefs\SnakeGame` registry key. This also resets the saved high score and skin.

## Project layout

```
Snake3D/
├── src/main/java/          Main.java, VillainSnakeAI.java, Launcher.java
├── src/main/resources/     images, sounds, background video, icon
├── src/test/java/          VillainSnakeAITest.java, UiSmokeHarness.java
├── scripts/                build-desktop.ps1
├── docs/                   AUDIT.md
├── dist/                   generated installers and portable builds
│   ├── installer/          Snake3D-1.0.0.exe
│   └── portable/           app image + portable ZIP
├── pom.xml
└── README.md
```

## Known limitations

- Only Windows builds are delivered. The Maven profiles are cross-platform, but macOS/Linux packaging is not included or tested.
- The executables are unsigned; there is no code-signing certificate in this project.
- The provided installer/portable builds were tested on Windows 11 x64. A second physical laptop was not available for testing, but the bundle contains no machine-specific paths and no external runtime requirement.
- The AI opponents and collision logic are tested at the unit/UI level; a very long play session and every possible collision path were not exhaustively simulated.
- Audio output and video playback were not verified by listening/watching; the menu video rendered in the UI harness and the packaged launch was verified visually.
- The original `Snake3D-1.0.exe` in the project root is left untouched for reference.

See [`docs/AUDIT.md`](docs/AUDIT.md) for the full issue list, test matrix, and verification details.
