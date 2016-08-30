package elasta.intfs;

/**
 * Created by shahadat on 3/30/16.
 */
public interface TriFunction<T1, T2, T3, R> {
    R apply(T1 t1, T2 t2, T3 t3);
}
