package elasta.orm.delete.dependency;

import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by sohan on 3/12/2017.
 */
final public class ProxyListTablesToDeleteFunctionImpl implements ListTablesToDeleteFunction {
    final String referencingEntity;
    final ListTablesToDeleteFunctionBuilderContext context;

    public ProxyListTablesToDeleteFunctionImpl(String referencingEntity, ListTablesToDeleteFunctionBuilderContext context) {
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
