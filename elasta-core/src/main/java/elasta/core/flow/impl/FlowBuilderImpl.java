package elasta.core.flow.impl;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import elasta.core.flow.*;
import elasta.core.intfs.Fun1Unckd;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;

import java.util.*;
import java.util.concurrent.Callable;

/**
 * Created by Jango on 11/13/2016.
 */
public class FlowBuilderImpl implements FlowBuilder {
    private String initialState;
    private final Map<String, Set<String>> eventsByStateMap = new HashMap<>();
    private final Map<String, Map<String, String>> eventToStateMapByState = new HashMap<>();
    private final Map<String, StateTransitionHandlers> stateCallbacksMap = new HashMap<>();

    public FlowBuilderImpl() {
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

    public FlowBuilder exec(String state, StateTransitionHandlers stateTransitionHandlers) {
        this.stateCallbacksMap.put(state, stateTransitionHandlers);
        return this;
    }

    public <T, R> FlowBuilder handlers(String state, Fun1Unckd<T, Promise<StateTrigger<R>>> onEnter) {
        return handlers(state, onEnter, null);
    }

    public <T, R> FlowBuilder handlers(String state, Fun1Unckd<T, Promise<StateTrigger<R>>> onEnter, Callable<Promise<Void>> onExit) {
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
                return Promises.just(Flow.trigger("err", new NullPointerException("ok")));
            }))
            .exec("errState", Flow.execP(
                o -> {
                    return Promises.just(Flow.triggerNext(o));
                },
                () -> {
                    System.out.println("onExist errorState: ");
                    return null;
                }))
            .exec("process", Flow.onEnterP(o -> {
                System.out.println("onEnter process: " + o);
                return Promises.just(Flow.triggerNext(o));
            }))
            .exec("end", Flow.execP(o -> {

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
