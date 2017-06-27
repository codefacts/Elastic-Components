package elasta.core.promise.impl;

import com.google.common.collect.ImmutableList;
import elasta.core.intfs.CallableUnckd;
import elasta.core.intfs.Consumer1Unckd;
import elasta.core.intfs.PredicateUnckd;
import elasta.core.intfs.RunnableUnckd;
import elasta.core.promise.intfs.Defer;
import elasta.core.promise.intfs.MapHandler;
import elasta.core.promise.intfs.Promise;
import elasta.core.promise.intfs.Signal;
import elasta.core.touple.*;

import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

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

    public static <T> Promise<T> filter(T value, final PredicateUnckd<T> predicateUnckd) {
        Objects.requireNonNull(predicateUnckd, "Argument to Promises.callable can't be null.");
        Defer<T> defer = defer();
        try {
            boolean pass = predicateUnckd.test(value);
            if (pass) {
                defer.resolve(value);
            } else {
                defer.filter();
            }
        } catch (Throwable ex) {
            defer.reject(ex, value);
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

    public static <T> Promise<T> error(Throwable error, Object value) {
        return new PromiseImpl<>(SignalImpl.error(error, value));
    }

    public static <T> Defer<T> defer() {
        return new PromiseImpl<>();
    }

    public static <T> Promise<List<T>> when(final Collection<Promise<T>> promises) {
        try {

            final ReentrantLock lock = new ReentrantLock();

            if (promises.size() == 0) {
                return Promises.of(ImmutableList.of());
            }

            final Defer<List<T>> defer = defer();

            final SimpleCounter counter = new SimpleCounter(0);

            final MutableTpl3<Boolean, Throwable, Object> pStatus = MutableTpls.of(true, null, null);

            promises.forEach(pm -> pm.cmp(pms -> {

                try {

                    lock.lock();

                    pStatus.t1 &= pms.isSuccess();

                    if (pStatus.t2 == null) {
                        pStatus.t2 = pms.err();
                        pStatus.t3 = pms.lastValue();
                    }

//                System.out.println(Thread.currentThread().toString());

                    counter.counter++;
                    if (counter.counter >= promises.size()) {
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

                } catch (Exception ex) {

                    pStatus.t1 = false;
                    pStatus.t2 = ex;
                    pStatus.t3 = pms.lastValue();

                } finally {
                    lock.unlock();
                }

            }));

            return defer.promise();

        } catch (Exception ex) {
            return Promises.error(ex);
        }
    }

    public static <T> Promise<List<Promise<T>>> allComplete(final Collection<Promise<T>> promises) {

        try {

            final ReentrantLock lock = new ReentrantLock();

            if (promises.size() <= 0) {
                return Promises.of(Collections.emptyList());
            }

            final Defer<List<Promise<T>>> defer = Promises.defer();

            final ImmutableList.Builder<Promise<T>> builder = ImmutableList.builder();

            final SimpleCounter counter = new SimpleCounter(0);

            promises.forEach(promise -> {

                final Promise<T> complete = promise.cmp(p -> {

                    try {

                        lock.lock();

                        if (defer.isComplete()) {
                            return;
                        }

                        counter.counter++;
                        if (counter.counter >= promises.size()) {
                            defer.resolve(builder.build());
                        }

                    } catch (Exception ex) {

                        defer.reject(ex);

                    } finally {

                        lock.unlock();
                    }

                });

                builder.add(complete);
            });

            return defer.promise();

        } catch (Throwable ex) {
            return error(ex);
        }
    }

    public static <T1, T2> Promise<MutableTpl2<T1, T2>> when(final Promise<T1> t1Promise, final Promise<T2> t2Promise) {

        try {

            final ReentrantLock lock = new ReentrantLock();

            final Defer<MutableTpl2<T1, T2>> defer = defer();

            final SimpleCounter counter = new SimpleCounter();

            final MutableTpl2<T1, T2> mutableTpl2 = new MutableTpl2<>();

            final int len = 2;

            Promises.<T1>onCompletePromise(t1Promise, lock, defer, counter, val -> {
                mutableTpl2.t1 = val;
                return mutableTpl2;
            }, len);

            Promises.<T2>onCompletePromise(t2Promise, lock, defer, counter, val -> {
                mutableTpl2.t2 = val;
                return mutableTpl2;
            }, len);

            return defer.promise();

        } catch (Exception ex) {
            return Promises.error(ex);
        }
    }

    public static <T1, T2, T3> Promise<MutableTpl3<T1, T2, T3>> when(final Promise<T1> t1Promise,
                                                                     final Promise<T2> t2Promise,
                                                                     final Promise<T3> t3Promise) {

        try {

            final Defer<MutableTpl3<T1, T2, T3>> defer = defer();
            SimpleCounter counter = new SimpleCounter();
            MutableTpl3<T1, T2, T3> mutableTpl3 = new MutableTpl3<>();
            final int len = 3;

            final ReentrantLock lock = new ReentrantLock();

            Promises.<T1>onCompletePromise(t1Promise, lock, defer, counter, val -> {
                mutableTpl3.t1 = val;
                return mutableTpl3;
            }, len);

            Promises.<T2>onCompletePromise(t2Promise, lock, defer, counter, val -> {
                mutableTpl3.t2 = val;
                return mutableTpl3;
            }, len);

            Promises.<T3>onCompletePromise(t3Promise, lock, defer, counter, val -> {
                mutableTpl3.t3 = val;
                return mutableTpl3;
            }, len);

            return defer.promise();

        } catch (Exception ex) {

            return Promises.error(ex);
        }
    }


    public static <T1, T2, T3, T4> Promise<MutableTpl4<T1, T2, T3, T4>> when(final Promise<T1> t1Promise,
                                                                             final Promise<T2> t2Promise,
                                                                             final Promise<T3> t3Promise,
                                                                             final Promise<T4> t4Promise) {
        try {

            final Defer<MutableTpl4<T1, T2, T3, T4>> defer = defer();
            SimpleCounter counter = new SimpleCounter();
            MutableTpl4<T1, T2, T3, T4> mutableTpl4 = new MutableTpl4<>();
            final int len = 4;

            final ReentrantLock lock = new ReentrantLock();

            Promises.<T1>onCompletePromise(t1Promise, lock, defer, counter, val -> {
                mutableTpl4.t1 = val;
                return mutableTpl4;
            }, len);

            Promises.<T2>onCompletePromise(t2Promise, lock, defer, counter, val -> {
                mutableTpl4.t2 = val;
                return mutableTpl4;
            }, len);

            Promises.<T3>onCompletePromise(t3Promise, lock, defer, counter, val -> {
                mutableTpl4.t3 = val;
                return mutableTpl4;
            }, len);

            Promises.<T4>onCompletePromise(t4Promise, lock, defer, counter, val -> {
                mutableTpl4.t4 = val;
                return mutableTpl4;
            }, len);

            return defer.promise();

        } catch (Exception ex) {
            return Promises.error(ex);
        }
    }

    public static <T1, T2, T3, T4, T5> Promise<MutableTpl5<T1, T2, T3, T4, T5>> when(final Promise<T1> t1Promise,
                                                                                     final Promise<T2> t2Promise,
                                                                                     final Promise<T3> t3Promise,
                                                                                     final Promise<T4> t4Promise,
                                                                                     final Promise<T5> t5Promise) {
        try {

            final Defer<MutableTpl5<T1, T2, T3, T4, T5>> defer = defer();
            SimpleCounter counter = new SimpleCounter();
            MutableTpl5<T1, T2, T3, T4, T5> mutableTpl5 = new MutableTpl5<>();
            final int len = 5;

            final ReentrantLock lock = new ReentrantLock();

            Promises.<T1>onCompletePromise(t1Promise, lock, defer, counter, val -> {
                mutableTpl5.t1 = val;
                return mutableTpl5;
            }, len);

            Promises.<T2>onCompletePromise(t2Promise, lock, defer, counter, val -> {
                mutableTpl5.t2 = val;
                return mutableTpl5;
            }, len);

            Promises.<T3>onCompletePromise(t3Promise, lock, defer, counter, val -> {
                mutableTpl5.t3 = val;
                return mutableTpl5;
            }, len);

            Promises.<T4>onCompletePromise(t4Promise, lock, defer, counter, val -> {
                mutableTpl5.t4 = val;
                return mutableTpl5;
            }, len);

            Promises.<T5>onCompletePromise(t5Promise, lock, defer, counter, val -> {
                mutableTpl5.t5 = val;
                return mutableTpl5;
            }, len);

            return defer.promise();

        } catch (Exception ex) {
            return Promises.error(ex);
        }
    }

    public static <T1, T2, T3, T4, T5, T6> Promise<MutableTpl6<T1, T2, T3, T4, T5, T6>> when(final Promise<T1> t1Promise,
                                                                                             final Promise<T2> t2Promise,
                                                                                             final Promise<T3> t3Promise,
                                                                                             final Promise<T4> t4Promise,
                                                                                             final Promise<T5> t5Promise,
                                                                                             final Promise<T6> t6Promise) {
        try {

            final Defer<MutableTpl6<T1, T2, T3, T4, T5, T6>> defer = defer();
            SimpleCounter counter = new SimpleCounter();
            MutableTpl6<T1, T2, T3, T4, T5, T6> mutableTpl6 = new MutableTpl6<>();
            final int len = 5;

            final ReentrantLock lock = new ReentrantLock();

            Promises.<T1>onCompletePromise(t1Promise, lock, defer, counter, val -> {
                mutableTpl6.t1 = val;
                return mutableTpl6;
            }, len);

            Promises.<T2>onCompletePromise(t2Promise, lock, defer, counter, val -> {
                mutableTpl6.t2 = val;
                return mutableTpl6;
            }, len);

            Promises.<T3>onCompletePromise(t3Promise, lock, defer, counter, val -> {
                mutableTpl6.t3 = val;
                return mutableTpl6;
            }, len);

            Promises.<T4>onCompletePromise(t4Promise, lock, defer, counter, val -> {
                mutableTpl6.t4 = val;
                return mutableTpl6;
            }, len);

            Promises.<T5>onCompletePromise(t5Promise, lock, defer, counter, val -> {
                mutableTpl6.t5 = val;
                return mutableTpl6;
            }, len);

            Promises.<T6>onCompletePromise(t6Promise, lock, defer, counter, val -> {
                mutableTpl6.t6 = val;
                return mutableTpl6;
            }, len);

            return defer.promise();

        } catch (Exception ex) {
            return Promises.error(ex);
        }
    }

    public static <T1, T2, T3, T4, T5, T6, T7> Promise<MutableTpl7<T1, T2, T3, T4, T5, T6, T7>> when(final Promise<T1> t1Promise,
                                                                                                     final Promise<T2> t2Promise,
                                                                                                     final Promise<T3> t3Promise,
                                                                                                     final Promise<T4> t4Promise,
                                                                                                     final Promise<T5> t5Promise,
                                                                                                     final Promise<T6> t6Promise,
                                                                                                     final Promise<T7> t7Promise
    ) {
        try {

            final Defer<MutableTpl7<T1, T2, T3, T4, T5, T6, T7>> defer = defer();
            SimpleCounter counter = new SimpleCounter();
            MutableTpl7<T1, T2, T3, T4, T5, T6, T7> mutableTpl7 = new MutableTpl7<>();
            final int len = 5;

            final ReentrantLock lock = new ReentrantLock();

            Promises.<T1>onCompletePromise(t1Promise, lock, defer, counter, val -> {
                mutableTpl7.t1 = val;
                return mutableTpl7;
            }, len);

            Promises.<T2>onCompletePromise(t2Promise, lock, defer, counter, val -> {
                mutableTpl7.t2 = val;
                return mutableTpl7;
            }, len);

            Promises.<T3>onCompletePromise(t3Promise, lock, defer, counter, val -> {
                mutableTpl7.t3 = val;
                return mutableTpl7;
            }, len);

            Promises.<T4>onCompletePromise(t4Promise, lock, defer, counter, val -> {
                mutableTpl7.t4 = val;
                return mutableTpl7;
            }, len);

            Promises.<T5>onCompletePromise(t5Promise, lock, defer, counter, val -> {
                mutableTpl7.t5 = val;
                return mutableTpl7;
            }, len);

            Promises.<T6>onCompletePromise(t6Promise, lock, defer, counter, val -> {
                mutableTpl7.t6 = val;
                return mutableTpl7;
            }, len);

            Promises.<T7>onCompletePromise(t7Promise, lock, defer, counter, val -> {
                mutableTpl7.t7 = val;
                return mutableTpl7;
            }, len);

            return defer.promise();

        } catch (Exception ex) {
            return Promises.error(ex);
        }
    }

    public static <T1, T2, T3, T4, T5, T6, T7, T8> Promise<MutableTpl8<T1, T2, T3, T4, T5, T6, T7, T8>> when(final Promise<T1> t1Promise,
                                                                                                             final Promise<T2> t2Promise,
                                                                                                             final Promise<T3> t3Promise,
                                                                                                             final Promise<T4> t4Promise,
                                                                                                             final Promise<T5> t5Promise,
                                                                                                             final Promise<T6> t6Promise,
                                                                                                             final Promise<T7> t7Promise,
                                                                                                             final Promise<T8> t8Promise
    ) {
        try {

            final Defer<MutableTpl8<T1, T2, T3, T4, T5, T6, T7, T8>> defer = defer();
            SimpleCounter counter = new SimpleCounter();
            MutableTpl8<T1, T2, T3, T4, T5, T6, T7, T8> mutableTpl8 = new MutableTpl8<>();
            final int len = 5;

            final ReentrantLock lock = new ReentrantLock();

            Promises.<T1>onCompletePromise(t1Promise, lock, defer, counter, val -> {
                mutableTpl8.t1 = val;
                return mutableTpl8;
            }, len);

            Promises.<T2>onCompletePromise(t2Promise, lock, defer, counter, val -> {
                mutableTpl8.t2 = val;
                return mutableTpl8;
            }, len);

            Promises.<T3>onCompletePromise(t3Promise, lock, defer, counter, val -> {
                mutableTpl8.t3 = val;
                return mutableTpl8;
            }, len);

            Promises.<T4>onCompletePromise(t4Promise, lock, defer, counter, val -> {
                mutableTpl8.t4 = val;
                return mutableTpl8;
            }, len);

            Promises.<T5>onCompletePromise(t5Promise, lock, defer, counter, val -> {
                mutableTpl8.t5 = val;
                return mutableTpl8;
            }, len);

            Promises.<T6>onCompletePromise(t6Promise, lock, defer, counter, val -> {
                mutableTpl8.t6 = val;
                return mutableTpl8;
            }, len);

            Promises.<T7>onCompletePromise(t7Promise, lock, defer, counter, val -> {
                mutableTpl8.t7 = val;
                return mutableTpl8;
            }, len);

            Promises.<T8>onCompletePromise(t8Promise, lock, defer, counter, val -> {
                mutableTpl8.t8 = val;
                return mutableTpl8;
            }, len);

            return defer.promise();

        } catch (Exception ex) {
            return Promises.error(ex);
        }
    }

    public static <T> MapHandler<T, Void> toVoid() {
        return s -> (Void) s;
    }

    private static <T1> void onCompletePromise(Promise<T1> t1Promise, ReentrantLock lock, Defer defer, SimpleCounter counter, MutableTplSetter<T1> mutableTplSetter, int len) {

        t1Promise.cmp(t -> {
            try {

                lock.lock();

                if (!defer.isComplete()) {
                    if (t.isSuccess()) {
                        Object mutableTpl2 = mutableTplSetter.setAndGet(t.val());
                        counter.counter++;
                        if (counter.counter == len) {
                            defer.resolve(mutableTpl2);
                        }
                    } else {
                        defer.reject(t.err(), t.lastValue());
                    }
                }

            } catch (Exception ex) {

                if (!defer.isComplete()) {

                    Signal<T1> signal = t1Promise.signal();

                    defer.reject(ex, signal.isSuccess() ? signal.val() : signal.lastValue());
                }

            } finally {
                lock.unlock();
            }
        });
    }

}
