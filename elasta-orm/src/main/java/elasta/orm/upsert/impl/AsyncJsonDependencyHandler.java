package elasta.orm.upsert.impl;

import com.google.common.collect.ImmutableList;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import elasta.orm.delete.JoHandler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by sohan on 6/27/2017.
 */
final public class AsyncJsonDependencyHandler<T> {
    private final AsyncJoHandler<T> asyncJoHandler;

    public AsyncJsonDependencyHandler(AsyncJoHandler<T> asyncJoHandler) {
        Objects.requireNonNull(asyncJoHandler);
        this.asyncJoHandler = asyncJoHandler;
    }

    public Promise<List<T>> handle(Object value) {

        if (value instanceof JsonObject) {

            return handleJo((JsonObject) value).map(ImmutableList::of);

        } else if (value instanceof Map) {

            return handleJo(new JsonObject(toMap(value))).map(ImmutableList::of);

        } else if (value instanceof JsonArray) {

            return handleJa((JsonArray) value);

        } else if (value instanceof List) {

            return handleJa(new JsonArray(toList(value)));
        }
        return Promises.of(ImmutableList.of());
    }

    private Promise<T> handleJo(JsonObject value) {
        return asyncJoHandler.handle(value);
    }

    private Promise<List<T>> handleJa(JsonArray value) {
        ImmutableList.Builder<Promise<T>> builder = ImmutableList.builder();
        for (int i = 0; i < value.size(); i++) {
            builder.add(handleJo(value.getJsonObject(i)));
        }
        return Promises.when(
            builder.build()
        );
    }

    private List toList(Object value) {
        return (List) value;
    }

    private Map<String, Object> toMap(Object value) {
        return (Map<String, Object>) value;
    }
}
