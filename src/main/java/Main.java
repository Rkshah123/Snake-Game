import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.media.MediaView;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.geometry.Pos;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.geometry.Insets;
import javafx.scene.input.KeyCode;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Stop;
import java.net.URL;
import java.util.*;
import java.util.function.Supplier;

public class Main extends Application {
    private double headX = 400;
    private double headY = 300;
    private double speed = 3;
    private int direction = 0; // 0=right, 1=down, 2=left, 3=up
    private double windowWidth = 800;
    private double windowHeight = 700;
    private final double HEAD_RADIUS = 18;
    private final double BODY_RADIUS = 17;
    private final double SEGMENT_SPACING = 16;
    private final double BOTTOM_OFFSET = 50;
    private final double LINE_MARGIN = 0;
    private Sphere head;
    private List<Sphere> bodyParts = new ArrayList<>();
    private List<Sphere> fruits = new ArrayList<>();
    private Random random = new Random();
    private int score = 0;
    private boolean appleVisible = true;
    private List<Sphere> specialItems = new ArrayList<>();
    private boolean specialItemVisible = false;
    private PauseTransition specialDespawnTimer;
    private Sphere lastSpecialItem = null;
    private Sphere killerItemLastGame = null;
    private List<double[]> positionHistory = new ArrayList<>();
    private Text scoreText;
    private Line topLine;
    private Line bottomLine;
    private long startTime;
    private boolean running = true;
    private Group gameElements;
    private AnimationTimer timer;
    private double villainX = 200;
    private double villainY = 200;
    private int villainDirection = 0;
    private List<Sphere> villainBodyParts = new ArrayList<>();
    private List<double[]> villainHistory = new ArrayList<>();
    private HeadVisual villainHead;
    private boolean villainAlive = true;
    private int framesSinceVillainTurn = 0;
    private static final int VILLAIN_TURN_COOLDOWN_FRAMES = 5;
    private PauseTransition villainRespawnTimer;
    private double villain2X = 400;
    private double villain2Y = 400;
    private int villain2Direction = 2;
    private List<Sphere> villain2BodyParts = new ArrayList<>();
    private List<double[]> villain2History = new ArrayList<>();
    private HeadVisual villain2Head;
    private boolean villain2Alive = true;
    private int framesSinceVillain2Turn = 0;
    private static final int VILLAIN2_TURN_COOLDOWN_FRAMES = 5;
    private PauseTransition villain2RespawnTimer;
    private double villain3X = 500;
    private double villain3Y = 500;
    private int villain3Direction = 3;
    private List<Sphere> villain3BodyParts = new ArrayList<>();
    private List<double[]> villain3History = new ArrayList<>();
    private HeadVisual villain3Head;
    private boolean villain3Alive = true;
    private int framesSinceVillain3Turn = 0;
    private static final int VILLAIN3_TURN_COOLDOWN_FRAMES = 5;
    private PauseTransition villain3RespawnTimer;
    private AudioClip eatSound;
    private MediaPlayer gameOverSound;
    private double villainSpeed = 3;
    private double villain2Speed = 3;
    private double villain3Speed = 3;
    private final double BASE_SPEED = 3;
    private final double SPEED_INCREMENT = 1;
    private int playerFruitCount = 0;
    private int villainFruitCount = 0;
    private int villain2FruitCount = 0;
    private int villain3FruitCount = 0;
    private Sphere goldenFruit;
    private boolean goldenFruitVisible = false;
    private final double GOLDEN_FRUIT_RADIUS = HEAD_RADIUS * 1.3;
    private final double GOLDEN_FRUIT_SPEED = 5.0;
    private final int GOLDEN_FRUIT_SHOW_DURATION = 5;
    private final int GOLDEN_FRUIT_APPEAR_INTERVAL = 10;
    private PauseTransition goldenFruitDisappearTimer;
    private PauseTransition goldenFruitRespawnTimer;
    private javafx.animation.ScaleTransition goldenPulse;
    private double previousPlayerSpeed = BASE_SPEED;
    private double previousVillainSpeed = BASE_SPEED;
    private double previousVillain2Speed = BASE_SPEED;
    private double previousVillain3Speed = BASE_SPEED;
    private PauseTransition playerGoldenBoostTimer;
    private PauseTransition villainGoldenBoostTimer;
    private PauseTransition villain2GoldenBoostTimer;
    private PauseTransition villain3GoldenBoostTimer;
    private enum SkinType { CLASSIC, NEON, RAINBOW, OCEAN, LIME, COSMIC }
    private static final String PREFS_NODE = "SnakeGame";
    private static final String PREF_LAST_SKIN = "lastSkin";
    private static final String PREF_HIGH_SCORE = "highScore";
    private final Map<Sphere, PhongMaterial> dynamicMaterials = new java.util.WeakHashMap<>();
    private boolean gameOverShown = false;
    private SkinType currentSkin = SkinType.CLASSIC;
    private double rainbowBaseHue = 0.0;
    private final double RAINBOW_HUE_SPEED = 60;
    private final List<Color> COSMIC_PALETTE = List.of(
            Color.DARKVIOLET, Color.ROYALBLUE, Color.MEDIUMVIOLETRED, Color.WHITE
    );
    private final List<Color> NEON_PALETTE = List.of(
            Color.BLACK, Color.BROWN, Color.DARKBLUE, Color.DARKRED
    );
    private final List<Color> RAINBOW_PALETTE = List.of(
            Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.INDIGO, Color.VIOLET
    );
    private final List<Color> OCEAN_PALETTE = List.of(
            Color.DEEPSKYBLUE, Color.ROYALBLUE, Color.DARKSLATEBLUE,
            Color.LIGHTSKYBLUE, Color.MEDIUMBLUE, Color.CORNFLOWERBLUE
    );
    private final List<Color> VILLAIN_PALETTE = List.of(
            Color.DARKGREY, Color.GREY, Color.BLACK, Color.MAROON
    );
    private final List<Color> VILLAIN2_PALETTE = List.of(
            Color.RED, Color.CORAL, Color.FIREBRICK, Color.DEEPSKYBLUE
    );
    private final List<Color> VILLAIN3_PALETTE = List.of(
            Color.PERU, Color.BURLYWOOD, Color.CHOCOLATE, Color.CORNFLOWERBLUE
    );
    private final PhongMaterial classicMat = new PhongMaterial(Color.web("#2ECC71")); // fill
    private final PhongMaterial classicHeadMat = new PhongMaterial(Color.web("#2ECC71"));
    private final PhongMaterial neonMat = new PhongMaterial(Color.web("#111111"));
    private final PhongMaterial neonHeadMat = new PhongMaterial(Color.web("#111111"));
    private final javafx.scene.effect.Glow neonGlow = new javafx.scene.effect.Glow(0.9);
    private final PhongMaterial oceanMat       = new PhongMaterial(Color.LIGHTBLUE);
    private final PhongMaterial oceanHeadMat   = new PhongMaterial(Color.ROYALBLUE);
    private final PhongMaterial limeMat        = new PhongMaterial(Color.YELLOWGREEN);
    private final PhongMaterial limeHeadMat    = new PhongMaterial(Color.LIMEGREEN);
    private Timeline timeline;
    private AnimationTimer animationTimer;
    private Button homeButton;
    private int highScore = 0;
    private Scene sceneRef;
    private HeadVisual headGroup;
    private AudioClip clickSound;
    private Button stopStartButton;

    // ==============================================================  settings  =
    private static final String PREF_SOUND = "soundOn";
    private static final String PREF_THEME = "theme";
    private static final String PREF_TOP5 = "topScores";
    private static final String PREF_VOLUME = "volume";
    /** Home-page background music: this file, looked up in the project root first. */
    private static final String HOME_MUSIC_FILE = "Backgroundaudio.wav";
    private boolean soundEnabled = true;
    private boolean beigeTheme = false;
    /** Master volume 0..1 applied to every sound (music and effects). */
    private double volume = 1.0;
    /** Home-page background music; null when the file is missing (silent, no error). */
    private AudioClip homeMusic;
    /** Set while a focus-loss pause is holding the board; cleared when focus returns. */
    private boolean pausedByFocusLoss = false;
    /** Local top-5 runs, highest first (see loadTopScores/submitScore). */
    private final List<Integer> topScores = new ArrayList<>();
    /** Rank (1-5) of the run that just ended, 0 when it did not make the board. */
    private int lastRunRank;
    /** Developer shown in the Settings panel. */
    private static final String DEVELOPER_NAME = "Shaharukh Khan";

    // =============================================================  particles  =
    /** Trail/glow dots live in their own layer so the game group stays small. */
    private Group particleLayer;
    /** Soft halos behind food. */
    private Group haloLayer;
    /** Menu background video, so the Settings panel can mute it live. */
    private MediaPlayer menuVideoPlayer;
    /** Set when a theme change should bring the Settings panel back up. */
    private boolean reopenSettingsRequested = false;
    /** True while the Settings panel covers the home page: the menu video stays paused. */
    private boolean settingsPanelOpen = false;
    /** Parsed once and reused by every menu player: the video is 54 MB. */
    private Media menuMedia;
    /** Font size pinned per styled control, captured on first styling (see styleBtn). */
    private final java.util.Map<Node, Double> pinnedFontPx = new java.util.WeakHashMap<>();
    /** Bottom control bar (kept as fields so the theme can restyle it). */
    private Pane buttonPane;
    private Button restartButton;
    private Button exitGameButton;
    private final List<Circle> particlePool = new ArrayList<>();
    private final List<ParticleFx> particleActive = new ArrayList<>();
    private static final int MAX_PARTICLES = 900;
    /** One shared emission tick per 50 ms instead of one per rendered frame. */
    private static final long EMIT_INTERVAL_NS = 50_000_000L;
    private long lastEmitNanos = 0;
    private long lastFrameNanos = 0;
    private boolean emitThisFrame = false;

    private static final class ParticleFx {
        final Circle node;
        final double life;
        double age;
        ParticleFx(Circle node, double life) {
            this.node = node;
            this.life = life;
        }
    }

    // ======================================================  arena background  =
    private Canvas spaceCanvas;
    private GraphicsContext gc;
    private javafx.scene.image.WritableImage bgImage;

    //=============================  snake head visual: skull + eyes integrated into the head  =====================
    // Eyes sit inside the head sphere and only bulge through its surface, so they look grown-in rather than stuck on.
    // Local +X is the travel direction; the whole head rotates about its own centre, so the eyes always face forward.
    private static final double EYE_ANGLE_DEG = 38.0;
    private static final double EYE_DISTANCE = 13.0;
    private static final double EYE_RADIUS = 7.0;
    private static final double EYE_PUPIL_SCALE = 1.38;
    private static final double PUPIL_RADIUS = 3.6;
    private static final double TURN_EASE_DEGREES = 16.0;

    private static List<Node> buildEyes() {
        double angle = Math.toRadians(EYE_ANGLE_DEG);
        List<Node> eyes = new ArrayList<>(4);
        for (int side = -1; side <= 1; side += 2) {
            double ex = Math.cos(angle) * EYE_DISTANCE;
            double ey = Math.sin(angle) * EYE_DISTANCE * side;
            Sphere white = new Sphere(EYE_RADIUS);
            PhongMaterial whiteMat = new PhongMaterial(Color.web("#FAFAFA"));
            whiteMat.setSpecularColor(Color.WHITE);
            white.setMaterial(whiteMat);
            white.setTranslateX(ex);
            white.setTranslateY(ey);
            white.setTranslateZ(0);
            Sphere pupil = new Sphere(PUPIL_RADIUS);
            PhongMaterial pupilMat = new PhongMaterial(Color.web("#141414"));
            pupilMat.setSpecularColor(Color.web("#555555"));
            pupil.setMaterial(pupilMat);
            pupil.setTranslateX(ex * EYE_PUPIL_SCALE);
            pupil.setTranslateY(ey * EYE_PUPIL_SCALE);
            pupil.setTranslateZ(0);
            eyes.add(white);
            eyes.add(pupil);
        }
        return eyes;
    }

    /** A snake head: skull sphere + integrated eyes, rotated about its own centre. */
    private final class HeadVisual extends Group {
        private final Rotate turn = new Rotate(0, 0, 0, 0);
        private double targetAngle = 0;
        private double shownAngle = 0;

        HeadVisual(Sphere skull) {
            getChildren().add(skull);
            getChildren().addAll(buildEyes());
            getTransforms().add(turn);
        }

        void face(int direction) {
            targetAngle = (((direction % 4) + 4) % 4) * 90.0;
        }

        void snapTo(int direction) {
            face(direction);
            shownAngle = targetAngle;
            turn.setAngle(shownAngle);
        }

        /** Eases towards the target angle along the shortest path so turns are smooth, never jittery. */
        void ease() {
            double diff = ((targetAngle - shownAngle) % 360 + 540) % 360 - 180;
            if (diff > TURN_EASE_DEGREES) diff = TURN_EASE_DEGREES;
            else if (diff < -TURN_EASE_DEGREES) diff = -TURN_EASE_DEGREES;
            shownAngle += diff;
            turn.setAngle(shownAngle);
        }
    }

    private HeadVisual newHead(Color diffuse, Color specular, double x, double y, int direction) {
        Sphere skull = new Sphere(HEAD_RADIUS);
        PhongMaterial material = new PhongMaterial(diffuse);
        material.setSpecularColor(specular);
        skull.setMaterial(material);
        HeadVisual visual = new HeadVisual(skull);
        visual.setTranslateX(x);
        visual.setTranslateY(y);
        visual.snapTo(direction);
        return visual;
    }

    //===================================  extra enemy snakes (3 more opponents)  =================================
    private final List<Enemy> enemies = new ArrayList<>();
    private final Map<javafx.animation.Animation, Node> activeShrinkAnims = new HashMap<>();
    private static final int ENEMY_TURN_COOLDOWN_FRAMES = 5;

    private final class Enemy {
        final Color headColor;
        final Color bodyColor;
        final List<Color> palette;
        final double spawnFractionX;
        final double spawnFractionY;
        final int spawnDirection;
        double x;
        double y;
        int direction;
        double speed = 3;
        double previousSpeed = 3;
        int fruitCount = 0;
        int framesSinceTurn = 0;
        boolean alive = true;
        HeadVisual head;
        final List<Sphere> body = new ArrayList<>();
        final List<double[]> history = new ArrayList<>();
        PauseTransition respawnTimer;
        PauseTransition boostTimer;

        Enemy(Color headColor, Color bodyColor, List<Color> palette,
              double spawnFractionX, double spawnFractionY, int spawnDirection) {
            this.headColor = headColor;
            this.bodyColor = bodyColor;
            this.palette = palette;
            this.spawnFractionX = spawnFractionX;
            this.spawnFractionY = spawnFractionY;
            this.spawnDirection = spawnDirection;
            this.direction = spawnDirection;
        }
    }

    private void createEnemies(Group gameElements) {
        enemies.clear();
        enemies.add(new Enemy(Color.web("#8E24AA"), Color.web("#CE93D8"),
                List.of(Color.web("#CE93D8"), Color.web("#8E24AA"), Color.web("#E1BEE7")),
                0.15, 0.78, 0));
        enemies.add(new Enemy(Color.web("#00897B"), Color.web("#4DB6AC"),
                List.of(Color.web("#4DB6AC"), Color.web("#00897B"), Color.web("#B2DFDB")),
                0.55, 0.18, 2));
        enemies.add(new Enemy(Color.web("#D81B60"), Color.web("#F48FB1"),
                List.of(Color.web("#F48FB1"), Color.web("#D81B60"), Color.web("#FF80AB")),
                0.85, 0.80, 3));
        for (Enemy e : enemies) {
            buildEnemy(e, gameElements);
        }
    }

    private void buildEnemy(Enemy e, Group root) {
        e.alive = true;
        e.x = spawnX(e.spawnFractionX);
        e.y = spawnY(e.spawnFractionY);
        e.direction = e.spawnDirection;
        e.framesSinceTurn = 0;
        e.history.clear();
        e.head = newHead(e.headColor, e.bodyColor.brighter(), e.x, e.y, e.direction);
        root.getChildren().add(e.head);
        e.body.clear();
        PhongMaterial bodyMaterial = new PhongMaterial(e.bodyColor);
        bodyMaterial.setSpecularColor(e.headColor);
        for (int i = 0; i < 3; i++) {
            Sphere segment = new Sphere(BODY_RADIUS);
            segment.setMaterial(bodyMaterial);
            segment.setTranslateX(e.x - (i + 1) * SEGMENT_SPACING);
            segment.setTranslateY(e.y);
            e.body.add(segment);
        }
        root.getChildren().addAll(e.body);
    }

    private void killEnemy(Enemy e, Group root) {
        if (!e.alive) return;
        e.alive = false;
        if (e.respawnTimer != null) e.respawnTimer.stop();
        if (e.head != null) root.getChildren().remove(e.head);
        if (!e.body.isEmpty()) {
            root.getChildren().removeAll(e.body);
            e.body.clear();
        }
        e.respawnTimer = new PauseTransition(Duration.seconds(3));
        e.respawnTimer.setOnFinished(ev -> respawnEnemy(e, root));
        e.respawnTimer.play();
    }

    private void respawnEnemy(Enemy e, Group root) {
        if (e.head != null) root.getChildren().remove(e.head);
        if (!e.body.isEmpty()) {
            root.getChildren().removeAll(e.body);
            e.body.clear();
        }
        buildEnemy(e, root);
    }

    /** Stops every pending enemy action: nothing may respawn or change speed while the game is frozen. */
    private void freezeEnemies() {
        for (Enemy e : enemies) {
            if (e.respawnTimer != null) e.respawnTimer.stop();
            if (e.boostTimer != null) e.boostTimer.stop();
        }
    }

    private void resetEnemies(Group root) {
        for (Enemy e : enemies) {
            if (e.respawnTimer != null) e.respawnTimer.stop();
            if (e.boostTimer != null) e.boostTimer.stop();
            e.respawnTimer = null;
            e.boostTimer = null;
            e.speed = BASE_SPEED;
            e.previousSpeed = BASE_SPEED;
            e.fruitCount = 0;
            if (e.head != null) root.getChildren().remove(e.head);
            if (!e.body.isEmpty()) {
                root.getChildren().removeAll(e.body);
                e.body.clear();
            }
            buildEnemy(e, root);
        }
    }

    private void spawnEnemyTrail(Group gameElements, Enemy e) {
        if (!e.alive || e.body.isEmpty()) return;
        int particleCount = 3 + random.nextInt(2);
        for (int i = 0; i < particleCount; i++) {
            Sphere segment = e.body.get(random.nextInt(e.body.size()));
            emitParticle(segment.getTranslateX() + (Math.random() - 0.5) * 20,
                    segment.getTranslateY() + (Math.random() - 0.5) * 20,
                    e.palette.get(random.nextInt(e.palette.size())),
                    2 + Math.random() * 2, 1.0 + Math.random() * 0.5);
        }
    }

    private void spawnEnemyHeadGlow(Group gameElements, Enemy e) {
        if (!e.alive || e.head == null) return;
        emitParticle(e.head.getTranslateX(), e.head.getTranslateY(), e.headColor, 6, 0.6);
    }

    // ---- accessors for the three original villains (used by the extra enemies' clash rules) ----
    /** Moves one extra enemy: seeks fruit, avoids the player, follows the same rules as the originals. */
    private void updateEnemyAI(Group gameElements, Enemy e) {
        if (!e.alive || e.head == null) return;
        Sphere targetFruit = null;
        if (goldenFruitVisible) {
            targetFruit = goldenFruit;
        } else {
            double best = Double.MAX_VALUE;
            for (Sphere fruit : fruits) {
                if (!gameElements.getChildren().contains(fruit)) continue;
                double dx = fruit.getTranslateX() - e.x;
                double dy = fruit.getTranslateY() - e.y;
                double d2 = dx * dx + dy * dy;
                if (d2 < best) {
                    best = d2;
                    targetFruit = fruit;
                }
            }
        }
        if (targetFruit == null) {
            int steps = Math.max(5, (int) Math.ceil(SEGMENT_SPACING / Math.max(1.0, e.speed)));
            if (!pathSafeFromPlayer(e.direction, steps)) {
                for (int d = 0; d < 4; d++) {
                    if (pathSafeFromPlayer(d, steps)) {
                        e.direction = d;
                        break;
                    }
                }
            }
        } else {
            int[] dirs = {0, 1, 2, 3};
            double[] scores = new double[4];
            for (int i = 0; i < 4; i++) {
                double tx = e.x;
                double ty = e.y;
                switch (dirs[i]) {
                    case 0 -> tx += e.speed;
                    case 1 -> ty += e.speed;
                    case 2 -> tx -= e.speed;
                    case 3 -> ty -= e.speed;
                }
                tx = wrapX(tx);
                ty = wrapY(ty);
                double dx = targetFruit.getTranslateX() - tx;
                double dy = targetFruit.getTranslateY() - ty;
                scores[i] = Math.hypot(dx, dy);
                if (dirs[i] != e.direction) scores[i] += 2.0;
            }
            int bestDir = -1;
            double bestScore = Double.MAX_VALUE;
            int steps = Math.max(6, (int) Math.ceil(SEGMENT_SPACING / Math.max(1.0, e.speed)) + 2);
            for (int i = 0; i < 4; i++) {
                for (int d = 0; d < 4; d++) {
                    if (scores[d] < bestScore && pathSafeFromPlayer(dirs[d], steps)) {
                        bestScore = scores[d];
                        bestDir = dirs[d];
                    }
                }
            }
            if (bestDir == -1) {
                double bestClear = -1;
                int fallback = e.direction;
                for (int d = 0; d < 4; d++) {
                    double tx = e.x;
                    double ty = e.y;
                    switch (d) {
                        case 0 -> tx += e.speed;
                        case 1 -> ty += e.speed;
                        case 2 -> tx -= e.speed;
                        case 3 -> ty -= e.speed;
                    }
                    tx = wrapX(tx);
                    ty = wrapY(ty);
                    double minD2 = dist2(tx, ty, headX, headY);
                    for (Sphere seg : bodyParts) {
                        double d2 = dist2(tx, ty, seg.getTranslateX(), seg.getTranslateY());
                        if (d2 < minD2) minD2 = d2;
                    }
                    if (minD2 > bestClear) {
                        bestClear = minD2;
                        fallback = d;
                    }
                }
                bestDir = fallback;
            }
            if (bestDir != e.direction) {
                if (e.framesSinceTurn >= ENEMY_TURN_COOLDOWN_FRAMES || !pathSafeFromPlayer(e.direction, 2)) {
                    e.direction = bestDir;
                    e.framesSinceTurn = 0;
                } else {
                    e.framesSinceTurn++;
                }
            } else {
                e.framesSinceTurn++;
            }
        }
        switch (e.direction) {
            case 0 -> e.x += e.speed;
            case 1 -> e.y += e.speed;
            case 2 -> e.x -= e.speed;
            case 3 -> e.y -= e.speed;
        }
        double oldX = e.x;
        double oldY = e.y;
        e.x = wrapX(e.x);
        e.y = wrapY(e.y);
        if (e.x != oldX || e.y != oldY) {
            if (!e.history.isEmpty()) e.history.remove(e.history.size() - 1);
            e.history.add(new double[]{e.x, e.y});
        }
        e.head.setTranslateX(e.x);
        e.head.setTranslateY(e.y);
        e.head.face(e.direction);
        e.head.ease();
        e.history.add(new double[]{e.x, e.y});
        int maxHistorySize = (e.body.size() + 1) * (int) (SEGMENT_SPACING / e.speed) + 50;
        trimHistory(e.history, maxHistorySize);
        int index = 1;
        double elapsedSeconds = (System.nanoTime() - startTime) / 1_000_000_000.0;
        for (Sphere segment : e.body) {
            int posIndex = e.history.size() - index * (int) (SEGMENT_SPACING / e.speed);
            if (posIndex < 0) posIndex = 0;
            double[] pos = posIndex < e.history.size()
                    ? e.history.get(posIndex)
                    : new double[]{segment.getTranslateX(), segment.getTranslateY()};
            segment.setTranslateX(pos[0]);
            segment.setTranslateY(pos[1]);
            if (index == e.body.size()) {
                double maxAmplitude = SEGMENT_SPACING / 2.0;
                double rippleAmplitude = Math.min(5 + e.body.size() * 0.2, maxAmplitude);
                segment.setTranslateX(pos[0] + rippleAmplitude * Math.sin(elapsedSeconds * 5));
            }
            index++;
        }
    }

    private double oldVillainX(int id) {
        return id == 1 ? villainX : id == 2 ? villain2X : villain3X;
    }

    private double oldVillainY(int id) {
        return id == 1 ? villainY : id == 2 ? villain2Y : villain3Y;
    }

    private boolean oldVillainAlive(int id) {
        return id == 1 ? villainAlive : id == 2 ? villain2Alive : villain3Alive;
    }

    private List<Sphere> oldVillainBody(int id) {
        return id == 1 ? villainBodyParts : id == 2 ? villain2BodyParts : villain3BodyParts;
    }

    /** Extra enemies eat fruit, take golden boosts and get shortened by pickups, exactly like the originals. */
    private void updateEnemyPickups(Group gameElements) {
        for (Enemy e : enemies) {
            if (!e.alive || e.body.isEmpty()) continue;
            for (Sphere fruit : fruits) {
                if (!gameElements.getChildren().contains(fruit)) continue;
                if (Math.hypot(e.x - fruit.getTranslateX(), e.y - fruit.getTranslateY())
                        < HEAD_RADIUS + HEAD_RADIUS * 0.9) {
                    e.fruitCount++;
                    if (e.fruitCount % 10 == 0) e.speed += SPEED_INCREMENT;
                    playClip(eatSound);
                    Sphere newSegment = new Sphere(BODY_RADIUS);
                    PhongMaterial bodyMaterial = new PhongMaterial(e.bodyColor);
                    bodyMaterial.setSpecularColor(e.headColor);
                    newSegment.setMaterial(bodyMaterial);
                    Sphere lastSegment = e.body.get(e.body.size() - 1);
                    newSegment.setTranslateX(lastSegment.getTranslateX());
                    newSegment.setTranslateY(lastSegment.getTranslateY());
                    newSegment.setTranslateZ(lastSegment.getTranslateZ());
                    e.body.add(newSegment);
                    gameElements.getChildren().add(newSegment);
                    gameElements.getChildren().remove(fruit);
                    PauseTransition pt = new PauseTransition(Duration.seconds(0.2));
                    pt.setOnFinished(ev -> respawnSingleFruit(gameElements, fruit));
                    pt.play();
                    break;
                }
            }
            if (specialItemVisible) {
                for (Sphere special : specialItems) {
                    if (!gameElements.getChildren().contains(special)) continue;
                    if (Math.hypot(e.x - special.getTranslateX(), e.y - special.getTranslateY())
                            < HEAD_RADIUS + HEAD_RADIUS * 0.9) {
                        shrinkSnake(e.body, gameElements);
                        gameElements.getChildren().remove(special);
                        specialItemVisible = false;
                        break;
                    }
                }
            }
            if (goldenFruitVisible && goldenFruit != null
                    && Math.hypot(e.x - goldenFruit.getTranslateX(), e.y - goldenFruit.getTranslateY())
                    < HEAD_RADIUS + GOLDEN_FRUIT_RADIUS) {
                e.previousSpeed = e.speed;
                e.speed = GOLDEN_FRUIT_SPEED;
                if (e.boostTimer != null) e.boostTimer.stop();
                e.boostTimer = new PauseTransition(Duration.seconds(10));
                e.boostTimer.setOnFinished(ev -> e.speed = e.previousSpeed);
                e.boostTimer.play();
                gameElements.getChildren().remove(goldenFruit);
                goldenFruitVisible = false;
            }
        }
    }

    /** Extra enemies clash with the three original villains and with each other using the original rules. */
    private void resolveEnemyClashes(Group gameElements) {
        for (Enemy e : enemies) {
            if (!e.alive) continue;
            for (int id = 1; id <= 3; id++) {
                if (!oldVillainAlive(id)) continue;
                double ox = oldVillainX(id);
                double oy = oldVillainY(id);
                List<Sphere> otherBody = oldVillainBody(id);
                if (Math.hypot(e.x - ox, e.y - oy) < HEAD_RADIUS * 2) {
                    killEnemy(e, gameElements);
                    disableVillain(id, gameElements);
                    break;
                }
                boolean headHit = false;
                for (Sphere segment : otherBody) {
                    if (Math.hypot(e.x - segment.getTranslateX(), e.y - segment.getTranslateY())
                            < HEAD_RADIUS + BODY_RADIUS) {
                        headHit = true;
                        break;
                    }
                }
                if (headHit) {
                    killEnemy(e, gameElements);
                    break;
                }
                for (Sphere segment : e.body) {
                    if (Math.hypot(ox - segment.getTranslateX(), oy - segment.getTranslateY())
                            < HEAD_RADIUS + BODY_RADIUS) {
                        disableVillain(id, gameElements);
                        break;
                    }
                }
            }
        }
        for (int i = 0; i < enemies.size(); i++) {
            Enemy a = enemies.get(i);
            if (!a.alive) continue;
            for (int j = i + 1; j < enemies.size(); j++) {
                Enemy b = enemies.get(j);
                if (!b.alive) continue;
                if (Math.hypot(a.x - b.x, a.y - b.y) < HEAD_RADIUS * 2) {
                    killEnemy(a, gameElements);
                    killEnemy(b, gameElements);
                    break;
                }
                boolean aHit = false;
                for (Sphere segment : b.body) {
                    if (Math.hypot(a.x - segment.getTranslateX(), a.y - segment.getTranslateY())
                            < HEAD_RADIUS + BODY_RADIUS) {
                        aHit = true;
                        break;
                    }
                }
                if (aHit) {
                    killEnemy(a, gameElements);
                    break;
                }
                boolean bHit = false;
                for (Sphere segment : a.body) {
                    if (Math.hypot(b.x - segment.getTranslateX(), b.y - segment.getTranslateY())
                            < HEAD_RADIUS + BODY_RADIUS) {
                        bHit = true;
                        break;
                    }
                }
                if (bHit) {
                    killEnemy(b, gameElements);
                    break;
                }
            }
        }
    }

    //=======================================================  dynamic skin material  ==============================
    private void setDynamicColor(Sphere sphere, Color color) {
        PhongMaterial material = dynamicMaterials.computeIfAbsent(sphere, s -> new PhongMaterial(color));
        material.setDiffuseColor(color);
        if (sphere.getMaterial() != material) {
            sphere.setMaterial(material);
        }
    }

    //=======================================================  spawnSpecialItem  =======================================
    private void spawnSpecialItem(Group gameElements) {
        if (specialItemVisible) return;
        List<Sphere> candidates = new ArrayList<>(specialItems);
        candidates.remove(lastSpecialItem);
        if (killerItemLastGame != null) {
            candidates.remove(killerItemLastGame);
        }
        if (candidates.isEmpty()) {
            candidates = new ArrayList<>(specialItems);
            candidates.remove(lastSpecialItem);
        }
        Sphere special = candidates.get(random.nextInt(candidates.size()));
        double minX = HEAD_RADIUS;
        double maxX = windowWidth - HEAD_RADIUS;
        double minY = topLine.getStartY() + HEAD_RADIUS;
        double maxY = bottomLine.getStartY() - HEAD_RADIUS - LINE_MARGIN;
        double x = minX + random.nextDouble() * (maxX - minX);
        double y = minY + random.nextDouble() * (maxY - minY);
        special.setTranslateX(x);
        special.setTranslateY(y);
        gameElements.getChildren().removeAll(specialItems);
        gameElements.getChildren().add(special);
        specialItemVisible = true;
        lastSpecialItem = special;
        specialDespawnTimer.playFromStart();
    }
    //=======================================================  spawnSparks  ============================================
    private void spawnSparks(Button btn, Pane overlay) {
        Random rand = new Random();
        int sparkCount = 12;
        Point2D center = btn.localToScene(btn.getWidth() / 2, btn.getHeight() / 2);
        for (int i = 0; i < sparkCount; i++) {
            Circle spark = new Circle(2.5, Color.hsb(rand.nextInt(360), 1, 1));
            spark.setManaged(false);
            spark.setMouseTransparent(true);
            overlay.getChildren().add(spark);
            spark.setLayoutX(center.getX());
            spark.setLayoutY(center.getY());
            double angle = rand.nextDouble() * 2 * Math.PI;
            double distance = 30 + rand.nextDouble() * 25;
            double dx = Math.cos(angle) * distance;
            double dy = Math.sin(angle) * distance;
            TranslateTransition move = new TranslateTransition(Duration.millis(600), spark);
            move.setByX(dx);
            move.setByY(dy);
            FadeTransition fade = new FadeTransition(Duration.millis(600), spark);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);
            ParallelTransition anim = new ParallelTransition(move, fade);
            anim.setOnFinished(e -> overlay.getChildren().remove(spark));
            anim.play();
        }
    }

    // ============================================   buildSkinSelectorOverlay   =======================================
    private Pane buildSkinSelectorOverlay(Runnable onStart) {
        loadLastSkin();
        var overlay = new StackPane();
        overlay.setPickOnBounds(true);
        overlay.setMinSize(0, 0);
        overlay.setFocusTraversable(true);
        overlay.setId("skin-overlay");
        final List<javafx.animation.Animation> overlayAnims = new ArrayList<>();
        final List<MediaPlayer> overlayMediaPlayers = new ArrayList<>();
        AudioClip clickSound = new AudioClip(getClass().getResource("s1.wav").toExternalForm());
        // The theme change is consumed once, right here: the panel is reopened at
        // the end of this method and the flag can never leak into a later build
        // (a leaked flag used to skip the video's play() for the rest of the run).
        final boolean reopenPanel = reopenSettingsRequested;
        reopenSettingsRequested = false;
        // Static backdrop *behind* the video: the home page always has a finished,
        // themed background even while the video is still loading or if it fails.
        Region videoFallback = new Region();
        videoFallback.setMouseTransparent(true);
        videoFallback.setStyle("-fx-background-color: " + (beigeTheme
                ? "linear-gradient(from 0% 0% to 100% 100%, #F7EFDE, #EFE3CC 55%, #E3D1AE);"
                : "linear-gradient(from 0% 0% to 100% 100%, #0B1036, #1B1050 55%, #071026);"));
        overlay.getChildren().add(videoFallback);
        MediaView videoView = buildMenuVideoView(overlayMediaPlayers);
        videoView.setPreserveRatio(false);
        videoView.fitWidthProperty().bind(overlay.widthProperty());
        videoView.fitHeightProperty().bind(overlay.heightProperty());
        overlay.getChildren().add(videoView);
        // Theme scrim: keeps the menu readable in either theme without touching the video.
        Region themeScrim = new Region();
        themeScrim.setMouseTransparent(true);
        themeScrim.setStyle("-fx-background-color: " +
                (beigeTheme ? "rgba(247,240,227,0.80);" : "rgba(7,11,32,0.42);"));
        overlay.getChildren().add(themeScrim);
        // Warm gradient wordmark: vibrant on the dark video and on beige alike.
        var title = new Text("Snake Game");
        title.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 64));
        title.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, beigeTheme
                ? List.of(new Stop(0, Color.web("#E0632F")), new Stop(1, Color.web("#B23A2E")))
                : List.of(new Stop(0, Color.web("#FF7A59")), new Stop(1, Color.web("#FFC24D")))));
        title.setEffect(new DropShadow(20, beigeTheme ? Color.web("#E8A06A") : Color.web("#FF6B81")));
        VBox.setMargin(title, new Insets(0, 0, 16, 0));
        ToggleGroup group = new ToggleGroup();
        var optClassic = new ToggleButton("Classic");
        var optNeon    = new ToggleButton("Neon");
        var optRainbow = new ToggleButton("Rainbow");
        var optOcean = new ToggleButton("Ocean");
        var optLime  = new ToggleButton("Lime");
        var optCosmic = new ToggleButton("Cosmic");
        ToggleButton[] skinOptions = {optClassic, optNeon, optRainbow, optOcean, optLime, optCosmic};
        for (ToggleButton tb : skinOptions) {
            tb.setPrefSize(150, 96);
            tb.setMinWidth(0);
            // The tile's size is set in code so styleBtn can pin it forever
            // (an inline -fx-font-weight without -fx-font-size is what let CSS
            // rebuild buttons at the inherited 12 px on a focus/hover re-apply).
            tb.setFont(Font.font(16));
            // Stable hover that follows selected/hover/armed state; never rebuilds
            // effects or changes size (all inline, fixed insets).
            styleSkinToggle(tb);
            tb.setOnMouseEntered(e -> styleSkinToggle(tb));
            tb.setOnMouseExited(e -> styleSkinToggle(tb));
            tb.setOnMousePressed(e -> styleSkinToggle(tb));
            tb.setOnMouseReleased(e -> styleSkinToggle(tb));
        }
        HBox skinsRow = new HBox(16, skinOptions);
        skinsRow.setAlignment(Pos.CENTER);
        // Keep all six skins on one row at any supported window size.
        Runnable resizeSkinOptions = () -> {
            double available = overlay.getWidth() > 0 ? overlay.getWidth() : windowWidth;
            if (sceneRef != null && sceneRef.getWidth() > 0) {
                available = Math.min(available, sceneRef.getWidth());
            }
            double optionWidth = Math.max(78, (available - 76 - 5 * 16) / 6.0);
            for (ToggleButton tb : skinOptions) {
                tb.setPrefWidth(optionWidth);
            }
        };
        overlay.widthProperty().addListener((obs, oldW, newW) -> resizeSkinOptions.run());
        if (sceneRef != null) {
            sceneRef.widthProperty().addListener((obs, oldW, newW) -> resizeSkinOptions.run());
        }
        resizeSkinOptions.run();
        optClassic.setTextFill(Color.WHITE);
        optNeon.setTextFill(Color.WHITE);
        optRainbow.setTextFill(Color.WHITE);
        optOcean.setTextFill(Color.WHITE);
        optLime.setTextFill(Color.WHITE);
        optCosmic.setTextFill(Color.WHITE);
        optClassic.setToggleGroup(group);
        optNeon.setToggleGroup(group);
        optRainbow.setToggleGroup(group);
        optOcean.setToggleGroup(group);
        optLime.setToggleGroup(group);
        optCosmic.setToggleGroup(group);
        switch (currentSkin) {
            case CLASSIC -> group.selectToggle(optClassic);
            case NEON    -> group.selectToggle(optNeon);
            case RAINBOW -> group.selectToggle(optRainbow);
            case OCEAN   -> group.selectToggle(optOcean);
            case LIME    -> group.selectToggle(optLime);
            case COSMIC -> group.selectToggle(optCosmic);
        }
        HBox preview = new HBox(24);
        preview.setAlignment(Pos.CENTER);
        List<Circle> previewSegs = java.util.stream.IntStream.range(0, 7)
                .mapToObj(i -> new Circle(16, Color.web("#2ECC71")))
                .toList();
        preview.getChildren().addAll(previewSegs);
        for (ToggleButton tb : skinOptions) {
            styleSkinToggle(tb);
        }
        group.selectedToggleProperty().addListener((o, oldT, newT) -> {
            if (newT == null && oldT != null) {
                group.selectToggle(oldT);
                return;
            }
            playClip(clickSound);
            for (ToggleButton tb : skinOptions) {
                styleSkinToggle(tb);
            }
            SkinType sel = newT == optClassic ? SkinType.CLASSIC
                    : (newT == optNeon ? SkinType.NEON
                    : (newT == optRainbow ? SkinType.RAINBOW
                    : (newT == optOcean ? SkinType.OCEAN
                    : (newT == optLime ? SkinType.LIME
                    : SkinType.COSMIC))));
            switch (sel) {
                case CLASSIC -> previewSegs.forEach(c -> c.setFill(Color.web("#2ECC71")));
                case NEON    -> previewSegs.forEach(c -> c.setFill(Color.web("#00FFFF")));
                case RAINBOW -> {
                    for (int i = 0; i < previewSegs.size(); i++) {
                        previewSegs.get(i).setFill(Color.hsb((i * 60) % 360, 1, 1));
                    }
                }
                case COSMIC -> {
                    for (int i = 0; i < previewSegs.size(); i++) {
                        Color c = switch (i % 4) {
                            case 0 -> Color.DARKVIOLET;
                            case 1 -> Color.ROYALBLUE;
                            case 2 -> Color.MEDIUMVIOLETRED;
                            default -> Color.WHITE; // sparkle
                        };
                        previewSegs.get(i).setFill(c);
                    }
                }
                case OCEAN   -> previewSegs.forEach(c -> c.setFill(Color.ROYALBLUE));
                case LIME    -> previewSegs.forEach(c -> c.setFill(Color.LIMEGREEN));
            }
        });
        Button play = new Button("Play");
        play.setPrefSize(90, 90);
        play.setFont(Font.font(22));
        hoverize(play, () -> btnBase("play"), () -> btnHover("play"), () -> btnPress("play"), btnText("play"), 70);
        play.setOnAction(e -> {
            stopHomeMusic();
            playClip(clickSound);
            spawnSparks(play, overlay);
            PauseTransition delay = new PauseTransition(Duration.seconds(1));
            delay.setOnFinished(ev -> {
                resetSnake();
                running = true;
                stopStartButton.setText("Stop");
                resumeGoldenFruitTimers();
                Toggle t = group.getSelectedToggle();
                currentSkin = (t == optClassic ? SkinType.CLASSIC
                        : (t == optNeon ? SkinType.NEON
                        : (t == optRainbow ? SkinType.RAINBOW
                        : (t == optOcean ? SkinType.OCEAN
                        : (t == optLime ? SkinType.LIME
                        : SkinType.COSMIC)))));
                saveLastSkin();
                onStart.run();
                ((Pane) overlay.getParent()).getChildren().remove(overlay);
            });
            delay.play();
        });
        overlay.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A) {
                if (group.getSelectedToggle() == optLime) group.selectToggle(optOcean);
                else if (group.getSelectedToggle() == optOcean) group.selectToggle(optRainbow);
                else if (group.getSelectedToggle() == optRainbow) group.selectToggle(optNeon);
                else if (group.getSelectedToggle() == optNeon) group.selectToggle(optClassic);
            } else if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) {
                if (group.getSelectedToggle() == optClassic) group.selectToggle(optNeon);
                else if (group.getSelectedToggle() == optNeon) group.selectToggle(optRainbow);
                else if (group.getSelectedToggle() == optRainbow) group.selectToggle(optOcean);
                else if (group.getSelectedToggle() == optOcean) group.selectToggle(optLime);
            } else if (e.getCode() == KeyCode.ENTER || e.getCode() == KeyCode.SPACE) {
                play.fire();
            } else if (e.getCode() == KeyCode.ESCAPE) {
                Platform.exit();
            }
        });
        Group starfield = new Group();
        // Static stars: 80 indefinite FadeTransitions used to repaint the whole menu
        // every frame (that constant recomposite was the source of the hover flicker).
        for (int i = 0; i < 80; i++) {
            Circle star = new Circle(Math.random() * 1.5 + 0.5,
                    beigeTheme ? Color.web("#A5854F") : Color.AQUA);
            star.setOpacity(Math.random() * 0.8);
            star.setTranslateX((Math.random() - 0.5) * 400);
            star.setTranslateY((Math.random() - 0.5) * 250);
            starfield.getChildren().add(star);
        }
        Group sparksGroup = new Group();
        int numSparks = 20;
        double radiusX = 160;
        double radiusY = 100;
        for (int i = 0; i < numSparks; i++) {
            Circle spark = new Circle(2, beigeTheme ? Color.web("#C2653A") : Color.AQUA);
            spark.setCenterX(radiusX * Math.cos(2 * Math.PI * i / numSparks));
            spark.setCenterY(radiusY * Math.sin(2 * Math.PI * i / numSparks));
            spark.setOpacity(0.55);
            sparksGroup.getChildren().add(spark);
        }
        // One timeline rotating the whole ring: same orbiting look, 20x fewer animations.
        Timeline orbit = new Timeline(new KeyFrame(Duration.seconds(30),
                new KeyValue(sparksGroup.rotateProperty(), 360)));
        orbit.setCycleCount(Animation.INDEFINITE);
        orbit.play();
        overlayAnims.add(orbit);
        Button backToGame = new Button("Back to Game");
        backToGame.setPrefSize(140, 50);
        hoverize(backToGame, () -> btnBase("back"), () -> btnHover("back"), () -> btnPress("back"), btnText("back"), 15);
        backToGame.setOnAction(ev -> {
            playClip(clickSound);
            spawnSparks(backToGame, overlay);
            stopHomeMusic();
            PauseTransition delay = new PauseTransition(Duration.seconds(1));
            delay.setOnFinished(e -> {
                running = true;
                if (stopStartButton != null) stopStartButton.setText("Stop");
                timer.start();
                resumeGoldenFruitTimers();
                if (overlay.getParent() instanceof Pane p) {
                    p.getChildren().remove(overlay);
                }
            });
            delay.play();
        });
        VBox card = new VBox(18, title, skinsRow, preview, play);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(20));
        card.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        card.setStyle("-fx-background-color: rgba(20,20,20,0.9); -fx-background-radius: 16; -fx-padding: 20;");
        DropShadow glow = new DropShadow();
        // Soft, theme-aware halo instead of the old neon cyan that clashed with
        // the muted button palette.
        glow.setColor(beigeTheme ? Color.rgb(194, 101, 58, 0.50) : Color.rgb(95, 140, 255, 0.55));
        glow.setRadius(beigeTheme ? 24 : 28);
        glow.setSpread(0.18);
        card.setEffect(glow);
        card.setOpacity(0);
        card.setScaleX(0.3);
        card.setScaleY(0.3);
        card.setRotate(-15);
        Timeline entrance = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(card.opacityProperty(), 0),
                        new KeyValue(card.scaleXProperty(), 0.3),
                        new KeyValue(card.scaleYProperty(), 0.3),
                        new KeyValue(card.rotateProperty(), -15)
                ),
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(card.opacityProperty(), 1),
                        new KeyValue(card.scaleXProperty(), 1.05),
                        new KeyValue(card.scaleYProperty(), 1.05),
                        new KeyValue(card.rotateProperty(), 5)
                ),
                new KeyFrame(Duration.seconds(1.2),
                        new KeyValue(card.scaleXProperty(), 1),
                        new KeyValue(card.scaleYProperty(), 1),
                        new KeyValue(card.rotateProperty(), 0)
                )
        );
        startHomeMusic();
        entrance.play();
        StackPane cardContainer = new StackPane(starfield, sparksGroup, card);
        overlay.getChildren().add(cardContainer);
        StackPane.setAlignment(cardContainer, Pos.CENTER);
        StackPane.setAlignment(backToGame, Pos.BOTTOM_LEFT);
        StackPane.setMargin(backToGame, new Insets(0, 0, 30, 30));
        overlay.getChildren().add(backToGame);
        overlay.sceneProperty().addListener((o, ov, nv) -> {
            if (nv != null) {
                // Focus must be granted after the overlay is laid out, otherwise
                // the menu's arrow/Enter/Escape shortcuts never receive key events.
                Platform.runLater(overlay::requestFocus);
            }
        });
        Button exitButton = new Button("Exit");
        exitButton.setPrefSize(140, 50); // wider rectangle
        exitButton.setFont(Font.font(22));
        hoverize(exitButton, () -> btnBase("exit"), () -> btnHover("exit"), () -> btnPress("exit"), btnText("exit"), 15);
        exitButton.setOnAction(e -> {
            playClip(clickSound);
            spawnSparks(exitButton, overlay);
            PauseTransition delay = new PauseTransition(Duration.seconds(1));
            delay.setOnFinished(ev -> Platform.exit());
            delay.play();
        });
        StackPane.setAlignment(exitButton, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(exitButton, new Insets(0, 30, 30, 0));
        overlay.getChildren().add(exitButton);
        card.setStyle(
                "-fx-background-color: transparent;" + // solid teal
                        "-fx-background-radius: 16;" +
                        "-fx-padding: 24;"
        );
        overlay.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene == null) {
                for (javafx.animation.Animation a : overlayAnims) {
                    try { a.stop(); } catch (Throwable ignore) {}
                }
                for (MediaPlayer mp : overlayMediaPlayers) {
                    try { mp.stop(); mp.dispose(); } catch (Throwable ignore) {}
                }
                overlayAnims.clear();
                overlayMediaPlayers.clear();
                if (menuVideoPlayer != null && overlayMediaPlayers.isEmpty()) menuVideoPlayer = null;
            }
        });
        // ----------------------------------------------------------  settings  --
        Button settingsButton = new Button("Settings");
        settingsButton.setFont(Font.font(17));
        hoverize(settingsButton, () -> btnBase("accent"), () -> btnHover("accent"), () -> btnPress("accent"), btnText("accent"), 20);
        settingsButton.setOnAction(ev -> {
            playClip(clickSound);
            openSettingsPanel(overlay);
        });
        StackPane.setAlignment(settingsButton, Pos.TOP_RIGHT);
        StackPane.setMargin(settingsButton, new Insets(58, 30, 0, 0));
        overlay.getChildren().add(settingsButton);
        // ------------------------------------------------------  leaderboard  --
        // Mirrors Settings on the top-left: opens the local top-5 board.
        Button leaderboardButton = new Button("Leaderboard");
        leaderboardButton.setFont(Font.font(15));
        hoverize(leaderboardButton, () -> btnBase("amber"), () -> btnHover("amber"),
                () -> btnPress("amber"), btnText("amber"), 20);
        leaderboardButton.setOnAction(ev -> {
            playClip(clickSound);
            openLeaderboardPanel(overlay);
        });
        StackPane.setAlignment(leaderboardButton, Pos.TOP_LEFT);
        StackPane.setMargin(leaderboardButton, new Insets(58, 0, 0, 30));
        overlay.getChildren().add(leaderboardButton);
        if (reopenPanel) {
            openSettingsPanel(overlay);
        }
        return overlay;
    }

    /**
     * Builds the home-page background video so it shows up reliably on every launch
     * and every return to the home page:
     * <ul>
     *   <li>playback starts only once the media reports READY (starting before it is
     *       loaded is what made the video sometimes never appear),</li>
     *   <li>it never starts while the Settings panel is up (see {@link #settingsPanelOpen}),</li>
     *   <li>it retries once on a loading/decoding error and then gives up quietly, so
     *       the static backdrop behind it keeps the home page intact.</li>
     * </ul>
     */
    private MediaView buildMenuVideoView(List<MediaPlayer> players) {
        MediaView view = new MediaView();
        URL videoUrl = getClass().getResource("/bgvideo.mp4");
        if (videoUrl == null) {
            System.err.println("[menu] background video /bgvideo.mp4 not found; using the static backdrop");
            return view;
        }
        attachMenuVideo(view, videoUrl.toExternalForm(), players, new boolean[]{false});
        return view;
    }

    /** Creates one attempt at the menu video; on failure it retries once, then stops. */
    private void attachMenuVideo(MediaView view, String path, List<MediaPlayer> players, boolean[] retried) {
        try {
            // The media is parsed once for the whole session; every rebuild after a
            // game (Home page, theme change) reuses it instead of re-reading 54 MB.
            if (menuMedia == null) menuMedia = new Media(path);
            MediaPlayer player = new MediaPlayer(menuMedia);
            player.setCycleCount(MediaPlayer.INDEFINITE);
            player.setMute(!soundEnabled);
            player.setAutoPlay(false);
            // Only start when the media is actually loaded and no panel covers it.
            player.setOnReady(() -> {
                if (!settingsPanelOpen) player.play();
            });
            player.setOnError(() -> {
                System.err.println("[menu] video failed: " + player.getError());
                try { player.dispose(); } catch (Throwable ignore) { }
                players.remove(player);
                if (menuVideoPlayer == player) menuVideoPlayer = null;
                if (!retried[0]) {
                    retried[0] = true;
                    attachMenuVideo(view, path, players, retried);
                } else {
                    view.setMediaPlayer(null);   // static backdrop stays visible
                }
            });
            players.add(player);
            menuVideoPlayer = player;
            view.setMediaPlayer(player);
            if (!settingsPanelOpen) player.play();
        } catch (Throwable t) {
            System.err.println("[menu] video init failed: " + t);
            view.setMediaPlayer(null);
        }
    }

    // -------------------------------------------------------------  settings  --
    /** Opens the Settings panel over the home page (idempotent). */
    private void openSettingsPanel(Pane overlay) {
        if (overlay == null || overlay.lookup("#settings-layer") != null) return;
        if (overlay.lookup("#leaderboard-layer") != null) return;   // one modal at a time
        // The panel is modal: freeze the menu's video so it stops decoding and
        // repainting behind the scrim while the user interacts with the buttons.
        settingsPanelOpen = true;
        if (menuVideoPlayer != null) menuVideoPlayer.pause();
        StackPane layer = new StackPane();
        layer.setId("settings-layer");
        layer.setPickOnBounds(true);
        Region scrim = new Region();
        scrim.setStyle("-fx-background-color: " + (beigeTheme
                ? "rgba(66,50,28,0.45);" : "rgba(3,5,18,0.62);"));
        scrim.setOnMouseClicked(e -> closeHomePanel(layer));
        layer.getChildren().add(scrim);
        layer.getChildren().add(buildSettingsCard(layer));
        overlay.getChildren().add(layer);
    }

    /** Opens the Leaderboard panel over the home page (idempotent). */
    private void openLeaderboardPanel(Pane overlay) {
        if (overlay == null || overlay.lookup("#leaderboard-layer") != null) return;
        if (overlay.lookup("#settings-layer") != null) return;       // one modal at a time
        loadTopScores();   // show the saved board as it is on disk right now
        settingsPanelOpen = true;   // same modal contract: pause the menu video
        if (menuVideoPlayer != null) menuVideoPlayer.pause();
        StackPane layer = new StackPane();
        layer.setId("leaderboard-layer");
        layer.setPickOnBounds(true);
        Region scrim = new Region();
        scrim.setStyle("-fx-background-color: " + (beigeTheme
                ? "rgba(66,50,28,0.45);" : "rgba(3,5,18,0.62);"));
        scrim.setOnMouseClicked(e -> closeHomePanel(layer));
        layer.getChildren().add(scrim);
        layer.getChildren().add(buildLeaderboardCard(layer));
        overlay.getChildren().add(layer);
    }

    /** Closes whichever home-page modal is up and lets the menu video resume. */
    private void closeHomePanel(StackPane layer) {
        if (layer == null) return;
        Node parent = layer.getParent();
        if (parent instanceof Pane p) p.getChildren().remove(layer);
        settingsPanelOpen = false;
        if (menuVideoPlayer != null) menuVideoPlayer.play();
    }

    /** The Leaderboard card: the five best runs stored on this computer. */
    private VBox buildLeaderboardCard(StackPane layer) {
        VBox card = new VBox(12);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(26, 36, 26, 36));
        card.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        card.setStyle("-fx-background-color: " + panelBgHex() + "F2; " +
                "-fx-background-radius: 20; " +
                "-fx-border-color: " + accent2Hex() + "; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 20;");
        card.setEffect(new DropShadow(34, beigeTheme
                ? Color.rgb(194, 101, 58, 0.55) : Color.rgb(124, 92, 255, 0.60)));

        Text heading = new Text("TOP RUNS");
        heading.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 30));
        heading.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, beigeTheme
                ? List.of(new Stop(0, Color.web("#B23A2E")), new Stop(1, Color.web("#E0A03A")))
                : List.of(new Stop(0, Color.web("#9B6BFF")), new Stop(1, Color.web("#5FE3FF")))));
        heading.setEffect(new DropShadow(16, beigeTheme ? Color.web("#C2653A") : Color.web("#7C4DFF")));

        Region divider = new Region();
        divider.setMaxWidth(Double.MAX_VALUE);
        divider.setPrefHeight(1);
        divider.setStyle("-fx-background-color: " + hudBorderHex() + ";");

        card.getChildren().addAll(heading, divider);
        for (int i = 0; i < 5; i++) {
            final int rank = i + 1;
            final boolean has = i < topScores.size();
            Text rankText = new Text("#" + rank);
            rankText.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 17));
            rankText.setFill(Color.web(rank == 1
                    ? (beigeTheme ? "#C2653A" : "#FFD976")
                    : (beigeTheme ? "#7C6B52" : "#9FA8DA")));
            Text scoreText = new Text(has ? String.valueOf(topScores.get(i)) : "\u2014");
            scoreText.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 17));
            scoreText.setFill(has ? panelText() : panelMuted());
            HBox row = new HBox(14, rankText, scoreText);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setMinWidth(320);
            row.setPrefWidth(320);
            row.setPadding(new Insets(7, 14, 7, 14));
            row.setStyle("-fx-background-color: " + (has
                    ? (rank == 1 ? (beigeTheme ? "#C2653A2E" : "#FFD54F2E")
                                 : (beigeTheme ? "#7C6B521A" : "#FFFFFF12"))
                    : "transparent") + "; -fx-background-radius: 10;");
            card.getChildren().add(row);
        }

        Text note = new Text("Saved on this computer\nHigh score: " + highScore);
        note.setFont(Font.font(14));
        note.setFill(panelMuted());
        note.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);

        Button close = new Button("Close");
        close.setFont(Font.font(16));
        hoverize(close, () -> btnBase("close"), () -> btnHover("close"),
                () -> btnPress("close"), btnText("close"), 20);
        close.setOnAction(e -> closeHomePanel(layer));

        card.getChildren().addAll(note, close);
        return card;
    }

    /** The Settings card: Sound, Theme and the developer credit. */
    private VBox buildSettingsCard(StackPane layer) {
        VBox card = new VBox(16);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(26, 36, 26, 36));
        card.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        card.setStyle("-fx-background-color: " + panelBgHex() + "F2; " +
                "-fx-background-radius: 20; " +
                "-fx-border-color: " + accentHex() + "; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 20;");
        card.setEffect(new DropShadow(34, beigeTheme
                ? Color.rgb(194, 101, 58, 0.55) : Color.rgb(124, 92, 255, 0.60)));

        Text heading = new Text("SETTINGS");
        heading.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 30));
        heading.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, beigeTheme
                ? List.of(new Stop(0, Color.web("#B23A2E")), new Stop(1, Color.web("#E0A03A")))
                : List.of(new Stop(0, Color.web("#9B6BFF")), new Stop(1, Color.web("#5FE3FF")))));
        heading.setEffect(new DropShadow(16, beigeTheme ? Color.web("#C2653A") : Color.web("#7C4DFF")));

        Text soundLabel = settingLabel("Sound");
        Button soundBtn = settingToggle(soundEnabled ? "On" : "Off", soundEnabled ? "toggleOn" : "toggleOff");
        soundBtn.setOnAction(e -> {
            soundEnabled = !soundEnabled;
            saveSettings();
            soundBtn.setText(soundEnabled ? "On" : "Off");
            styleStateToggle(soundBtn, soundEnabled);
            if (menuVideoPlayer != null) menuVideoPlayer.setMute(!soundEnabled);
            if (gameOverSound != null) gameOverSound.setMute(!soundEnabled);
            if (!soundEnabled) {
                if (gameOverSound != null) gameOverSound.stop();
                stopHomeMusic();
            } else {
                playClip(clickSound);
                startHomeMusic();
            }
        });

        Text themeLabel = settingLabel("Theme");
        Button themeBtn = settingToggle(beigeTheme ? "Beige" : "Dark", "theme");
        themeBtn.setOnAction(e -> {
            beigeTheme = !beigeTheme;
            saveSettings();
            applyTheme();                 // arena, rails, HUD and buttons follow the theme
            reopenSettingsRequested = true;
            rebuildHomeOverlay();
        });

        HBox soundRow = settingRow(soundLabel, soundBtn);
        HBox themeRow = settingRow(themeLabel, themeBtn);
        Text devLabel = settingLabel("Developer");
        Text devValue = new Text(DEVELOPER_NAME);
        devValue.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 18));
        devValue.setFill(Color.web(beigeTheme ? "#B4552F" : "#FFD54F"));
        HBox devRow = settingRow(devLabel, devValue);

        Button close = new Button("Close");
        close.setFont(Font.font(16));
        hoverize(close, () -> btnBase("close"), () -> btnHover("close"), () -> btnPress("close"), btnText("close"), 20);
        close.setOnAction(e -> closeHomePanel(layer));

        Region divider = new Region();
        divider.setPrefHeight(1);
        divider.setStyle("-fx-background-color: " + hudBorderHex() + ";");

        // Volume slider directly under Sound On/Off: 0-100%, live, remembered.
        Text volumeLabel = settingLabel("Volume");
        Text volumePct = new Text(Math.round(volume * 100) + "%");
        volumePct.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 16));
        volumePct.setFill(Color.web(accentHex()));
        Pane volumeTrack = buildVolumeTrack(volumePct);
        HBox volumeRow = new HBox(18, volumeLabel, volumeTrack, volumePct);
        volumeRow.setAlignment(Pos.CENTER_LEFT);
        volumeRow.setMinWidth(360);

        card.getChildren().addAll(heading, divider, soundRow, volumeRow, themeRow, devRow, close);
        return card;
    }

    /**
     * Horizontal 0-100% volume slider built from plain nodes so it matches the
     * Settings card exactly and never rebuilds CSS while you drag it. Dragging the
     * track (or the thumb) changes the volume live - music and sound effects - and
     * saves it, so the position is still there the next time the panel opens.
     */
    private Pane buildVolumeTrack(Text percentOut) {
        Pane track = new Pane();
        track.setId("volume-track");
        track.setPrefSize(200, 22);
        track.setMinSize(200, 22);
        track.setMaxSize(200, 22);
        track.setPickOnBounds(true);
        track.setCursor(javafx.scene.Cursor.HAND);

        final String baseColor = beigeTheme ? "#E3D5B7" : "#242A42";
        final String fillColor = accentHex();
        final String thumbColor = beigeTheme ? "#FFF9EF" : "#F6F9FF";

        Region base = new Region();
        base.setStyle("-fx-background-color: " + baseColor + "; -fx-background-radius: 4;");
        Region fill = new Region();
        fill.setStyle("-fx-background-color: " + fillColor + "; -fx-background-radius: 4;");
        Circle thumb = new Circle(9, Color.web(thumbColor));
        thumb.setStroke(Color.web(fillColor));
        thumb.setStrokeWidth(2);
        thumb.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.35)));
        track.getChildren().addAll(base, fill, thumb);

        final double[] frac = {Math.max(0, Math.min(1, volume))};
        Runnable relayout = () -> {
            double w = track.getWidth() > 1 ? track.getWidth() : 200;
            double x = frac[0] * w;
            base.resizeRelocate(0, 7, w, 8);
            fill.resizeRelocate(0, 7, x, 8);
            thumb.setCenterX(x);
            thumb.setCenterY(11);
            percentOut.setText(Math.round(frac[0] * 100) + "%");
        };
        track.widthProperty().addListener((obs, oldW, newW) -> relayout.run());
        relayout.run();

        javafx.event.EventHandler<javafx.scene.input.MouseEvent> apply = e -> {
            double w = track.getWidth() > 1 ? track.getWidth() : 200;
            double v = Math.max(0, Math.min(1, e.getX() / w));
            if (Math.abs(v - frac[0]) < 0.0005) return;
            frac[0] = v;
            volume = v;
            applyVolume();   // live: whatever is already playing follows instantly
            saveSettings();  // remembered when the panel is reopened
            relayout.run();
        };
        track.setOnMousePressed(apply);
        track.setOnMouseDragged(apply);
        return track;
    }

    private Text settingLabel(String text) {
        Text t = new Text(text);
        t.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 18));
        t.setFill(panelText());
        return t;
    }

    private HBox settingRow(Node label, Node value) {
        HBox row = new HBox(18, label, value);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMinWidth(360);
        return row;
    }

    private Button settingToggle(String text, String role) {
        Button b = new Button(text);
        b.setFont(Font.font(16));
        b.setPrefWidth(110);
        hoverize(b, () -> btnBase(role), () -> btnHover(role), () -> btnPress(role), btnText(role), 20);
        return b;
    }

    /** Re-applies the sound pill's colours after On/Off changes (no effect rebuild). */
    private void styleStateToggle(Button b, boolean on) {
        String role = on ? "toggleOn" : "toggleOff";
        b.setStyle(styleBtn(b, btnBase(role), btnText(role), 20));
        b.setOnMouseEntered(e -> b.setStyle(styleBtn(b, btnHover(role), btnText(role), 20)));
        b.setOnMouseExited(e -> b.setStyle(styleBtn(b, btnBase(role), btnText(role), 20)));
        b.setOnMousePressed(e -> b.setStyle(styleBtn(b, btnPress(role), btnText(role), 20)));
        b.setOnMouseReleased(e -> b.setStyle(styleBtn(b, b.isHover() ? btnHover(role) : btnBase(role), btnText(role), 20)));
    }

    /** Rebuilds the home page in place after a theme change (settings panel may reopen). */
    private void rebuildHomeOverlay() {
        if (sceneRef == null) return;
        StackPane root = (StackPane) sceneRef.getRoot();
        Pane existing = null;
        for (Node n : new ArrayList<>(root.getChildren())) {
            if (n instanceof Pane && "skin-overlay".equals(n.getId())) {
                existing = (Pane) n;
            }
        }
        if (existing == null) {
            reopenSettingsRequested = false;
            return;
        }
        root.getChildren().remove(existing);
        Pane overlay = buildSkinSelectorOverlay(() -> {
            applySkinToPlayerSnake();
            resumeGoldenFruitTimers();
            running = true;
            if (timer != null) timer.start();
        });
        root.getChildren().add(overlay);
    }

    //==========================================================   start method   ======================================
    // ---------------------------------------------------------------  theme  --
    private Color starColor()            { return beigeTheme ? Color.web("#4A3926") : Color.WHITE; }
    private Color hudBandColor()         { return beigeTheme ? Color.web("#FDF7EA") : Color.web("#0D1236"); }
    private Color hudBorderColor()       { return beigeTheme ? Color.web("#DFCDA9") : Color.web("#2A3370"); }
    private Color arenaTopColor()        { return beigeTheme ? Color.web("#F6EEDD") : Color.web("#111A4E"); }
    private Color arenaMidColor()        { return beigeTheme ? Color.web("#EFE3CC") : Color.web("#0B1036"); }
    private Color arenaBotColor()        { return beigeTheme ? Color.web("#E7D8BC") : Color.web("#070B26"); }
    private Color gridColor()            { return beigeTheme ? Color.web("#BFA170") : Color.web("#4657BE"); }
    private Color accentColor()          { return beigeTheme ? Color.web("#1F7A8C") : Color.web("#5FE3FF"); }
    private Color accentColor2()         { return beigeTheme ? Color.web("#C2653A") : Color.web("#9B6BFF"); }
    private Color hudTextColor()         { return beigeTheme ? Color.web("#3B2E1C") : Color.web("#FFF8DC"); }
    private Color panelBg()              { return beigeTheme ? Color.web("#FFF9EF") : Color.web("#121740"); }
    private Color panelText()            { return beigeTheme ? Color.web("#3B2E1C") : Color.web("#E8EEFF"); }
    private Color panelMuted()           { return beigeTheme ? Color.web("#7C6B52") : Color.web("#9FA8DA"); }
    private String panelBgHex()          { return beigeTheme ? "#FFF9EF" : "#121740"; }
    private String panelTextHex()        { return beigeTheme ? "#3B2E1C" : "#E8EEFF"; }
    private String accentHex()           { return beigeTheme ? "#1F7A8C" : "#5FE3FF"; }
    private String accent2Hex()          { return beigeTheme ? "#C2653A" : "#9B6BFF"; }
    private String bandHex()             { return beigeTheme ? "#FDF7EA" : "#0D1236"; }
    private String hudBorderHex()        { return beigeTheme ? "#DFCDA9" : "#2A3370"; }

    // -----------------------------------------------------------  preferences  --
    private void loadSettings() {
        try {
            var prefs = java.util.prefs.Preferences.userRoot().node(PREFS_NODE);
            soundEnabled = prefs.getBoolean(PREF_SOUND, true);
            beigeTheme = prefs.getBoolean(PREF_THEME, false);
            volume = Math.max(0, Math.min(1, prefs.getDouble(PREF_VOLUME, 1.0)));
        } catch (Exception ignore) {
            soundEnabled = true;
            beigeTheme = false;
            volume = 1.0;
        }
    }

    private void saveSettings() {
        try {
            var prefs = java.util.prefs.Preferences.userRoot().node(PREFS_NODE);
            prefs.putBoolean(PREF_SOUND, soundEnabled);
            prefs.putBoolean(PREF_THEME, beigeTheme);
            prefs.putDouble(PREF_VOLUME, volume);
        } catch (Exception ignore) {
            // preferences are optional; the session keeps the values either way
        }
    }

    /** Plays a clip only while sound is enabled, at the master volume. */
    private void playClip(AudioClip clip) {
        if (soundEnabled && clip != null) clip.play(volume);
    }

    // ---------------------------------------------------------  home music  --
    /**
     * Loads the home-page background music: {@code Backgroundaudio.wav} in the
     * working directory first (the project root while developing, the app folder
     * when launched from the packaged exe), then the copy bundled in the jar.
     * A missing or unreadable file just leaves the menu silent - never an error.
     */
    private void loadHomeMusic() {
        homeMusic = null;
        try {
            java.io.File local = new java.io.File(HOME_MUSIC_FILE);
            String source;
            if (local.isFile() && local.canRead()) {
                source = local.toURI().toString();
                System.out.println("[music] home music: " + local.getAbsolutePath());
            } else {
                java.net.URL bundled = getClass().getResource("/" + HOME_MUSIC_FILE);
                if (bundled == null) {
                    System.out.println("[music] " + HOME_MUSIC_FILE
                            + " not found - home music disabled");
                    return;
                }
                source = bundled.toExternalForm();
                System.out.println("[music] home music (bundled): " + HOME_MUSIC_FILE);
            }
            AudioClip clip = new AudioClip(source);
            clip.setCycleCount(AudioClip.INDEFINITE);   // loops while the menu is up
            clip.setVolume(volume);
            homeMusic = clip;
        } catch (Throwable t) {
            System.err.println("[music] home music unavailable: " + t);
            homeMusic = null;   // silent menu is better than a broken start
        }
    }

    /** Starts the home-page music (menu only): sound on, some volume, not already playing. */
    private void startHomeMusic() {
        if (homeMusic == null || !soundEnabled || volume <= 0) return;
        if (homeMusic.isPlaying()) return;
        homeMusic.setVolume(volume);
        homeMusic.play();
    }

    private void stopHomeMusic() {
        if (homeMusic != null && homeMusic.isPlaying()) homeMusic.stop();
    }

    /** Pushes the current volume to whatever is already making sound. */
    private void applyVolume() {
        if (homeMusic != null) homeMusic.setVolume(volume);
        if (gameOverSound != null) gameOverSound.setVolume(volume);
    }

    // -------------------------------------------------------------  arena art  --
    /** Paints the whole arena once into an image the game loop can blit per frame. */
    private void renderBackground() {
        if (gc == null) return;
        double w = Math.max(64, Math.round(windowWidth));
        double h = Math.max(64, Math.round(windowHeight));
        try {
            Canvas off = new Canvas(w, h);
            GraphicsContext g = off.getGraphicsContext2D();
            paintArena(g, w, h);
            bgImage = off.snapshot(null, bgImage != null
                    && bgImage.getWidth() == w && bgImage.getHeight() == h ? bgImage : null);
        } catch (Throwable t) {
            bgImage = null;
        }
    }

    /** Backdrop, arena floor, grid, centre emblem, rails and HUD bands. */
    private void paintArena(GraphicsContext g, double w, double h) {
        double floorTop = 50;
        double floorBottom = h - BOTTOM_OFFSET;
        double floorH = Math.max(10, floorBottom - floorTop);

        // 1. deep gradient backdrop
        g.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, List.of(
                new Stop(0, arenaTopColor()),
                new Stop(0.45, arenaMidColor()),
                new Stop(1, arenaBotColor()))));
        g.fillRect(0, 0, w, h);

        // 2. arena floor: a touch lighter than the sky, with a clean grid
        g.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, List.of(
                new Stop(0, arenaMidColor().deriveColor(0, 1, 1.06, 1)),
                new Stop(1, arenaBotColor().deriveColor(0, 1, 1.05, 1)))));
        g.fillRect(0, floorTop, w, floorH);

        g.setLineWidth(1);
        double cell = 44;
        g.setStroke(gridColor());
        g.setGlobalAlpha(beigeTheme ? 0.16 : 0.20);
        for (double gx = cell; gx < w; gx += cell) {
            boolean major = ((int) (gx / cell)) % 4 == 0;
            g.setGlobalAlpha(major ? (beigeTheme ? 0.24 : 0.30) : (beigeTheme ? 0.12 : 0.16));
            g.strokeLine(gx, floorTop, gx, floorBottom);
        }
        for (double gy = floorTop + cell; gy < floorBottom; gy += cell) {
            boolean major = ((int) ((gy - floorTop) / cell)) % 4 == 0;
            g.setGlobalAlpha(major ? (beigeTheme ? 0.24 : 0.30) : (beigeTheme ? 0.12 : 0.16));
            g.strokeLine(0, gy, w, gy);
        }
        g.setGlobalAlpha(1);

        // 3. centre emblem: soft glow with two thin rings
        double cx = w / 2;
        double cy = (floorTop + floorBottom) / 2;
        double emblemR = Math.min(w * 0.32, floorH * 0.62);
        g.setFill(new RadialGradient(0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE, List.of(
                new Stop(0, accentColor2().deriveColor(0, 1, 1, beigeTheme ? 0.16 : 0.22)),
                new Stop(0.55, accentColor().deriveColor(0, 1, 1, beigeTheme ? 0.07 : 0.10)),
                new Stop(1, accentColor().deriveColor(0, 1, 1, 0)))));
        g.fillOval(cx - emblemR, cy - emblemR, emblemR * 2, emblemR * 2);
        g.setStroke(accentColor());
        g.setGlobalAlpha(beigeTheme ? 0.28 : 0.24);
        g.setLineWidth(2);
        g.strokeOval(cx - emblemR * 0.62, cy - emblemR * 0.62, emblemR * 1.24, emblemR * 1.24);
        g.setGlobalAlpha(beigeTheme ? 0.16 : 0.14);
        g.setLineWidth(1.5);
        g.strokeOval(cx - emblemR * 0.38, cy - emblemR * 0.38, emblemR * 0.76, emblemR * 0.76);
        g.setGlobalAlpha(1);

        // 4. glow bands hugging the top and bottom rails
        g.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, List.of(
                new Stop(0, accentColor().deriveColor(0, 1, 1, beigeTheme ? 0.18 : 0.30)),
                new Stop(1, accentColor().deriveColor(0, 1, 1, 0)))));
        g.fillRect(0, floorTop, w, 70);
        g.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, List.of(
                new Stop(0, accentColor2().deriveColor(0, 1, 1, 0)),
                new Stop(1, accentColor2().deriveColor(0, 1, 1, beigeTheme ? 0.16 : 0.28)))));
        g.fillRect(0, floorBottom - 70, w, 70);

        // 5. decorative studs along both rails
        g.setFill(accentColor());
        for (double sx = 22; sx < w; sx += 88) {
            g.setGlobalAlpha(0.55);
            g.fillOval(sx - 2.5, floorTop - 4, 5, 5);
            g.setGlobalAlpha(0.35);
            g.fillOval(sx - 2.5, floorBottom - 1, 5, 5);
        }
        g.setGlobalAlpha(1);

        // 6. HUD bands (score row and control row)
        g.setFill(hudBandColor());
        g.fillRect(0, 0, w, floorTop);
        g.fillRect(0, floorBottom, w, h - floorBottom);
        g.setStroke(hudBorderColor());
        g.setLineWidth(2);
        g.strokeLine(0, floorTop, w, floorTop);
        g.strokeLine(0, floorBottom, w, floorBottom);
        g.setStroke(accentColor());
        g.setGlobalAlpha(0.75);
        g.setLineWidth(2);
        g.strokeLine(0, floorTop - 2, w, floorTop - 2);
        g.strokeLine(0, floorBottom + 2, w, floorBottom + 2);
        g.setGlobalAlpha(1);

        // score pill behind the HUD text
        g.setFill(accentColor().deriveColor(0, 1, 1, beigeTheme ? 0.10 : 0.16));
        g.fillRoundRect(8, 7, 420, 36, 16, 16);
        g.setStroke(accentColor());
        g.setGlobalAlpha(0.55);
        g.setLineWidth(1.5);
        g.strokeRoundRect(8, 7, 420, 36, 16, 16);
        g.setGlobalAlpha(1);

        // 7. vignette so the edges recede (warm brown on beige, black on dark)
        if (beigeTheme) {
            g.setFill(new RadialGradient(0, 0, 0.5, 0.5, 0.72, true, CycleMethod.NO_CYCLE, List.of(
                    new Stop(0, Color.web("#6B5335").deriveColor(0, 1, 1, 0)),
                    new Stop(1, Color.web("#6B5335").deriveColor(0, 1, 1, 0.22)))));
            g.fillRect(0, 0, w, h);
        } else {
            g.setFill(new RadialGradient(0, 0, 0.5, 0.5, 0.72, true, CycleMethod.NO_CYCLE, List.of(
                    new Stop(0, Color.BLACK.deriveColor(0, 1, 1, 0)),
                    new Stop(1, Color.BLACK.deriveColor(0, 1, 1, 0.45)))));
            g.fillRect(0, 0, w, h);
        }
    }

    /** Re-styles everything that is already on screen after a theme change. */
    private void applyTheme() {
        // No per-frame effects here: the glow is baked into the cached arena image.
        if (topLine != null && bottomLine != null) {
            topLine.setStroke(accentColor());
            bottomLine.setStroke(accentColor());
        }
        if (scoreText != null) {
            scoreText.setFill(hudTextColor());
        }
        if (buttonPane != null) {
            buttonPane.setStyle("-fx-background-color: " + bandHex() + ";");
        }
        if (restartButton != null) {
            stylePill(restartButton, () -> btnBase("play"), btnText("play"));
        }
        if (stopStartButton != null) {
            stylePill(stopStartButton, () -> btnBase("amber"), btnText("amber"));
        }
        if (exitGameButton != null) {
            stylePill(exitGameButton, () -> btnBase("exit"), btnText("exit"));
        }
        if (homeButton != null) {
            stylePill(homeButton, () -> btnBase("accent"), btnText("accent"));
        }
        renderBackground();
    }

    /**
     * Stable button style. Deliberately contains NO -fx-effect: an inline effect is
     * destroyed and rebuilt by the CSS engine every time a pseudo-class (:hover,
     * :pressed) changes, which caused the visible flicker. The shadow is attached
     * once as a Node effect instead (see hoverize/stylePill). Fixed background-insets
     * keep Modena's hover layer shifts from moving the gradient (the "shrink" look).
     *
     * <p>Font size and padding are pinned as well. Declaring {@code -fx-font-weight}
     * without a {@code -fx-font-size} lets every CSS re-apply (a :focused/:hover
     * pseudo-class change) rebuild the font from the <em>inherited</em> 12 px value,
     * so a focused button shrank from 89.6x39.2 to 64x27.2 and popped back on the
     * next hover style pass - the reported blink.</p>
     */
    private String styleBtn(Node owner, String background, String textFill, double radius) {
        double px = fontPxFor(owner);
        double padV = round2(px * 0.333333);
        double padH = round2(px * 0.666667);
        return "-fx-background-color: " + background + "; " +
                "-fx-text-fill: " + textFill + "; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: " + px + "px; " +
                "-fx-padding: " + padV + " " + padH + " " + padV + " " + padH + "; " +
                "-fx-background-radius: " + radius + "; " +
                "-fx-border-radius: " + radius + "; " +
                "-fx-background-insets: 0; " +
                "-fx-border-insets: 0; " +
                "-fx-border-color: " + btnBorderHex() + "; " +
                "-fx-border-width: 1;";
    }

    /** Font size captured the first time a control is styled, then pinned forever. */
    private double fontPxFor(Node owner) {
        Double px = pinnedFontPx.get(owner);
        if (px == null) {
            double size = Font.getDefault().getSize();
            if (owner instanceof Labeled l && l.getFont() != null && l.getFont().getSize() > 0) {
                size = l.getFont().getSize();
            }
            px = round2(size);
            pinnedFontPx.put(owner, px);
        }
        return px;
    }

    private static double round2(double v) {
        return Math.round(v * 100) / 100.0;
    }

    /** The one and only shadow used on buttons: set once, never re-created by CSS. */
    private DropShadow btnShadow() {
        return new DropShadow(8, 0, 2, Color.rgb(0, 0, 0, 0.45));
    }

    /**
     * Stable hover/press interaction. Only the background colour ever changes between
     * states; insets, padding, radius and the shadow are constant, so a hover can never
     * blink, resize or re-trigger an animation. Colours are read through suppliers, so
     * a theme switch updates every state automatically.
     */
    private void hoverize(Button b, Supplier<String> base, Supplier<String> hover,
                          Supplier<String> press, String textFill, double radius) {
        b.setStyle(styleBtn(b, base.get(), textFill, radius));
        b.setEffect(btnShadow());
        b.setOnMouseEntered(e -> b.setStyle(styleBtn(b, hover.get(), textFill, radius)));
        b.setOnMouseExited(e -> b.setStyle(styleBtn(b, base.get(), textFill, radius)));
        b.setOnMousePressed(e -> b.setStyle(styleBtn(b, press.get(), textFill, radius)));
        b.setOnMouseReleased(e -> b.setStyle(styleBtn(b, b.isHover() ? hover.get() : base.get(), textFill, radius)));
    }

    /** Skin chooser tile: muted base, muted accent when selected; hover follows state. */
    private void styleSkinToggle(javafx.scene.control.ToggleButton tb) {
        String role = tb.isSelected() ? "accent" : "neutral";
        String bg = tb.isArmed() ? btnPress(role) : (tb.isHover() ? btnHover(role) : btnBase(role));
        tb.setStyle(styleBtn(tb, bg, btnText(role), 12));
    }

    /** One-shot restyle for buttons without hover (in-game HUD). Shadow attached once. */
    private void stylePill(Button b, Supplier<String> background, String textFill) {
        b.setStyle(styleBtn(b, background.get(), textFill, 20));
        if (b.getEffect() == null) b.setEffect(btnShadow());
    }

    // ---------------- fresh, theme-aware button palette (base, hover, pressed) ----------------
    // Every role keeps its own hue across states so a hover only brightens the
    // colour: never rebuilds an effect, never changes size.
    private String btnBase(String role) {
        if (beigeTheme) return switch (role) {
            case "play"      -> "#249058";
            case "back"      -> "#7A5AC7";
            case "exit"      -> "#C74A3F";
            case "accent"    -> "#2A7F95";
            case "amber"     -> "#C98A21";
            case "theme"     -> "#C2456B";
            case "close"     -> "#7E7261";
            case "toggleOn"  -> "#14876F";
            case "toggleOff" -> "#9A8C74";
            default           -> "#F0E4CE";  // neutral / skin toggle
        };
        return switch (role) {
            case "play"      -> "#17A06B";
            case "back"      -> "#6D5BD6";
            case "exit"      -> "#D9534F";
            case "accent"    -> "#2F7FD1";
            case "amber"     -> "#D9992B";
            case "theme"     -> "#C0478F";
            case "close"     -> "#454D6B";
            case "toggleOn"  -> "#0E9B93";
            case "toggleOff" -> "#4E5871";
            default           -> "#2C3350";
        };
    }

    private String btnHover(String role) {
        if (beigeTheme) return switch (role) {
            case "play"      -> "#2CA367";
            case "back"      -> "#8C6BD9";
            case "exit"      -> "#DA5C50";
            case "accent"    -> "#3394AC";
            case "amber"     -> "#DCA033";
            case "theme"     -> "#D75A80";
            case "close"     -> "#8F8271";
            case "toggleOn"  -> "#18A085";
            case "toggleOff" -> "#AA9B82";
            default           -> "#F7EEDC";
        };
        return switch (role) {
            case "play"      -> "#1CB87C";
            case "back"      -> "#7E6DE8";
            case "exit"      -> "#E66663";
            case "accent"    -> "#3B93EA";
            case "amber"     -> "#EBAE3D";
            case "theme"     -> "#D65AA4";
            case "close"     -> "#545C7E";
            case "toggleOn"  -> "#11B2A8";
            case "toggleOff" -> "#5D678A";
            default           -> "#394166";
        };
    }

    private String btnPress(String role) {
        if (beigeTheme) return switch (role) {
            case "play"      -> "#1D7A4A";
            case "back"      -> "#6749B0";
            case "exit"      -> "#AF3E34";
            case "accent"    -> "#226A7D";
            case "amber"     -> "#B07818";
            case "theme"     -> "#A93A5B";
            case "close"     -> "#6E6353";
            case "toggleOn"  -> "#10725E";
            case "toggleOff" -> "#877A63";
            default           -> "#E3D5B7";
        };
        return switch (role) {
            case "play"      -> "#12855A";
            case "back"      -> "#5C4CC0";
            case "exit"      -> "#BF4340";
            case "accent"    -> "#2768B0";
            case "amber"     -> "#BC8221";
            case "theme"     -> "#A83A7A";
            case "close"     -> "#3A4160";
            case "toggleOn"  -> "#0B7F79";
            case "toggleOff" -> "#414A60";
            default           -> "#242A42";
        };
    }

    private String btnText(String role) {
        if (beigeTheme) return switch (role) {
            case "neutral" -> "#4A3B26";
            case "amber"   -> "#33240A";
            default        -> "#FFFFFF";
        };
        return switch (role) {
            case "neutral" -> "#CFD8F0";
            case "amber"   -> "#2B1E05";
            default        -> "#F6F9FF";
        };
    }

    private String btnBorderHex() { return beigeTheme ? "rgba(74,59,38,0.32)" : "rgba(255,255,255,0.18)"; }

    // -------------------------------------------------------------  food glow  --
    /** A soft halo that follows a fruit wherever the game moves it. */
    private void addFruitHalo(Sphere fruit, double radius, Color color) {
        if (haloLayer == null || fruit == null) return;
        Circle halo = new Circle(radius, new RadialGradient(0, 0, 0.5, 0.5, 0.5, true,
                CycleMethod.NO_CYCLE, List.of(
                new Stop(0, color.deriveColor(0, 1, 1, 0.55)),
                new Stop(0.5, color.deriveColor(0, 1, 1, 0.20)),
                new Stop(1, color.deriveColor(0, 1, 1, 0)))));
        halo.setMouseTransparent(true);
        halo.translateXProperty().bind(fruit.translateXProperty());
        halo.translateYProperty().bind(fruit.translateYProperty());
        halo.visibleProperty().bind(javafx.beans.binding.Bindings.createBooleanBinding(
                () -> fruit.getParent() != null, fruit.parentProperty()));
        haloLayer.getChildren().add(halo);
    }

    /** Gentle breathing scale so food looks alive without costing anything. */
    private void pulseNode(Node node, double toScale, double seconds) {
        if (node == null) return;
        javafx.animation.ScaleTransition pulse = new javafx.animation.ScaleTransition(Duration.seconds(seconds), node);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(toScale);
        pulse.setToY(toScale);
        pulse.setCycleCount(javafx.animation.Animation.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.play();
    }

    //==========================================================   start method   ======================================
    @Override
    public void start(Stage stage) {
        startTime = System.nanoTime();
        loadHighScore();
        loadTopScores();
        loadSettings();
        loadHomeMusic();
        // Dark theme keeps its original 200-star field untouched; the Beige theme
        // uses a much denser, evenly distributed field (beigeStars).
        final int numStars = 200;
        final int beigeStars = 460;
        final int maxStars = Math.max(numStars, beigeStars);
        double[] x = new double[maxStars];
        double[] y = new double[maxStars];
        double[] starSpeed = new double[maxStars];
        double[] starSize = new double[maxStars];
        double[] starPhase = new double[maxStars];
        double[] starCol = new double[maxStars];   // beige: column as a fraction of the width
        double[] starRow = new double[maxStars];   // beige: start row as a fraction of the arena
        for (int i = 0; i < maxStars; i++) {
            x[i] = Math.random() * windowWidth;
            y[i] = Math.random() * windowHeight;
            starSpeed[i] = 0.3 + Math.random() * 1.7;
            starSize[i] = 1 + Math.random() * 2;
            starPhase[i] = Math.random() * Math.PI * 2;
        }
        // Even coverage: 23 columns x 20 rows = 460 cells, each with a little jitter,
        // so centre, edges and corners all stay populated.
        final int beigeCols = 23;
        final int beigeRows = 20;
        for (int i = 0; i < beigeStars; i++) {
            int col = i % beigeCols;
            int row = i / beigeCols;
            starCol[i] = (col + 0.15 + Math.random() * 0.70) / beigeCols;
            starRow[i] = (row + 0.15 + Math.random() * 0.70) / beigeRows;
        }
        // (Re)places every beige particle between the rails: the HUD bands stay clean
        // and the rows follow the current window size.
        Runnable layOutBeigeField = () -> {
            if (!beigeTheme) return;
            final double fieldTop = 56;
            final double span = Math.max(60, windowHeight - 56 - 58);
            for (int i = 0; i < beigeStars; i++) {
                y[i] = fieldTop + starRow[i] * span;
            }
        };
        layOutBeigeField.run();
        spaceCanvas = new Canvas(windowWidth, windowHeight);
        gc = spaceCanvas.getGraphicsContext2D();
        eatSound = new AudioClip(getClass().getResource("/eat.wav").toExternalForm());
        Media gameOverMedia = new Media(getClass().getResource("/gameover.wav").toExternalForm());
        gameOverSound = new MediaPlayer(gameOverMedia);
        gameOverSound.setVolume(volume);
        clickSound = new AudioClip(getClass().getResource("s2.wav").toExternalForm());
        topLine = new Line(0, 50, windowWidth, 50);
        topLine.setStroke(Color.WHITE);
        topLine.setStrokeWidth(2);
        bottomLine = new Line(0, windowHeight - BOTTOM_OFFSET, windowWidth, windowHeight - BOTTOM_OFFSET);
        bottomLine.setStroke(Color.WHITE);
        bottomLine.setStrokeWidth(2);
        PhongMaterial headMaterial = new PhongMaterial(Color.BLACK);
        headMaterial.setSpecularColor(Color.PINK);
        head = new Sphere(HEAD_RADIUS);
        head.setMaterial(headMaterial);
        head.setTranslateZ(0);
        headGroup = new HeadVisual(head);
        headGroup.snapTo(direction);
        PhongMaterial bodyMaterial = new PhongMaterial(Color.CHOCOLATE);
        bodyMaterial.setSpecularColor(Color.CHOCOLATE);
        for (int i = 0; i < 3; i++) {
            Sphere segment = new Sphere(BODY_RADIUS);
            segment.setMaterial(bodyMaterial);
            segment.setTranslateX(headX - (i + 1) * SEGMENT_SPACING);
            segment.setTranslateY(headY);
            segment.setTranslateZ(0);
            bodyParts.add(segment);
        }
        fruits.add(makeTexturedSphere("/images/apple.png"));
        fruits.add(makeTexturedSphere("/images/orange.png"));
        fruits.add(makeTexturedSphere("/images/pumpkin.png"));
        fruits.add(makeTexturedSphere("/images/apple.png"));
        fruits.add(makeTexturedSphere("/images/orange.png"));
        specialItems.add(makeTexturedSphere("/images/eagle.png"));
        specialItems.add(makeTexturedSphere("/images/rodent.png"));
        specialItems.add(makeTexturedSphere("/images/mongoose.png"));
        {
            classicMat.setSpecularColor(Color.web("#1E8449"));
            classicHeadMat.setSpecularColor(Color.web("#1E8449"));
            neonMat.setSpecularColor(Color.web("#00FFFF"));
            neonHeadMat.setSpecularColor(Color.web("#00FFFF"));
            oceanMat.setSpecularColor(Color.LIGHTSKYBLUE);
            oceanHeadMat.setSpecularColor(Color.DEEPSKYBLUE);
            limeMat.setSpecularColor(Color.web("#9ACD32"));   // YellowGreen-like shine
            limeHeadMat.setSpecularColor(Color.web("#32CD32")); // LimeGreen shine
        }
        specialDespawnTimer = new PauseTransition(Duration.seconds(3));
        specialDespawnTimer.setOnFinished(ev -> {
            if (specialItemVisible) {
                Group gameElements = (Group) head.getParent().getParent();
                gameElements.getChildren().removeAll(specialItems);
                specialItemVisible = false;
            }
        });
        Group gameElements = new Group(headGroup, topLine, bottomLine);
        this.gameElements = gameElements;
        // Trail particles live in their own layer: cheap add/remove, and the game
        // group's child list stays small so collision scans never pay for them.
        particleLayer = new Group();
        gameElements.getChildren().add(particleLayer);
        haloLayer = new Group();
        gameElements.getChildren().add(haloLayer);
        // Food reads clearly against the arena: a soft halo plus a slow breathing scale.
        addFruitHalo(fruits.get(0), 34, Color.web("#FF5252"));
        addFruitHalo(fruits.get(1), 34, Color.web("#FFA726"));
        addFruitHalo(fruits.get(2), 34, Color.web("#FFCA28"));
        addFruitHalo(fruits.get(3), 34, Color.web("#FF5252"));
        addFruitHalo(fruits.get(4), 34, Color.web("#FFA726"));
        addFruitHalo(specialItems.get(0), 36, Color.web("#7C4DFF"));
        addFruitHalo(specialItems.get(1), 36, Color.web("#FF4081"));
        addFruitHalo(specialItems.get(2), 36, Color.web("#00E5FF"));
        pulseNode(fruits.get(0), 1.12, 1.10);
        pulseNode(fruits.get(1), 1.12, 1.25);
        pulseNode(fruits.get(2), 1.12, 1.40);
        pulseNode(fruits.get(3), 1.12, 1.20);
        pulseNode(fruits.get(4), 1.12, 1.35);
        pulseNode(specialItems.get(0), 1.16, 0.95);
        pulseNode(specialItems.get(1), 1.16, 1.10);
        pulseNode(specialItems.get(2), 1.16, 1.25);
        gameElements.getChildren().addAll(bodyParts);
        villainHead = newHead(Color.DARKSLATEGREY, Color.CORNSILK, villainX, villainY, villainDirection);
        PhongMaterial villainBodyMaterial = new PhongMaterial(Color.CORNSILK);
        for (int i = 0; i < 3; i++) {
            Sphere seg = new Sphere(BODY_RADIUS);
            seg.setMaterial(villainBodyMaterial);
            seg.setTranslateX(villainX - (i + 1) * SEGMENT_SPACING);
            seg.setTranslateY(villainY);
            villainBodyParts.add(seg);
        }
        villain2Head = newHead(Color.MAROON, Color.TOMATO, villain2X, villain2Y, villain2Direction);
        PhongMaterial villain2BodyMaterial = new PhongMaterial(Color.TOMATO);
        for (int i = 0; i < 3; i++) {
            Sphere seg = new Sphere(BODY_RADIUS);
            seg.setMaterial(villain2BodyMaterial);
            seg.setTranslateX(villain2X - (i + 1) * SEGMENT_SPACING);
            seg.setTranslateY(villain2Y);
            villain2BodyParts.add(seg);
        }
        villain3Head = newHead(Color.SADDLEBROWN, Color.ROSYBROWN, villain3X, villain3Y, villain3Direction);
        PhongMaterial villain3BodyMaterial = new PhongMaterial(Color.ROSYBROWN);
        for (int i = 0; i < 3; i++) {
            Sphere seg = new Sphere(BODY_RADIUS);
            seg.setMaterial(villain3BodyMaterial);
            seg.setTranslateX(villain3X - (i + 1) * SEGMENT_SPACING);
            seg.setTranslateY(villain3Y);
            villain3BodyParts.add(seg);
        }
        homeButton = new Button("Home page");
        homeButton.setFont(Font.font(16));
        hoverize(homeButton, () -> btnBase("accent"), () -> btnHover("accent"), () -> btnPress("accent"), btnText("accent"), 20);
        homeButton.setPrefSize(150, 40);
        homeButton.setTranslateX(windowWidth - 165);
        homeButton.setTranslateY(4);
        homeButton.setOnAction(event -> {
            playClip(clickSound);
            running = false;
            timer.stop();
            pauseGoldenFruitTimers();
            StackPane root = (StackPane) sceneRef.getRoot();
            for (Iterator<Node> it = root.getChildren().iterator(); it.hasNext(); ) {
                Node n = it.next();
                if (n instanceof Pane && "skin-overlay".equals(n.getId())) {
                    it.remove();
                }
            }
            Pane overlay = buildSkinSelectorOverlay(() -> {
                applySkinToPlayerSnake();
                running = true;
                timer.start();
            });
            root.getChildren().add(overlay);
        });
        gameElements.getChildren().add(homeButton);
        gameElements.getChildren().addAll(villainHead);
        gameElements.getChildren().addAll(villainBodyParts);
        gameElements.getChildren().addAll(villain2Head);
        gameElements.getChildren().addAll(villain2BodyParts);
        gameElements.getChildren().addAll(villain3Head);
        gameElements.getChildren().addAll(villain3BodyParts);
        createEnemies(gameElements);
        scoreText = new Text(scoreLabel());
        scoreText.setFill(Color.web("#FFF8DC"));
        scoreText.setFont(Font.font(24));
        scoreText.setTranslateX(20);
        scoreText.setTranslateY(30);
        gameElements.getChildren().add(scoreText);
        buttonPane = new Pane();
        buttonPane.setPrefHeight(50);
        buttonPane.setStyle("-fx-background-color: " + bandHex() + ";");
        restartButton = new Button("Restart");
        restartButton.setPrefSize(100, 40);
        restartButton.setFont(Font.font(18));
        hoverize(restartButton, () -> btnBase("play"), () -> btnHover("play"), () -> btnPress("play"), btnText("play"), 20);
        stopStartButton = new Button("Stop");
        stopStartButton.setPrefSize(100, 40);
        stopStartButton.setFont(Font.font(18));
        hoverize(stopStartButton, () -> btnBase("amber"), () -> btnHover("amber"), () -> btnPress("amber"), btnText("amber"), 20);
        exitGameButton = new Button("Exit");
        exitGameButton.setPrefSize(100, 40);
        exitGameButton.setFont(Font.font(18));
        hoverize(exitGameButton, () -> btnBase("exit"), () -> btnHover("exit"), () -> btnPress("exit"), btnText("exit"), 20);
        buttonPane.getChildren().addAll(restartButton, stopStartButton, exitGameButton);
        gameElements.getChildren().add(buttonPane);
        Runnable updateButtonPositions = () -> {
            buttonPane.setPrefWidth(windowWidth);
            restartButton.setLayoutX(10);
            stopStartButton.setLayoutX((windowWidth - stopStartButton.getWidth()) / 2);
            exitGameButton.setLayoutX(windowWidth - exitGameButton.getWidth() - 10);
            buttonPane.setTranslateY(windowHeight - 50);
        };
        Group content = new Group(spaceCanvas, gameElements);
        content.setManaged(false);
        StackPane layeredRoot = new StackPane(content);
        StackPane.setAlignment(content, Pos.TOP_LEFT);
        Scene scene = new Scene(layeredRoot, windowWidth, windowHeight, false);
        this.sceneRef = scene;
        applyTheme();
        running = false;
        Pane overlay = buildSkinSelectorOverlay(() -> {
            applySkinToPlayerSnake();
            running = true;
        });
        layeredRoot.getChildren().add(overlay);
        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            windowWidth = newVal.doubleValue();
            topLine.setEndX(windowWidth);
            bottomLine.setEndX(windowWidth);
            spaceCanvas.setWidth(windowWidth);
            renderBackground();
            updateButtonPositions.run();
            double maxX = windowWidth - HEAD_RADIUS;
            for (Sphere f : fruits) {
                if (gameElements.getChildren().contains(f) && f.getTranslateX() > maxX) {
                    f.setTranslateX(maxX);
                }
            }
            for (Sphere s : specialItems) {
                if (gameElements.getChildren().contains(s) && s.getTranslateX() > maxX) {
                    s.setTranslateX(maxX);
                }
            }
            if (goldenFruitVisible && goldenFruit.getTranslateX() > maxX) {
                goldenFruit.setTranslateX(maxX);
            }
        });
        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            windowHeight = newVal.doubleValue();
            layOutBeigeField.run();   // keep the beige field evenly spread after a resize
            bottomLine.setStartY(windowHeight - BOTTOM_OFFSET + LINE_MARGIN);
            bottomLine.setEndY(windowHeight - BOTTOM_OFFSET + LINE_MARGIN);
            spaceCanvas.setHeight(windowHeight);
            renderBackground();
            updateButtonPositions.run();
            double maxY = bottomLine.getStartY() - HEAD_RADIUS - LINE_MARGIN;
            for (Sphere f : fruits) {
                if (gameElements.getChildren().contains(f) && f.getTranslateY() > maxY) {
                    f.setTranslateY(maxY);
                }
            }
            for (Sphere s : specialItems) {
                if (gameElements.getChildren().contains(s) && s.getTranslateY() > maxY) {
                    s.setTranslateY(maxY);
                }
            }
            if (goldenFruitVisible && goldenFruit.getTranslateY() > maxY) {
                goldenFruit.setTranslateY(maxY);
            }
        });
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case W, UP -> direction = 3;    // Up
                case S, DOWN -> direction = 1;  // Down
                case A, LEFT -> direction = 2;  // Left
                case D, RIGHT -> direction = 0; // Right
            }
        });
        respawnRandomFruit(gameElements);
        setupGoldenFruit(gameElements);
        addFruitHalo(goldenFruit, 46, Color.web("#FFD54F"));
        //========================================================  Animation Timer  ===================================
        if (timer != null) try { timer.stop(); } catch (Throwable ignored) {}
        // Game loop
        timer = new AnimationTimer() {
            private boolean waitingToRespawn = false;
            @Override
            public void handle(long now) {
                // One shared emission tick every 50 ms keeps trails cheap at any frame rate.
                if (lastEmitNanos == 0) lastEmitNanos = now;
                emitThisFrame = (now - lastEmitNanos) >= EMIT_INTERVAL_NS;
                if (emitThisFrame) lastEmitNanos = now;
                double dt = lastFrameNanos == 0 ? 0.0
                        : Math.min(0.25, (now - lastFrameNanos) / 1_000_000_000.0);
                lastFrameNanos = now;
                updateParticles(dt);

                // The arena is painted once into a cached image, then blitted each frame.
                if (bgImage != null) {
                    gc.drawImage(bgImage, 0, 0);
                } else {
                    Stop[] stops = new Stop[] { new Stop(0, Color.BLACK), new Stop(1, Color.DARKBLUE) };
                    gc.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, stops));
                    gc.fillRect(0, 0, windowWidth, windowHeight);
                }
                gc.setFill(starColor());
                if (beigeTheme) {
                    // Beige field: 460 dark-brown particles, one fixed column each, falling
                    // between the rails. Columns are fractions of the current width, so a
                    // resize can never leave an empty strip, and the alpha floor keeps every
                    // particle clearly visible even at the dimmest point of its twinkle.
                    final double fieldBottom = windowHeight - 58;
                    for (int i = 0; i < beigeStars; i++) {
                        double twinkle = 0.55 + 0.45 * Math.sin(now * 1.6e-8 + starPhase[i]);
                        gc.setGlobalAlpha(0.55 + 0.45 * twinkle);
                        double size = starSize[i] * (1.0 + 0.6 * twinkle) * 1.25;
                        gc.fillOval(starCol[i] * windowWidth, y[i], size, size);
                        y[i] += starSpeed[i] * 1.2;
                        if (y[i] > fieldBottom) {
                            y[i] = 56 - size - Math.random() * 60;   // re-enter at the top of its own column
                        }
                    }
                } else {
                    for (int i = 0; i < numStars; i++) {
                        double twinkle = 0.55 + 0.45 * Math.sin(now * 1.6e-8 + starPhase[i]);
                        gc.setGlobalAlpha(0.35 + 0.65 * twinkle);
                        double size = starSize[i] * twinkle;
                        gc.fillOval(x[i], y[i], size, size);
                        y[i] += starSpeed[i];
                        if (y[i] > windowHeight) {
                            y[i] = 0;
                            x[i] = Math.random() * windowWidth;
                        }
                    }
                }
                gc.setGlobalAlpha(1);
                if (Math.random() < 0.002) {
                    gc.setStroke(starColor());
                    double sx = Math.random() * windowWidth;
                    double sy = Math.random() * windowHeight / 2;
                    gc.strokeLine(sx, sy, sx + 50, sy + 15);
                }
                if (!running) return;
                updateVillainAI(gameElements);
                spawnVillainTrail(gameElements);
                spawnVillainHeadGlow(gameElements, Color.MAROON);
                if (!running) return;
                updateVillain2AI(gameElements);
                spawnVillain2Trail(gameElements);
                spawnVillain2HeadGlow(gameElements, Color.BLUE);
                if (!running) return;
                updateVillain3AI(gameElements);
                spawnVillain3Trail(gameElements);
                spawnVillain3HeadGlow(gameElements, Color.LIGHTYELLOW);
                for (Enemy e : enemies) {
                    if (!running) return;
                    updateEnemyAI(gameElements, e);
                    spawnEnemyTrail(gameElements, e);
                    spawnEnemyHeadGlow(gameElements, e);
                }
                double elapsedSeconds = (now - startTime) / 1_000_000_000.0;
                if (currentSkin == SkinType.RAINBOW) {
                    updateRainbowColors(elapsedSeconds);
                }
                if (currentSkin == SkinType.COSMIC) {
                    spawnTrailParticles(gameElements, COSMIC_PALETTE, 4, 25);
                    spawnHeadGlow(gameElements, Color.WHITE);

                } else if (currentSkin == SkinType.NEON) {
                    spawnTrailParticles(gameElements, NEON_PALETTE, 3, 20);
                    spawnHeadGlow(gameElements, Color.DARKRED);

                } else if (currentSkin == SkinType.RAINBOW) {
                    spawnTrailParticles(gameElements, RAINBOW_PALETTE, 5, 25);
                    spawnRainbowHeadGlow(gameElements, elapsedSeconds);

                } else if (currentSkin == SkinType.OCEAN) {
                    spawnTrailParticles(gameElements, OCEAN_PALETTE, 4, 22);
                    spawnHeadGlow(gameElements, Color.DEEPSKYBLUE);
                }
                // ===============================
                // PLAYER SNAKE MOVEMENT
                // ===============================
                switch (direction) {
                    case 0 -> headX += speed;
                    case 1 -> headY += speed;
                    case 2 -> headX -= speed;
                    case 3 -> headY -= speed;
                }
                if (headX > windowWidth) {
                    headX = 0;
                    if (!positionHistory.isEmpty()) positionHistory.remove(positionHistory.size() - 1); // remove bad pre-wrap entry
                    positionHistory.add(new double[]{headX, headY});
                }
                if (headX < 0) {
                    headX = windowWidth;
                    if (!positionHistory.isEmpty()) positionHistory.remove(positionHistory.size() - 1);
                    positionHistory.add(new double[]{headX, headY});
                }
                double topY = topLine.getStartY() + HEAD_RADIUS;
                double bottomY = bottomLine.getStartY() - HEAD_RADIUS - LINE_MARGIN;
                if (headY > bottomY) {
                    headY = topY;
                    if (!positionHistory.isEmpty()) positionHistory.remove(positionHistory.size() - 1);
                    positionHistory.add(new double[]{headX, headY});
                }
                if (headY < topY) {
                    headY = bottomY;
                    if (!positionHistory.isEmpty()) positionHistory.remove(positionHistory.size() - 1);
                    positionHistory.add(new double[]{headX, headY});
                }
                headGroup.setTranslateX(headX);
                headGroup.setTranslateY(headY);
                double angle;
                switch (direction) {
                    case 0 -> angle = 0;
                    case 1 -> angle = 90;
                    case 2 -> angle = 180;
                    case 3 -> angle = 270;
                    default -> angle = 0;
                }
                headGroup.face(direction);
                headGroup.ease();
                positionHistory.add(new double[]{headX, headY});
                int maxHistorySize = (bodyParts.size() + 1) * (int)(SEGMENT_SPACING / speed) + 50;
                trimHistory(positionHistory, maxHistorySize);
                int index = 1;
                for (Sphere segment : bodyParts) {
                    int posIndex = positionHistory.size() - index * (int)(SEGMENT_SPACING / speed);
                    if (posIndex < 0) posIndex = 0;
                    double[] pos = posIndex < positionHistory.size()
                            ? positionHistory.get(posIndex)
                            : new double[]{segment.getTranslateX(), segment.getTranslateY()};
                    segment.setTranslateX(pos[0]);
                    segment.setTranslateY(pos[1]);
                    double bobbing = 3 * Math.sin(elapsedSeconds * 4 + index);
                    segment.setTranslateZ(bobbing);
                    segment.setRotationAxis(Rotate.Z_AXIS);
                    segment.setRotate(angle);
                    if (index == bodyParts.size()) { // last segment only
                        double rippleAmplitude = 5 + bodyParts.size() * 0.5; // grows with more segments
                        double rippleFrequency = 5;
                        segment.setTranslateX(pos[0] + rippleAmplitude * Math.sin(elapsedSeconds * rippleFrequency));
                    }
                    index++;
                }
                // ===============================
                // SPECIAL ITEM HANDLING (PLAYER)
                // ===============================
                if (specialItemVisible) {
                    for (Sphere special : specialItems) {
                        if (!gameElements.getChildren().contains(special)) continue;
                        double dx = headX - special.getTranslateX();
                        double dy = headY - special.getTranslateY();
                        double distance = Math.sqrt(dx * dx + dy * dy);
                        if (distance < HEAD_RADIUS + HEAD_RADIUS * 0.9) {
                            shrinkSnake(bodyParts, gameElements);
                            gameElements.getChildren().remove(special);
                            specialItemVisible = false;
                            break;
                        }
                    }
                } else {
                    if (random.nextDouble() < 0.005) {
                        spawnSpecialItem(gameElements);
                    }
                }
                // ===============================
                // PLAYER FRUIT COLLISION
                // ===============================
                for (Sphere fruit : fruits) {
                    if (!gameElements.getChildren().contains(fruit)) continue;
                    double dx = headX - fruit.getTranslateX();
                    double dy = headY - fruit.getTranslateY();
                    double distance = Math.sqrt(dx * dx + dy * dy);
                    if (distance < HEAD_RADIUS + HEAD_RADIUS * 0.9) {
                        score++;
                        scoreText.setText(scoreLabel());
                        playerFruitCount++;
                        if (playerFruitCount % 10 == 0) {
                            speed += SPEED_INCREMENT;
                            System.out.println("Player speed increased to: " + speed);
                        }
                        playClip(eatSound);
                        Sphere newSegment = createPlayerBodySegmentLikeTail();
                        Sphere lastSegment = bodyParts.get(bodyParts.size() - 1);
                        newSegment.setTranslateX(lastSegment.getTranslateX());
                        newSegment.setTranslateY(lastSegment.getTranslateY());
                        newSegment.setTranslateZ(lastSegment.getTranslateZ());
                        bodyParts.add(newSegment);
                        ((Group) head.getParent().getParent()).getChildren().add(newSegment);
                        gameElements.getChildren().remove(fruit);
                        PauseTransition pt = new PauseTransition(Duration.seconds(0.2));
                        pt.setOnFinished(ev -> {
                            respawnSingleFruit(gameElements, fruit);
                        });
                        pt.play();
                        break;
                    }
                }
                // ===============================
                // VILLAIN1 FRUIT COLLISION  (REPLACEMENT)
                // ===============================
                for (Sphere fruit : fruits) {
                    if (!gameElements.getChildren().contains(fruit)) continue;
                    double dx = villainX - fruit.getTranslateX();
                    double dy = villainY - fruit.getTranslateY();
                    double distance = Math.sqrt(dx * dx + dy * dy);
                    if (distance < HEAD_RADIUS + HEAD_RADIUS * 0.9) {
                        villainFruitCount++;
                        if (villainFruitCount % 10 == 0) {
                            villainSpeed += SPEED_INCREMENT;
                            System.out.println("Villain speed increased to: " + villainSpeed);
                        }
                        playClip(eatSound);
                        PhongMaterial bodyMaterial = new PhongMaterial(Color.CORNSILK);
                        Sphere newSegment = new Sphere(BODY_RADIUS);
                        newSegment.setMaterial(bodyMaterial);
                        Sphere lastSeg = villainBodyParts.get(villainBodyParts.size() - 1);
                        newSegment.setTranslateX(lastSeg.getTranslateX());
                        newSegment.setTranslateY(lastSeg.getTranslateY());
                        newSegment.setTranslateZ(lastSeg.getTranslateZ());
                        villainBodyParts.add(newSegment);
                        ((Group) head.getParent().getParent()).getChildren().add(newSegment);
                        gameElements.getChildren().remove(fruit);
                        PauseTransition pt = new PauseTransition(Duration.seconds(0.2));
                        pt.setOnFinished(ev -> {
                            respawnSingleFruit(gameElements, fruit);
                        });
                        pt.play();
                        break;
                    }
                }
                // ===============================
                // VILLAIN2 FRUIT COLLISION
                // ===============================
                for (Sphere fruit : fruits) {
                    if (!gameElements.getChildren().contains(fruit)) continue;
                    double dx = villain2X - fruit.getTranslateX();
                    double dy = villain2Y - fruit.getTranslateY();
                    double distance = Math.sqrt(dx * dx + dy * dy);
                    if (distance < HEAD_RADIUS + HEAD_RADIUS * 0.9) {
                        villain2FruitCount++;
                        if (villain2FruitCount % 10 == 0) {
                            villain2Speed += SPEED_INCREMENT;
                            System.out.println("Villain 2 speed increased to: " + villain2Speed);
                        }
                        playClip(eatSound);
                        PhongMaterial bodyMaterial = new PhongMaterial(Color.TOMATO);
                        Sphere newSegment = new Sphere(BODY_RADIUS);
                        newSegment.setMaterial(bodyMaterial);
                        Sphere lastSeg = villain2BodyParts.get(villain2BodyParts.size() - 1);
                        newSegment.setTranslateX(lastSeg.getTranslateX());
                        newSegment.setTranslateY(lastSeg.getTranslateY());
                        newSegment.setTranslateZ(lastSeg.getTranslateZ());
                        villain2BodyParts.add(newSegment);
                        ((Group) head.getParent().getParent()).getChildren().add(newSegment);
                        gameElements.getChildren().remove(fruit);
                        PauseTransition pt = new PauseTransition(Duration.seconds(0.2));
                        pt.setOnFinished(ev -> {
                            respawnSingleFruit(gameElements, fruit);
                        });
                        pt.play();
                        break;
                    }
                }
                // ===============================
                // VILLAIN3 FRUIT COLLISION
                // ===============================
                for (Sphere fruit : fruits) {
                    if (!gameElements.getChildren().contains(fruit)) continue;
                    double dx = villain3X - fruit.getTranslateX();
                    double dy = villain3Y - fruit.getTranslateY();
                    double distance = Math.sqrt(dx * dx + dy * dy);
                    if (distance < HEAD_RADIUS + HEAD_RADIUS * 0.9) {
                        villain3FruitCount++;
                        if (villain3FruitCount % 10 == 0) {
                            villain3Speed += SPEED_INCREMENT;
                            System.out.println("Villain 3 speed increased to: " + villain3Speed);
                        }
                        playClip(eatSound);
                        PhongMaterial bodyMaterial = new PhongMaterial(Color.ROSYBROWN);
                        Sphere newSegment = new Sphere(BODY_RADIUS);
                        newSegment.setMaterial(bodyMaterial);
                        Sphere lastSeg = villain3BodyParts.get(villain3BodyParts.size() - 1);
                        newSegment.setTranslateX(lastSeg.getTranslateX());
                        newSegment.setTranslateY(lastSeg.getTranslateY());
                        newSegment.setTranslateZ(lastSeg.getTranslateZ());
                        villain3BodyParts.add(newSegment);
                        ((Group) head.getParent().getParent()).getChildren().add(newSegment);
                        gameElements.getChildren().remove(fruit);
                        PauseTransition pt = new PauseTransition(Duration.seconds(0.2));
                        pt.setOnFinished(ev -> {
                            respawnSingleFruit(gameElements, fruit);
                        });
                        pt.play();
                        break;
                    }
                }
                // ===============================
                // VILLAIN1 SPECIAL ITEM HANDLING
                // ===============================
                if (specialItemVisible) {
                    for (Sphere special : specialItems) {
                        if (!gameElements.getChildren().contains(special)) continue;
                        double dx = villainX - special.getTranslateX();
                        double dy = villainY - special.getTranslateY();
                        double distance = Math.sqrt(dx * dx + dy * dy);
                        if (distance < HEAD_RADIUS + HEAD_RADIUS * 0.9) {
                            shrinkSnake(villainBodyParts, gameElements);
                            gameElements.getChildren().remove(special);
                            specialItemVisible = false;
                            break;
                        }
                    }
                }
                // ===============================
                // VILLAIN2 SPECIAL ITEM HANDLING
                // ===============================
                if (specialItemVisible) {
                    for (Sphere special : specialItems) {
                        if (!gameElements.getChildren().contains(special)) continue;
                        double dx = villain2X - special.getTranslateX();
                        double dy = villain2Y - special.getTranslateY();
                        double distance = Math.sqrt(dx * dx + dy * dy);
                        if (distance < HEAD_RADIUS + HEAD_RADIUS * 0.9) {
                            shrinkSnake(villain2BodyParts, gameElements);
                            gameElements.getChildren().remove(special);
                            specialItemVisible = false;
                            break;
                        }
                    }
                }
                // ===============================
                // VILLAIN3 SPECIAL ITEM HANDLING
                // ===============================
                if (specialItemVisible) {
                    for (Sphere special : specialItems) {
                        if (!gameElements.getChildren().contains(special)) continue;
                        double dx = villain3X - special.getTranslateX();
                        double dy = villain3Y - special.getTranslateY();
                        double distance = Math.sqrt(dx * dx + dy * dy);
                        if (distance < HEAD_RADIUS + HEAD_RADIUS * 0.9) {
                            shrinkSnake(villain3BodyParts, gameElements);
                            gameElements.getChildren().remove(special);
                            specialItemVisible = false;
                            break;
                        }
                    }
                }
                // ===============================
                // GOLDEN FRUIT COLLISION (Player)
                //===============================
                if (goldenFruitVisible) {
                    double dx = headX - goldenFruit.getTranslateX();
                    double dy = headY - goldenFruit.getTranslateY();
                    double distance = Math.sqrt(dx * dx + dy * dy);
                    if (distance < HEAD_RADIUS + GOLDEN_FRUIT_RADIUS) {
                        previousPlayerSpeed = speed;
                        speed = GOLDEN_FRUIT_SPEED;
                        if (playerGoldenBoostTimer != null) playerGoldenBoostTimer.stop();
                        playerGoldenBoostTimer = new PauseTransition(Duration.seconds(10));
                        playerGoldenBoostTimer.setOnFinished(e -> speed = previousPlayerSpeed);
                        playerGoldenBoostTimer.play();
                        gameElements.getChildren().remove(goldenFruit);
                        goldenFruitVisible = false;
                    }
                }

                // ===============================
                // GOLDEN FRUIT COLLISION (Villain1)
                // ===============================
                if (goldenFruitVisible) {
                    double dx = villainX - goldenFruit.getTranslateX();
                    double dy = villainY - goldenFruit.getTranslateY();
                    double distance = Math.sqrt(dx * dx + dy * dy);
                    if (distance < HEAD_RADIUS + GOLDEN_FRUIT_RADIUS) {
                        previousVillainSpeed = villainSpeed;
                        villainSpeed = GOLDEN_FRUIT_SPEED;
                        if (villainGoldenBoostTimer != null) villainGoldenBoostTimer.stop();
                        villainGoldenBoostTimer = new PauseTransition(Duration.seconds(10));
                        villainGoldenBoostTimer.setOnFinished(e -> villainSpeed = previousVillainSpeed);
                        villainGoldenBoostTimer.play();
                        gameElements.getChildren().remove(goldenFruit);
                        goldenFruitVisible = false;
                    }
                }

                // ===============================
                // GOLDEN FRUIT COLLISION (Villain2)
                // ===============================
                if (goldenFruitVisible) {
                    double dx = villain2X - goldenFruit.getTranslateX();
                    double dy = villain2Y - goldenFruit.getTranslateY();
                    double distance = Math.sqrt(dx * dx + dy * dy);
                    if (distance < HEAD_RADIUS + GOLDEN_FRUIT_RADIUS) {
                        previousVillain2Speed = villain2Speed;
                        villain2Speed = GOLDEN_FRUIT_SPEED;
                        if (villain2GoldenBoostTimer != null) villain2GoldenBoostTimer.stop();
                        villain2GoldenBoostTimer = new PauseTransition(Duration.seconds(10));
                        villain2GoldenBoostTimer.setOnFinished(e -> villain2Speed = previousVillain2Speed);
                        villain2GoldenBoostTimer.play();
                        gameElements.getChildren().remove(goldenFruit);
                        goldenFruitVisible = false;
                    }
                }
                // ===============================
                // GOLDEN FRUIT COLLISION (Villain3)
                // ===============================
                if (goldenFruitVisible) {
                    double dx = villain3X - goldenFruit.getTranslateX();
                    double dy = villain3Y - goldenFruit.getTranslateY();
                    double distance = Math.sqrt(dx * dx + dy * dy);
                    if (distance < HEAD_RADIUS + GOLDEN_FRUIT_RADIUS) {
                        previousVillain3Speed = villain3Speed;
                        villain3Speed = GOLDEN_FRUIT_SPEED;
                        if (villain3GoldenBoostTimer != null) villain3GoldenBoostTimer.stop();
                        villain3GoldenBoostTimer = new PauseTransition(Duration.seconds(10));
                        villain3GoldenBoostTimer.setOnFinished(e -> villain3Speed = previousVillain3Speed);
                        villain3GoldenBoostTimer.play();
                        gameElements.getChildren().remove(goldenFruit);
                        goldenFruitVisible = false;
                    }
                }
                // ===============================
                // EXTRA ENEMIES: fruit, pickups and golden boosts
                // ===============================
                updateEnemyPickups(gameElements);
                // Squared hit radii: no sqrt, no temporary arrays in the hot loop.
                final double headHitR2 = (HEAD_RADIUS + HEAD_RADIUS) * (HEAD_RADIUS + HEAD_RADIUS);
                final double bodyHitR2 = (HEAD_RADIUS + BODY_RADIUS) * (HEAD_RADIUS + BODY_RADIUS);
                // ===============================
                // COLLISION DETECTION (Player ↔ Villain1)
                // ===============================
                double pX = headX,     pY = headY;
                double vX = villainX,  vY = villainY;
                if (villainAlive && dist2(pX, pY, vX, vY) <= headHitR2) {
                    killVillain();
                    gameOver(stage);
                    return;
                }
                for (Sphere seg : villainBodyParts) {
                    if (dist2(pX, pY, seg.getTranslateX(), seg.getTranslateY()) <= bodyHitR2) {
                        gameOver(stage);
                        return;
                    }
                }
                if (villainAlive) {
                    for (Sphere seg : bodyParts) {
                        if (dist2(vX, vY, seg.getTranslateX(), seg.getTranslateY()) <= bodyHitR2) {
                            killVillain();
                            return;
                        }
                    }
                }
                // ===============================
                // COLLISION DETECTION (Player ↔ Villain2)
                // ===============================
                double rX = headX,     rY = headY;
                double uX = villain2X,  uY = villain2Y;
                if (villain2Alive && dist2(rX, rY, uX, uY) <= headHitR2) {
                    killVillain2();
                    gameOver(stage);
                    return;
                }
                for (Sphere seg : villain2BodyParts) {
                    if (dist2(rX, rY, seg.getTranslateX(), seg.getTranslateY()) <= bodyHitR2) {
                        gameOver(stage);
                        return;
                    }
                }
                if (villain2Alive) {
                    for (Sphere seg : bodyParts) {
                        if (dist2(uX, uY, seg.getTranslateX(), seg.getTranslateY()) <= bodyHitR2) {
                            killVillain2();
                            return;
                        }
                    }
                }
                // ===============================
                // COLLISION DETECTION (Player ↔ Villain3)
                // ===============================
                double sX = headX,     sY = headY;
                double wX = villain3X,  wY = villain3Y;
                if (villain3Alive && dist2(sX, sY, wX, wY) <= headHitR2) {
                    killVillain3();
                    gameOver(stage);
                    return;
                }
                for (Sphere seg : villain3BodyParts) {
                    if (dist2(sX, sY, seg.getTranslateX(), seg.getTranslateY()) <= bodyHitR2) {
                        gameOver(stage);
                        return;
                    }
                }
                if (villain3Alive) {
                    for (Sphere seg : bodyParts) {
                        if (dist2(wX, wY, seg.getTranslateX(), seg.getTranslateY()) <= bodyHitR2) {
                            killVillain3();
                            return;
                        }
                    }
                }
                // ===============================
                // COLLISION DETECTION (Player ↔ Extra Enemies)
                // ===============================
                for (Enemy e : enemies) {
                    if (!e.alive) continue;
                    double enX = e.x;
                    double enY = e.y;
                    if (dist2(headX, headY, enX, enY) <= headHitR2) {
                        killEnemy(e, gameElements);
                        gameOver(stage);
                        return;
                    }
                    for (Sphere seg : e.body) {
                        if (dist2(headX, headY, seg.getTranslateX(), seg.getTranslateY()) <= bodyHitR2) {
                            gameOver(stage);
                            return;
                        }
                    }
                    for (Sphere seg : bodyParts) {
                        if (dist2(enX, enY, seg.getTranslateX(), seg.getTranslateY()) <= bodyHitR2) {
                            killEnemy(e, gameElements);
                            return;
                        }
                    }
                }
                handleVillainCollision(gameElements, 1, 2);
                handleVillainCollision(gameElements, 1, 3);
                handleVillainCollision(gameElements, 2, 3);
                resolveEnemyClashes(gameElements);
            }
        };
        timer.start();

        stopStartButton.setOnAction(e -> {
            // The game-over dialog freezes the board; toggling Start behind it would
            // resurrect movement that gameOver() will never re-freeze (it early-returns).
            if (gameOverShown) return;
            playClip(clickSound);
            running = !running;
            stopStartButton.setText(running ? "Stop" : "Start");
            if (running) {
                resumeGoldenFruitTimers();
                if (timer != null) timer.start();
            } else {
                pauseGoldenFruitTimers();
                if (timer != null) timer.stop();
            }
        });
        restartButton.setOnAction(e -> {
            restartGame();
        });
        exitGameButton.setOnAction(e -> {
            playClip(clickSound);
            stage.close();
        });
        // Auto-pause: alt-tabbing or clicking another window must not cost a life.
        // Mirrors the Stop button exactly and only while a round is really running.
        stage.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (gameOverShown) return;
            if (!isFocused) {
                if (running) {
                    running = false;
                    pausedByFocusLoss = true;
                    pauseGoldenFruitTimers();
                    if (timer != null) timer.stop();
                    if (stopStartButton != null) stopStartButton.setText("Start");
                }
            } else if (pausedByFocusLoss) {
                pausedByFocusLoss = false;
                running = true;
                resumeGoldenFruitTimers();
                if (timer != null) timer.start();
                if (stopStartButton != null) stopStartButton.setText("Stop");
            }
        });
        stage.setScene(scene);
        stage.setTitle("Snake3D");
        stage.setMinWidth(640);
        stage.setMinHeight(560);
        stage.setOnCloseRequest(event -> {
            running = false;
            stopHomeMusic();
            if (timer != null) timer.stop();
            if (gameOverSound != null) gameOverSound.dispose();
            saveHighScore();
            saveSettings();
        });
        stage.show();
        stage.widthProperty().addListener((obs, oldVal, newVal) -> {
            homeButton.setTranslateX(newVal.doubleValue() - 165);
        });
        updateButtonPositions.run();
    }
    //==================================================================   shrinkSnake   ===============================
    private void shrinkSnake(List<Sphere> bodyParts, Group gameElements) {
        if (bodyParts.size() > 1) {
            Sphere lastSegment = bodyParts.remove(bodyParts.size() - 1);
            javafx.animation.ScaleTransition scale = new javafx.animation.ScaleTransition(Duration.seconds(0.4), lastSegment);
            scale.setToX(0.1);
            scale.setToY(0.1);
            scale.setToZ(0.1);
            javafx.animation.FadeTransition fade = new javafx.animation.FadeTransition(Duration.seconds(0.4), lastSegment);
            fade.setToValue(0);
            javafx.animation.ParallelTransition anim = new javafx.animation.ParallelTransition(scale, fade);
            anim.setOnFinished(ev -> {
                gameElements.getChildren().remove(lastSegment);
                activeShrinkAnims.remove(anim);
            });
            activeShrinkAnims.put(anim, lastSegment);
            anim.play();
        }
    }
    //===========================================================   score label   ======================================
    private String scoreLabel() {
        return "Score: " + score + "    High Score: " + highScore;
    }

    //===========================================================   loadHighScore   ====================================
    private void loadHighScore() {
        var prefs = java.util.prefs.Preferences.userRoot().node(PREFS_NODE);
        highScore = Math.max(0, prefs.getInt(PREF_HIGH_SCORE, 0));
    }

    //===========================================================   saveHighScore   ====================================
    private void saveHighScore() {
        var prefs = java.util.prefs.Preferences.userRoot().node(PREFS_NODE);
        prefs.putInt(PREF_HIGH_SCORE, highScore);
    }

    // ---------------------------------------------------------  leaderboard  --
    /** Loads the saved top-5 runs (highest first); a broken value simply starts empty. */
    private void loadTopScores() {
        topScores.clear();
        try {
            var prefs = java.util.prefs.Preferences.userRoot().node(PREFS_NODE);
            String raw = prefs.get(PREF_TOP5, "");
            if (!raw.isEmpty()) {
                for (String part : raw.split("\\|")) {
                    try { topScores.add(Integer.parseInt(part.trim())); }
                    catch (NumberFormatException ignore) { }
                }
            }
        } catch (Exception ignore) {
            // preferences are optional; the board simply starts empty
        }
        sortTopScores();
    }

    private void saveTopScores() {
        try {
            var prefs = java.util.prefs.Preferences.userRoot().node(PREFS_NODE);
            StringBuilder sb = new StringBuilder();
            for (int s : topScores) {
                if (sb.length() > 0) sb.append('|');
                sb.append(s);
            }
            prefs.put(PREF_TOP5, sb.toString());
        } catch (Exception ignore) {
            // preferences are optional; the session keeps the board either way
        }
    }

    private void sortTopScores() {
        topScores.sort(Collections.reverseOrder());
        while (topScores.size() > 5) topScores.remove(topScores.size() - 1);
    }

    /**
     * Records a finished run on the local leaderboard.
     *
     * @return 1-5 when the run made the top five, 0 otherwise
     */
    private int submitScore(int score) {
        if (score <= 0) return 0;
        boolean qualifies = topScores.size() < 5 || score > topScores.get(topScores.size() - 1);
        if (!qualifies) return 0;
        topScores.add(score);
        sortTopScores();
        int rank = 1;
        for (int s : topScores) {
            if (s > score) rank++;
        }
        saveTopScores();
        return rank;
    }

    //===========================================================   loadLastSkin   =====================================
    private void loadLastSkin() {
        var prefs = java.util.prefs.Preferences.userRoot().node(PREFS_NODE);
        String s = prefs.get(PREF_LAST_SKIN, "CLASSIC");
        try { currentSkin = SkinType.valueOf(s); } catch (Exception ignore) { currentSkin = SkinType.CLASSIC; }
    }
    //===============================================================   saveLastSkin   =================================
    private void saveLastSkin() {
        var prefs = java.util.prefs.Preferences.userRoot().node(PREFS_NODE);
        prefs.put(PREF_LAST_SKIN, currentSkin.name());
    }
    //============================================================   applySkinToPlayerSnake   ==========================
    private void applySkinToPlayerSnake() {
        switch (currentSkin) {
            case CLASSIC -> {
                head.setMaterial(classicHeadMat);
                headGroup.setEffect(null);
                for (Sphere seg : bodyParts) seg.setMaterial(classicMat);
            }
            case NEON -> {
                head.setMaterial(neonHeadMat);
                headGroup.setEffect(neonGlow);
                for (Sphere seg : bodyParts) {
                    seg.setMaterial(neonMat);
                    seg.setEffect(neonGlow);
                }
            }
            case RAINBOW -> {
                headGroup.setEffect(null);
                updateRainbowColors(0);
            }
            case COSMIC -> {
                headGroup.setEffect(new Glow(0.7));
                updateCosmicColors(0);
            }
            case OCEAN -> {
                head.setMaterial(oceanHeadMat);
                headGroup.setEffect(null);
                for (Sphere seg : bodyParts) seg.setMaterial(oceanMat);
            }
            case LIME -> {
                head.setMaterial(limeHeadMat);
                headGroup.setEffect(null);
                for (Sphere seg : bodyParts) seg.setMaterial(limeMat);
            }
        }
    }
    //======================================================================   maybeSpawnCosmicParticles   ============= With No Usage
    private void maybeSpawnCosmicParticles(Group gameElements) {
        int starCount = 1 + random.nextInt(1);
        for (int i = 0; i < starCount; i++) {
            Sphere seg = bodyParts.get(random.nextInt(bodyParts.size()));
            Circle star = new Circle(1 + Math.random() * 2);
            Color[] cosmicPalette = {
                    Color.DARKVIOLET, Color.ROYALBLUE,
                    Color.MEDIUMVIOLETRED, Color.WHITE
            };
            star.setFill(cosmicPalette[random.nextInt(cosmicPalette.length)]);
            star.setTranslateX(seg.getTranslateX() + (Math.random() - 0.5) * 25);
            star.setTranslateY(seg.getTranslateY() + (Math.random() - 0.5) * 25);
            gameElements.getChildren().add(star);
            FadeTransition twinkle = new FadeTransition(Duration.seconds(1.5 + Math.random()), star);
            twinkle.setFromValue(1.0);
            twinkle.setToValue(0.0);
            twinkle.setOnFinished(e -> gameElements.getChildren().remove(star));
            twinkle.play();
        }
        Circle headGlow = new Circle(4, Color.WHITE); // bigger star
        headGlow.setTranslateX(head.getTranslateX() + (Math.random() - 0.5) * 8);
        headGlow.setTranslateY(head.getTranslateY() + (Math.random() - 0.5) * 8);
        gameElements.getChildren().add(headGlow);
        FadeTransition glowFade = new FadeTransition(Duration.seconds(0.8), headGlow);
        glowFade.setFromValue(1.0);
        glowFade.setToValue(0.0);
        glowFade.setOnFinished(e -> gameElements.getChildren().remove(headGlow));
        glowFade.play();
    }
    /**
     * Pooled particle emission. Nodes are reused and faded by the game loop, so a
     * trail never allocates nodes or animations while the board is being drawn.
     */
    private void emitParticle(double x, double y, Color color, double radius, double lifeSeconds) {
        if (!emitThisFrame || particleLayer == null) return;
        if (particleActive.size() >= MAX_PARTICLES) return;
        Circle node;
        if (!particlePool.isEmpty()) {
            node = particlePool.remove(particlePool.size() - 1);
            node.setRadius(radius);
            node.setFill(color);
            node.setVisible(true);
        } else {
            node = new Circle(radius, color);
            particleLayer.getChildren().add(node);
        }
        node.setTranslateX(x);
        node.setTranslateY(y);
        node.setOpacity(1.0);
        particleActive.add(new ParticleFx(node, lifeSeconds));
    }

    /** Short one-shot burst used when the player eats (same pool, no extra cost). */
    private void burst(double x, double y, List<Color> palette, int count) {
        boolean saved = emitThisFrame;
        emitThisFrame = true;
        for (int i = 0; i < count; i++) {
            double a = Math.random() * Math.PI * 2;
            double d = Math.random() * 14;
            emitParticle(x + Math.cos(a) * d, y + Math.sin(a) * d,
                    palette.get(random.nextInt(palette.size())),
                    2 + Math.random() * 3, 0.5 + Math.random() * 0.35);
        }
        emitThisFrame = saved;
    }

    /** Fades live particles out and recycles their nodes; called once per frame. */
    private void updateParticles(double dtSeconds) {
        if (particleActive.isEmpty()) return;
        for (int i = particleActive.size() - 1; i >= 0; i--) {
            ParticleFx p = particleActive.get(i);
            p.age += dtSeconds;
            double k = 1.0 - p.age / p.life;
            if (k <= 0) {
                p.node.setVisible(false);
                p.node.setOpacity(0);
                particleActive.remove(i);
                particlePool.add(p.node);
            } else {
                p.node.setOpacity(k);
            }
        }
    }

    /** Clears every floating particle (used by game over and by a restart). */
    private void clearParticles() {
        for (ParticleFx p : particleActive) {
            p.node.setVisible(false);
            p.node.setOpacity(0);
            if (!particlePool.contains(p.node)) particlePool.add(p.node);
        }
        particleActive.clear();
    }

    //================================================================   spawnTrailParticles   =========================
    private void spawnTrailParticles(Group gameElements, List<Color> palette, int count, double spread) {
        if (bodyParts.isEmpty()) return;
        for (int i = 0; i < count; i++) {
            Sphere seg = bodyParts.get(random.nextInt(bodyParts.size()));
            Color color = palette.get(random.nextInt(palette.size()));
            emitParticle(seg.getTranslateX() + (Math.random() - 0.5) * spread,
                    seg.getTranslateY() + (Math.random() - 0.5) * spread,
                    color, 2 + Math.random() * 2, 1.2);
        }
    }
    //==============================================================   spawnVillain1Trail   ============================
    private void spawnVillainTrail(Group gameElements) {
        if (!villainAlive || villainBodyParts.isEmpty()) return;
        int particleCount = 3 + random.nextInt(2);
        for (int i = 0; i < particleCount; i++) {
            Sphere seg = villainBodyParts.get(random.nextInt(villainBodyParts.size()));
            emitParticle(seg.getTranslateX() + (Math.random() - 0.5) * 20,
                    seg.getTranslateY() + (Math.random() - 0.5) * 20,
                    VILLAIN_PALETTE.get(random.nextInt(VILLAIN_PALETTE.size())),
                    2 + Math.random() * 2, 1.0 + Math.random() * 0.5);
        }
    }
    //==============================================================   spawnVillain2Trail   ============================
    private void spawnVillain2Trail(Group gameElements) {
        if (!villain2Alive || villain2BodyParts.isEmpty()) return;
        int particleCount = 3 + random.nextInt(2);
        for (int i = 0; i < particleCount; i++) {
            Sphere seg = villain2BodyParts.get(random.nextInt(villain2BodyParts.size()));
            emitParticle(seg.getTranslateX() + (Math.random() - 0.5) * 20,
                    seg.getTranslateY() + (Math.random() - 0.5) * 20,
                    VILLAIN2_PALETTE.get(random.nextInt(VILLAIN2_PALETTE.size())),
                    2 + Math.random() * 2, 1.0 + Math.random() * 0.5);
        }
    }
    //==============================================================   spawnVillainTrail   =============================
    private void spawnVillain3Trail(Group gameElements) {
        if (!villain3Alive || villain3BodyParts.isEmpty()) return;
        int particleCount = 3 + random.nextInt(2);
        for (int i = 0; i < particleCount; i++) {
            Sphere seg = villain3BodyParts.get(random.nextInt(villain3BodyParts.size()));
            emitParticle(seg.getTranslateX() + (Math.random() - 0.5) * 20,
                    seg.getTranslateY() + (Math.random() - 0.5) * 20,
                    VILLAIN3_PALETTE.get(random.nextInt(VILLAIN3_PALETTE.size())),
                    2 + Math.random() * 2, 1.0 + Math.random() * 0.5);
        }
    }
    //=====================================================================   spawnVillain1HeadGlow   ==================
    private void spawnVillainHeadGlow(Group gameElements, Color glowColor) {
        if (!villainAlive) return;
        emitParticle(villainHead.getTranslateX(), villainHead.getTranslateY(), glowColor, 6, 0.6);
    }
    //=====================================================================   spawnVillain2HeadGlow   ==================
    private void spawnVillain2HeadGlow(Group gameElements, Color glowColor) {
        if (!villain2Alive) return;
        emitParticle(villain2Head.getTranslateX(), villain2Head.getTranslateY(), glowColor, 6, 0.6);
    }
    //=====================================================================   spawnVillain3HeadGlow   ==================
    private void spawnVillain3HeadGlow(Group gameElements, Color glowColor) {
        if (!villain3Alive) return;
        emitParticle(villain3Head.getTranslateX(), villain3Head.getTranslateY(), glowColor, 6, 0.6);
    }
    //=====================================================================   spawnHeadGlow   ==========================
    private void spawnHeadGlow(Group gameElements, Color glowColor) {
        emitParticle(head.getTranslateX(), head.getTranslateY(), glowColor, 6, 0.6);
    }
    //=========================================================================   spawnRainbowHeadGlow   ===============
    private void spawnRainbowHeadGlow(Group gameElements, double elapsedSeconds) {
        for (int i = 0; i < RAINBOW_PALETTE.size(); i++) {
            Color color = RAINBOW_PALETTE.get(i);
            double angle = (elapsedSeconds * 120 + i * (360.0 / RAINBOW_PALETTE.size())) % 360;
            double radius = 25;
            double x = head.getTranslateX() + Math.cos(Math.toRadians(angle)) * radius;
            double y = head.getTranslateY() + Math.sin(Math.toRadians(angle)) * radius;
            emitParticle(x, y, color, 4, 0.6);
        }
    }
    //===================================================================   updateRainbowColors   ======================
    private void updateRainbowColors(double elapsedSeconds) {
        double base = (elapsedSeconds * RAINBOW_HUE_SPEED) % 360.0;
        setDynamicColor(head, Color.hsb(base, 1.0, 1.0));
        for (int i = 0; i < bodyParts.size(); i++) {
            double h = (base + i * 18.0) % 360.0;
            setDynamicColor(bodyParts.get(i), Color.hsb(h, 1.0, 1.0));
        }
    }
    //=================================================================   generateCosmicColor   ========================
    private Color generateCosmicColor() {
        Color[] palette = {
                Color.DARKVIOLET,
                Color.ROYALBLUE,
                Color.MEDIUMVIOLETRED,
                Color.WHITE
        };
        return palette[random.nextInt(palette.length)];
    }
    //===============================================================   updateCosmicColors   ===========================
    private void updateCosmicColors(double elapsedSeconds) {
        double hueShift = (elapsedSeconds * 40) % 360;
        for (int i = 0; i < bodyParts.size(); i++) {
            double hue = (hueShift + i * 45) % 360;
            Color cosmic = Color.hsb(hue, 0.8, 1.0);
            if (i % 5 == 0) cosmic = Color.WHITE;
            setDynamicColor(bodyParts.get(i), cosmic);
        }
        setDynamicColor(head, Color.hsb((hueShift + 180) % 360, 0.8, 1.0));
    }
    //======================================================   createPlayerBodySegmentLikeTail   =======================
    private Sphere createPlayerBodySegmentLikeTail() {
        Sphere lastSegment = bodyParts.get(bodyParts.size() - 1);
        Sphere seg = new Sphere(BODY_RADIUS);

        switch (currentSkin) {
            case CLASSIC -> seg.setMaterial(classicMat);
            case NEON -> { seg.setMaterial(neonMat); seg.setEffect(neonGlow); }
            case RAINBOW -> setDynamicColor(seg, Color.hsb(rainbowBaseHue, 1, 1));
            case OCEAN -> seg.setMaterial(oceanMat);
            case LIME  -> seg.setMaterial(limeMat);
            case COSMIC -> setDynamicColor(seg, generateCosmicColor());
        }
        seg.setTranslateX(lastSegment.getTranslateX());
        seg.setTranslateY(lastSegment.getTranslateY());
        seg.setTranslateZ(lastSegment.getTranslateZ());
        return seg;
    }
    //===================================================================   makeTexturedSphere   =======================
    private Sphere makeTexturedSphere(String imagePath) {
        Image img = new Image(getClass().getResource(imagePath).toExternalForm());
        PhongMaterial mat = new PhongMaterial();
        mat.setDiffuseMap(img);
        Sphere s = new Sphere(HEAD_RADIUS * 0.9);
        s.setMaterial(mat);
        s.setTranslateZ(0);
        return s;
    }
    //==============================================================   respawnRandomFruit   ============================
    /** Scatters every fruit at a fresh random spot, spread out across the whole arena. */
    private void respawnRandomFruit(Group gameElements) {
        gameElements.getChildren().removeAll(fruits);
        if (fruits.isEmpty()) return;
        double minX = HEAD_RADIUS;
        double maxX = windowWidth - HEAD_RADIUS;
        double minY = topLine.getStartY() + HEAD_RADIUS;
        double maxY = bottomLine.getStartY() - HEAD_RADIUS - LINE_MARGIN;
        double spanX = Math.max(1, maxX - minX);
        double spanY = Math.max(1, maxY - minY);
        final double minGap = HEAD_RADIUS * 3;
        List<double[]> placed = new ArrayList<>();
        for (Sphere f : fruits) {
            double x = minX + random.nextDouble() * spanX;
            double y = minY + random.nextDouble() * spanY;
            // Keep fruits from stacking, but never spin forever on a crowded board.
            for (int attempt = 0; attempt < 80; attempt++) {
                boolean clear = true;
                for (double[] p : placed) {
                    if (Math.hypot(x - p[0], y - p[1]) < minGap) {
                        clear = false;
                        break;
                    }
                }
                if (clear) break;
                x = minX + random.nextDouble() * spanX;
                y = minY + random.nextDouble() * spanY;
            }
            f.setTranslateX(x);
            f.setTranslateY(y);
            placed.add(new double[]{x, y});
            if (!gameElements.getChildren().contains(f)) gameElements.getChildren().add(f);
        }
    }
    //==============================================================   respawnSingleFruit   ============================
    private void respawnSingleFruit(Group gameElements, Sphere fruit) {
        double minX = HEAD_RADIUS;
        double maxX = windowWidth - HEAD_RADIUS;
        double minY = topLine.getStartY() + HEAD_RADIUS;
        double maxY = bottomLine.getStartY() - HEAD_RADIUS - LINE_MARGIN;
        double x = minX + random.nextDouble() * (maxX - minX);
        double y = minY + random.nextDouble() * (maxY - minY);
        int attempts = 0;
        boolean ok;
        do {
            ok = true;
            for (Sphere f : fruits) {
                if (f == fruit) continue;
                if (!gameElements.getChildren().contains(f)) continue;
                double dx = x - f.getTranslateX();
                double dy = y - f.getTranslateY();
                if (Math.hypot(dx, dy) < HEAD_RADIUS * 3) { ok = false; break; }
            }
            if (!ok) {
                x = minX + random.nextDouble() * (maxX - minX);
                y = minY + random.nextDouble() * (maxY - minY);
            }
        } while (!ok && attempts++ < 50);
        fruit.setTranslateX(x);
        fruit.setTranslateY(y);
        if (!gameElements.getChildren().contains(fruit)) {
            gameElements.getChildren().add(fruit);
        }
    }
    //=====================================================  handleVillainCollision  ===================================
    private void handleVillainCollision(Group gameElements, int villainId1, int villainId2) {
        Group head1 = null, head2 = null;
        List<Sphere> body1 = null, body2 = null;
        double x1 = 0, y1 = 0, x2 = 0, y2 = 0;
        double speed1 = 0, speed2 = 0;
        switch (villainId1) {
            case 1 -> {
                head1 = villainHead;
                body1 = villainBodyParts;
                x1 = villainX;
                y1 = villainY;
                speed1 = villainSpeed;
            }
            case 2 -> {
                head1 = villain2Head;
                body1 = villain2BodyParts;
                x1 = villain2X;
                y1 = villain2Y;
                speed1 = villain2Speed;
            }
            case 3 -> {
                head1 = villain3Head;
                body1 = villain3BodyParts;
                x1 = villain3X;
                y1 = villain3Y;
                speed1 = villain3Speed;
            }
        }
        switch (villainId2) {
            case 1 -> {
                head2 = villainHead;
                body2 = villainBodyParts;
                x2 = villainX;
                y2 = villainY;
                speed2 = villainSpeed;
            }
            case 2 -> {
                head2 = villain2Head;
                body2 = villain2BodyParts;
                x2 = villain2X;
                y2 = villain2Y;
                speed2 = villain2Speed;
            }
            case 3 -> {
                head2 = villain3Head;
                body2 = villain3BodyParts;
                x2 = villain3X;
                y2 = villain3Y;
                speed2 = villain3Speed;
            }
        }
        if (head1 == null || head2 == null) return;
        double distance = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        if (distance < HEAD_RADIUS * 2) {
            disableVillain(villainId1, gameElements);
            disableVillain(villainId2, gameElements);
            return;
        }
        for (Sphere seg : body2) {
            double d = Math.sqrt((x1 - seg.getTranslateX()) * (x1 - seg.getTranslateX()) +
                    (y1 - seg.getTranslateY()) * (y1 - seg.getTranslateY()));
            if (d < HEAD_RADIUS + BODY_RADIUS) {
                disableVillain(villainId1, gameElements);
                return;
            }
        }
        for (Sphere seg : body1) {
            double d = Math.sqrt((x2 - seg.getTranslateX()) * (x2 - seg.getTranslateX()) +
                    (y2 - seg.getTranslateY()) * (y2 - seg.getTranslateY()));
            if (d < HEAD_RADIUS + BODY_RADIUS) {
                disableVillain(villainId2, gameElements);
                return;
            }
        }
    }
    //=================================================  disableVillain  ===============================================
private void disableVillain(int id, Group gameElements) {
    switch (id) {
        case 1 -> {
            villainAlive = false;
            gameElements.getChildren().removeAll(villainHead);
            gameElements.getChildren().removeAll(villainBodyParts);
            if (villainRespawnTimer != null) villainRespawnTimer.stop();
            villainRespawnTimer = new PauseTransition(Duration.seconds(3));
            villainRespawnTimer.setOnFinished(ev -> respawnVillain(gameElements));
            villainRespawnTimer.play();
        }
        case 2 -> {
            villain2Alive = false;
            gameElements.getChildren().removeAll(villain2Head);
            gameElements.getChildren().removeAll(villain2BodyParts);
            if (villain2RespawnTimer != null) villain2RespawnTimer.stop();
            villain2RespawnTimer = new PauseTransition(Duration.seconds(3));
            villain2RespawnTimer.setOnFinished(ev -> respawnVillain2(gameElements));
            villain2RespawnTimer.play();
        }
        case 3 -> {
            villain3Alive = false;
            gameElements.getChildren().removeAll(villain3Head);
            gameElements.getChildren().removeAll(villain3BodyParts);
            if (villain3RespawnTimer != null) villain3RespawnTimer.stop();
            villain3RespawnTimer = new PauseTransition(Duration.seconds(3));
            villain3RespawnTimer.setOnFinished(ev -> respawnVillain3(gameElements));
            villain3RespawnTimer.play();
        }
    }
}
    //==============================================================   setupGoldenFruit   ==============================
    private void setupGoldenFruit(Group gameElements) {
        Image goldenImg = new Image(getClass().getResource("/images/golden.png").toExternalForm());
        PhongMaterial goldenMat = new PhongMaterial();
        goldenMat.setDiffuseMap(goldenImg);
        goldenFruit = new Sphere(GOLDEN_FRUIT_RADIUS);
        goldenFruit.setMaterial(goldenMat);
        goldenFruit.setTranslateZ(0);
        goldenFruitDisappearTimer = new PauseTransition(Duration.seconds(GOLDEN_FRUIT_SHOW_DURATION));
        goldenFruitDisappearTimer.setOnFinished(e -> {
            if (goldenFruitVisible) {
                gameElements.getChildren().remove(goldenFruit);
                goldenFruitVisible = false;
            }
        });
        goldenFruitRespawnTimer = new PauseTransition(Duration.seconds(GOLDEN_FRUIT_APPEAR_INTERVAL));
        goldenFruitRespawnTimer.setOnFinished(e -> {
            spawnGoldenFruit(gameElements);
            goldenFruitRespawnTimer.playFromStart();
        });
        // Started by the Play / Restart action so it does not run behind the menu.
    }
    //=====================================================================   pauseGoldenFruitTimers   =================
    private void pauseGoldenFruitTimers() {
        if (goldenFruitRespawnTimer != null) goldenFruitRespawnTimer.pause();
        if (goldenFruitDisappearTimer != null) goldenFruitDisappearTimer.pause();
        if (goldenPulse != null) goldenPulse.pause();
    }
    //====================================================================   resumeGoldenFruitTimers   =================
    private void resumeGoldenFruitTimers() {
        if (goldenFruitRespawnTimer != null) goldenFruitRespawnTimer.play();
        if (goldenFruitVisible && goldenFruitDisappearTimer != null) goldenFruitDisappearTimer.play();
        if (goldenPulse != null) goldenPulse.play();
    }
    //==================================================================   spawnGoldenFruit   ==========================
    private void spawnGoldenFruit(Group gameElements) {
        if (goldenFruit != null) {
            gameElements.getChildren().remove(goldenFruit);
        }
        double minX = HEAD_RADIUS;
        double maxX = windowWidth - HEAD_RADIUS;
        double minY = topLine.getStartY() + HEAD_RADIUS;
        double maxY = bottomLine.getStartY() - HEAD_RADIUS - LINE_MARGIN;
        double x = minX + random.nextDouble() * (maxX - minX);
        double y = minY + random.nextDouble() * (maxY - minY);
        goldenFruit.setTranslateX(x);
        goldenFruit.setTranslateY(y);
        if (!gameElements.getChildren().contains(goldenFruit)) {
            gameElements.getChildren().add(goldenFruit);
        }
        goldenFruitVisible = true;
        if (goldenPulse != null) goldenPulse.stop();
        goldenPulse = new javafx.animation.ScaleTransition(Duration.seconds(1), goldenFruit);
        goldenPulse.setFromX(1.0);
        goldenPulse.setFromY(1.0);
        goldenPulse.setToX(1.2);
        goldenPulse.setToY(1.2);
        goldenPulse.setCycleCount(javafx.animation.Animation.INDEFINITE);
        goldenPulse.setAutoReverse(true);
        goldenPulse.play();
        goldenFruitDisappearTimer.playFromStart();
    }
    //======================================================================   gameOver   ==============================
    private void gameOver(Stage stage) {
        if (gameOverShown) return;
        gameOverShown = true;
        // ------------------------------------------------------------------
        // FREEZE: stop the loop, every timer and every in-flight animation so
        // nothing on the board moves, shrinks or changes while the dialog is up.
        // ------------------------------------------------------------------
        running = false;
        stopHomeMusic();
        if (timer != null) timer.stop();
        specialItemVisible = false;
        if (specialDespawnTimer != null) specialDespawnTimer.stop();
        if (villainRespawnTimer != null) villainRespawnTimer.stop();
        if (villain2RespawnTimer != null) villain2RespawnTimer.stop();
        if (villain3RespawnTimer != null) villain3RespawnTimer.stop();
        if (playerGoldenBoostTimer != null) playerGoldenBoostTimer.stop();
        if (villainGoldenBoostTimer != null) villainGoldenBoostTimer.stop();
        if (villain2GoldenBoostTimer != null) villain2GoldenBoostTimer.stop();
        if (villain3GoldenBoostTimer != null) villain3GoldenBoostTimer.stop();
        freezeEnemies();
        stopShrinkAnimations();
        pauseGoldenFruitTimers();
        Group gameElements = (Group) head.getParent().getParent();
        // Leave the board perfectly still: drop every floating trail/glow particle.
        clearParticles();
        gameElements.getChildren().removeAll(specialItems);
        goldenFruitVisible = false;
        if (goldenFruit != null) {
            gameElements.getChildren().remove(goldenFruit);
        }
        final int finalScore = score;
        final boolean newHighScore = finalScore > highScore;
        if (newHighScore) {
            highScore = finalScore;
        }
        saveHighScore();
        lastRunRank = submitScore(finalScore);   // local top-5 leaderboard
        scoreText.setText(scoreLabel());
        if (gameOverSound != null) {
            gameOverSound.stop();
            if (soundEnabled) {
                gameOverSound.setMute(false);
                gameOverSound.play();
            }
        }
        Platform.runLater(() -> showGameOverDialog(stage, finalScore, newHighScore));
    }

    /** Halts any shrink animation still running and removes its segment from the board. */
    private void stopShrinkAnimations() {
        if (activeShrinkAnims.isEmpty()) return;
        for (Map.Entry<javafx.animation.Animation, Node> entry : activeShrinkAnims.entrySet()) {
            entry.getKey().stop();
            Node node = entry.getValue();
            if (node.getParent() instanceof Group parentGroup) {
                parentGroup.getChildren().remove(node);
            }
        }
        activeShrinkAnims.clear();
    }

    /**
     * Premium game-over screen: a full-window panel with a deep space gradient,
     * a rainbow accent, glowing score cards and gradient action buttons.
     */
    private void showGameOverDialog(Stage owner, int finalScore, boolean newHighScore) {
        // Compact card: the game-over window stays small and centred over the board.
        final double width = 560;
        final double height = 480;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        try {
            alert.initOwner(owner);
        } catch (Exception ignore) {
            // owner is optional; the dialog still opens without it
        }
        // No OS chrome around the card: the dialog window itself is exactly the
        // game-over card (no white title bar, no close button, no frame, no gap
        // to a second background window).
        try {
            alert.initStyle(javafx.stage.StageStyle.UNDECORATED);
        } catch (Exception ignore) {
            // style must be set before show; the dialog still opens without it
        }
        alert.setTitle(newHighScore ? "New High Score!" : "Game Over");
        alert.setHeaderText(null);
        alert.setContentText(null);

        ButtonType restartButton = new ButtonType("Restart", ButtonBar.ButtonData.OK_DONE);
        ButtonType exitButton = new ButtonType("Exit", ButtonBar.ButtonData.CANCEL_CLOSE);

        DialogPane dialogPane = alert.getDialogPane();
        // Nothing painted around the rounded card: the corners show the frozen
        // board instead of an empty window background.
        if (dialogPane.getScene() != null) dialogPane.getScene().setFill(null);
        dialogPane.sceneProperty().addListener((o, oldScene, newScene) -> {
            if (newScene != null) newScene.setFill(null);
        });
        dialogPane.setGraphic(null);
        dialogPane.getButtonTypes().setAll(restartButton, exitButton);
        dialogPane.setPadding(Insets.EMPTY);
        dialogPane.setPrefSize(width, height);
        dialogPane.setMaxSize(width, height);
        dialogPane.setMinSize(480, 420);
        dialogPane.setEffect(new DropShadow(45, beigeTheme
                ? Color.rgb(31, 122, 140, 0.60) : Color.rgb(124, 92, 255, 0.75)));
        // Coordinated shell: deep indigo/violet in dark, warm cream/teal in beige.
        dialogPane.setStyle(
                (beigeTheme
                        ? "-fx-background-color: linear-gradient(to bottom, #FFFDF7 0%, #FBF2E1 45%, #F1E5CE 100%); " +
                          "-fx-border-color: #1F7A8C; "
                        : "-fx-background-color: linear-gradient(to bottom, #191448 0%, #241A63 45%, #0F1638 100%); " +
                          "-fx-border-color: #7C5CFF; ") +
                        "-fx-background-radius: 20; " +
                        "-fx-border-width: 3; " +
                        "-fx-border-radius: 20;");

        Node contentArea = dialogPane.lookup(".content");
        if (contentArea != null) {
            contentArea.setStyle("-fx-background-color: transparent; -fx-padding: 0;");
        }
        Node buttonBar = dialogPane.lookup(".button-bar");
        if (buttonBar != null) {
            buttonBar.setStyle("-fx-background-color: transparent; -fx-padding: 0 0 30 0;");
        }

        VBox content = new VBox(12);
        content.setAlignment(Pos.CENTER);
        content.setMaxWidth(Double.MAX_VALUE);
        content.setPadding(new Insets(24, 40, 6, 40));
        content.setStyle("-fx-background-color: transparent;");

        Region accent = new Region();
        accent.setMaxWidth(Double.MAX_VALUE);
        accent.setPrefHeight(10);
        accent.setStyle("-fx-background-color: linear-gradient(to right, #FF6B6B, #FFB020, " +
                "#34C79B, #5FA8FF, #7C5CFF); -fx-background-radius: 5;");

        Text title = new Text(newHighScore ? "NEW HIGH SCORE!" : "GAME OVER");
        title.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 44));
        title.setFill(new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, beigeTheme
                ? List.of(new Stop(0, Color.web("#0E5A6B")), new Stop(1, Color.web("#1F9BB4")))
                : List.of(new Stop(0, Color.web("#FFFFFF")),
                          new Stop(0.5, Color.web("#FFD976")),
                          new Stop(1, Color.web("#FF9E3D")))));
        title.setEffect(new DropShadow(26, beigeTheme ? Color.web("#1F7A8C") : Color.web("#FFB44D")));

        Text subtitle = new Text(newHighScore ? "A brand new best run" : "Your snake went down fighting");
        subtitle.setFont(Font.font("Segoe UI", 24));
        subtitle.setFill(panelText());

        Text scoreValue = new Text(String.valueOf(finalScore));
        scoreValue.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 40));
        scoreValue.setFill(beigeTheme ? Color.web("#3B2E1C") : Color.WHITE);
        Text bestValue = new Text(String.valueOf(highScore));
        bestValue.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 40));
        bestValue.setFill(beigeTheme ? Color.web("#C2653A") : Color.web("#FFD54F"));
        VBox scoreCard = statCard("SCORE", scoreValue,
                beigeTheme ? "#1F7A8C14" : "#FFFFFF24", beigeTheme ? "#1F7A8C66" : "#FFFFFF55");
        VBox bestCard = statCard("BEST", bestValue,
                beigeTheme ? "#C2653A2E" : "#FFD54F2E", beigeTheme ? "#C2653A88" : "#FFD54F88");
        HBox stats = new HBox(20, scoreCard, bestCard);
        stats.setAlignment(Pos.CENTER);

        HBox dots = new HBox(12);
        dots.setAlignment(Pos.CENTER);
        for (String hex : new String[]{"#FF6B6B", "#FFB020", "#34C79B", "#5FA8FF", "#7C5CFF", "#C0478F", "#FF8A5E"}) {
            Circle dot = new Circle(8, Color.web(hex));
            dot.setStroke(Color.web("#FFFFFF55"));
            dot.setStrokeWidth(1.5);
            dot.setEffect(new DropShadow(12, Color.web(hex)));
            dots.getChildren().add(dot);
        }

        Text hint = new Text("Restart for a new round    ·    Exit for the home page");
        hint.setFont(Font.font("Segoe UI", 17));
        hint.setFill(panelMuted());

        content.getChildren().addAll(accent, title, subtitle, stats, dots);
        if (lastRunRank > 0) {
            // The run made the local top five: celebrate it right on the card.
            Text rank = new Text(lastRunRank == 1
                    ? "\u2605  NEW TOP SCORE  \u2605"
                    : "TOP-5 FINISH   #" + lastRunRank);
            rank.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 18));
            rank.setFill(Color.web(beigeTheme ? "#C2653A" : "#FFD976"));
            rank.setEffect(new DropShadow(12, beigeTheme ? Color.web("#C2653A") : Color.web("#FFB44D")));
            content.getChildren().add(rank);
        }
        content.getChildren().add(hint);
        dialogPane.setContent(content);

        Button restartBtn = (Button) dialogPane.lookupButton(restartButton);
        styleDialogAction(restartBtn,
                "linear-gradient(to bottom, #1FBF7A, #0E8F5C)",
                "linear-gradient(to bottom, #28D68C, #12A46B)",
                "linear-gradient(to bottom, #16A76A, #0A7549)",
                Color.web("#22C77B"));
        Button exitBtn = (Button) dialogPane.lookupButton(exitButton);
        styleDialogAction(exitBtn,
                "linear-gradient(to bottom, #E7605A, #B9343A)",
                "linear-gradient(to bottom, #F2736C, #CB4047)",
                "linear-gradient(to bottom, #D4544F, #A62B31)",
                Color.web("#FF8A80"));

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == restartButton) {
            restartGame();
        } else {
            // Exit (or closing the window) takes the player back to the home page
            // with a freshly reset board waiting behind the skin menu.
            returnToHome();
        }
    }

    /** Leaves the game-over screen and shows the home (skin selection) page over a fresh board. */
    private void returnToHome() {
        running = false;
        if (timer != null) timer.stop();
        pauseGoldenFruitTimers();
        resetSnake();
        if (stopStartButton != null) stopStartButton.setText("Stop");
        StackPane root = (StackPane) sceneRef.getRoot();
        for (Iterator<Node> it = root.getChildren().iterator(); it.hasNext(); ) {
            Node n = it.next();
            if (n instanceof Pane && "skin-overlay".equals(n.getId())) {
                it.remove();
            }
        }
        Pane overlay = buildSkinSelectorOverlay(() -> {
            applySkinToPlayerSnake();
            resumeGoldenFruitTimers();
            running = true;
            timer.start();
        });
        root.getChildren().add(overlay);
    }

    /**
     * Game-over action button. The gradient background is the only thing that
     * changes between states (base/hover/pressed) and the glow is attached once as
     * a Node effect, so hovering can never rebuild CSS effects, blink or resize it.
     */
    private void styleDialogAction(Button b, String base, String hover, String press, Color glow) {
        final String skin = "-fx-text-fill: #FFFFFF; -fx-font-weight: bold; -fx-font-size: 18px; " +
                "-fx-background-radius: 30; -fx-background-insets: 0; -fx-padding: 12 44; " +
                "-fx-border-width: 0; -fx-cursor: hand;";
        b.setStyle("-fx-background-color: " + base + "; " + skin);
        b.setEffect(new DropShadow(20, glow));
        b.setOnMouseEntered(e -> b.setStyle("-fx-background-color: " + hover + "; " + skin));
        b.setOnMouseExited(e -> b.setStyle("-fx-background-color: " + base + "; " + skin));
        b.setOnMousePressed(e -> b.setStyle("-fx-background-color: " + press + "; " + skin));
        b.setOnMouseReleased(e -> b.setStyle("-fx-background-color: " + (b.isHover() ? hover : base) + "; " + skin));
    }

    private VBox statCard(String caption, Text value, String fill, String stroke) {
        Label label = new Label(caption);
        label.setTextFill(panelMuted());
        label.setFont(javafx.scene.text.Font.font("Segoe UI", javafx.scene.text.FontWeight.BOLD, 16));
        VBox card = new VBox(6, label, value);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(16, 40, 16, 40));
        card.setStyle("-fx-background-color: " + fill + "; " +
                "-fx-background-radius: 18; " +
                "-fx-border-color: " + stroke + "; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 18;");
        card.setEffect(new DropShadow(20, Color.web("#00000066")));
        return card;
    }
    //==============================================================   killVillain1   ==================================
    private void killVillain() {
        if (!villainAlive) return;
        villainAlive = false;
        Group rootGroup = (Group) head.getParent().getParent();
        if (villainRespawnTimer != null) {
            villainRespawnTimer.stop();
        }
        if (villainHead != null) rootGroup.getChildren().remove(villainHead);
        if (!villainBodyParts.isEmpty()) {
            rootGroup.getChildren().removeAll(villainBodyParts);
            villainBodyParts.clear();
        }
        villainRespawnTimer = new PauseTransition(Duration.seconds(3));
        villainRespawnTimer.setOnFinished(e -> respawnVillain(rootGroup));
        villainRespawnTimer.play();
    }
    //==============================================================   killVillain2   ==================================
    private void killVillain2() {
        if (!villain2Alive) return;
        villain2Alive = false;
        Group rootGroup = (Group) head.getParent().getParent();
        if (villain2RespawnTimer != null) {
            villain2RespawnTimer.stop();
        }
        if (villain2Head != null) rootGroup.getChildren().remove(villain2Head);
        if (!villain2BodyParts.isEmpty()) {
            rootGroup.getChildren().removeAll(villain2BodyParts);
            villain2BodyParts.clear();
        }
        villain2RespawnTimer = new PauseTransition(Duration.seconds(3));
        villain2RespawnTimer.setOnFinished(e -> respawnVillain2(rootGroup));
        villain2RespawnTimer.play();
    }
    //==============================================================   killVillain3   ==================================
    private void killVillain3() {
        if (!villain3Alive) return;
        villain3Alive = false;
        Group rootGroup = (Group) head.getParent().getParent();
        if (villain3RespawnTimer != null) {
            villain3RespawnTimer.stop();
        }
        if (villain3Head != null) rootGroup.getChildren().remove(villain3Head);
        if (!villain3BodyParts.isEmpty()) {
            rootGroup.getChildren().removeAll(villain3BodyParts);
            villain3BodyParts.clear();
        }
        villain3RespawnTimer = new PauseTransition(Duration.seconds(3));
        villain3RespawnTimer.setOnFinished(e -> respawnVillain3(rootGroup));
        villain3RespawnTimer.play();
    }
    //==============================================================   playfield spawn helpers   ========================
    private double spawnX(double fraction) {
        return Math.max(HEAD_RADIUS, Math.min(windowWidth - HEAD_RADIUS, windowWidth * fraction));
    }

    private double spawnY(double fraction) {
        double top = topLine.getStartY() + HEAD_RADIUS;
        double bottom = bottomLine.getStartY() - HEAD_RADIUS - LINE_MARGIN;
        return Math.max(top, Math.min(bottom, windowHeight * fraction));
    }

    //==============================================================   respawnVillain1   ===============================
    private void respawnVillain(Group rootGroup) {
        if (villainHead != null) rootGroup.getChildren().remove(villainHead);
        if (!villainBodyParts.isEmpty()) {
            rootGroup.getChildren().removeAll(villainBodyParts);
            villainBodyParts.clear();
        }
        villainAlive = true;
        villainX = spawnX(0.25);
        villainY = spawnY(0.30);
        villainDirection = 0;
        villainHistory.clear();
        villainHead = newHead(Color.DARKSLATEGREY, Color.CORNSILK, villainX, villainY, villainDirection);
        rootGroup.getChildren().add(villainHead);
        PhongMaterial bodyMat = new PhongMaterial(Color.CORNSILK);
        for (int i = 0; i < 3; i++) {
            Sphere seg = new Sphere(BODY_RADIUS);
            seg.setMaterial(bodyMat);
            seg.setTranslateX(villainX - (i + 1) * SEGMENT_SPACING);
            seg.setTranslateY(villainY);
            villainBodyParts.add(seg);
        }
        rootGroup.getChildren().addAll(villainBodyParts);
    }
    //==============================================================   respawnVillain2   ===============================
    private void respawnVillain2(Group rootGroup) {
        if (villain2Head != null) rootGroup.getChildren().remove(villain2Head);
        if (!villain2BodyParts.isEmpty()) {
            rootGroup.getChildren().removeAll(villain2BodyParts);
            villain2BodyParts.clear();
        }
        villain2Alive = true;
        villain2X = spawnX(0.65);
        villain2Y = spawnY(0.62);
        villain2Direction = 2;
        villain2History.clear();
        villain2Head = newHead(Color.MAROON, Color.TOMATO, villain2X, villain2Y, villain2Direction);
        rootGroup.getChildren().add(villain2Head);
        PhongMaterial bodyMat = new PhongMaterial(Color.TOMATO);
        for (int i = 0; i < 3; i++) {
            Sphere seg = new Sphere(BODY_RADIUS);
            seg.setMaterial(bodyMat);
            seg.setTranslateX(villain2X + (i + 1) * SEGMENT_SPACING);
            seg.setTranslateY(villain2Y);
            villain2BodyParts.add(seg);
        }
        rootGroup.getChildren().addAll(villain2BodyParts);
    }
    //==============================================================   respawnVillain3   ===============================
    private void respawnVillain3(Group rootGroup) {
        if (villain3Head != null) rootGroup.getChildren().remove(villain3Head);
        if (!villain3BodyParts.isEmpty()) {
            rootGroup.getChildren().removeAll(villain3BodyParts);
            villain3BodyParts.clear();
        }
        villain3Alive = true;
        villain3X = spawnX(0.80);
        villain3Y = spawnY(0.30);
        villain3Direction = 3;
        villain3History.clear();
        villain3Head = newHead(Color.SADDLEBROWN, Color.ROSYBROWN, villain3X, villain3Y, villain3Direction);
        rootGroup.getChildren().add(villain3Head);
        PhongMaterial bodyMat = new PhongMaterial(Color.ROSYBROWN);
        for (int i = 0; i < 3; i++) {
            Sphere seg = new Sphere(BODY_RADIUS);
            seg.setMaterial(bodyMat);
            seg.setTranslateX(villain3X - (i + 1) * SEGMENT_SPACING);
            seg.setTranslateY(villain3Y + (i + 1) * SEGMENT_SPACING);
            villain3BodyParts.add(seg);
        }
        rootGroup.getChildren().addAll(villain3BodyParts);
    }
    //================================================================   resetSnake   ==================================
    private void resetSnake() {
        stopShrinkAnimations();
        clearParticles();
        headX = windowWidth / 2;
        headY = windowHeight / 2;
        direction = 0;
        positionHistory.clear();
        playerFruitCount = 0;
        villainFruitCount = 0;
        villain2FruitCount = 0;
        villain3FruitCount = 0;
        speed = BASE_SPEED;
        villainSpeed = BASE_SPEED;
        villain2Speed = BASE_SPEED;
        villain3Speed = BASE_SPEED;
        if (playerGoldenBoostTimer != null) playerGoldenBoostTimer.stop();
        if (villainGoldenBoostTimer != null) villainGoldenBoostTimer.stop();
        if (villain2GoldenBoostTimer != null) villain2GoldenBoostTimer.stop();
        if (villain3GoldenBoostTimer != null) villain3GoldenBoostTimer.stop();
        goldenFruitVisible = false;
        ((Group) head.getParent().getParent()).getChildren().remove(goldenFruit);
        if (specialDespawnTimer != null) specialDespawnTimer.stop();
        specialItemVisible = false;
        Group rootGroup = (Group) head.getParent().getParent();
        rootGroup.getChildren().removeAll(fruits);
        rootGroup.getChildren().removeAll(specialItems);
        for (Sphere s : bodyParts) rootGroup.getChildren().remove(s);
        bodyParts.clear();
        PhongMaterial bodyMaterial = new PhongMaterial(Color.CHOCOLATE);
        bodyMaterial.setSpecularColor(Color.CHOCOLATE);
        for (int i = 0; i < 3; i++) {
            Sphere segment = new Sphere(BODY_RADIUS);
            segment.setMaterial(bodyMaterial);
            segment.setTranslateX(headX - (i + 1) * SEGMENT_SPACING);
            segment.setTranslateY(headY);
            bodyParts.add(segment);
        }
        rootGroup.getChildren().addAll(bodyParts);
        if (villainRespawnTimer != null) {
            villainRespawnTimer.stop();
        }
        if (villain2RespawnTimer != null) {
            villain2RespawnTimer.stop();
        }
        if (villain3RespawnTimer != null) {
            villain3RespawnTimer.stop();
        }
        if (villainHead != null) rootGroup.getChildren().remove(villainHead);
        if (!villainBodyParts.isEmpty()) {
            rootGroup.getChildren().removeAll(villainBodyParts);
            villainBodyParts.clear();
        }
        if (villain2Head != null) rootGroup.getChildren().remove(villain2Head);
        if (!villain2BodyParts.isEmpty()) {
            rootGroup.getChildren().removeAll(villain2BodyParts);
            villain2BodyParts.clear();
        }
        if (villain3Head != null) rootGroup.getChildren().remove(villain3Head);
        if (!villain3BodyParts.isEmpty()) {
            rootGroup.getChildren().removeAll(villain3BodyParts);
            villain3BodyParts.clear();
        }
        villainAlive = true;
        villainX = spawnX(0.25);
        villainY = spawnY(0.30);
        villainDirection = 0;
        villainHistory.clear();
        villain2Alive = true;
        villain2X = spawnX(0.65);
        villain2Y = spawnY(0.62);
        villain2Direction = 2;
        villain2History.clear();
        villain3Alive = true;
        villain3X = spawnX(0.80);
        villain3Y = spawnY(0.30);
        villain3Direction = 3;
        villain3History.clear();
        villainHead = newHead(Color.DARKSLATEGREY, Color.CORNSILK, villainX, villainY, villainDirection);
        rootGroup.getChildren().add(villainHead);
        villain2Head = newHead(Color.MAROON, Color.TOMATO, villain2X, villain2Y, villain2Direction);
        rootGroup.getChildren().add(villain2Head);
        villain3Head = newHead(Color.SADDLEBROWN, Color.ROSYBROWN, villain3X, villain3Y, villain3Direction);
        rootGroup.getChildren().add(villain3Head);
        PhongMaterial villainBodyMaterial = new PhongMaterial(Color.CORNSILK);
        for (int i = 0; i < 3; i++) {
            Sphere segment = new Sphere(BODY_RADIUS);
            segment.setMaterial(villainBodyMaterial);
            segment.setTranslateX(villainX - (i + 1) * SEGMENT_SPACING);
            segment.setTranslateY(villainY);
            villainBodyParts.add(segment);
        }
        rootGroup.getChildren().addAll(villainBodyParts);
        PhongMaterial villain2BodyMaterial = new PhongMaterial(Color.TOMATO);
        for (int i = 0; i < 3; i++) {
            Sphere segment = new Sphere(BODY_RADIUS);
            segment.setMaterial(villain2BodyMaterial);
            segment.setTranslateX(villain2X + (i + 1) * SEGMENT_SPACING);
            segment.setTranslateY(villain2Y);
            villain2BodyParts.add(segment);
        }
        rootGroup.getChildren().addAll(villain2BodyParts);
        PhongMaterial villain3BodyMaterial = new PhongMaterial(Color.ROSYBROWN);
        for (int i = 0; i < 3; i++) {
            Sphere segment = new Sphere(BODY_RADIUS);
            segment.setMaterial(villain3BodyMaterial);
            segment.setTranslateX(villain3X - (i + 1) * SEGMENT_SPACING);
            segment.setTranslateY(villain3Y + (i + 1) * SEGMENT_SPACING);
            villain3BodyParts.add(segment);
        }
        rootGroup.getChildren().addAll(villain3BodyParts);
        resetEnemies(rootGroup);
        respawnRandomFruit(rootGroup);
        appleVisible = true;
        score = 0;
        scoreText.setText(scoreLabel());
        lastSpecialItem = null;
        gameOverShown = false;
        startTime = System.nanoTime();
        dynamicMaterials.clear();
        applySkinToPlayerSnake();
    }
    //===========================================================================   restartGame   ======================
    private void restartGame() {
        playClip(clickSound);
        resetSnake();
        running = true;
        stopStartButton.setText("Stop");
        resumeGoldenFruitTimers();
        if (timer != null) {
            timer.start();
        }
    }
    /** Drops the oldest trail entries so looking up a tail position stays O(1). */
    private void trimHistory(List<double[]> history, int maxSize) {
        int excess = history.size() - maxSize;
        if (excess > 0) {
            history.subList(0, excess).clear();
        }
    }

    //====================================================================   dist2   ===================================
    private double dist2(double x1, double y1, double x2, double y2) {
        double dx = x1 - x2, dy = y1 - y2;
        return dx*dx + dy*dy;
    }
    //====================================================================   dist3   ===================================
    private double dist3(double x11, double y11, double x21, double y21) {
        double dx = x11 - x21, dy = y11 - y21;
        return dx*dx + dy*dy;
    }
    //====================================================================   dist4   ===================================
    private double dist4(double x12, double y12, double x22, double y22) {
        double dx = x12 - x22, dy = y12 - y22;
        return dx*dx + dy*dy;
    }
    //===================================================================   wrapX   ====================================
    private double wrapX(double x) {
        double minX = HEAD_RADIUS;
        double maxX = windowWidth - HEAD_RADIUS;
        if (x > maxX) return minX;
        if (x < minX) return maxX;
        return x;
    }
    //===================================================================   wrapX1   ===================================
    private double wrapX1(double x1) {
        return wrapX(x1);
    }
    //===================================================================   wrapX2   ===================================
    private double wrapX2(double x2) {
        return wrapX(x2);
    }
    //====================================================================   wrapY   ===================================
    private double wrapY(double y) {
        double top = topLine.getStartY() + HEAD_RADIUS;
        double bottom = bottomLine.getStartY() - HEAD_RADIUS - LINE_MARGIN;
        if (y > bottom) return top;
        if (y < top) return bottom;
        return y;
    }
    //====================================================================   wrapY1   ==================================
    private double wrapY1(double y1) {
        double top = topLine.getStartY() + HEAD_RADIUS;
        double bottom = bottomLine.getStartY() - HEAD_RADIUS - LINE_MARGIN;
        if (y1 > bottom) return top;
        if (y1 < top) return bottom;
        return y1;
    }
    //====================================================================   wrapY2   ==================================
    private double wrapY2(double y2) {
        double top = topLine.getStartY() + HEAD_RADIUS;
        double bottom = bottomLine.getStartY() - HEAD_RADIUS - LINE_MARGIN;
        if (y2 > bottom) return top;
        if (y2 < top) return bottom;
        return y2;
    }
    //====================================================================   pathSafeFromPlayer1   =====================
    private boolean pathSafeFromPlayer(int dir, int steps) {
        double x = villainX, y = villainY;
        double dangerR = HEAD_RADIUS + BODY_RADIUS * 0.95;
        double dangerR2 = dangerR * dangerR;
        for (int i = 0; i < steps; i++) {
            switch (dir) {
                case 0 -> x += villainSpeed;
                case 1 -> y += villainSpeed;
                case 2 -> x -= villainSpeed;
                case 3 -> y -= villainSpeed;
            }
            x = wrapX(x);
            y = wrapY(y);
            if (dist2(x, y, headX, headY) <= dangerR2) return false;
            for (Sphere seg : bodyParts) {
                if (dist2(x, y, seg.getTranslateX(), seg.getTranslateY()) <= dangerR2) return false;
            }
            if (dist2(x, y, villain2X, villain2Y) <= dangerR2) return false;
            for (Sphere seg : villain2BodyParts) {
                if (dist2(x, y, seg.getTranslateX(), seg.getTranslateY()) <= dangerR2) return false;
            }
            if (dist2(x, y, villain3X, villain3Y) <= dangerR2) return false;
            for (Sphere seg : villain3BodyParts) {
                if (dist2(x, y, seg.getTranslateX(), seg.getTranslateY()) <= dangerR2) return false;
            }
        }
        double px = headX, py = headY;
        switch (direction) {
            case 0 -> px += speed;
            case 1 -> py += speed;
            case 2 -> px -= speed;
            case 3 -> py -= speed;
        }
        px = wrapX(px);
        py = wrapY(py);
        double headOnR = HEAD_RADIUS * 2.0;
        if (dist2(x, y, px, py) <= headOnR * headOnR) return false;
        return true;
    }
    //====================================================================   pathSafeFromPlayer2   =====================
    private boolean pathSafeFromPlayer2(int dir2, int steps2) {
        double x1 = villain2X, y1 = villain2Y;
        double dangerR1 = HEAD_RADIUS + BODY_RADIUS * 0.95;
        double dangerR21 = dangerR1 * dangerR1;
        for (int i = 0; i < steps2; i++) {
            switch (dir2) {
                case 0 -> x1 += villain2Speed;
                case 1 -> y1 += villain2Speed;
                case 2 -> x1 -= villain2Speed;
                case 3 -> y1 -= villain2Speed;
            }
            x1 = wrapX1(x1);
            y1 = wrapY1(y1);
            if (dist3(x1, y1, headX, headY) <= dangerR21) return false;
            for (Sphere seg : bodyParts) {
                if (dist3(x1, y1, seg.getTranslateX(), seg.getTranslateY()) <= dangerR21) return false;
            }
            if (dist3(x1, y1, villainX, villainY) <= dangerR21) return false;
            for (Sphere seg : villainBodyParts) {
                if (dist3(x1, y1, seg.getTranslateX(), seg.getTranslateY()) <= dangerR21) return false;
            }
            if (dist3(x1, y1, villain3X, villain3Y) <= dangerR21) return false;
            for (Sphere seg : villain3BodyParts) {
                if (dist3(x1, y1, seg.getTranslateX(), seg.getTranslateY()) <= dangerR21) return false;
            }
        }
        double px1 = headX, py1 = headY;
        switch (direction) {
            case 0 -> px1 += speed;
            case 1 -> py1 += speed;
            case 2 -> px1 -= speed;
            case 3 -> py1 -= speed;
        }
        px1 = wrapX1(px1);
        py1 = wrapY1(py1);
        double headOnR = HEAD_RADIUS * 2.0;
        if (dist3(x1, y1, px1, py1) <= headOnR * headOnR) return false;
        return true;
    }
    //====================================================================   pathSafeFromPlayer3   =====================
    private boolean pathSafeFromPlayer3(int dir3, int steps3) {
        double x2 = villain3X, y2 = villain3Y;
        double dangerR2 = HEAD_RADIUS + BODY_RADIUS * 0.95;
        double dangerR22 = dangerR2 * dangerR2;
        for (int i = 0; i < steps3; i++) {
            switch (dir3) {
                case 0 -> x2 += villain3Speed;
                case 1 -> y2 += villain3Speed;
                case 2 -> x2 -= villain3Speed;
                case 3 -> y2 -= villain3Speed;
            }
            x2 = wrapX2(x2);
            y2 = wrapY2(y2);
            if (dist4(x2, y2, headX, headY) <= dangerR22) return false;
            for (Sphere seg : bodyParts) {
                if (dist4(x2, y2, seg.getTranslateX(), seg.getTranslateY()) <= dangerR22) return false;
            }
            if (dist4(x2, y2, villainX, villainY) <= dangerR22) return false;
            for (Sphere seg : villainBodyParts) {
                if (dist4(x2, y2, seg.getTranslateX(), seg.getTranslateY()) <= dangerR22) return false;
            }
            if (dist4(x2, y2, villain2X, villain2Y) <= dangerR22) return false;
            for (Sphere seg : villain2BodyParts) {
                if (dist4(x2, y2, seg.getTranslateX(), seg.getTranslateY()) <= dangerR22) return false;
            }
        }
        double px2 = headX, py2 = headY;
        switch (direction) {
            case 0 -> px2 += speed;
            case 1 -> py2 += speed;
            case 2 -> px2 -= speed;
            case 3 -> py2 -= speed;
        }
        px2 = wrapX2(px2);
        py2 = wrapY2(py2);
        double headOnR = HEAD_RADIUS * 2.0;
        if (dist4(x2, y2, px2, py2) <= headOnR * headOnR) return false;
        return true;
    }
    //===================================================================   updateVillainAI 1  =========================
    private void updateVillainAI(Group gameElements) {
        if (!villainAlive) return;
        Sphere targetFruit = null;
        if (goldenFruitVisible) {
            targetFruit = goldenFruit;
        } else {
            double best = Double.MAX_VALUE;
            for (Sphere fruit : fruits) {
                if (!gameElements.getChildren().contains(fruit)) continue;
                double dx = fruit.getTranslateX() - villainX;
                double dy = fruit.getTranslateY() - villainY;
                double d2 = dx * dx + dy * dy;
                if (d2 < best) {
                    best = d2;
                    targetFruit = fruit;
                }
            }
        }
        if (targetFruit == null) {
            int steps = Math.max(5, (int)Math.ceil(SEGMENT_SPACING / Math.max(1.0, villainSpeed)));
            if (!pathSafeFromPlayer(villainDirection, steps)) {
                for (int d = 0; d < 4; d++) {
                    if (pathSafeFromPlayer(d, steps)) {
                        villainDirection = d;
                        break;
                    }
                }
            }
        } else {
            int[] dirs = {0, 1, 2, 3};
            double[] scores = new double[4];
            for (int i = 0; i < 4; i++) {
                double tx = villainX, ty = villainY;
                switch (dirs[i]) {
                    case 0 -> tx += villainSpeed;
                    case 1 -> ty += villainSpeed;
                    case 2 -> tx -= villainSpeed;
                    case 3 -> ty -= villainSpeed;
                }
                tx = wrapX(tx);
                ty = wrapY(ty);
                double dx = targetFruit.getTranslateX() - tx;
                double dy = targetFruit.getTranslateY() - ty;
                scores[i] = Math.hypot(dx, dy);
                if (dirs[i] != villainDirection) scores[i] += 2.0;
            }
            int bestDir = -1;
            double bestScore = Double.MAX_VALUE;
            int steps = Math.max(6, (int)Math.ceil(SEGMENT_SPACING / Math.max(1.0, villainSpeed)) + 2);

            for (int i = 0; i < 4; i++) {
                for (int d = 0; d < 4; d++) {
                    if (scores[d] < bestScore && pathSafeFromPlayer(dirs[d], steps)) {
                        bestScore = scores[d];
                        bestDir = dirs[d];
                    }
                }
            }
            if (bestDir == -1) {
                double bestClear = -1;
                int fallback = villainDirection;
                for (int d = 0; d < 4; d++) {
                    double tx = villainX, ty = villainY;
                    switch (d) {
                        case 0 -> tx += villainSpeed;
                        case 1 -> ty += villainSpeed;
                        case 2 -> tx -= villainSpeed;
                        case 3 -> ty -= villainSpeed;
                    }
                    tx = wrapX(tx);
                    ty = wrapY(ty);
                    double minD2 = dist2(tx, ty, headX, headY);
                    for (Sphere seg : bodyParts) {
                        double d2 = dist2(tx, ty, seg.getTranslateX(), seg.getTranslateY());
                        if (d2 < minD2) minD2 = d2;
                    }
                    if (minD2 > bestClear) { bestClear = minD2; fallback = d; }
                }
                bestDir = fallback;
            }
            if (bestDir != villainDirection) {
                if (framesSinceVillainTurn >= VILLAIN_TURN_COOLDOWN_FRAMES || !pathSafeFromPlayer(villainDirection, 2)) {
                    villainDirection = bestDir;
                    framesSinceVillainTurn = 0;
                } else {
                    framesSinceVillainTurn++;
                }
            } else {
                framesSinceVillainTurn++;
            }
        }
        switch (villainDirection) {
            case 0 -> villainX += villainSpeed;
            case 1 -> villainY += villainSpeed;
            case 2 -> villainX -= villainSpeed;
            case 3 -> villainY -= villainSpeed;
        }
        double oldX = villainX;
        double oldY = villainY;
        villainX = wrapX(villainX);
        villainY = wrapY(villainY);
        if (villainX != oldX || villainY != oldY) {
            if (!villainHistory.isEmpty()) villainHistory.remove(villainHistory.size() - 1);
            villainHistory.add(new double[]{villainX, villainY});
        }
        villainHead.setTranslateX(villainX);
        villainHead.setTranslateY(villainY);
        villainHead.face(villainDirection);
        villainHead.ease();
        villainHistory.add(new double[]{villainX, villainY});
        int maxHistorySize = (villainBodyParts.size() + 1) * (int)(SEGMENT_SPACING / villainSpeed) + 50;
        trimHistory(villainHistory, maxHistorySize);
        int index = 1;
        double elapsedSeconds = (System.nanoTime() - startTime) / 1_000_000_000.0;
        for (Sphere segment : villainBodyParts) {
            int posIndex = villainHistory.size() - index * (int)(SEGMENT_SPACING / villainSpeed);
            if (posIndex < 0) posIndex = 0;
            double[] pos = posIndex < villainHistory.size()
                    ? villainHistory.get(posIndex)
                    : new double[]{segment.getTranslateX(), segment.getTranslateY()};
            segment.setTranslateX(pos[0]);
            segment.setTranslateY(pos[1]);
            if (index == villainBodyParts.size()) {
                double maxAmplitude = SEGMENT_SPACING / 2.0;
                double rippleAmplitude = Math.min(5 + villainBodyParts.size() * 0.2, maxAmplitude);
                double rippleFrequency = 5;
                segment.setTranslateX(pos[0] + rippleAmplitude * Math.sin(elapsedSeconds * rippleFrequency));
            }
            index++;
        }
    }
    //===================================================================  updateVillainAI 2  ==========================
    private void updateVillain2AI(Group gameElements) {
        if (!villain2Alive) return;
        Sphere targetFruit = null;
        if (goldenFruitVisible) {
            targetFruit = goldenFruit;
        } else {
            double best = Double.MAX_VALUE;
            for (Sphere fruit : fruits) {
                if (!gameElements.getChildren().contains(fruit)) continue;
                double dx = fruit.getTranslateX() - villain2X;
                double dy = fruit.getTranslateY() - villain2Y;
                double d2 = dx * dx + dy * dy;
                if (d2 < best) {
                    best = d2;
                    targetFruit = fruit;
                }
            }
        }
        if (targetFruit == null) {
            int steps = Math.max(5, (int)Math.ceil(SEGMENT_SPACING / Math.max(1.0, villain2Speed)));
            if (!pathSafeFromPlayer2(villain2Direction, steps)) {
                for (int d = 0; d < 4; d++) {
                    if (pathSafeFromPlayer2(d, steps)) {
                        villain2Direction = d;
                        break;
                    }
                }
            }
        } else {
            int[] dirs1 = {1, 0, 2, 3};
            double[] scores1 = new double[4];
            for (int i = 0; i < 4; i++) {
                double tx1 = villain2X, ty1 = villain2Y;
                switch (dirs1[i]) {
                    case 0 -> tx1 += villain2Speed;
                    case 1 -> ty1 += villain2Speed;
                    case 2 -> tx1 -= villain2Speed;
                    case 3 -> ty1 -= villain2Speed;
                }
                tx1 = wrapX1(tx1);
                ty1 = wrapY1(ty1);
                double dx = targetFruit.getTranslateX() - tx1;
                double dy = targetFruit.getTranslateY() - ty1;
                scores1[i] = Math.hypot(dx, dy);
                if (dirs1[i] != villain2Direction) scores1[i] += 2.0;
            }
            int bestDir1 = -1;
            double bestScore = Double.MAX_VALUE;
            int steps = Math.max(6, (int)Math.ceil(SEGMENT_SPACING / Math.max(1.0, villain2Speed)) + 2);

            for (int i = 0; i < 4; i++) {
                for (int d = 0; d < 4; d++) {
                    if (scores1[d] < bestScore && pathSafeFromPlayer2(dirs1[d], steps)) {
                        bestScore = scores1[d];
                        bestDir1 = dirs1[d];
                    }
                }
            }
            if (bestDir1 == -1) {
                double bestClear = -1;
                int fallback = villain2Direction;
                for (int d = 0; d < 4; d++) {
                    double tx1 = villain2X, ty1 = villain2Y;
                    switch (d) {
                        case 0 -> tx1 += villain2Speed;
                        case 1 -> ty1 += villain2Speed;
                        case 2 -> tx1 -= villain2Speed;
                        case 3 -> ty1 -= villain2Speed;
                    }
                    tx1 = wrapX1(tx1);
                    ty1 = wrapY1(ty1);

                    double minD2 = dist3(tx1, ty1, headX, headY);
                    for (Sphere seg : bodyParts) {
                        double d2 = dist3(tx1, ty1, seg.getTranslateX(), seg.getTranslateY());
                        if (d2 < minD2) minD2 = d2;
                    }
                    if (minD2 > bestClear) { bestClear = minD2; fallback = d; }
                }
                bestDir1 = fallback;
            }
            if (bestDir1 != villain2Direction) {
                if (framesSinceVillain2Turn >= VILLAIN2_TURN_COOLDOWN_FRAMES || !pathSafeFromPlayer2(villain2Direction, 2)) {
                    villain2Direction = bestDir1;
                    framesSinceVillain2Turn = 0;
                } else {
                    framesSinceVillain2Turn++;
                }
            } else {
                framesSinceVillain2Turn++;
            }
        }
        switch (villain2Direction) {
            case 0 -> villain2X += villain2Speed;
            case 1 -> villain2Y += villain2Speed;
            case 2 -> villain2X -= villain2Speed;
            case 3 -> villain2Y -= villain2Speed;
        }
        double oldX1 = villain2X;
        double oldY1 = villain2Y;
        villain2X = wrapX1(villain2X);
        villain2Y = wrapY1(villain2Y);
        if (villain2X != oldX1 || villain2Y != oldY1) {
            if (!villain2History.isEmpty()) villain2History.remove(villain2History.size() - 1);
            villain2History.add(new double[]{villain2X, villain2Y});
        }
        villain2Head.setTranslateX(villain2X);
        villain2Head.setTranslateY(villain2Y);
        villain2Head.face(villain2Direction);
        villain2Head.ease();
        villain2History.add(new double[]{villain2X, villain2Y});
        int maxHistorySize = (villainBodyParts.size() + 1) * (int)(SEGMENT_SPACING / villain2Speed) + 50;
        trimHistory(villain2History, maxHistorySize);
        int index = 1;
        double elapsedSeconds = (System.nanoTime() - startTime) / 1_000_000_000.0;
        for (Sphere segment : villain2BodyParts) {
            int posIndex = villain2History.size() - index * (int)(SEGMENT_SPACING / villain2Speed);
            if (posIndex < 0) posIndex = 0;
            double[] pos = posIndex < villain2History.size()
                    ? villain2History.get(posIndex)
                    : new double[]{segment.getTranslateX(), segment.getTranslateY()};
            segment.setTranslateX(pos[0]);
            segment.setTranslateY(pos[1]);
            if (index == villain2BodyParts.size()) {
                double maxAmplitude = SEGMENT_SPACING / 2.0;
                double rippleAmplitude = Math.min(5 + villain2BodyParts.size() * 0.2, maxAmplitude);
                double rippleFrequency = 5;
                segment.setTranslateX(pos[0] + rippleAmplitude * Math.sin(elapsedSeconds * rippleFrequency));
            }
            index++;
        }
    }
    //==================================================  updateVillainAI 3 ============================================
    private void updateVillain3AI(Group gameElements) {
        if (!villain3Alive) return;
        Sphere targetFruit = null;
        if (goldenFruitVisible) {
            targetFruit = goldenFruit;
        } else {
            double best = Double.MAX_VALUE;
            for (Sphere fruit : fruits) {
                if (!gameElements.getChildren().contains(fruit)) continue;
                double dx = fruit.getTranslateX() - villain3X;
                double dy = fruit.getTranslateY() - villain3Y;
                double d2 = dx * dx + dy * dy;
                if (d2 < best) {
                    best = d2;
                    targetFruit = fruit;
                }
            }
        }
        if (targetFruit == null) {
            int steps = Math.max(5, (int)Math.ceil(SEGMENT_SPACING / Math.max(1.0, villain3Speed)));
            if (!pathSafeFromPlayer3(villain3Direction, steps)) {
                for (int d = 0; d < 4; d++) {
                    if (pathSafeFromPlayer3(d, steps)) {
                        villain3Direction = d;
                        break;
                    }
                }
            }
        } else {
            int[] dirs2 = {3, 2, 1, 0};
            double[] scores2 = new double[4];
            for (int i = 0; i < 4; i++) {
                double tx2 = villain3X, ty2 = villain3Y;
                switch (dirs2[i]) {
                    case 0 -> tx2 += villain3Speed;
                    case 1 -> ty2 += villain3Speed;
                    case 2 -> tx2 -= villain3Speed;
                    case 3 -> ty2 -= villain3Speed;
                }
                tx2 = wrapX2(tx2);
                ty2 = wrapY2(ty2);
                double dx = targetFruit.getTranslateX() - tx2;
                double dy = targetFruit.getTranslateY() - ty2;
                scores2[i] = Math.hypot(dx, dy);
                if (dirs2[i] != villain3Direction) scores2[i] += 2.0;
            }
            int bestDir2 = -1;
            double bestScore2 = Double.MAX_VALUE;
            int steps = Math.max(6, (int)Math.ceil(SEGMENT_SPACING / Math.max(1.0, villain3Speed)) + 2);
            for (int i = 0; i < 4; i++) {
                for (int d = 0; d < 4; d++) {
                    if (scores2[d] < bestScore2 && pathSafeFromPlayer3(dirs2[d], steps)) {
                        bestScore2 = scores2[d];
                        bestDir2 = dirs2[d];
                    }
                }
            }
            if (bestDir2 == -1) {
                double bestClear = -1;
                int fallback = villain3Direction;
                for (int d = 0; d < 4; d++) {
                    double tx2 = villain3X, ty2 = villain3Y;
                    switch (d) {
                        case 0 -> tx2 += villain3Speed;
                        case 1 -> ty2 += villain3Speed;
                        case 2 -> tx2 -= villain3Speed;
                        case 3 -> ty2 -= villain3Speed;
                    }
                    tx2 = wrapX2(tx2);
                    ty2 = wrapY2(ty2);
                    double minD2 = dist4(tx2, ty2, headX, headY);
                    for (Sphere seg : bodyParts) {
                        double d2 = dist4(tx2, ty2, seg.getTranslateX(), seg.getTranslateY());
                        if (d2 < minD2) minD2 = d2;
                    }
                    if (minD2 > bestClear) { bestClear = minD2; fallback = d; }
                }
                bestDir2 = fallback;
            }
            if (bestDir2 != villain3Direction) {
                if (framesSinceVillain3Turn >= VILLAIN3_TURN_COOLDOWN_FRAMES || !pathSafeFromPlayer3(villain3Direction, 2)) {
                    villain3Direction = bestDir2;
                    framesSinceVillain3Turn = 0;
                } else {
                    framesSinceVillain3Turn++;
                }
            } else {
                framesSinceVillain3Turn++;
            }
        }
        switch (villain3Direction) {
            case 0 -> villain3X += villain3Speed;
            case 1 -> villain3Y += villain3Speed;
            case 2 -> villain3X -= villain3Speed;
            case 3 -> villain3Y -= villain3Speed;
        }
        double oldX2 = villain3X;
        double oldY2 = villain3Y;
        villain3X = wrapX2(villain3X);
        villain3Y = wrapY2(villain3Y);
        if (villain3X != oldX2 || villain3Y != oldY2) {
            if (!villain3History.isEmpty()) villain3History.remove(villain3History.size() - 1);
            villain3History.add(new double[]{villain3X, villain3Y});
        }
        villain3Head.setTranslateX(villain3X);
        villain3Head.setTranslateY(villain3Y);
        villain3Head.face(villain3Direction);
        villain3Head.ease();
        villain3History.add(new double[]{villain3X, villain3Y});
        int maxHistorySize = (villain3BodyParts.size() + 1) * (int)(SEGMENT_SPACING / villain3Speed) + 50;
        trimHistory(villain3History, maxHistorySize);
        int index = 1;
        double elapsedSeconds = (System.nanoTime() - startTime) / 1_000_000_000.0;
        for (Sphere segment : villain3BodyParts) {
            int posIndex = villain3History.size() - index * (int)(SEGMENT_SPACING / villain3Speed);
            if (posIndex < 0) posIndex = 0;
            double[] pos = posIndex < villain3History.size()
                    ? villain3History.get(posIndex)
                    : new double[]{segment.getTranslateX(), segment.getTranslateY()};
            segment.setTranslateX(pos[0]);
            segment.setTranslateY(pos[1]);
            if (index == villain3BodyParts.size()) {
                double maxAmplitude = SEGMENT_SPACING / 2.0;
                double rippleAmplitude = Math.min(5 + villain3BodyParts.size() * 0.2, maxAmplitude);double rippleFrequency = 5;
                segment.setTranslateX(pos[0] + rippleAmplitude * Math.sin(elapsedSeconds * rippleFrequency));
            }
            index++;
        }
    }
    public static void main(String[] args) {
        launch();
    }
}