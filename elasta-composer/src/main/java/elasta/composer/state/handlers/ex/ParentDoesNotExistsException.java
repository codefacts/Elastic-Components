package elasta.composer.state.handlers.ex;

/**
 * Created by sohan on 5/25/2017.
 */
final public class ParentDoesNotExistsException extends RuntimeException {
    public ParentDoesNotExistsException(String msg) {
        super(msg);
    }
}
