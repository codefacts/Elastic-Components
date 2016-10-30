package elasta.core.flow;

/**
 * Created by Khan on 5/7/2016.
 */
public class EventToStateMapping {
    final String event;
    final String state;

    private EventToStateMapping(String event, String state) {
        this.event = event;
        this.state = state;
    }

    public static EventToStateMapping on(String event, String state) {
        return new EventToStateMapping(event, state);
    }
}
