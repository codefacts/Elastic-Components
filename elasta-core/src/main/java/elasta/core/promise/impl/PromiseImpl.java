package elasta.core.promise.impl;

import elasta.core.promise.intfs.*;

import java.util.Objects;

/**
 * Created by Shahadat on 8/24/2016.
 */
final public class PromiseImpl<T> implements Promise<T>, Defer<T> {
    private final Executor executor;
    private SignalImpl<T> signal;
    private PromiseImpl next;

    PromiseImpl() {
        this.executor = null;
        this.signal = null;
    }

    PromiseImpl(SignalImpl<T> signal) {
        Objects.requireNonNull(signal);
        this.executor = null;
        this.signal = signal;
    }

    PromiseImpl(Executor executor) {
        Objects.requireNonNull(executor);
        this.executor = executor;
    }

    @Override
    public Promise<T> filter(FilterHandler<T> filterHandler) {

        PromiseImpl<T> promise = createPromise(Executors.filterExecutor(filterHandler));

        return executeOrSchedule(promise);
    }

    @Override
    public Promise<T> filterP(FilterPHandler<T> filterPHandler) {

        PromiseImpl<T> promise = createPromise(Executors.deferredFilterExecutor(filterPHandler));

        return executeOrSchedule(promise);
    }

    @Override
    public <R> Promise<R> map(MapHandler<T, R> mapHandler) {

        PromiseImpl<R> promise = createPromise(Executors.mapExecutor(mapHandler));

        return executeOrSchedule(promise);
    }

    @Override
    public <R> Promise<R> mapP(MapPHandler<T, R> mapPHandler) {

        PromiseImpl<R> promise = createPromise(Executors.deferredMapExecutor(mapPHandler));

        return executeOrSchedule(promise);
    }

    @Override
    public Promise<T> then(ThenHandler<T> thenHandler) {

        PromiseImpl<T> promise = createPromise(Executors.thenExecutor(thenHandler));

        return executeOrSchedule(promise);
    }

    @Override
    public Promise<T> thenP(ThenPHandler<T> thenPHandler) {

        PromiseImpl<T> promise = createPromise(Executors.deferredThenExecutor(thenPHandler));

        return executeOrSchedule(promise);
    }

    @Override
    public Promise<T> error(ErrorHandler errorHandler) {

        PromiseImpl<T> promise = createPromise(Executors.errorExecutor(errorHandler));

        return executeOrSchedule(promise);
    }

    @Override
    public Promise<T> errorP(ErrorPHandler errorPHandler) {

        PromiseImpl<T> promise = createPromise(Executors.deferredErrorExecutor(errorPHandler));

        return executeOrSchedule(promise);
    }

    @Override
    public Promise<T> complete(CompleteHandler<T> completeHandler) {

        PromiseImpl<T> promise = createPromise(Executors.completeExecutor(completeHandler));

        return executeOrSchedule(promise);
    }

    @Override
    public Promise<T> completeP(CompletePHandler<T> completePHandler) {

        PromiseImpl<T> promise = createPromise(Executors.deferredCompleteExecutor(completePHandler));

        return executeOrSchedule(promise);
    }

    @Override
    public boolean isComplete() {
        return signal != null;
    }

    @Override
    public boolean isFiltered() {
        return signal.type == Signal.Type.FILTERED;
    }

    @Override
    public boolean isSuccess() {
        return signal.type == Signal.Type.SUCCESS;
    }

    @Override
    public boolean isError() {
        return signal.type == Signal.Type.ERROR;
    }

    @Override
    public T value() {
        return signal.value();
    }

    @Override
    public T orElse(T defaultValue) {
        return (signal.value() == null) ? defaultValue : signal.value();
    }

    @Override
    public Throwable error() {
        return signal.error();
    }

    public static <T> void reject(final PromiseImpl<T> prevPromise, final Throwable throwable) {

        execute(prevPromise.next, prevPromise.signal = SignalImpl.error(throwable));
    }

    public static <T> void resolve(final PromiseImpl<T> promise, final T value) {

        execute(promise.next, promise.signal = SignalImpl.success(value));
    }

    private static <T> void execute(PromiseImpl promise, SignalImpl<T> signal) {

        for (; ; ) {

            if ((promise == null) | (signal == null)) {
                break;
            }

            if (promise.executor.type() == Executor.INSTANT) {

                signal = promise.signal = (SignalImpl) promise.executor.execute(signal);

                promise = promise.next;

            } else {

                PromiseImpl<SignalImpl> signalPromise = (PromiseImpl<SignalImpl>) promise.executor.execute(signal);

                final PromiseImpl prms = promise;

                signalPromise.complete(retSignal -> {

                    prms.signal = (SignalImpl) retSignal;

                    execute(prms.next, prms.signal);
                });

            }

        }

    }

    private <TT> Promise<TT> executeOrSchedule(PromiseImpl<TT> promise) {

        if (isComplete()) {

            execute(promise, signal);

            return promise;

        } else {
            return promise;
        }
    }

    private <TT, RR> PromiseImpl<RR> createPromise(InstantExecutor<TT, RR> ttInstantExecutor) {
        PromiseImpl<RR> promise = new PromiseImpl<>(ttInstantExecutor);
        this.next = promise;
        return promise;
    }

    private <TT, RR> PromiseImpl<RR> createPromise(DeferredExecutor<TT, RR> ttDeferredExecutor) {
        PromiseImpl<RR> promise = new PromiseImpl<>(ttDeferredExecutor);
        this.next = promise;
        return promise;
    }

    @Override
    public void reject(Throwable throwable) {
        reject(this, throwable);
    }

    @Override
    public void resolve() {
        resolve(this, null);
    }

    @Override
    public void resolve(T value) {
        resolve(this, value);
    }

    @Override
    public Promise<T> promise() {
        return this;
    }
}
