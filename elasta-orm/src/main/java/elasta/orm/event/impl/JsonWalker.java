package elasta.orm.event.impl;

import com.google.common.collect.ImmutableList;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import elasta.orm.event.JsonWalkerFunction;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Created by sohan on 3/29/2017.
 */
final public class JsonWalker {
    final JsonWalkerFunction jsonWalkerFunction;

    public JsonWalker(JsonWalkerFunction jsonWalkerFunction) {
        Objects.requireNonNull(jsonWalkerFunction);
        this.jsonWalkerFunction = jsonWalkerFunction;
    }

    public Promise<? extends Object> handle(Object value) {
        if (value instanceof Map) {
            return jsonWalkerFunction.apply(new JsonObject((Map) value), -1);
        } else if (value instanceof JsonObject) {
            return jsonWalkerFunction.apply((JsonObject) value, -1);
        } else if (value instanceof List) {
            return handleArray((List) value);
        } else if (value instanceof JsonArray) {
            return handleArray(((JsonArray) value).getList());
        }
        return Promises.of(value);
    }

    private Promise<List<Object>> handleArray(List<Object> list) {
        List<Promise<Object>> promiseList = new ArrayList<>();

        for (int index = 0; index < list.size(); index++) {
            Object value = list.get(index);
            if (value instanceof Map) {

                promiseList.add(
                    jsonWalkerFunction.apply(new JsonObject((Map) value), index).map(v -> v)
                );

            } else if (value instanceof JsonObject) {

                promiseList.add(
                    jsonWalkerFunction.apply((JsonObject) value, index).map(v -> v)
                );

            } else {
                promiseList.add(Promises.of(value));
            }
        }

        return Promises.when(promiseList);
    }
}
