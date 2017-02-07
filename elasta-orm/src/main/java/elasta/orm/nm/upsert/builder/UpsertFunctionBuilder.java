package elasta.orm.nm.upsert.builder;

import elasta.orm.nm.upsert.UpsertFunction;

/**
 * Created by Jango on 2017-01-21.
 */
public interface UpsertFunctionBuilder {
    UpsertFunction create(String entity);
}
