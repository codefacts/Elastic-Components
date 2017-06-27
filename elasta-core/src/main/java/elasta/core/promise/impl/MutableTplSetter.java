package elasta.core.promise.impl;

/**
 * Created by sohan on 6/27/2017.
 */
@FunctionalInterface
public interface MutableTplSetter<T> {

    Object setAndGet(T val);
}
