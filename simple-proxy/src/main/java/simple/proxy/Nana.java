package simple.proxy;

import io.reactivex.Observable;

import java.rmi.UnexpectedException;
import java.util.concurrent.TimeUnit;

/**
 * Created by sohan on 2017-08-08.
 */
public interface Nana {
    static void main(String[] args) throws InterruptedException {
        Observable
            .create(e -> {
                try {

                    System.out.println("### onNext: value and isDisposed" + e.isDisposed());
                    e.onNext("value");

//                    Observable.timer(5, TimeUnit.SECONDS)
//                        .subscribe(aLong -> {
//                            System.out.println("## 22: " + e.isDisposed());
//                            e.onNext("22");
//                            if (!e.isDisposed()) {
//                                e.onError(new UnexpectedException("error"));
//                            }
//                        });

                } catch (Exception ex) {
                    System.out.println(ex.toString());
//                    ex.printStackTrace();
                }
            })
            .doOnNext(o -> System.out.println("#### got11: " + o))
            .flatMap(o -> Observable.timer(200, TimeUnit.MILLISECONDS).map(aLong -> o)
                .doOnNext(o1 -> System.out.println("###from timer")))
            .doOnNext(o -> System.out.println("#### got22: " + o))
            .timeout(0, TimeUnit.MILLISECONDS)
            .subscribe(o -> {
            }, throwable -> System.out.println("##### " + throwable));
        Thread.sleep(1000000);
    }
}
