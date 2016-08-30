package elasta.core.promise.intfs;

/**
 * Created by someone on 15/10/2015.
 */
public interface Defer<T> {

    void reject(Throwable throwable);

    void resolve();

    void resolve(T value);

    Promise<T> promise();
}
