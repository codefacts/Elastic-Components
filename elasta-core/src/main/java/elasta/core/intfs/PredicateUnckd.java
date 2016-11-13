package elasta.core.intfs;

/**
 * Created by someone on 08/11/2015.
 */
public interface PredicateUnckd<T> {
    boolean test(T t) throws Throwable;
}
