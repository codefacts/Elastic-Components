package elasta.composer.utils;

import elasta.commons.Utils;
import elasta.core.intfs.CallableUnckd;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.subjects.PublishSubject;

import java.util.Objects;

/**
 * Created by sohan on 2017-09-02.
 */
@FunctionalInterface
public interface ReactiveLoop<T> {
    Observable<T> start();

    static <T> ReactiveLoop<T> create(CreateParams<T> params) {
        return () -> {
            try {
                final PublishSubject<T> subject = PublishSubject.create();

                params.initialState.call()
                    .doOnEvent((t, throwable) -> handle(t, throwable, subject))
                    .subscribe();

                return subject
                    .doOnNext(t -> {

                        if (Utils.not(params.hasNext.test(t))) {
                            subject.onComplete();
                            return;
                        }

                        params.nextState.apply(t)
                            .doOnEvent((newState, throwable) -> handle(newState, throwable, subject))
                            .subscribe();

                    });
            } catch (Throwable ex) {
                return Observable.error(ex);
            }
        };
    }

    static <T> void handle(T state, Throwable throwable, PublishSubject<T> subject) {
        if (throwable != null) {
            subject.onError(throwable);
        } else {
            subject.onNext(state);
        }
    }


    final class CreateParams<T> {
        private final CallableUnckd<Single<T>> initialState;
        private final Predicate<T> hasNext;
        private final Function<T, Single<T>> nextState;

        public CreateParams(CallableUnckd<Single<T>> initialState, Predicate<T> hasNext, Function<T, Single<T>> nextState) {
            Objects.requireNonNull(initialState);
            Objects.requireNonNull(hasNext);
            Objects.requireNonNull(nextState);
            this.initialState = initialState;
            this.hasNext = hasNext;
            this.nextState = nextState;
        }
    }
}
