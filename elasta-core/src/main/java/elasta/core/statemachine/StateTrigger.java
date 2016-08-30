package elasta.core.statemachine;

/**
 * Created by Khan on 5/7/2016.
 */
public class StateTrigger<T> {
    final String event;
    final T message;

    private StateTrigger(String event, T message) {
        this.event = event;
        this.message = message;
    }

    static <T> StateTrigger<T> create(String event, T message) {
        return new StateTrigger<>(event, message);
    }

    static <T> StateTrigger<T> exit(T message) {
        return new StateTrigger<>(null, message);
    }

    static <T> StateTrigger<T> exit() {
        return new StateTrigger<>(null, null);
    }
}
