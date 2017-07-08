package elasta.composer.state.handlers.impl;

import com.google.common.collect.ImmutableList;
import elasta.composer.Events;
import elasta.composer.Msg;
import elasta.composer.state.handlers.BeforeUpdateAllStateHandler;
import elasta.core.flow.Flow;
import elasta.core.flow.StateTrigger;
import elasta.core.intfs.Fun2Async;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Objects;

/**
 * Created by sohan on 7/7/2017.
 */
final public class BeforeUpdateAllStateHandlerImpl implements BeforeUpdateAllStateHandler {
    final Fun2Async<Msg<JsonArray>, JsonObject, JsonObject> fun2Async;

    public BeforeUpdateAllStateHandlerImpl() {
        this((msg, jsonObject) -> Promises.of(jsonObject));
    }

    public BeforeUpdateAllStateHandlerImpl(Fun2Async<Msg<JsonArray>, JsonObject, JsonObject> fun2Async) {
        Objects.requireNonNull(fun2Async);
        this.fun2Async = fun2Async;
    }

    @Override
    public Promise<StateTrigger<Msg<JsonArray>>> handle(Msg<JsonArray> msg) throws Throwable {
        return toJsonArray(msg, msg.body())
            .map(jsonArray -> Flow.trigger(Events.next, msg.withBody(
                jsonArray
            )));
    }

    private Promise<JsonArray> toJsonArray(Msg<JsonArray> msg, JsonArray jsonArray) throws Throwable {

        List<JsonObject> list = jsonArray.getList();

        ImmutableList.Builder<Promise<JsonObject>> builder = ImmutableList.builder();

        for (JsonObject jsonObject : list) {
            builder.add(
                fun2Async.apply(msg, jsonObject)
            );
        }

        return Promises.when(
            builder.build()
        ).map(JsonArray::new);
    }
}
