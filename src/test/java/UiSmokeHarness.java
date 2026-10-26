import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Manual GUI smoke test. Not picked up by surefire (name does not end in Test).
 *
 * <p>Run it after {@code mvn test-compile} with:</p>
 * <pre>
 * java -cp "target/classes;target/desktop-input/*;target/test-classes" UiSmokeHarness
 * </pre>
 *
 * <p>It opens the real game, fires the menu buttons, saves screenshots into
 * {@code .tmp/} and fails with a non-zero exit code if a basic flow breaks.</p>
 */
public final class UiSmokeHarness {

    public static void main(String[] args) {
        Application.launch(SmokeApp.class, args);
    }

    public static final class SmokeApp extends Application {
        private final List<String> failures = new ArrayList<>();
        private Main game;
        private Stage stage;
        private int step = 0;

        @Override
        public void start(Stage primaryStage) {
            this.stage = primaryStage;
            try {
                game = new Main();
                game.start(primaryStage);
                primaryStage.setX(20);
                primaryStage.setY(20);
                primaryStage.setWidth(Double.parseDouble(System.getProperty("ui.width", "1280")));
                primaryStage.setHeight(Double.parseDouble(System.getProperty("ui.height", "820")));
                primaryStage.show();
                step("startup");
            } catch (Throwable t) {
                t.printStackTrace();
                Platform.exit();
            }
        }

        private void step(String name) {
            step++;
            PauseTransition pause = new PauseTransition(Duration.seconds(1.5));
            pause.setOnFinished(e -> {
                try {
                    Scene scene = stage.getScene();
                    File shot = new File(".tmp/ui-" + step + "-" + name + ".png");
                    save(scene, shot);
                    System.out.println("STEP " + step + " " + name
                            + " scene=" + (int) scene.getWidth() + "x" + (int) scene.getHeight()
                            + " screenshot=" + shot.getPath());

                    if (step == 1) {
                        checkStartScreen(scene);
                        Button play = findButton(scene.getRoot(), "Play");
                        if (play == null) {
                            fail("Play button not found");
                            finish();
                            return;
                        }
                        dumpMenuLayout(scene);
                        Node focus = scene.getFocusOwner();
                        System.out.println("  focusOwner=" + (focus == null ? "null" : focus.getClass().getSimpleName()));
                        if (focus == null) {
                            fail("Menu did not receive keyboard focus");
                        } else {
                            // Exercise the real keyboard path (Enter starts the game)
                            // instead of invoking the button's action directly.
                            Event.fireEvent(focus, new KeyEvent(KeyEvent.KEY_PRESSED, "\r", "Enter",
                                    KeyCode.ENTER, false, false, false, false));
                        }
                        step("playing");
                    } else if (step == 2) {
                        checkPlaying(scene);
                        Button stop = findButton(scene.getRoot(), "Stop");
                        if (stop == null) {
                            fail("Stop button not found while playing");
                        } else {
                            stop.fire();
                        }
                        step("paused");
                    } else if (step == 3) {
                        Button start = findButton(scene.getRoot(), "Start");
                        if (start == null) {
                            fail("Stop button did not turn into Start while paused");
                        } else {
                            start.fire();
                        }
                        Button home = findButton(scene.getRoot(), "Home page");
                        if (home == null) {
                            fail("Home page button not found");
                        } else {
                            home.fire();
                        }
                        step("home");
                    } else if (step == 4) {
                        if (scene.getRoot().lookup("#skin-overlay") == null) {
                            fail("Home page did not show the skin overlay");
                        }
                        Button play = findButton(scene.getRoot(), "Play");
                        if (play == null) {
                            fail("Play button missing from home overlay");
                        } else {
                            play.fire();
                        }
                        step("replayed");
                    } else if (step == 5) {
                        if (scene.getRoot().lookup("#skin-overlay") != null) {
                            fail("Play from home overlay did not remove the overlay");
                        }
                        Button exit = findButton(scene.getRoot(), "Exit");
                        if (exit == null) {
                            fail("Exit button not found");
                        }
                        triggerGameOver();
                        step("game-over");
                    } else if (step == 6) {
                        // The helper below should have dismissed the dialog by now.
                        if (countDialogs() != 0) {
                            fail("Game-over dialog is still open (or more than one dialog opened)");
                            closeDialogs();
                        }
                        if (!readBoolean("running")) {
                            fail("Restart from the game-over dialog did not resume the game");
                        }
                        if (scene.getRoot().lookup("#skin-overlay") != null) {
                            fail("Restart unexpectedly opened the menu overlay");
                        }
                        finish();
                    }
                } catch (Throwable t) {
                    t.printStackTrace();
                    fail(t.getClass().getSimpleName() + ": " + t.getMessage());
                    finish();
                }
            });
            pause.play();
        }

        private void checkStartScreen(Scene scene) {
            if (scene.getRoot().lookup("#skin-overlay") == null) {
                fail("Start screen overlay is missing");
            }
            if (findButton(scene.getRoot(), "Play") == null) {
                fail("Start screen has no Play button");
            }
            List<ToggleButton> skins = new ArrayList<>();
            collect(scene.getRoot(), ToggleButton.class, skins);
            if (skins.size() != 6) {
                fail("Expected 6 skin options, found " + skins.size());
            }
            for (ToggleButton tb : skins) {
                javafx.geometry.Bounds bounds = tb.localToScene(tb.getBoundsInLocal());
                if (bounds.getMaxX() > scene.getWidth() + 0.5 || bounds.getMinX() < -0.5) {
                    fail("Skin option '" + tb.getText() + "' overflows the window: " + bounds);
                }
                if (bounds.getWidth() < 60) {
                    fail("Skin option '" + tb.getText() + "' is too narrow to read");
                }
            }
        }

        /** Invokes the private gameOver() and clicks Restart in the dialog it opens. */
        private void triggerGameOver() {
            PauseTransition dismiss = new PauseTransition(Duration.seconds(1.2));
            dismiss.setOnFinished(e -> {
                List<Stage> dialogs = dialogs();
                System.out.println("  game-over dialogs open: " + dialogs.size());
                if (dialogs.size() != 1) {
                    fail("Expected exactly one game-over dialog, found " + dialogs.size());
                }
                for (Stage dialog : dialogs) {
                    Button restart = findButton(dialog.getScene().getRoot(), "Restart");
                    if (restart == null) {
                        fail("Game-over dialog has no Restart button");
                    } else {
                        restart.fire();
                    }
                }
            });
            dismiss.play();
            try {
                Method gameOver = Main.class.getDeclaredMethod("gameOver", Stage.class);
                gameOver.setAccessible(true);
                gameOver.invoke(game, stage);
            } catch (Exception e) {
                fail("Could not trigger game over: " + e);
            }
        }

        private void closeDialogs() {
            for (Stage dialog : dialogs()) {
                dialog.close();
            }
        }

        private List<Stage> dialogs() {
            List<Stage> result = new ArrayList<>();
            for (Window window : Window.getWindows()) {
                if (window instanceof Stage s && s != stage
                        && s.getScene() != null
                        && s.getScene().getRoot() instanceof DialogPane) {
                    result.add(s);
                }
            }
            return result;
        }

        private int countDialogs() {
            return dialogs().size();
        }

        private boolean readBoolean(String fieldName) {
            try {
                Field field = Main.class.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field.getBoolean(game);
            } catch (Exception e) {
                fail("Could not read Main." + fieldName + ": " + e);
                return false;
            }
        }

        private void checkPlaying(Scene scene) {
            if (scene.getRoot().lookup("#skin-overlay") != null) {
                fail("Overlay was not removed after Play");
            }
            if (findButton(scene.getRoot(), "Stop") == null) {
                fail("Game did not start (Stop button missing)");
            }
            // Five fruits share the arena now, so the player may already have
            // scored by this point: assert the HUD text is present, not its value.
            if (findTextContaining(scene.getRoot(), "Score:") == null) {
                fail("Score HUD missing");
            }
        }

        private void dumpMenuLayout(Scene scene) {
            Button play = findButton(scene.getRoot(), "Play");
            if (play == null) return;
            System.out.println("  play sceneBounds=" + play.localToScene(play.getBoundsInLocal())
                    + " parent=" + play.getParent().getClass().getSimpleName()
                    + " parentBounds=" + play.getParent().getLayoutBounds());
            List<ToggleButton> skins = new ArrayList<>();
            collect(scene.getRoot(), ToggleButton.class, skins);
            for (ToggleButton tb : skins) {
                System.out.println("  skin=" + tb.getText()
                        + " sceneBounds=" + tb.localToScene(tb.getBoundsInLocal()));
            }
        }

        private void finish() {
            System.out.println("RESULT " + (failures.isEmpty() ? "PASS" : "FAIL"));
            for (String f : failures) {
                System.out.println("  FAILURE: " + f);
            }
            Platform.exit();
            if (!failures.isEmpty()) {
                Runtime.getRuntime().halt(1);
            }
        }

        private void fail(String message) {
            failures.add(message);
            System.out.println("  FAILURE: " + message);
        }

        private void save(Scene scene, File file) {
            try {
                file.getParentFile().mkdirs();
                WritableImage image = scene.snapshot(null);
                int w = (int) image.getWidth();
                int h = (int) image.getHeight();
                BufferedImage buffered = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
                PixelReader reader = image.getPixelReader();
                for (int y = 0; y < h; y++) {
                    for (int x = 0; x < w; x++) {
                        buffered.setRGB(x, y, reader.getArgb(x, y));
                    }
                }
                ImageIO.write(buffered, "png", file);
            } catch (Exception e) {
                fail("Could not save screenshot " + file + ": " + e);
            }
        }

        private static Button findButton(Parent root, String text) {
            List<Button> buttons = new ArrayList<>();
            collect(root, Button.class, buttons);
            for (Button b : buttons) {
                if (text.equals(b.getText())) return b;
            }
            return null;
        }

        private static String findTextContaining(Parent root, String needle) {
            List<javafx.scene.text.Text> texts = new ArrayList<>();
            collect(root, javafx.scene.text.Text.class, texts);
            for (javafx.scene.text.Text t : texts) {
                if (t.getText() != null && t.getText().contains(needle)) return t.getText();
            }
            return null;
        }

        private static <T extends Node> void collect(Node node, Class<T> type, List<T> out) {
            if (type.isInstance(node)) out.add(type.cast(node));
            if (node instanceof Parent parent) {
                for (Node child : parent.getChildrenUnmodifiable()) {
                    collect(child, type, out);
                }
            }
        }
    }
}
