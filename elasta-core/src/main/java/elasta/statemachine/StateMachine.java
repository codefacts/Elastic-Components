package elasta.statemachine;

import elasta.intfs.FunctionUnchecked;
import elasta.promise.impl.Promises;
import elasta.promise.intfs.Defer;
import elasta.promise.intfs.Promise;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

/**
 * Created by Khan on 5/7/2016.
 */
public class StateMachine {
    private static final StateTrigger STATE_TRIGGER = StateTrigger.create(null, null);
    private static final String NEXT = "NEXT";
    private final String initialState;
    private final Map<String, Set<String>> stateEvents;
    private final Map<String, Map<String, String>> eventStateMapByState;
    private final Map<String, StateCallbacks> stateCallbacksMap;

    public StateMachine(String initialState, Map<String, Set<String>> stateEvents,
                        Map<String, Map<String, String>> eventStateMapByState,
                        Map<String, StateCallbacks> stateCallbacksMap) {
        this.initialState = initialState;
        this.stateEvents = stateEvents;
        this.eventStateMapByState = eventStateMapByState;
        this.stateCallbacksMap = stateCallbacksMap;
    }

    public <T, R> Promise<R> start(T message) {
        try {
            final StateCallbacks<T, Object> stateCallbacks = stateCallbacksMap.get(initialState);
            return execute(stateCallbacks, message)
                    .mapP(trigger -> executeNext(trigger, initialState))
                    .map(stateTrigger -> (R) stateTrigger.message);
        } catch (Throwable ex) {
            return Promises.error(ex);
        }
    }

    public <T, R> Promise<R> start(String state, T message) {
        try {
            final StateCallbacks<T, Object> stateCallbacks = stateCallbacksMap.get(state);
            return execute(stateCallbacks, message)
                    .mapP(trigger -> executeNext(trigger, state))
                    .map(stateTrigger -> (R) stateTrigger.message);
        } catch (Throwable ex) {
            return Promises.error(ex);
        }
    }

    private <R> Promise<StateTrigger<R>> executeNext(StateTrigger<Object> trigger, String prevState) {

        final Set<String> events = stateEvents.get(prevState);

        if (events == null || events.size() == 0) {

            if (trigger == null) {
                return Promises.just(StateMachine.this.defaultStateTrigger());
            }

            return Promises.just((StateTrigger<R>) trigger);
        }

        if (trigger == null || trigger.event == null) {
            return Promises.error(new StateMachineException("Invalid trigger '" + trigger + "' from state '" + prevState + "'."));
        }

        if (!events.contains(trigger.event)) {
            return Promises.error(new StateMachineException("Invalid event '" + trigger.event + "' on trigger from state '" + prevState + "'."));
        }

        final Map<String, String> esMap = eventStateMapByState.get(prevState);

        if (esMap == null) {
            return Promises.error(new StateMachineException("No \"event to state\" mapping found for state '" + prevState + "'."));
        }

        final String nextState = esMap.get(trigger.event);

        if (nextState == null) {
            return Promises.error(new StateMachineException("No state is found for event '" + trigger.event + "' for state '" + prevState + "'."));
        }

        final StateCallbacks<Object, Object> nextStateCallbacks = stateCallbacksMap.get(nextState);

        if (nextStateCallbacks == null) {
            return Promises.error(new StateMachineException("Callbacks is missing for state '" + nextState + "'"));
        }

        return StateMachine.this.execute(nextStateCallbacks, trigger.message)
                .mapP(sTrigger -> executeNext(sTrigger, nextState));
    }

    private <T, R> Promise<StateTrigger<R>> execute(StateCallbacks<T, R> stateCallbacks, T message) {
        try {
            final Defer<StateTrigger<R>> defer = Promises.defer();
            stateCallbacks.onEnter.apply(message)
                    .complete(
                            promise -> {
                                if (promise.isError()) {
                                    defer.reject(promise.error());
                                    return;
                                }
                                final Promise<Void> voidPromise = stateCallbacks.onExit.call();
                                if (voidPromise == null) {
                                    defer.resolve(promise.value());
                                } else {
                                    voidPromise
                                            .complete(p -> {
                                                if (p.isSuccess()) {
                                                    defer.resolve(promise.value());
                                                } else {
                                                    defer.reject(p.error());
                                                }
                                            });
                                }
                            })
                    .error(defer::reject)
            ;
            return defer.promise();
        } catch (Throwable e) {
            return Promises.error(e);
        }
    }

    private <RR> StateTrigger<RR> defaultStateTrigger() {
        return STATE_TRIGGER;
    }

    public static StateMachineBuilder builder() {
        return StateMachineBuilder.create();
    }

    public static StateEntry on(String event, String state) {
        return StateEntry.on(event, state);
    }

    public static StateEntry next(String state) {
        return StateEntry.on(NEXT, state);
    }

    public static <T, R> StateCallbacks<T, R> exec(
            FunctionUnchecked<T, Promise<StateTrigger<R>>> onEnter,
            Callable<Promise<Void>> onExit) {
        return new StateCallbacks<>(onEnter, onExit);
    }

    public static <T, R> StateCallbacksBuilder<T, R> exec() {
        return StateCallbacksBuilder.<T, R>create();
    }

    public static <T> StateTrigger<T> trigger(String event, T message) {
        return StateTrigger.create(event, message);
    }

    public static <T> StateTrigger<T> exit(T message) {
        return StateTrigger.create(null, message);
    }

    public static <T> StateTrigger<T> exit() {
        return StateTrigger.create(null, null);
    }
}
