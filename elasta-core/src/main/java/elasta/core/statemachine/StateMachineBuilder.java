package elasta.core.statemachine;

import java.util.*;

public class StateMachineBuilder {
    private String initialState;
    private final Map<String, Set<String>> stateEvents = new HashMap<>();
    private final Map<String, Map<String, String>> eventStateByState = new HashMap<>();
    private final Map<String, StateCallbacks> stateCallbacksMap = new HashMap<>();

    private StateMachineBuilder() {
    }

    public static StateMachineBuilder create() {
        return new StateMachineBuilder();
    }

    public StateMachineBuilder initialState(String initialState) {
        this.initialState = initialState;
        return this;
    }

    public StateMachineBuilder when(String state, StateEntry... stateEntries) {
        Set<String> events = stateEvents.get(state);
        if (events == null) {
            events = new HashSet<>();
            stateEvents.put(state, events);
        }

        final Set<String> eventSet = events;
        Arrays.asList(stateEntries).forEach(entry -> {
            eventSet.add(entry.event);

            Map<String, String> eventStateMap = eventStateByState.get(state);
            if (eventStateMap == null) {
                eventStateMap = new HashMap<>();
                eventStateByState.put(state, eventStateMap);
            }

            eventStateMap.put(entry.event, entry.state);
        });

        return this;
    }

    public StateMachineBuilder handlers(String state, StateCallbacks stateCallbacks) {
        this.stateCallbacksMap.put(state, stateCallbacks);
        return this;
    }

    public StateMachine build() {

        if (stateCallbacksMap.size() < stateEvents.size()) {
            throw new StateMachineException("State callbacks are missing for some states.");
        }

        if (initialState == null)
            throw new StateMachineException("Initial state ['" + initialState + "'] is required.");

        if (!stateCallbacksMap.containsKey(initialState))
            throw new StateMachineException("Callbacks is not defined for initial state ['" + initialState + "'].");

        return new StateMachine(initialState, stateEvents, eventStateByState, stateCallbacksMap);
    }

    public static void main(String[] args) {
        StateMachine.builder()
            .when("", StateMachine.on("", ""))
            .handlers("", StateMachine.exec().onEnter(null).onExit(null).build())
            .build();
    }
}