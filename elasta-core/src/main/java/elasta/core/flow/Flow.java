package elasta.core.flow;

import elasta.core.flow.impl.FlowCallbacksBuilder;
import elasta.core.intfs.Fun1Unckd;
import elasta.core.intfs.RunnableUnckd;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;

/**
 * Created by Khan on 5/7/2016.
 */
public interface Flow {

    String NEXT = "next";

    public <T, R> Promise<R> start(T message);

    public <T, R> Promise<R> start(String state, T message);

    public static FlowBuilder builder(Flow flow) {
        return FlowBuilder.create(flow);
    }

    public static FlowBuilder builder() {
        return FlowBuilder.create();
    }

    public static EventAndState on(String event, String state) {
        return EventAndState.on(event, state);
    }

    public static EventAndState next(String state) {
        return EventAndState.on(NEXT, state);
    }

    public static EventAndState[] end() {
        return new EventAndState[]{};
    }

    public static <T> StateTrigger<T> trigger(String event, T message) {
        return StateTrigger.create(event, message);
    }

    public static <T> StateTrigger<T> triggerExit(T message) {
        return StateTrigger.create(null, message);
    }

    public static <T> StateTrigger<T> triggerExit() {
        return StateTrigger.create(null, null);
    }

    public static <T> StateTrigger<T> triggerValue(T value) {
        return trigger(null, value);
    }

    public static <T> StateTrigger<T> triggerNext(T val) {
        return trigger(NEXT, val);
    }

    public static <T, R> FlowCallbacksBuilder<T, R> execP() {
        return FlowCallbacksBuilder.<T, R>create();
    }

    public static <T, R> StateTransitionHandlers<T, R> exec(
        Fun1Unckd<T, StateTrigger<R>> onEnter,
        RunnableUnckd onExit) {
        return new StateTransitionHandlers<>(t -> Promises.of(
            onEnter.apply(t)
        ), () -> Promises.runnable(onExit));
    }

    public static <T, R> StateTransitionHandlers<T, R> execP(
        EnterEventHandlerP<T, R> onEnter,
        ExitEventHandlerP onExit) {
        return new StateTransitionHandlers<>(onEnter, onExit);
    }

    public static <T, R> StateTransitionHandlers onEnter(Fun1Unckd<T, StateTrigger<R>> startHandler) {
        return Flow.<T, R>execP(o -> Promises.of(
            startHandler.apply(o)
        ), null);
    }

    public static <T, R> StateTransitionHandlers onEnterP(EnterEventHandlerP<T, R> startHandler) {
        return execP(startHandler, null);
    }

    public static StateTransitionHandlers onExit(RunnableUnckd endHandler) {
        return execP(null, () -> Promises.runnable(endHandler));
    }

    public static StateTransitionHandlers onExitP(ExitEventHandlerP endHandler) {
        return execP(null, endHandler);
    }
}
