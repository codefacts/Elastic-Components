package elasta.core.promise.intfs;

/**
 * Created by someone on 16/10/2015.
 */
public interface CompleteHandler<T> extends Invokable {
    void accept(Signal<T> promise) throws Throwable;
}
