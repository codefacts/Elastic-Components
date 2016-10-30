package elasta.core.flow;

/**
 * Created by Khan on 5/7/2016.
 */
public class FlowTrigger<T> {
    final String event;
    final T message;

    private FlowTrigger(String event, T message) {
        this.event = event;
        this.message = message;
    }

    static <T> FlowTrigger<T> create(String event, T message) {
        return new FlowTrigger<>(event, message);
    }

    static <T> FlowTrigger<T> exit(T message) {
        return new FlowTrigger<>(null, message);
    }

    static <T> FlowTrigger<T> exit() {
        return new FlowTrigger<>(null, null);
    }
}
