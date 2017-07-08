package elasta.composer.state.handlers.impl;

import elasta.composer.Events;
import elasta.composer.Msg;
import elasta.composer.state.handlers.BeforeUpdateStateHandler;
import elasta.core.flow.Flow;
import elasta.core.flow.StateTrigger;
import elasta.core.intfs.Fun2Async;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import io.vertx.core.json.JsonObject;

import java.util.Objects;

/**
 * Created by sohan on 7/7/2017.
 */
final public class BeforeUpdateStateHandlerImpl implements BeforeUpdateStateHandler {
    final Fun2Async<Msg<JsonObject>, JsonObject, JsonObject> fun2Async;

    public BeforeUpdateStateHandlerImpl() {
        this((msg, jsonObject) -> Promises.of(jsonObject));
    }

    public BeforeUpdateStateHandlerImpl(Fun2Async<Msg<JsonObject>, JsonObject, JsonObject> fun2Async) {
        Objects.requireNonNull(fun2Async);
        this.fun2Async = fun2Async;
    }

    @Override
    public Promise<StateTrigger<Msg<JsonObject>>> handle(Msg<JsonObject> msg) throws Throwable {
        return fun2Async.apply(msg, msg.body()).map(jsonObject -> Flow.trigger(Events.next, msg.withBody(
            jsonObject
        )));
    }
}
