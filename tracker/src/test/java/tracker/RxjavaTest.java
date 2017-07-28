package tracker;

import io.reactivex.Emitter;
import io.reactivex.Observable;
import io.reactivex.functions.BiConsumer;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * Created by sohan on 2017-07-28.
 */
public interface RxjavaTest {
    static void main(String[] as) throws Exception {
        Observable
            .create(e -> {
                System.out.println("Setting the timer");
                Observable.timer(20, TimeUnit.SECONDS)
                    .subscribe(aLong -> {
                        System.out.println("emitting values");
                        e.onNext("1");
                        e.onNext("2");
                        e.onNext("3");
                        e.onComplete();
                    });
            })
            .doOnError(throwable -> System.out.println("Throwable == " + throwable))
            .toList()
            .subscribe(o -> {
                System.out.println("value received: " + o);
            }, throwable -> System.out.println("throwable = " + throwable));

        Thread.sleep(500000L);
    }
}
