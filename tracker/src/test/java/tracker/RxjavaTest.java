package tracker;

import io.reactivex.Observable;

/**
 * Created by sohan on 2017-07-28.
 */
public interface RxjavaTest {
    static void main(String[] as) {
        Observable
            .<String>defer(() -> {
                return Observable.just("ok");
            })
            .flatMap(s -> Observable.defer(() -> Observable.just("ok22")))
            .flatMap(s -> Observable.error(new RuntimeException("Exception")))
            .doOnError(throwable -> System.out.println("Throwable == " + throwable))
            .toList()
            .subscribe(o -> {
            }, throwable -> System.out.println("throwable = " + throwable));
    }
}
