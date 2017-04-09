package elasta.orm.relation.delete.impl;

import elasta.orm.delete.impl.JsonDependencyHandler;
import elasta.orm.relation.delete.DeleteChildRelationsContext;
import elasta.orm.relation.delete.DeleteChildRelationsFunction;
import elasta.orm.relation.delete.RelationFieldHandler;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 4/8/2017.
 */
final public class DeleteChildRelationsFunctionImpl implements DeleteChildRelationsFunction {
    final List<RelationFieldHandler> relationFieldHandlers;

    public DeleteChildRelationsFunctionImpl(List<RelationFieldHandler> relationFieldHandlers) {
        Objects.requireNonNull(relationFieldHandlers);
        this.relationFieldHandlers = relationFieldHandlers;
    }

    @Override
    public void deleteChildRelations(JsonObject entity, DeleteChildRelationsContext context) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(context);

        relationFieldHandlers.forEach(
            relationFieldHandler -> relationFieldHandler.handleDeleteRelation(entity, context)
        );
    }
}
