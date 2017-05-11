package jpatest.core;

/**
 * Created by Jango on 10/2/2016.
 */
public class Main {
    public static final String PU = "jpatest";

    public static void main(String[] args) {
        Promise promise = empty();
        promise.err2(new Error2Handler<Object>() {
            @Override
            public void accept(Throwable throwable, Object o) throws Throwable {
                Throwable throwa = throwable;
                Object kk = o;
            }
        })
        ;
    }

    private static Promise empty() {
        return null;
    }

    private interface Promise<T> {
        Promise<T> err2(Error2Handler errorHandler);
    }

    public interface Error2Handler<T> extends Consumer2Unckd<Throwable, T> {
        @Override
        void accept(Throwable throwable, T t) throws Throwable;
    }

    public interface Consumer2Unckd<T1, T2> {
        void accept(T1 t1, T2 t2) throws Throwable;
    }
}
