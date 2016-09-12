package elasta.webutils;

/**
 * Created by Jango on 9/12/2016.
 */
@FunctionalInterface
public interface EventNameGenerator {
    String eventName(String event, String resourceName);
}
