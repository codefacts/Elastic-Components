package tracker.server.component.ex;

/**
 * Created by sohan on 7/3/2017.
 */
public class AuthTokenGeneratorException extends RuntimeException {
    public AuthTokenGeneratorException(String msg) {
        super(msg);
    }

    public AuthTokenGeneratorException(String message, Throwable cause) {
        super(message, cause);
    }
}
