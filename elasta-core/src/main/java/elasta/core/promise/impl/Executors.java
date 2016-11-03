package elasta.core.promise.impl;

import elasta.core.promise.intfs.*;

/**
 * Created by Jango on 8/25/2016.
 */
final public class Executors {

    public static final <T> InstantExecutor<T, T> thenExecutor(ThenHandler<T> thenHandler) {
        return signal -> {

            if (canExecuteThen(signal)) {

                try {

                    thenHandler.accept(signal.val());

                } catch (Throwable throwable) {
                    return SignalImpl.error(throwable);
                }
            }
            return signal;
        };
    }

    private static boolean canExecuteThen(SignalImpl signal) {
        return signal.type == Signal.Type.SUCCESS;
    }

    public static final <T> InstantExecutor<T, T> filterExecutor(FilterHandler<T> filterHandler) {
        return signal -> {

            if (canExecuteFilter(signal)) {

                try {

                    boolean status = filterHandler.test(signal.val());

                    if (status) {

                        return signal;

                    } else {

                        return SignalImpl.filtered();
                    }

                } catch (Throwable throwable) {

                    return SignalImpl.error(throwable);
                }

            } else {
                return signal;
            }
        };
    }

    private static <T> boolean canExecuteFilter(SignalImpl<T> signal) {
        return signal.type == Signal.Type.SUCCESS;
    }

    public static final <T, R> InstantExecutor<T, R> mapExecutor(MapHandler<T, R> mapHandler) {
        return signal -> {

            if (canExecuteMap(signal)) {

                try {

                    final R value = mapHandler.apply(signal.val());

                    return SignalImpl.success(value);

                } catch (Throwable throwable) {

                    return SignalImpl.error(throwable);
                }
            }
            return (SignalImpl<R>) signal;
        };
    }

    private static boolean canExecuteMap(SignalImpl signal) {
        return signal.type == Signal.Type.SUCCESS;
    }

    public static final <T> InstantExecutor<T, T> errorExecutor(ErrorHandler errorHandler) {
        return signal -> {

            if (canExecuteError(signal)) {

                final Throwable error = signal.err();

                try {

                    errorHandler.accept(error);

                } catch (Throwable throwable) {

                    error.addSuppressed(throwable);

                    return signal;
                }
            }

            return signal;
        };
    }

    public static final <T> InstantExecutor<T, T> errorExecutor(DoOnErrorHandler<T> errorHandler) {
        return signal -> {

            if (canExecuteError(signal)) {

                final Throwable error = signal.err();

                try {

                    return SignalImpl.success(errorHandler.apply(error));

                } catch (Throwable throwable) {

                    error.addSuppressed(throwable);

                    return signal;
                }
            }

            return signal;
        };
    }

    private static boolean canExecuteError(SignalImpl signal) {
        return signal.type == Signal.Type.ERROR;
    }

    public static final <T> InstantExecutor<T, T> completeExecutor(CompleteHandler<T> completeHandler) {
        return signal -> {
            try {

                completeHandler.accept(signal);

            } catch (Throwable throwable) {

                return SignalImpl.error(throwable);
            }
            return signal;
        };
    }

    public static final <T> DeferredExecutor<T, T> deferredThenExecutor(ThenPHandler<T> thenHandler) {
        return signal -> {

            if (canExecuteThen(signal)) {
                try {

                    PromiseImpl<T> promise = new PromiseImpl<>();

                    thenHandler.apply(signal.val()).cmp(ss -> {
                        if (ss.isError()) {
                            promise.reject(ss.err());
                        } else {
                            PromiseImpl.signal(promise, signal);
                        }
                    });

                    return promise;

                } catch (Throwable throwable) {

                    return new PromiseImpl(SignalImpl.error(throwable));
                }
            }
            return new PromiseImpl(signal);
        };
    }

    public static final <T> DeferredExecutor<T, T> deferredFilterExecutor(FilterPHandler<T> filterPHandler) {
        return signal -> {
            if (canExecuteFilter(signal)) {

                try {

                    PromiseImpl<T> promise = new PromiseImpl<>();

                    filterPHandler.test(signal.val()).cmp(ss -> {
                        if (ss.isError()) {
                            promise.reject(ss.err());
                        } else {

                            if (ss.val()) {
                                PromiseImpl.signal(promise, signal);
                            } else {
                                promise.filter();
                            }
                        }
                    });

                    return promise;

                } catch (Throwable throwable) {

                    return new PromiseImpl<>(SignalImpl.error(throwable));
                }

            } else {
                return new PromiseImpl(signal);
            }
        };
    }

    public static final <T, R> DeferredExecutor<T, R> deferredMapExecutor(MapPHandler<T, R> mapHandler) {
        return signal -> {

            if (canExecuteMap(signal)) {

                try {

                    return (PromiseImpl<R>) mapHandler.apply(signal.val());

                } catch (Throwable throwable) {

                    return new PromiseImpl<>(SignalImpl.error(throwable));
                }
            }

            return new PromiseImpl(signal);
        };
    }

    public static final <T> DeferredExecutor<T, T> deferredErrorExecutor(ErrorPHandler errorHandler) {
        return signal -> {

            if (canExecuteError(signal)) {

                final Throwable error = signal.err();

                try {

                    PromiseImpl<T> tPromise = new PromiseImpl<>();

                    errorHandler.apply(error).cmp(ss -> {

                        if (ss.isError()) {

                            error.addSuppressed(ss.err());
                        }

                        PromiseImpl.signal(tPromise, signal);
                    });

                    return tPromise;

                } catch (Throwable throwable) {

                    error.addSuppressed(throwable);

                    return new PromiseImpl(signal);
                }
            }

            return new PromiseImpl(signal);
        };
    }

    public static final <T> DeferredExecutor<T, T> deferredErrorExecutor(DoOnErrorPHandler<T> errorHandler) {
        return signal -> {

            if (canExecuteError(signal)) {

                final Throwable error = signal.err();

                try {

                    PromiseImpl<T> tPromise = new PromiseImpl<>();

                    errorHandler.apply(error).cmp(ss -> {

                        if (ss.isError()) {

                            error.addSuppressed(ss.err());
                            PromiseImpl.signal(tPromise, signal);
                            return;
                        }

                        PromiseImpl.signal(tPromise, (SignalImpl<T>) ss);
                    });

                    return tPromise;

                } catch (Throwable throwable) {

                    error.addSuppressed(throwable);

                    return new PromiseImpl(signal);
                }
            }

            return new PromiseImpl(signal);
        };
    }

    public static final <T> DeferredExecutor<T, T> deferredCompleteExecutor(CompletePHandler<T> completeHandler) {

        return signal -> {

            try {

                PromiseImpl<T> promise = new PromiseImpl<>();

                completeHandler.apply(signal).cmp(ss -> {
                    if (ss.isError()) {
                        promise.reject(ss.err());
                    } else {
                        PromiseImpl.signal(promise, signal);
                    }
                });

                return promise;

            } catch (Throwable throwable) {

                return new PromiseImpl<>(SignalImpl.error(throwable));
            }
        };
    }
}
