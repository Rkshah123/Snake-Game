/**
 * Thin launcher used by the packaged desktop application.
 *
 * <p>Launching an {@link javafx.application.Application} subclass directly from a
 * plain classpath jar makes the JDK launcher print "JavaFX runtime components are
 * missing". Going through a regular class avoids that check while JavaFX classes
 * are provided on the classpath.</p>
 */
public final class Launcher {
    private Launcher() {
    }

    public static void main(String[] args) {
        Main.main(args);
    }
}
