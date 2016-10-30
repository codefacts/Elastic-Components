package elasta.core.flow;
/**
 * Created by Khan on 5/7/2016.
 */
public class FlowEntry {
    final String event;
    final String state;

    private FlowEntry(String event, String state) {
        this.event = event;
        this.state = state;
    }

    public static FlowEntry on(String event, String state) {
        return new FlowEntry(event, state);
    }
}
