package elasta.composer.ex;

/**
 * Created by sohan on 5/15/2017.
 */
final public class NoUserInContextException extends RuntimeException {
    public NoUserInContextException(String msg) {
        super(msg);
    }
}
