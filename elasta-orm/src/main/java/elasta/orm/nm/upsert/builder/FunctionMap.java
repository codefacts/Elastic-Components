package elasta.orm.nm.upsert.builder;

import elasta.orm.nm.upsert.UpsertFunction;

/**
 * Created by Jango on 2017-01-23.
 */
public interface FunctionMap {

    FunctionMap putEmpty(String entity);

    FunctionMap put(String entity, UpsertFunction upsertFunction);

    boolean exists(String entity);

    UpsertFunction get(String entity);

    FunctionMap makeImmutable();
}
