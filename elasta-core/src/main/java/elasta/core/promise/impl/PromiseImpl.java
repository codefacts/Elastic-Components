package elasta.core.promise.impl;

import elasta.core.promise.ex.PromiseException;
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

        return executeOrSchedule(
            createPromise(Executors.filterExecutor(filterHandler))
        );
    }

    @Override
    public Promise<T> filterP(FilterPHandler<T> filterPHandler) {

        return executeOrSchedule(
            createPromise(Executors.deferredFilterExecutor(filterPHandler))
        );
    }

    @Override
    public <R> Promise<R> map(MapHandler<T, R> mapHandler) {

        return executeOrSchedule(
            createPromise(Executors.mapExecutor(mapHandler))
        );
    }

    @Override
    public <R> Promise<R> mapP(MapPHandler<T, R> mapPHandler) {

        return executeOrSchedule(
            createPromise(Executors.deferredMapExecutor(mapPHandler))
        );
    }

    @Override
    public Promise<T> then(ThenHandler<T> thenHandler) {

        return executeOrSchedule(
            createPromise(Executors.thenExecutor(thenHandler))
        );
    }

    @Override
    public Promise<T> thenP(ThenPHandler<T> thenPHandler) {

        return executeOrSchedule(
            createPromise(Executors.deferredThenExecutor(thenPHandler))
        );
    }

    @Override
    public Promise<T> err(ErrorHandler errorHandler) {

        return executeOrSchedule(
            createPromise(Executors.errorExecutor(errorHandler))
        );
    }

    @Override
    public Promise<T> err2(Error2Handler errorHandler) {

        return executeOrSchedule(
            createPromise(Executors.errorExecutor(errorHandler))
        );
    }

    @Override
    public Promise<T> errP(ErrorPHandler errorPHandler) {

        return executeOrSchedule(
            createPromise(Executors.deferredErrorExecutor(errorPHandler))
        );
    }

    @Override
    public Promise<T> err2P(Error2PHandler errorHandler) {

        return executeOrSchedule(
            createPromise(Executors.deferredErrorExecutor(errorHandler))
        );
    }

    @Override
    public <P> Promise<T> errd(DoOnErrorHandler<P, T> doOnErrorHandler) {

        return executeOrSchedule(
            createPromise(Executors.errorExecutor(doOnErrorHandler))
        );
    }

    @Override
    public <P> Promise<T> errdP(DoOnErrorPHandler<P, T> doOnErrorHandler) {

        return executeOrSchedule(
            createPromise(Executors.deferredErrorExecutor(doOnErrorHandler))
        );
    }

    @Override
    public Promise<T> cmp(CompleteHandler<T> completeHandler) {

        return executeOrSchedule(
            createPromise(Executors.completeExecutor(completeHandler))
        );
    }

    @Override
    public Promise<T> cmpP(CompletePHandler<T> completePHandler) {

        return executeOrSchedule(
            createPromise(Executors.deferredCompleteExecutor(completePHandler))
        );
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
    public Signal<T> signal() {
        checkSignalNonNull();
        return signal;
    }

    private void checkSignalNonNull() {
        if (signal == null) {
            throw new PromiseException("Promise is not complete yet");
        }
    }

    @Override
    public T val() {
        checkSignalNonNull();
        return signal.val();
    }

    @Override
    public T orElse(T defaultValue) {
        checkSignalNonNull();
        return (signal.val() == null) ? defaultValue : signal.val();
    }

    @Override
    public Throwable err() {
        checkSignalNonNull();
        return signal.err();
    }

    static <T> void signal(final PromiseImpl<T> promise, final SignalImpl<T> signal) {

        if (promise.signal != null) {
            throw new PromiseException("Promise is already complete");
        }

        execute(promise.next, promise.signal = signal);
        promise.next = null;
    }

    private static <T> void execute(PromiseImpl promise, SignalImpl<T> signal) {

        for (; ; ) {

            if ((promise == null) | (signal == null)) {
                break;
            }

            if (promise.executor.type() == Executor.INSTANT) {

                signal = promise.signal = (SignalImpl) promise.executor.execute(signal);

                final PromiseImpl pp = promise;
                promise = promise.next;
                pp.next = null;

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

            this.next = promise;

            return promise;
        }
    }

    private <TT, RR> PromiseImpl<RR> createPromise(InstantExecutor<TT, RR> ttInstantExecutor) {
        return new PromiseImpl<>(ttInstantExecutor);
    }

    private <TT, RR> PromiseImpl<RR> createPromise(DeferredExecutor<TT, RR> ttDeferredExecutor) {
        return new PromiseImpl<>(ttDeferredExecutor);
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
