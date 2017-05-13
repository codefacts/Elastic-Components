package elasta.composer.state.handlers.ex;

/**
 * Created by sohan on 5/12/2017.
 */
final public class UserIdNotFoundException extends RuntimeException {
    public UserIdNotFoundException(String msg) {
        super(msg);
    }
}
