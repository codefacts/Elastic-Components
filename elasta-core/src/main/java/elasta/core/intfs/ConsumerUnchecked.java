package elasta.core.intfs;

/**
 * Created by someone on 13-Aug-2015.
 */
public interface ConsumerUnchecked<T> {
    void accept(T val) throws Throwable;
}
