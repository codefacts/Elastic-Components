package elasta.core.promise.intfs;

/**
 * Created by Jango on 8/25/2016.
 */
public interface Signal<T> {

    boolean isFiltered();

    boolean isSuccess();

    boolean isError();

    T val();

    T orElse(T t);

    Throwable err();

    enum Type {
        SUCCESS, ERROR, FILTERED
    }
}
