import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by sohan on 2017-08-18.
 */
public interface rxtest {
    public static void main(String[] asfd) {

        Scheduler computation = Schedulers.single();

        System.out.println("Blocking....");
        String first = Observable.zip(
            Observable.<String>fromCallable(() -> "1").map(s -> s).subscribeOn(computation),
            Observable.<String>fromCallable(() -> "2").subscribeOn(computation),
            Observable.<String>fromCallable(() -> "3").subscribeOn(computation),
            Observable.<String>fromCallable(() -> "4").subscribeOn(computation),
            (s, s2, s3, s4) -> "ok"
        ).blockingFirst();

        System.out.println("### first: " + first);
    }
}
