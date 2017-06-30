package elasta.composer.state.handlers.builder.impl;

import elasta.composer.Events;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.state.handlers.builder.FindOneStateHandlerBuilder;
import elasta.composer.state.handlers.impl.FindOneStateHandlerImpl;
import elasta.core.flow.Flow;
import elasta.orm.Orm;
import elasta.orm.query.expression.FieldExpression;
import io.vertx.core.json.JsonObject;

import java.util.Collection;
import java.util.Objects;

/**
 * Created by sohan on 5/12/2017.
 */
final public class FindOneStateHandlerBuilderImpl implements FindOneStateHandlerBuilder {
    final String alias;
    final String entity;
    final Collection<FieldExpression> selections;
    final Orm orm;

    public FindOneStateHandlerBuilderImpl(String alias, String entity, Collection<FieldExpression> selections, Orm orm) {
        Objects.requireNonNull(alias);
        Objects.requireNonNull(entity);
        Objects.requireNonNull(selections);
        Objects.requireNonNull(orm);
        this.alias = alias;
        this.entity = entity;
        this.selections = selections;
        this.orm = orm;
    }

    @Override
    public MsgEnterEventHandlerP<JsonObject, JsonObject> build() {
        return new FindOneStateHandlerImpl(
            alias,
            entity,
            selections,
            orm
        );
    }
}
