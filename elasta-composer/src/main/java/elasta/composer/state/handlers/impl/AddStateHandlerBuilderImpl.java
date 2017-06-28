package elasta.composer.state.handlers.impl;

import com.google.common.base.Preconditions;
import elasta.composer.Events;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.state.handlers.InsertStateHandlerBuilder;
import elasta.core.flow.EnterEventHandlerP;
import elasta.core.flow.Flow;
import elasta.orm.Orm;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by sohan on 5/12/2017.
 */
final public class AddStateHandlerBuilderImpl implements InsertStateHandlerBuilder {
    final String entity;
    final Orm orm;

    public AddStateHandlerBuilderImpl(String entity, Orm orm) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(orm);
        this.entity = entity;
        this.orm = orm;
    }

    @Override
    public MsgEnterEventHandlerP<JsonObject, JsonObject> build() {
        return msg -> orm.upsert(entity, msg.body())
            .map(jo -> Flow.trigger(Events.next, msg));
    }
}
