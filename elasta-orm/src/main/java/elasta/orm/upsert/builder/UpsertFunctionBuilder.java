package elasta.orm.upsert.builder;

import elasta.orm.upsert.UpsertFunction;

/**
 * Created by Jango on 2017-01-21.
 */
public interface UpsertFunctionBuilder {
    UpsertFunction build(String entity, FunctionMap<UpsertFunction> functionMap);
}
