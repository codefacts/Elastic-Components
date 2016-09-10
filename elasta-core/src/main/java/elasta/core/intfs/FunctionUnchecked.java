package elasta.core.intfs;

/**
 * Created by someone on 15/10/2015.
 */
public interface FunctionUnchecked<T, R> {
    public R apply(T val) throws Throwable;
}
