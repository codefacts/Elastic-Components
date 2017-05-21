package elasta.composer.state.handlers.impl;

import elasta.composer.Events;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.state.handlers.DeleteAllStateHandlerBuilder;
import elasta.core.flow.Flow;
import elasta.orm.Orm;
import io.vertx.core.json.JsonArray;

import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 5/21/2017.
 */
final public class DeleteAllStateHandlerBuilderImpl implements DeleteAllStateHandlerBuilder {
    final Orm orm;
    final String entity;

    public DeleteAllStateHandlerBuilderImpl(Orm orm, String entity) {
        Objects.requireNonNull(orm);
        Objects.requireNonNull(entity);
        this.orm = orm;
        this.entity = entity;
    }

    @Override
    public MsgEnterEventHandlerP<JsonArray, JsonArray> build() {

        return msg -> orm.deleteAll(entity, msg.body().getList())
            .map(objectList -> Flow.trigger(Events.next, msg));
    }
}
