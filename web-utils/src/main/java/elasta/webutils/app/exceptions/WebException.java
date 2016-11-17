package elasta.webutils.app.exceptions;

/**
 * Created by Jango on 11/18/2016.
 */
public class WebException extends RuntimeException {
    public WebException(String msg) {
        super(msg);
    }

    public WebException(String msg, Throwable throwable) {
        super(msg, throwable);
    }
}
