package elasta.composer.state.handlers.impl;

import elasta.composer.Events;
import elasta.composer.Msg;
import elasta.composer.state.handlers.FindOneStateHandler;
import elasta.core.flow.Flow;
import elasta.core.flow.StateTrigger;
import elasta.core.promise.intfs.Promise;
import elasta.orm.Orm;
import elasta.orm.query.expression.FieldExpression;
import io.vertx.core.json.JsonObject;

import java.util.Collection;
import java.util.Objects;

/**
 * Created by sohan on 6/30/2017.
 */
final public class FindOneStateHandlerImpl implements FindOneStateHandler<JsonObject, JsonObject> {
    final String alias;
    final String entity;
    final Collection<FieldExpression> selections;
    final Orm orm;

    public FindOneStateHandlerImpl(String alias, String entity, Collection<FieldExpression> selections, Orm orm) {
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
    public Promise<StateTrigger<Msg<JsonObject>>> handle(Msg<JsonObject> msg) throws Throwable {
        return orm.findOne(entity, alias, msg.body(), selections)
            .map(jsonObject -> Flow.trigger(Events.next, msg.withBody(jsonObject)));
    }
}
