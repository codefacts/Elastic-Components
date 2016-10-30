package elasta.core.flow;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import elasta.core.intfs.FunctionUnchecked;
import elasta.core.promise.intfs.Promise;

import java.util.*;
import java.util.concurrent.Callable;

public class FlowBuilder {
    private String initialState;
    private final Map<String, Set<String>> eventsByStateMap = new HashMap<>();
    private final Map<String, Map<String, String>> eventToStateMapByState = new HashMap<>();
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

        Objects.requireNonNull(state, "State is null.");

        Set<String> events = eventsByStateMap.get(state);
        if (events == null) {
            events = new HashSet<>();
            eventsByStateMap.put(state, events);
        }

        final Set<String> eventSet = events;

        Map<String, String> eventToStateMap = eventToStateMapByState.get(state);

        if (eventToStateMap == null) {
            eventToStateMap = new HashMap<>();
            eventToStateMapByState.put(state, eventToStateMap);
        }

        final Map<String, String> stateMap = eventToStateMap;

        Arrays.asList(stateEntries).forEach(entry -> {

            Objects.requireNonNull(entry, "EventToStateMapping is null.");
            Objects.requireNonNull(entry.event, "Event is null.");
            Objects.requireNonNull(entry.state, "State is null for event '" + entry.event + "'.");

            eventSet.add(entry.event);

            stateMap.put(entry.event, entry.state);
        });

        return this;
    }

    public Flow build() {

        if (stateCallbacksMap.size() < eventsByStateMap.size()) {
            throw new FlowException("State callbacks are missing for some states.");
        }

        if (initialState == null) {
            throw new FlowException("Initial state ['" + initialState + "'] is required.");
        }

        if (!stateCallbacksMap.containsKey(initialState)) {
            throw new FlowException("Callbacks is not defined for initial state ['" + initialState + "'].");
        }

        eventsByStateMap.forEach(this::ensureStateMappingRecursive);

        return new Flow(initialState, immutableCopyOf(eventsByStateMap), immutableCopyOf2(eventToStateMapByState), immutableCopyOf3(stateCallbacksMap));
    }

    private Map<String, FlowCallbacks> immutableCopyOf3(Map<String, FlowCallbacks> stateCallbacksMap) {
        return ImmutableMap.copyOf(stateCallbacksMap);
    }

    private Map<String, Map<String, String>> immutableCopyOf2(Map<String, Map<String, String>> eventToStateMapByState) {

        ImmutableMap.Builder<String, Map<String, String>> mapBuilder = ImmutableMap.builder();

        eventToStateMapByState.forEach((state, eventToStateMap) -> mapBuilder.put(state, ImmutableMap.copyOf(eventToStateMap)));

        return null;
    }

    private Map<String, Set<String>> immutableCopyOf(Map<String, Set<String>> eventsByStateMap) {
        ImmutableMap.Builder<String, Set<String>> mapBuilder = ImmutableMap.builder();

        eventsByStateMap.forEach((state, events) -> mapBuilder.put(state, ImmutableSet.copyOf(events)));

        return mapBuilder.build();
    }

    private void ensureStateMappingRecursive(String state, Set<String> events) {

        if (!stateCallbacksMap.containsKey(state)) {
            throw new FlowException("Callbacks is missing for state '" + state + "'.");
        }

        if (events == null) {
            throw new FlowException("Set of events is null for state '" + state + "'.");
        }

        Map<String, String> eventToStateMap = eventToStateMapByState.get(state);

        events.forEach(event -> {

            String nextState = eventToStateMap.get(event);

            ensureStateMappingRecursive(nextState, eventsByStateMap.get(nextState));

        });
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

    public static void main(String[] args) {
        Flow.builder()
            .when("", Flow.on("", ""))
            .exec("", Flow.exec().onEnter(null).onExit(null).build())
            .build();
    }
}