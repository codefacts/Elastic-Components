package elasta.core.flow;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import elasta.core.intfs.FunctionUnchecked;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;

import java.util.*;
import java.util.concurrent.Callable;

public class FlowBuilder {
    private String initialState;
    private final Map<String, Set<String>> eventsByStateMap = new HashMap<>();
    private final Map<String, Map<String, String>> eventToStateMapByState = new HashMap<>();
    private final Map<String, FlowCallbacks> stateCallbacksMap = new HashMap<>();

    private Map<String, Set<Class<? extends Throwable>>> errorsByStateMap = new HashMap<>();
    private Map<String, Map<Class<? extends Throwable>, String>> errorToStateMapByState = new HashMap<>();

    private FlowBuilder() {
    }

    public static FlowBuilder create() {
        return new FlowBuilder();
    }

    public FlowBuilder initialState(String initialState) {
        this.initialState = initialState;
        return this;
    }

    public FlowBuilder when(String state, EventToStateMapping... stateEntries) {

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

    public FlowBuilder whenError(String state, ErrorToStateMapping... errorToStateMappings) {

        Objects.requireNonNull(state, "State is null.");

        Set<Class<? extends Throwable>> throwables = errorsByStateMap.get(state);
        if (throwables == null) {
            throwables = new HashSet<>();
            errorsByStateMap.put(state, throwables);
        }

        final Set<Class<? extends Throwable>> throwableSet = throwables;

        Map<Class<? extends Throwable>, String> errorToStateMap = errorToStateMapByState.get(state);

        if (errorToStateMap == null) {
            errorToStateMap = new HashMap<>();
            errorToStateMapByState.put(state, errorToStateMap);
        }

        final Map<Class<? extends Throwable>, String> stateMap = errorToStateMap;

        Arrays.asList(errorToStateMappings).forEach(entry -> {

            Objects.requireNonNull(entry, "EventToStateMapping is null.");
            Objects.requireNonNull(entry.getErrorClass(), "Event is null.");
            Objects.requireNonNull(entry.getNextState(), "State is null for event '" + entry.getNextState() + "'.");

            throwableSet.add(entry.getErrorClass());

            stateMap.put(entry.getErrorClass(), entry.getNextState());
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

        errorsByStateMap.forEach((state, classes) -> ensureStateAndErrorMappingRecursive(state, classes, eventsByStateMap.get(state)));

        return new Flow(
            initialState,
            immutableCopyOf(eventsByStateMap),
            immutableCopyOf2(eventToStateMapByState),
            immutableCopyOf3(errorsByStateMap),
            immutableCopyOf4(errorToStateMapByState),
            immutableCopyOf5(stateCallbacksMap));
    }

    private void ensureStateAndErrorMappingRecursive(String state, Set<Class<? extends Throwable>> classes, Set<String> events) {


        if (!stateCallbacksMap.containsKey(state)) {
            throw new FlowException("Callbacks is missing for state '" + state + "'.");
        }

        if (events == null) {
            throw new FlowException("Events is null for state '" + state + "'.");
        }

        Map<String, String> eventToStateMap = eventToStateMapByState.get(state);
        Map<Class<? extends Throwable>, String> errorToStateMap = errorToStateMapByState.get(state);

        ImmutableSet.Builder<String> nextStatesBuilder = ImmutableSet.builder();

        events.forEach(event -> nextStatesBuilder.add(
            eventToStateMap.get(event)
        ));

        if (classes != null) {

            classes.forEach(errClass -> nextStatesBuilder.add(
                errorToStateMap.get(errClass)
            ));
        }

        nextStatesBuilder.build().forEach(nextState -> ensureStateAndErrorMappingRecursive(nextState, errorsByStateMap.get(nextState), eventsByStateMap.get(nextState)));
    }

    private Map<String, Set<Class<? extends Throwable>>> immutableCopyOf3(Map<String, Set<Class<? extends Throwable>>> errorsByStateMap) {
        ImmutableMap.Builder<String, Set<Class<? extends Throwable>>> mapBuilder = ImmutableMap.builder();

        errorsByStateMap.forEach((state, classes) -> mapBuilder.put(state, ImmutableSet.copyOf(classes)));

        return mapBuilder.build();
    }

    private Map<String, Map<Class<? extends Throwable>, String>> immutableCopyOf4(Map<String, Map<Class<? extends Throwable>, String>> errorToStateMapByState) {
        ImmutableMap.Builder<String, Map<Class<? extends Throwable>, String>> mapBuilder = ImmutableMap.builder();

        errorToStateMapByState.forEach((state, classStringMap) -> mapBuilder.put(state, ImmutableMap.copyOf(classStringMap)));

        return mapBuilder.build();
    }

    private Map<String, FlowCallbacks> immutableCopyOf5(Map<String, FlowCallbacks> stateCallbacksMap) {
        return ImmutableMap.copyOf(stateCallbacksMap);
    }

    private Map<String, Map<String, String>> immutableCopyOf2(Map<String, Map<String, String>> eventToStateMapByState) {

        ImmutableMap.Builder<String, Map<String, String>> mapBuilder = ImmutableMap.builder();

        eventToStateMapByState.forEach((state, eventToStateMap) -> mapBuilder.put(state, ImmutableMap.copyOf(eventToStateMap)));

        return mapBuilder.build();
    }

    private Map<String, Set<String>> immutableCopyOf(Map<String, Set<String>> eventsByStateMap) {
        ImmutableMap.Builder<String, Set<String>> mapBuilder = ImmutableMap.builder();

        eventsByStateMap.forEach((state, events) -> mapBuilder.put(state, ImmutableSet.copyOf(events)));

        return mapBuilder.build();
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
        Flow flow = Flow.builder()
            .initialState("start")
            .when("start",
                Flow.next("process"),
                Flow.on("err", "errState"))
            .whenError("start", Flow.onErr(NullPointerException.class, "errState"))
            .when("errState", Flow.next("end"))
            .when("process", Flow.next("end"))
            .when("end", Flow.end())
            .exec("start", Flow.onEnter(o -> {
                System.out.println("onStart: " + o);
                return Promises.just(Flow.trigger("err", new NullPointerException("ok")));
            }))
            .exec("errState", Flow.exec(
                o -> {
                    return Promises.just(Flow.triggerNext(o));
                },
                () -> {
                    System.out.println("onExist errorState: ");
                    return null;
                }))
            .exec("process", Flow.onEnter(o -> {
                System.out.println("onEnter process: " + o);
                return Promises.just(Flow.triggerNext(o));
            }))
            .exec("end", Flow.exec(o -> {

                System.out.println("onEnter: end -> " + o);
                return Promises.just(Flow.triggerValue(o));
            }, () -> {

                System.out.println("onExit: end -> ");
                return null;
            }))
            .build();

        flow.start("go").then(System.out::println).err(Throwable::printStackTrace);
    }
}