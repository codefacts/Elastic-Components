package elasta.core.flow;

import elasta.core.intfs.FunctionUnchecked;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Defer;
import elasta.core.promise.intfs.Promise;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Created by Khan on 5/7/2016.
 */
public class Flow {
    private static final FlowTrigger STATE_TRIGGER = FlowTrigger.create(null, null);
    private static final String NEXT = "next";
    private final String initialState;
    private final Map<String, Set<String>> stateEvents;
    private final Map<String, Map<String, String>> eventStateMapByState;
    private final Map<String, FlowCallbacks> stateCallbacksMap;

    public Flow(String initialState, Map<String, Set<String>> stateEvents,
                Map<String, Map<String, String>> eventStateMapByState,
                Map<String, FlowCallbacks> stateCallbacksMap) {
        this.initialState = initialState;
        this.stateEvents = stateEvents;
        this.eventStateMapByState = eventStateMapByState;
        this.stateCallbacksMap = stateCallbacksMap;
    }

    public <T, R> Promise<R> start(T message) {
        try {
            final FlowCallbacks<T, Object> flowCallbacks = stateCallbacksMap.get(initialState);
            return execute(flowCallbacks, message)
                .mapP(trigger -> executeNext(trigger, initialState))
                .map(stateTrigger -> (R) stateTrigger.message);
        } catch (Throwable ex) {
            return Promises.error(ex);
        }
    }

    public <T, R> Promise<R> start(String state, T message) {
        try {
            final FlowCallbacks<T, Object> flowCallbacks = stateCallbacksMap.get(state);
            return execute(flowCallbacks, message)
                .mapP(trigger -> executeNext(trigger, state))
                .map(stateTrigger -> (R) stateTrigger.message);
        } catch (Throwable ex) {
            return Promises.error(ex);
        }
    }

    private <R> Promise<FlowTrigger<R>> executeNext(FlowTrigger<Object> trigger, String prevState) {

        final Set<String> events = stateEvents.get(prevState);

        if (events == null || events.size() == 0) {

            if (trigger == null) {
                return Promises.just(Flow.this.defaultStateTrigger());
            }

            return Promises.just((FlowTrigger<R>) trigger);
        }

        if (trigger == null || trigger.event == null) {
            return Promises.error(new FlowException("Invalid trigger '" + trigger + "' from state '" + prevState + "'."));
        }

        if (!events.contains(trigger.event)) {
            return Promises.error(new FlowException("Invalid event '" + trigger.event + "' on trigger from state '" + prevState + "'."));
        }

        final Map<String, String> esMap = eventStateMapByState.get(prevState);

        if (esMap == null) {
            return Promises.error(new FlowException("No \"event to state\" mapping found for state '" + prevState + "'."));
        }

        final String nextState = esMap.get(trigger.event);

        if (nextState == null) {
            return Promises.error(new FlowException("No state is found for event '" + trigger.event + "' for state '" + prevState + "'."));
        }

        final FlowCallbacks<Object, Object> nextFlowCallbacks = stateCallbacksMap.get(nextState);

        if (nextFlowCallbacks == null) {
            return Promises.error(new FlowException("Callbacks is missing for state '" + nextState + "'"));
        }

        return Flow.this.execute(nextFlowCallbacks, trigger.message)
            .mapP(sTrigger -> executeNext(sTrigger, nextState));
    }

    private <T, R> Promise<FlowTrigger<R>> execute(FlowCallbacks<T, R> flowCallbacks, T message) {
        try {
            final Defer<FlowTrigger<R>> defer = Promises.defer();
            flowCallbacks.onEnter.apply(message)
                .cmp(
                    promise -> {
                        if (promise.isError()) {
                            defer.reject(promise.err());
                            return;
                        }
                        final Promise<Void> voidPromise = flowCallbacks.onExit.call();
                        if (voidPromise == null) {
                            defer.resolve(promise.val());
                        } else {
                            voidPromise
                                .cmp(p -> {
                                    if (p.isSuccess()) {
                                        defer.resolve(promise.val());
                                    } else {
                                        defer.reject(p.err());
                                    }
                                });
                        }
                    })
                .err(defer::reject)
            ;
            return defer.promise();
        } catch (Throwable e) {
            return Promises.error(e);
        }
    }

    private <RR> FlowTrigger<RR> defaultStateTrigger() {
        return STATE_TRIGGER;
    }

    public static FlowBuilder builder() {
        return FlowBuilder.create();
    }

    public static FlowEntry on(String event, String state) {
        return FlowEntry.on(event, state);
    }

    public static FlowEntry next(String state) {
        return FlowEntry.on(NEXT, state);
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

    public static <T, R> FlowCallbacks execStart(FunctionUnchecked<T, Promise<FlowTrigger<R>>> startHandler) {
        return exec(startHandler, null);
    }

    public static FlowCallbacks execEnd(Callable<Promise<Void>> endHandler) {
        return exec(null, endHandler);
    }

    public static <T> FlowTrigger<T> triggerNext(T val) {
        return trigger(NEXT, val);
    }
}
