package elasta.orm.event.builder.impl;

import elasta.core.promise.intfs.Promise;
import elasta.orm.event.EventDispatcher;
import elasta.orm.event.builder.BuilderContext;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by sohan on 3/28/2017.
 */
final public class EventDispatcherProxyImpl implements EventDispatcher {
    final String entity;
    final BuilderContext<EventDispatcher> context;

    public EventDispatcherProxyImpl(String entity, BuilderContext<EventDispatcher> context) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(context);
        this.entity = entity;
        this.context = context;
    }

    @Override
    public Promise<JsonObject> dispatch(EventDispatcher.Params params) {
        return context.get(entity).dispatch(params);
    }
}
