package elasta.core.flow.impl;

import elasta.core.flow.Flow;
import elasta.core.flow.FlowException;
import elasta.core.flow.StateTransitionHandlers;
import elasta.core.flow.StateTrigger;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;

import java.util.Map;
import java.util.Set;

/**
 * Created by Jango on 11/13/2016.
 */
public class FlowImpl implements Flow {
    private static final StateTrigger DEFAULT_STATE_TRIGGER = StateTrigger.create(null, null);
    private final String initialState;
    private final Map<String, Set<String>> eventsByStateMap;
    private final Map<String, Map<String, String>> eventToStateMapByState;

    private final Map<String, StateTransitionHandlers> stateCallbacksMap;

    FlowImpl(String initialState, Map<String, Set<String>> eventsByStateMap,
             Map<String, Map<String, String>> eventToStateMapByState,
             Map<String, StateTransitionHandlers> stateCallbacksMap) {

        this.initialState = initialState;
        this.eventsByStateMap = eventsByStateMap;
        this.eventToStateMapByState = eventToStateMapByState;
        this.stateCallbacksMap = stateCallbacksMap;
    }

    public <R> Promise<R> start(Object message) {
        return start(initialState, message);
    }

    public <R> Promise<R> start(String state, Object message) {
        return execState(state, message);
    }

    private <R, T> Promise<R> execState(String state, T message) {

        final StateTransitionHandlers<T, Object> stateTransitionHandlers = stateCallbacksMap.get(state);

        return this.execute(stateTransitionHandlers, message).mapP(trigger -> {

            final NextStateAndMessage nextStateAndMessage = nextStateAndMessage(trigger, state);

            if (nextStateAndMessage.nextState == null) {
                return Promises.of((R) nextStateAndMessage.message);
            }

            return execState(nextStateAndMessage.nextState, nextStateAndMessage.message);
        });
    }

    private <P> NextStateAndMessage<P> nextStateAndMessage(StateTrigger<P> trigger, String state) {

        final Set<String> events = eventsByStateMap.get(state);

        if (events.size() == 0) {

            if (trigger == null) {
                return new NextStateAndMessage<>(null, null);
            }

            return new NextStateAndMessage<>(null, trigger.getMessage());
        }

        if (trigger == null || trigger.getEvent() == null) {
            throw new FlowException("Invalid trigger '" + trigger + "' from state '" + state + "'.");
        }

        if (!events.contains(trigger.getEvent())) {
            throw new FlowException("Invalid event '" + trigger.getEvent() + "' on trigger from state '" + state + "'.");
        }

        return new NextStateAndMessage<>(eventToStateMapByState.get(state).get(trigger.getEvent()), trigger.getMessage());
    }

    private <T, R> Promise<StateTrigger<R>> execute(StateTransitionHandlers<T, R> stateTransitionHandlers, T message) {
        try {
            return stateTransitionHandlers.getOnEnter().handle(message)
                .cmpP(signal -> {

                    Promise<Void> voidPromise = stateTransitionHandlers.getOnExit().handle();
                    return voidPromise == null ? Promises.empty() : voidPromise;
                });

        } catch (Throwable throwable) {
            return Promises.error(throwable);
        }
    }

    private <RR> StateTrigger<RR> defaultStateTrigger() {
        return DEFAULT_STATE_TRIGGER;
    }

    private static class NextStateAndMessage<T> {
        private final String nextState;
        private final T message;

        public NextStateAndMessage(String nextState, T message) {
            this.nextState = nextState;
            this.message = message;
        }
    }

    public static StateTrigger getDefaultStateTrigger() {
        return DEFAULT_STATE_TRIGGER;
    }

    public String getInitialState() {
        return initialState;
    }

    public Map<String, Set<String>> getEventsByStateMap() {
        return eventsByStateMap;
    }

    public Map<String, Map<String, String>> getEventToStateMapByState() {
        return eventToStateMapByState;
    }

    public Map<String, StateTransitionHandlers> getStateCallbacksMap() {
        return stateCallbacksMap;
    }
}
