package elasta.orm.event.builder.impl;

import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import elasta.orm.event.EventHandler;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 3/30/2017.
 */
final public class DelegatingEventHandlerImpl implements EventHandler {
    final List<EventHandler> eventHandlers;

    public DelegatingEventHandlerImpl(List<EventHandler> eventHandlers) {
        Objects.requireNonNull(eventHandlers);
        this.eventHandlers = eventHandlers;
    }

    @Override
    public Promise<JsonObject> handle(Params params) {
        if (eventHandlers.size() <= 0) {
            return Promises.of(params.getEntity());
        }

        Promise<JsonObject> promise = eventHandlers.get(0).handle(params);

        for (int i = 1; i < eventHandlers.size(); i++) {

            final EventHandler eventHandler = eventHandlers.get(i);

            promise = promise.mapP(
                jsonObject -> eventHandler.handle(
                    Params.builder()
                        .entity(jsonObject)
                        .operationType(params.getOperationType())
                        .path(params.getPath())
                        .root(params.getRoot())
                        .build()
                ));
        }

        return promise;
    }
}
