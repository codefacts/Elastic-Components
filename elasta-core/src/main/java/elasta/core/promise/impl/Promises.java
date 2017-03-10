package elasta.core.promise.impl;

import com.google.common.collect.ImmutableList;
import elasta.core.intfs.CallableUnckd;
import elasta.core.intfs.Consumer1Unckd;
import elasta.core.intfs.RunnableUnckd;
import elasta.core.promise.intfs.Defer;
import elasta.core.promise.intfs.MapHandler;
import elasta.core.promise.intfs.Promise;
import elasta.core.touple.*;

import java.util.*;

/**
 * Created by someone on 16/10/2015.
 */
final public class Promises {

    public static <R> Promise<R> empty() {
        return of(null);
    }

    public static <T> Promise<T> of(final T val) {
        final PromiseImpl<T> promise = new PromiseImpl<>(SignalImpl.success(val));
        return promise;
    }

    public static Promise<Void> runnable(final RunnableUnckd runnableUnckd) {
        Objects.requireNonNull(runnableUnckd, "Argument to Promises.runnable can't be null.");
        Defer<Void> defer = defer();
        try {
            runnableUnckd.run();
            defer.resolve();
        } catch (Throwable ex) {
            defer.reject(ex);
        }
        return defer.promise();
    }

    public static <T> Promise<T> callable(final CallableUnckd<T> callableUnckd) {
        Objects.requireNonNull(callableUnckd, "Argument to Promises.callable can't be null.");
        Defer<T> defer = defer();
        try {
            T retVal = callableUnckd.call();
            defer.resolve(retVal);
        } catch (Throwable ex) {
            defer.reject(ex);
        }
        return defer.promise();
    }

    public static <T> Promise<T> callableP(final CallableUnckd<Promise<T>> callableUnckd) {
        try {
            return callableUnckd.call();
        } catch (Throwable throwable) {
            return Promises.error(throwable);
        }
    }

    public static <T> Promise<T> exec(Consumer1Unckd<Defer<T>> consumer1Unckd) {
        Defer<T> defer = Promises.defer();
        try {
            consumer1Unckd.accept(defer);
        } catch (Throwable throwable) {
            defer.reject(throwable);
        }
        return defer.promise();
    }

    public static <T> Promise<T> error(Throwable error) {
        return new PromiseImpl<>(SignalImpl.error(error));
    }

    public static <T> Defer<T> defer() {
        return new PromiseImpl<>();
    }

    public static <T> Promise<List<T>> when(final Collection<Promise<T>> promises) {
        if (promises.size() == 0) {
            return Promises.of(ImmutableList.of());
        }
        Defer<List<T>> defer = defer();
        final SimpleCounter counter = new SimpleCounter(0);
        MutableTpl3<Boolean, Throwable, Object> pStatus = MutableTpls.of(true, null, null);
        promises.forEach(pm -> pm.cmp(pms -> {
            pStatus.t1 &= pms.isSuccess();

            if (pStatus.t2 == null) {
                pStatus.t2 = pms.err();
                pStatus.t3 = pms.lastValue();
            }

            counter.counter++;
            if (counter.counter == promises.size()) {
                if (pStatus.t1) {
                    Object[] vals = new Object[promises.size()];
                    int index = 0;
                    for (Promise<T> promise : promises) {
                        vals[index] = promise.val();
                        index = index + 1;
                    }
                    defer.resolve(new MyImmutableList<>(vals));
                } else {
                    defer.reject(pStatus.t2, pStatus.t3);
                }
            }
        }));
        return defer.promise();
    }

    public static <T> Promise<List<Promise<T>>> allComplete(final Collection<Promise<T>> promises) {
        final Defer<List<Promise<T>>> defer = Promises.defer();
        if (promises.size() <= 0) defer.resolve(Collections.emptyList());
        try {
            final ImmutableList.Builder<Promise<T>> builder = ImmutableList.builder();
            final SimpleCounter counter = new SimpleCounter(0);
            promises.forEach(promise -> {
                final Promise<T> complete = promise.cmp(p -> {
                    counter.counter++;
                    if (counter.counter == promises.size()) {
                        defer.resolve(builder.build());
                    }
                });
                builder.add(complete);
            });
        } catch (Throwable ex) {
            defer.reject(ex);
        }
        return defer.promise();
    }

    public static <T1, T2> Promise<MutableTpl2<T1, T2>> when(final Promise<T1> t1Promise, final Promise<T2> t2Promise) {
        final Defer<MutableTpl2<T1, T2>> defer = defer();
        SimpleCounter counter = new SimpleCounter();
        MutableTpl2<T1, T2> mutableTpl2 = new MutableTpl2<>();
        final int len = 2;
        t1Promise.cmp(t -> {
            if (!defer.promise().isComplete()) {
                if (t.isSuccess()) {
                    mutableTpl2.t1 = t.val();
                    counter.counter++;
                    if (counter.counter == len) {
                        defer.resolve(mutableTpl2);
                    }
                } else {
                    defer.reject(t.err(), t.lastValue());
                }
            }
        });

        t2Promise.cmp(t -> {
            if (!defer.promise().isComplete()) {
                if (t.isSuccess()) {
                    mutableTpl2.t2 = t.val();
                    counter.counter++;
                    if (counter.counter == len) {
                        defer.resolve(mutableTpl2);
                    }
                } else {
                    defer.reject(t.err(), t.lastValue());
                }
            }
        });
        return defer.promise();
    }

    public static <T1, T2, T3> Promise<MutableTpl3<T1, T2, T3>> when(final Promise<T1> t1Promise,
                                                                     final Promise<T2> t2Promise,
                                                                     final Promise<T3> t3Promise) {
        final Defer<MutableTpl3<T1, T2, T3>> defer = defer();
        SimpleCounter counter = new SimpleCounter();
        MutableTpl3<T1, T2, T3> mutableTpl3 = new MutableTpl3<>();
        final int len = 3;
        t1Promise.cmp(t -> {
            if (!defer.promise().isComplete()) {
                if (t.isSuccess()) {
                    mutableTpl3.setT1(t.val());
                    counter.counter++;
                    if (counter.counter == len) {
                        defer.resolve(mutableTpl3);
                    }
                } else {
                    defer.reject(t.err(), t.lastValue());
                }
            }
        });

        t2Promise.cmp(t -> {
            if (!defer.promise().isComplete()) {
                if (t.isSuccess()) {
                    mutableTpl3.setT2(t.val());
                    counter.counter++;
                    if (counter.counter == len) {
                        defer.resolve(mutableTpl3);
                    }
                } else {
                    defer.reject(t.err(), t.lastValue());
                }
            }
        });

        t3Promise.cmp(t -> {
            if (!defer.promise().isComplete()) {
                if (t.isSuccess()) {
                    mutableTpl3.setT3(t.val());
                    counter.counter++;
                    if (counter.counter == len) {
                        defer.resolve(mutableTpl3);
                    }
                } else {
                    defer.reject(t.err(), t.lastValue());
                }
            }
        });
        return defer.promise();
    }


    public static <T1, T2, T3, T4> Promise<MutableTpl4<T1, T2, T3, T4>> when(final Promise<T1> t1Promise,
                                                                             final Promise<T2> t2Promise,
                                                                             final Promise<T3> t3Promise,
                                                                             final Promise<T4> t4Promise) {
        final Defer<MutableTpl4<T1, T2, T3, T4>> defer = defer();
        SimpleCounter counter = new SimpleCounter();
        MutableTpl4<T1, T2, T3, T4> mutableTpl4 = new MutableTpl4<>();
        final int len = 4;
        t1Promise.cmp(t -> {
            if (!defer.promise().isComplete()) {
                if (t.isSuccess()) {
                    mutableTpl4.setT1(t.val());
                    counter.counter++;
                    if (counter.counter == len) {
                        defer.resolve(mutableTpl4);
                    }
                } else {
                    defer.reject(t.err(), t.lastValue());
                }
            }
        });

        t2Promise.cmp(t -> {
            if (!defer.promise().isComplete()) {
                if (t.isSuccess()) {
                    mutableTpl4.setT2(t.val());
                    counter.counter++;
                    if (counter.counter == len) {
                        defer.resolve(mutableTpl4);
                    }
                } else {
                    defer.reject(t.err(), t.lastValue());
                }
            }
        });

        t3Promise.cmp(t -> {
            if (!defer.promise().isComplete()) {
                if (t.isSuccess()) {
                    mutableTpl4.setT3(t.val());
                    counter.counter++;
                    if (counter.counter == len) {
                        defer.resolve(mutableTpl4);
                    }
                } else {
                    defer.reject(t.err(), t.lastValue());
                }
            }
        });

        t4Promise.cmp(t -> {
            if (!defer.promise().isComplete()) {
                if (t.isSuccess()) {
                    mutableTpl4.setT4(t.val());
                    counter.counter++;
                    if (counter.counter == len) {
                        defer.resolve(mutableTpl4);
                    }
                } else {
                    defer.reject(t.err(), t.lastValue());
                }
            }
        });
        return defer.promise();
    }

    public static <T1, T2, T3, T4, T5> Promise<MutableTpl5<T1, T2, T3, T4, T5>> when(final Promise<T1> t1Promise,
                                                                                     final Promise<T2> t2Promise,
                                                                                     final Promise<T3> t3Promise,
                                                                                     final Promise<T4> t4Promise,
                                                                                     final Promise<T5> t5Promise) {
        final Defer<MutableTpl5<T1, T2, T3, T4, T5>> defer = defer();
        SimpleCounter counter = new SimpleCounter();
        MutableTpl5<T1, T2, T3, T4, T5> mutableTpl5 = new MutableTpl5<>();
        final int len = 5;
        t1Promise.cmp(t -> {
            if (!defer.promise().isComplete()) {
                if (t.isSuccess()) {
                    mutableTpl5.setT1(t.val());
                    counter.counter++;
                    if (counter.counter == len) {
                        defer.resolve(mutableTpl5);
                    }
                } else {
                    defer.reject(t.err(), t.lastValue());
                }
            }
        });

        t2Promise.cmp(t -> {
            if (!defer.promise().isComplete()) {
                if (t.isSuccess()) {
                    mutableTpl5.setT2(t.val());
                    counter.counter++;
                    if (counter.counter == len) {
                        defer.resolve(mutableTpl5);
                    }
                } else {
                    defer.reject(t.err(), t.lastValue());
                }
            }
        });

        t3Promise.cmp(t -> {
            if (!defer.promise().isComplete()) {
                if (t.isSuccess()) {
                    mutableTpl5.setT3(t.val());
                    counter.counter++;
                    if (counter.counter == len) {
                        defer.resolve(mutableTpl5);
                    }
                } else {
                    defer.reject(t.err(), t.lastValue());
                }
            }
        });

        t4Promise.cmp(t -> {
            if (!defer.promise().isComplete()) {
                if (t.isSuccess()) {
                    mutableTpl5.setT4(t.val());
                    counter.counter++;
                    if (counter.counter == len) {
                        defer.resolve(mutableTpl5);
                    }
                } else {
                    defer.reject(t.err(), t.lastValue());
                }
            }
        });

        t5Promise.cmp(t -> {
            if (!defer.promise().isComplete()) {
                if (t.isSuccess()) {
                    mutableTpl5.setT5(t.val());
                    counter.counter++;
                    if (counter.counter == len) {
                        defer.resolve(mutableTpl5);
                    }
                } else {
                    defer.reject(t.err(), t.lastValue());
                }
            }
        });

        return defer.promise();
    }

    public static <T> MapHandler<T, Void> toVoid() {
        return s -> (Void) s;
    }
}
