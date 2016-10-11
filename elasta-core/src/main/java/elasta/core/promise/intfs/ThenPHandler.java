package elasta.core.promise.intfs;

/**
 * Created by Shahadat on 8/24/2016.
 */
public interface ThenPHandler<T> extends Invokable {
    Promise<Void> apply(T t) throws Throwable;
}
