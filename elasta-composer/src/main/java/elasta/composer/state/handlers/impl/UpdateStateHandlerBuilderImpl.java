package elasta.composer.state.handlers.impl;

import elasta.composer.Events;
import elasta.composer.state.handlers.UpdateStateHandlerBuilder;
import elasta.core.flow.EnterEventHandlerP;
import elasta.core.flow.Flow;
import elasta.core.promise.impl.Promises;
import elasta.orm.Orm;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by sohan on 5/12/2017.
 */
final public class UpdateStateHandlerBuilderImpl implements UpdateStateHandlerBuilder {
    final String entity;
    final Orm orm;

    public UpdateStateHandlerBuilderImpl(String entity, Orm orm) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(orm);
        this.entity = entity;
        this.orm = orm;
    }

    @Override
    public EnterEventHandlerP<JsonObject, JsonObject> build() {
        return jsonObject -> orm.upsert(entity, jsonObject)
            .map(jo -> Flow.trigger(Events.next, jo));
    }
}
