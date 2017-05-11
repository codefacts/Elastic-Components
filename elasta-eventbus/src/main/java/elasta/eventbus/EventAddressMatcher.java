package elasta.eventbus;

/**
 * Created by sohan on 5/9/2017.
 */
public interface EventAddressMatcher {
    boolean matches(String eventAddress);
}
