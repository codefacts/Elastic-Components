package elasta.core.promise.intfs;

/**
 * Created by someone on 16/10/2015.
 */
public interface MapPHandler<T, R> extends Invokable {
    Promise<R> apply(T t) throws Throwable;
}
