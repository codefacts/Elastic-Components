package elasta.core.promise.intfs;

/**
 * Created by someone on 15/10/2015.
 */
public interface Defer<T> {

    void reject(Throwable throwable);

    <P> void reject(Throwable throwable, P lastValue);

    void resolve();

    void resolve(T value);

    void filter();

    void signal(Signal<T> signal);

    Promise<T> promise();

    boolean isComplete();
}
