package elasta.core.statemachine;
/**
 * Created by Khan on 5/7/2016.
 */
public class StateEntry {
    final String event;
    final String state;

    private StateEntry(String event, String state) {
        this.event = event;
        this.state = state;
    }

    public static StateEntry on(String event, String state) {
        return new StateEntry(event, state);
    }
}
