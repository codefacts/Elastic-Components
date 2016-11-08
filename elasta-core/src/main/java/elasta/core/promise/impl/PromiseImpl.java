package elasta.core.promise.impl;

import elasta.core.promise.intfs.*;

import java.util.Objects;

/**
 * Created by Shahadat on 8/24/2016.
 */
final public class PromiseImpl<T> implements Promise<T>, Defer<T> {
    private final Executor executor;
    private SignalImpl<T> signal = null;
    private PromiseImpl next = null;

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
    public Promise<T> err(ErrorHandler errorHandler) {

        PromiseImpl<T> promise = createPromise(Executors.errorExecutor(errorHandler));

        return executeOrSchedule(promise);
    }

    @Override
    public Promise<T> err2(Error2Handler errorHandler) {

        PromiseImpl<T> promise = createPromise(Executors.errorExecutor(errorHandler));

        return executeOrSchedule(promise);
    }

    @Override
    public Promise<T> errP(ErrorPHandler errorPHandler) {

        PromiseImpl<T> promise = createPromise(Executors.deferredErrorExecutor(errorPHandler));

        return executeOrSchedule(promise);
    }

    @Override
    public Promise<T> err2P(Error2PHandler errorHandler) {

        PromiseImpl<T> promise = createPromise(Executors.deferredErrorExecutor(errorHandler));

        return executeOrSchedule(promise);
    }

    @Override
    public <P> Promise<T> errd(DoOnErrorHandler<P, T> doOnErrorHandler) {

        PromiseImpl<T> promise = createPromise(Executors.errorExecutor(doOnErrorHandler));

        return executeOrSchedule(promise);
    }

    @Override
    public <P> Promise<T> errdP(DoOnErrorPHandler<P, T> doOnErrorHandler) {

        PromiseImpl<T> promise = createPromise(Executors.deferredErrorExecutor(doOnErrorHandler));

        return executeOrSchedule(promise);
    }

    @Override
    public Promise<T> cmp(CompleteHandler<T> completeHandler) {

        PromiseImpl<T> promise = createPromise(Executors.completeExecutor(completeHandler));

        return executeOrSchedule(promise);
    }

    @Override
    public Promise<T> cmpP(CompletePHandler<T> completePHandler) {

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
    public T val() {
        return signal.val();
    }

    @Override
    public T orElse(T defaultValue) {
        return (signal.val() == null) ? defaultValue : signal.val();
    }

    @Override
    public Throwable err() {
        return signal.err();
    }

    static <T> void signal(final PromiseImpl<T> promise, final SignalImpl<T> signal) {

        execute(promise.next, promise.signal = signal);
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

                final PromiseImpl prms = promise;

                PromiseImpl pp = (PromiseImpl) promise.executor.execute(signal);

                pp.cmp(ss -> signal(prms, (SignalImpl<T>) ss));

                break;
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
        signal(this, SignalImpl.error(throwable));
    }

    @Override
    public <P> void reject(Throwable throwable, P lastValue) {
        signal(this, SignalImpl.error(throwable, lastValue));
    }

    @Override
    public void resolve() {
        signal(this, SignalImpl.success(null));
    }

    @Override
    public void resolve(T value) {
        signal(this, SignalImpl.success(value));
    }

    @Override
    public void filter() {
        signal(this, SignalImpl.filtered());
    }

    @Override
    public void signal(Signal<T> signal) {
        signal(this, (SignalImpl<T>) signal);
    }

    @Override
    public Promise<T> promise() {
        return this;
    }
}
