package tracker.server;

import java.io.File;
import java.io.PrintStream;
import java.util.Objects;

/**
 * Created by sohan on 7/8/2017.
 */
public class FileLogger {
    final PrintStream out;

    public FileLogger(File file) throws Exception {
        Objects.requireNonNull(file);
        out = new PrintStream(file);
    }

    void log(String uri, String method, String body) {
        out.println("----------------------------------------------------------------------------------------");
        out.println("uri: " + uri);
        out.println("method: " + method);
        out.println("body: " + body);
        out.flush();
    }
}
