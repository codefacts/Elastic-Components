package elasta.composer.state.handlers.builder.ex;

/**
 * Created by sohan on 5/25/2017.
 */
final public class NoResultFoundException extends RuntimeException {
    public NoResultFoundException(String msg) {
        super(msg);
    }

    public NoResultFoundException() {
        super("No result found");
    }
}
