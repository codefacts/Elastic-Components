package elasta.composer.state.handlers.impl;

import elasta.composer.Events;
import elasta.composer.MsgEnterEventHandlerP;
import elasta.composer.state.handlers.DeleteStateHandlerBuilder;
import elasta.core.flow.EnterEventHandlerP;
import elasta.core.flow.Flow;
import elasta.core.promise.impl.Promises;
import elasta.orm.Orm;

import java.util.Objects;

/**
 * Created by sohan on 5/12/2017.
 */
final public class DeleteStateHandlerBuilderImpl implements DeleteStateHandlerBuilder {
    final Orm orm;
    final String entity;

    public DeleteStateHandlerBuilderImpl(Orm orm, String entity) {
        Objects.requireNonNull(orm);
        Objects.requireNonNull(entity);
        this.orm = orm;
        this.entity = entity;
    }

    @Override
    public MsgEnterEventHandlerP<Object, Object> build() {

        return msg -> orm.delete(entity, msg.body())
            .map(deletedId -> Flow.trigger(Events.next, msg.withBody(deletedId)));
    }
}
