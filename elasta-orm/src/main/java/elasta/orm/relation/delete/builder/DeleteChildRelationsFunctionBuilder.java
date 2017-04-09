package elasta.orm.relation.delete.builder;

import elasta.orm.event.builder.BuilderContext;
import elasta.orm.relation.delete.DeleteChildRelationsFunction;

/**
 * Created by sohan on 4/8/2017.
 */
public interface DeleteChildRelationsFunctionBuilder {
    DeleteChildRelationsFunction build(String entity, BuilderContext<DeleteChildRelationsFunction> context);
}
