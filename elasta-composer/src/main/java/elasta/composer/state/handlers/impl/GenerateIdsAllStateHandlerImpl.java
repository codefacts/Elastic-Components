package elasta.composer.state.handlers.impl;

import elasta.composer.Events;
import elasta.composer.Msg;
import elasta.composer.state.handlers.GenerateIdsAllStateHandler;
import elasta.core.flow.Flow;
import elasta.core.flow.StateTrigger;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import elasta.orm.idgenerator.ObjectIdGenerator;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by sohan on 6/30/2017.
 */
final public class GenerateIdsAllStateHandlerImpl implements GenerateIdsAllStateHandler<JsonArray, JsonArray> {
    final String entity;
    final ObjectIdGenerator<Object> objectIdGenerator;

    public GenerateIdsAllStateHandlerImpl(String entity, ObjectIdGenerator<Object> objectIdGenerator) {
        Objects.requireNonNull(entity);
        Objects.requireNonNull(objectIdGenerator);
        this.entity = entity;
        this.objectIdGenerator = objectIdGenerator;
    }

    @Override
    public Promise<StateTrigger<Msg<JsonArray>>> handle(Msg<JsonArray> msg) throws Throwable {

        List<JsonObject> list = msg.body().getList();

        List<Promise<JsonObject>> promises = list.stream()
            .map(jsonObject -> objectIdGenerator.generateId(entity, jsonObject))
            .collect(Collectors.toList());

        return Promises.when(promises)
            .map(jsonObjects -> Flow.trigger(Events.next, msg.withBody(
                new JsonArray(jsonObjects)
            )));
    }
}
