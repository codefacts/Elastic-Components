package elasta.orm.upsert;

import elasta.core.promise.intfs.Promise;

import java.util.function.Function;

/**
 * Created by sohan on 5/14/2017.
 */
public interface IdGenerator<T> {
    Promise<T> nextId(String entity);
}
