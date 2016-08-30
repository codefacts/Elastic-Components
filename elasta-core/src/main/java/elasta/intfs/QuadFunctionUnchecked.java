package elasta.intfs;

/**
 * Created by someone on 08/11/2015.
 */
public interface QuadFunctionUnchecked<T1, T2, T3, T4, R> {
    public R apply(T1 t1, T2 t2, T3 t3, T4 t4) throws Throwable;
}
