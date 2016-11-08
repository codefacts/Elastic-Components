package elasta.core.promise.intfs;

/**
 * Created by Jango on 11/9/2016.
 */
public interface Error2PHandler<T> extends Invokable {
    Promise<Void> apply(Throwable e, T t) throws Throwable;
}
