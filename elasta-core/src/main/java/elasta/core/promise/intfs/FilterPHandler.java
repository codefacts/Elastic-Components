package elasta.core.promise.intfs;

/**
 * Created by Shahadat on 8/24/2016.
 */
public interface FilterPHandler<T> extends Invokable {
    Promise<Boolean> test(T t) throws Throwable;
}
