package elasta.promise.intfs;

/**
 * Created by Jango on 8/25/2016.
 */
public interface Signal<T> {

    boolean isFiltered();

    boolean isSuccess();

    boolean isError();

    T value();

    T orElse(T t);

    Throwable error();

    enum Type {
        SUCCESS, ERROR, FILTERED
    }
}
