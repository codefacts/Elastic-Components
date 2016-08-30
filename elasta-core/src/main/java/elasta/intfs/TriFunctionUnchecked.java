package elasta.intfs;

/**
 * Created by someone on 08/11/2015.
 */
public interface TriFunctionUnchecked<T1, T2, T3, R> {
    public R apply(T1 t1, T2 t2, T3 t3) throws Throwable;
}
