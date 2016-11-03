package elasta.core.flow;

import elasta.core.intfs.FunctionUnchecked;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Created by Khan on 5/7/2016.
 */
public class Flow {
    private static final FlowTrigger DEFAULT_STATE_TRIGGER = FlowTrigger.create(null, null);
    private static final String NEXT = "next";
    private final String initialState;
    private final Map<String, Set<String>> eventsByStateMap;
    private final Map<String, Map<String, String>> eventToStateMapByState;

    private final Map<String, FlowCallbacks> stateCallbacksMap;

    public Flow(String initialState, Map<String, Set<String>> eventsByStateMap,
                Map<String, Map<String, String>> eventToStateMapByState,
                Map<String, FlowCallbacks> stateCallbacksMap) {

        this.initialState = initialState;
        this.eventsByStateMap = eventsByStateMap;
        this.eventToStateMapByState = eventToStateMapByState;
        this.stateCallbacksMap = stateCallbacksMap;
    }

    public <T, R> Promise<R> start(T message) {
        return start(initialState, message);
    }

    public <T, R> Promise<R> start(String state, T message) {
        return execState(state, message);
    }

    private <R, T> Promise<R> execState(String state, T message) {

        final FlowCallbacks<T, Object> flowCallbacks = stateCallbacksMap.get(state);

        return this.execute(flowCallbacks, message).mapP(trigger -> {

            final NextStateAndMessage nextStateAndMessage = nextStateAndMessage(trigger, state);

            if (nextStateAndMessage.nextState == null) {
                return Promises.just((R) nextStateAndMessage.message);
            }

            return execState(nextStateAndMessage.nextState, nextStateAndMessage.message);
        });
    }

    private <P> NextStateAndMessage<P> nextStateAndMessage(FlowTrigger<P> trigger, String state) {

        final Set<String> events = eventsByStateMap.get(state);

        if (events.size() == 0) {

            if (trigger == null) {
                return new NextStateAndMessage<>(null, null);
            }

            return new NextStateAndMessage<>(null, trigger.message);
        }

        if (trigger == null || trigger.event == null) {
            throw new FlowException("Invalid trigger '" + trigger + "' from state '" + state + "'.");
        }

        if (!events.contains(trigger.event)) {
            throw new FlowException("Invalid event '" + trigger.event + "' on trigger from state '" + state + "'.");
        }

        return new NextStateAndMessage<>(eventToStateMapByState.get(state).get(trigger.event), trigger.message);
    }

    private <T, R> Promise<FlowTrigger<R>> execute(FlowCallbacks<T, R> flowCallbacks, T message) {
        try {
            return flowCallbacks.onEnter.apply(message)
                .cmpP(signal -> {

                    Promise<Void> voidPromise = flowCallbacks.onExit.call();
                    return voidPromise == null ? Promises.empty() : voidPromise;
                });

        } catch (Throwable throwable) {
            return Promises.error(throwable);
        }
    }

    private <RR> FlowTrigger<RR> defaultStateTrigger() {
        return DEFAULT_STATE_TRIGGER;
    }

    public static FlowBuilder builder() {
        return FlowBuilder.create();
    }

    public static EventToStateMapping on(String event, String state) {
        return EventToStateMapping.on(event, state);
    }

    public static EventToStateMapping next(String state) {
        return EventToStateMapping.on(NEXT, state);
    }

    public static <T, R> FlowCallbacks<T, R> exec(
        FunctionUnchecked<T, Promise<FlowTrigger<R>>> onEnter,
        Callable<Promise<Void>> onExit) {
        return new FlowCallbacks<>(onEnter, onExit);
    }

    public static <T, R> FlowCallbacksBuilder<T, R> exec() {
        return FlowCallbacksBuilder.<T, R>create();
    }

    public static <T> FlowTrigger<T> trigger(String event, T message) {
        return FlowTrigger.create(event, message);
    }

    public static <T> FlowTrigger<T> exit(T message) {
        return FlowTrigger.create(null, message);
    }

    public static <T> FlowTrigger<T> exit() {
        return FlowTrigger.create(null, null);
    }

    public static <T, R> FlowCallbacks onEnter(FunctionUnchecked<T, Promise<FlowTrigger<R>>> startHandler) {
        return exec(startHandler, null);
    }

    public static FlowCallbacks onExit(Callable<Promise<Void>> endHandler) {
        return exec(null, endHandler);
    }

    public static <T> FlowTrigger<T> triggerNext(T val) {
        return trigger(NEXT, val);
    }

    public static EventToStateMapping[] end() {
        return new EventToStateMapping[]{};
    }

    public static <T> FlowTrigger<T> triggerValue(T value) {
        return trigger(null, value);
    }

    private static class NextStateAndMessage<T> {
        private final String nextState;
        private final T message;

        public NextStateAndMessage(String nextState, T message) {
            this.nextState = nextState;
            this.message = message;
        }
    }
}
