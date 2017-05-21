package elasta.composer.state.handlers.impl;

import elasta.composer.Events;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.state.handlers.UpdateAllStateHandlerBuilder;
import elasta.core.flow.Flow;
import elasta.orm.Orm;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 5/21/2017.
 */
final public class UpdateAllStateHandlerBuilderImpl implements UpdateAllStateHandlerBuilder {
    final String entity;
    final Orm orm;

    public UpdateAllStateHandlerBuilderImpl(String entity, Orm orm) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(orm);
        this.entity = entity;
        this.orm = orm;
    }

    @Override
    public MsgEnterEventHandlerP<JsonArray, JsonArray> build() {
        return msg -> orm.upsertAll(entity, msg.body().getList())
            .map(jo -> Flow.trigger(Events.next, msg));
    }
}
