package elasta.eventbus;

/**
 * Created by sohan on 5/9/2017.
 */
public interface EventHandlerBase {
    default int code() {
        throw new UnsupportedOperationException();
    }
}
