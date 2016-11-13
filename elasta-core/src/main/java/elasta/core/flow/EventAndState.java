package elasta.core.flow;

/**
 * Created by Khan on 5/7/2016.
 */
public class EventAndState {
    final String event;
    final String state;

    private EventAndState(String event, String state) {
        this.event = event;
        this.state = state;
    }

    public static EventAndState on(String event, String state) {
        return new EventAndState(event, state);
    }

    public String getEvent() {
        return event;
    }

    public String getState() {
        return state;
    }
}
