package elasta.core.promise.intfs;

/**
 * Created by Shahadat on 8/24/2016.
 */
public interface CompletePHandler<T> extends Invokable {
    Promise<Void> apply(Signal<T> tPromise) throws Throwable;
}
