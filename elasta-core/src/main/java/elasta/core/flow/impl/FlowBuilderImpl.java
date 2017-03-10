package elasta.core.flow.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import elasta.core.flow.*;
import elasta.core.promise.impl.Promises;

import java.util.*;

/**
 * Created by Jango on 11/13/2016.
 */
public class FlowBuilderImpl implements FlowBuilder {
    private String initialState;
    private final Map<String, Set<String>> eventsByStateMap;
    private final Map<String, Map<String, String>> eventToStateMapByState;
    private final Map<String, StateTransitionHandlers> stateCallbacksMap;

    public FlowBuilderImpl() {
        eventsByStateMap = new HashMap<>();
        eventToStateMapByState = new HashMap<>();
        stateCallbacksMap = new HashMap<>();
    }

    public FlowBuilderImpl(Flow flow) {
        FlowImpl flowImpl = (FlowImpl) flow;
        initialState = flowImpl.getInitialState();
        eventsByStateMap = new HashMap<>(flowImpl.getEventsByStateMap());
        eventToStateMapByState = new HashMap<>(flowImpl.getEventToStateMapByState());
        stateCallbacksMap = new HashMap<>(flowImpl.getStateCallbacksMap());
    }

    public static FlowBuilder create() {
        return new FlowBuilderImpl();
    }

    public FlowBuilder initialState(String initialState) {
        this.initialState = initialState;
        return this;
    }

    public FlowBuilder when(String state, EventAndState... stateEntries) {

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

            Objects.requireNonNull(entry, "EventAndState is null.");
            Objects.requireNonNull(entry.getEvent(), "Event is null.");
            Objects.requireNonNull(entry.getState(), "State is null for event '" + entry.getEvent() + "'.");

            eventSet.add(entry.getEvent());

            stateMap.put(entry.getEvent(), entry.getState());
        });

        return this;
    }

    @Override
    public FlowBuilder replace(String prevState, String newState) {
        Set<String> events = eventsByStateMap.get(prevState);

        if (events != null) {

            eventsByStateMap.remove(prevState);
            eventsByStateMap.put(newState, events);
        }

        Map<String, String> eventToStateMap = eventToStateMapByState.get(prevState);

        if (eventToStateMap != null) {

            eventToStateMapByState.remove(prevState);
            eventToStateMapByState.put(newState, eventToStateMap);
        }

        StateTransitionHandlers handlers = stateCallbacksMap.get(prevState);

        if (handlers != null) {

            stateCallbacksMap.remove(prevState);
            stateCallbacksMap.put(newState, handlers);
        }

        if (initialState.equals(prevState)) {
            initialState = newState;
        }

        return this;
    }

    @Override
    public FlowBuilder remove(String state) {
        Set<String> events = eventsByStateMap.get(state);

        if (events != null) {

            eventsByStateMap.remove(state);
        }

        Map<String, String> eventToStateMap = eventToStateMapByState.get(state);

        if (eventToStateMap != null) {

            eventToStateMapByState.remove(state);
        }

        StateTransitionHandlers handlers = stateCallbacksMap.get(state);

        if (handlers != null) {

            stateCallbacksMap.remove(state);
        }

        if (initialState.equals(state)) {
            initialState = null;
        }

        return this;
    }

    public FlowBuilder exec(String state, StateTransitionHandlers stateTransitionHandlers) {
        this.stateCallbacksMap.put(state, stateTransitionHandlers);
        return this;
    }

    @Override
    public <T, R> FlowBuilder handlers(String state, EnterEventHandler<T, R> onEnter) {
        this.<T, R>handlersP(state, o -> Promises.of(
            onEnter.handle(o)
        ));
        return this;
    }

    @Override
    public <T, R> FlowBuilder handlers(String state, EnterEventHandler<T, R> onEnter, ExitEventHandler onExit) {
        this.<T, R>handlersP(state, o -> Promises.of(
            onEnter.handle(o)
        ), () -> Promises.runnable(onExit::handle));
        return this;
    }

    public <T, R> FlowBuilder handlersP(String state, EnterEventHandlerP<T, R> onEnter) {
        return handlersP(state, onEnter, null);
    }

    public <T, R> FlowBuilder handlersP(String state, EnterEventHandlerP<T, R> onEnter, ExitEventHandlerP onExit) {
        this.stateCallbacksMap.put(state, Flow.execP(onEnter, onExit));
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

        return new FlowImpl(
            initialState,
            immutableCopyOf(eventsByStateMap),
            immutableCopyOf2(eventToStateMapByState),
            immutableCopyOf5(stateCallbacksMap));
    }

    private void ensureStateAndErrorMappingRecursive(String state, Set<String> events) {


        if (!stateCallbacksMap.containsKey(state)) {
            throw new FlowException("Callbacks is missing for state '" + state + "'.");
        }

        if (events == null) {
            throw new FlowException("Events is null for state '" + state + "'.");
        }

        Map<String, String> eventToStateMap = eventToStateMapByState.get(state);

        ImmutableSet.Builder<String> nextStatesBuilder = ImmutableSet.builder();

        events.forEach(event -> nextStatesBuilder.add(
            eventToStateMap.get(event)
        ));

        nextStatesBuilder.build().forEach(nextState -> ensureStateAndErrorMappingRecursive(nextState, eventsByStateMap.get(nextState)));
    }

    private Map<String, StateTransitionHandlers> immutableCopyOf5(Map<String, StateTransitionHandlers> stateCallbacksMap) {
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

    public static void main(String[] args) {
        Flow flow = Flow.builder()
            .initialState("start")
            .when("start",
                Flow.next("process"),
                Flow.on("err", "errState"))
            .when("errState", Flow.next("end"))
            .when("process", Flow.next("end"))
            .when("end", Flow.end())
            .exec("start", Flow.onEnterP(o -> {
                System.out.println("onStart: " + o);
                return Promises.of(Flow.trigger("err", new NullPointerException("ok")));
            }))
            .exec("errState", Flow.execP(
                o -> {
                    return Promises.of(Flow.triggerNext(o));
                },
                () -> {
                    System.out.println("onExist errorState: ");
                    return null;
                }))
            .exec("process", Flow.onEnterP(o -> {
                System.out.println("onEnter process: " + o);
                return Promises.of(Flow.triggerNext(o));
            }))
            .exec("end", Flow.execP(o -> {

                System.out.println("onEnter: end -> " + o);
                return Promises.of(Flow.triggerValue(o));
            }, () -> {

                System.out.println("onExit: end -> ");
                return null;
            }))
            .build();

        flow.start("go").then(System.out::println).err(Throwable::printStackTrace);
    }
}
