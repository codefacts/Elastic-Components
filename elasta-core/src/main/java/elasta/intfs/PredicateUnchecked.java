package elasta.intfs;

/**
 * Created by someone on 08/11/2015.
 */
public interface PredicateUnchecked<T> {
    public boolean test(T t) throws Throwable;
}
