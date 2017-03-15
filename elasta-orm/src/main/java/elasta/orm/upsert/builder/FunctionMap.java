package elasta.orm.upsert.builder;

/**
 * Created by Jango on 2017-01-23.
 */
public interface FunctionMap<T> {

    FunctionMap<T> putEmpty(String entity);

    FunctionMap<T> put(String entity, T upsertFunction);

    boolean exists(String entity);

    T get(String entity);

    FunctionMap makeImmutable();
}
