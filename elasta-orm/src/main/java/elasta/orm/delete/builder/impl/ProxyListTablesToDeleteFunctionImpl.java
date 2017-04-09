package elasta.orm.delete.builder.impl;

import elasta.orm.delete.ListTablesToDeleteContext;
import elasta.orm.delete.ListTablesToDeleteFunction;
import elasta.orm.event.builder.BuilderContext;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by sohan on 3/12/2017.
 */
final public class ProxyListTablesToDeleteFunctionImpl implements ListTablesToDeleteFunction {
    final String referencingEntity;
    final BuilderContext<ListTablesToDeleteFunction> context;

    public ProxyListTablesToDeleteFunctionImpl(String referencingEntity, BuilderContext<ListTablesToDeleteFunction> context) {
        Objects.requireNonNull(referencingEntity);
        Objects.requireNonNull(context);
        this.referencingEntity = referencingEntity;
        this.context = context;
    }

    @Override
    public void listTables(JsonObject entity, ListTablesToDeleteContext context) {
        this.context.get(referencingEntity).listTables(entity, context);
    }
}
