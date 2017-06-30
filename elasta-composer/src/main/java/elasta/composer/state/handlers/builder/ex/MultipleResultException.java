package elasta.composer.state.handlers.builder.ex;

/**
 * Created by sohan on 5/25/2017.
 */
final public class MultipleResultException extends RuntimeException {
    public MultipleResultException() {
        super("Multiple result found but expected single result");
    }

    public MultipleResultException(String msg) {
        super(msg);
    }
}
