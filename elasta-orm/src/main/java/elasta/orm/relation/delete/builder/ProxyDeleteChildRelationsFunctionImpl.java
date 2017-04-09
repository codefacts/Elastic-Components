package elasta.orm.relation.delete.builder;

import elasta.orm.event.builder.BuilderContext;
import elasta.orm.relation.delete.DeleteChildRelationsContext;
import elasta.orm.relation.delete.DeleteChildRelationsFunction;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by sohan on 4/8/2017.
 */
final public class ProxyDeleteChildRelationsFunctionImpl implements DeleteChildRelationsFunction {
    final String entity;
    final BuilderContext<DeleteChildRelationsFunction> context;

    public ProxyDeleteChildRelationsFunctionImpl(String entity, BuilderContext<DeleteChildRelationsFunction> context) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(context);
        this.entity = entity;
        this.context = context;
    }

    @Override
    public void deleteChildRelations(JsonObject entity, DeleteChildRelationsContext context) {
        this.context.get(this.entity).deleteChildRelations(entity, context);
    }
}
