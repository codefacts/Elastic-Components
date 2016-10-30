package elasta.core.flow;

import elasta.core.intfs.FunctionUnchecked;
import elasta.core.promise.intfs.Promise;

import java.util.*;
import java.util.concurrent.Callable;

public class FlowBuilder {
    private String initialState;
    private final Map<String, Set<String>> stateEvents = new HashMap<>();
    private final Map<String, Map<String, String>> eventStateByState = new HashMap<>();
    private final Map<String, FlowCallbacks> stateCallbacksMap = new HashMap<>();

    private FlowBuilder() {
    }

    public static FlowBuilder create() {
        return new FlowBuilder();
    }

    public FlowBuilder startPoint(String initialState) {
        this.initialState = initialState;
        return this;
    }

    public FlowBuilder when(String state, FlowEntry... stateEntries) {
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

    public FlowBuilder exec(String state, FlowCallbacks flowCallbacks) {
        this.stateCallbacksMap.put(state, flowCallbacks);
        return this;
    }

    public <T, R> FlowBuilder handlers(String state, FunctionUnchecked<T, Promise<FlowTrigger<R>>> onEnter) {
        return handlers(state, onEnter, null);
    }

    public <T, R> FlowBuilder handlers(String state, FunctionUnchecked<T, Promise<FlowTrigger<R>>> onEnter, Callable<Promise<Void>> onExit) {
        this.stateCallbacksMap.put(state, Flow.exec(onEnter, onExit));
        return this;
    }

    public Flow build() {

        if (stateCallbacksMap.size() < stateEvents.size()) {
            throw new FlowException("State callbacks are missing for some states.");
        }

        if (initialState == null)
            throw new FlowException("Initial state ['" + initialState + "'] is required.");

        if (!stateCallbacksMap.containsKey(initialState))
            throw new FlowException("Callbacks is not defined for initial state ['" + initialState + "'].");

        return new Flow(initialState, stateEvents, eventStateByState, stateCallbacksMap);
    }

    public static void main(String[] args) {
        Flow.builder()
            .when("", Flow.on("", ""))
            .exec("", Flow.exec().onEnter(null).onExit(null).build())
            .build();
    }
}