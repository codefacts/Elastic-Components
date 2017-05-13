package elasta.composer.state.handlers.impl;

import elasta.composer.Events;
import elasta.composer.state.handlers.FindOneStateHandlerBuilder;
import elasta.core.flow.EnterEventHandlerP;
import elasta.core.flow.Flow;
import elasta.core.promise.impl.Promises;
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
    public EnterEventHandlerP<JsonObject, JsonObject> build() {
        return criteria -> orm.findOne(entity, alias, criteria, selections)
            .map(jsonObject -> Flow.trigger(Events.next, jsonObject));
    }
}
