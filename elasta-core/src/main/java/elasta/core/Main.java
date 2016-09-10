package elasta.core;

import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Defer;
import elasta.core.promise.intfs.Promise;

/**
 * Created by Jango on 8/31/2016.
 */
public class Main {
    public static void main(String[] args) {

        Promises.just("ok")
            .then(val -> System.out.println("val: " + val))
            .mapP(s -> Promises.just(s + " then"))
            .completeP(
                signal ->
                    Promises
                        .just(signal.value() + " complete")
                        .then(val -> System.out.println(val))
                        .map(Promises.toVoid())
            );
    }

    private static void test6() {

        Defer<Object> defer = Promises.defer();

        {
            defer.reject(new NullPointerException());
            defer.resolve("ok");
        }

        Promise<Object> promise1 = defer.promise();
        promise1.then(val -> {
        });

        Promises.just(978)
            .filter(integer -> integer.equals(868))
            .then(val -> System.out.println("Value Got: " + val))
            .error(Throwable::printStackTrace)
            .complete(promise -> System.out.println("COmplete"))
        ;
    }

    public void test5() {
        Promises.just("val")
            .filter(o -> true)
            .map(v -> v.toUpperCase())
            .filter(o -> o == "ok")
            .error(val -> {
                System.out.println("error: " + val);

            })
            .then(p -> System.out.println("p: " + p))
            .complete(v -> System.out.println("ok: " + v))
            .error(val -> System.out.println("error: " + val))
            .map(val -> "[" + val + "]")
            .then(p -> System.out.println("p: " + p))
            .filter(o -> false)
            .complete(v -> System.out.println("ok: " + v))
            .error(val -> System.out.println("error: " + val))
            .then(p -> System.out.println("p: " + p))
            .filter(o -> true)
            .complete(v -> System.out.println("ok: " + v))
        ;
    }

    public void test4() {
        Promises.just("val")
            .filter(o -> true)
            .filter(o -> true)
            .error(val -> System.out.println("error: " + val))
            .then(p -> System.out.println("p: " + p))
            .complete(v -> System.out.println("ok: " + v))
            .error(val -> System.out.println("error: " + val))
            .then(p -> System.out.println("p: " + p))
            .filter(o -> false)
            .complete(v -> System.out.println("ok: " + v))
            .error(val -> System.out.println("error: " + val))
            .then(p -> System.out.println("p: " + p))
            .filter(o -> true)
            .complete(v -> System.out.println("ok: " + v))
        ;
    }

    public void test3() {
        Promises.error(new NullPointerException("catch it"))
            .filter(o -> false)
            .filter(o -> false)
            .error(val -> System.out.println("error: " + val))
            .then(p -> System.out.println("p: " + p))
            .complete(v -> System.out.println("ok: " + v))
            .error(val -> System.out.println("error: " + val))
            .then(p -> System.out.println("p: " + p))
            .filter(o -> false)
            .complete(v -> System.out.println("ok: " + v))
            .error(val -> System.out.println("error: " + val))
            .then(p -> System.out.println("p: " + p))
            .filter(o -> true)
            .complete(v -> System.out.println("ok: " + v))
        ;
    }

    public void test2() {
        Promises.error(new NullPointerException("catch it"))
            .error(val -> System.out.println("error: " + val))
            .then(p -> System.out.println("p: " + p))
            .complete(v -> System.out.println("ok: " + v))
            .error(val -> System.out.println("error: " + val))
            .then(p -> System.out.println("p: " + p))
            .complete(v -> System.out.println("ok: " + v))
            .error(val -> System.out.println("error: " + val))
            .then(p -> System.out.println("p: " + p))
            .complete(v -> System.out.println("ok: " + v))
        ;
    }

    public void test1() {
        Promises.empty()
            .error(val -> System.out.println("error: " + val))
            .then(p -> System.out.println("p: " + p))
            .complete(v -> System.out.println("ok: " + v))
            .error(val -> System.out.println("error: " + val))
            .then(p -> System.out.println("p: " + p))
            .complete(v -> System.out.println("ok: " + v))
            .error(val -> System.out.println("error: " + val))
            .then(p -> System.out.println("p: " + p))
            .complete(v -> System.out.println("ok: " + v))
        ;

        Promises.just("value")
            .error(val -> System.out.println("error: " + val))
            .then(p -> System.out.println("p: " + p))
            .complete(v -> System.out.println("ok: " + v))
            .error(val -> System.out.println("error: " + val))
            .then(p -> System.out.println("p: " + p))
            .complete(v -> System.out.println("ok: " + v))
            .error(val -> System.out.println("error: " + val))
            .then(p -> System.out.println("p: " + p))
            .complete(v -> System.out.println("ok: " + v))
        ;
    }
}
