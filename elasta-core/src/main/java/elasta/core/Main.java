package elasta.core;

import elasta.core.promise.impl.Promises;

/**
 * Created by Jango on 8/31/2016.
 */
public class Main {
    public static void main(String[] args) {
        new Main().test5();
    }

    public void test5() {
        Promises.just("val")
                .filter(o -> true)
                .map(v -> v.toUpperCase())
                .filter(o -> true)
                .error(val -> System.out.println("error: " + val))
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
