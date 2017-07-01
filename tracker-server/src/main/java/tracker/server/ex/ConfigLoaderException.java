package tracker.server.ex;

/**
 * Created by sohan on 7/1/2017.
 */
final public class ConfigLoaderException extends RuntimeException {
    public ConfigLoaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public ConfigLoaderException(String msg) {
        super(msg);
    }
}
