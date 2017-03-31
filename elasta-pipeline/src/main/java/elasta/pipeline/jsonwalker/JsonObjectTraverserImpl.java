package elasta.pipeline.jsonwalker;

import com.google.common.collect.ImmutableList;
import elasta.commons.Utils;
import elasta.core.promise.impl.Promises;
import elasta.core.promise.intfs.Promise;
import elasta.pipeline.jsonwalker.pathspecs.JsonArrayPathSpecImpl;
import elasta.pipeline.jsonwalker.pathspecs.JsonObjectPathSpecImpl;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.Value;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by sohan on 3/26/2017.
 */
final public class JsonObjectTraverserImpl implements JsonObjectTraverser {
    @Override
    public Promise<JsonObject> traverse(Params params) {

        return new Traverser(
            params.getJsonObject(), params.getJsonObjectTraverseFunction(),
            params.getJsonObjectTraverseFilter()
        ).travers();
    }

    private final class Traverser {
        final JsonObject root;
        final JsonObjectTraverseFunction jsonObjectTraverseFunction;
        final JsonObjectTraverseFilter jsonObjectTraverseFilter;

        public Traverser(JsonObject root, JsonObjectTraverseFunction jsonObjectTraverseFunction, JsonObjectTraverseFilter jsonObjectTraverseFilter) {
            Objects.requireNonNull(root);
            Objects.requireNonNull(jsonObjectTraverseFunction);
            Objects.requireNonNull(jsonObjectTraverseFilter);
            this.root = root;
            this.jsonObjectTraverseFunction = jsonObjectTraverseFunction;
            this.jsonObjectTraverseFilter = jsonObjectTraverseFilter;
        }

        public Promise<JsonObject> travers() {

            return traverseRecursive(root, JsonPath.root());
        }

        private Promise<JsonObject> traverseRecursive(JsonObject jsonObject, JsonPath path) {

            JsonObjectTraverseFunction.Params params = JsonObjectTraverseFunction.Params.builder()
                .jsonObject(jsonObject)
                .path(path)
                .root(root)
                .build();

            if (Utils.not(jsonObjectTraverseFilter.filter(params))) {

                return Promises.of(jsonObject);
            }

            return jsonObjectTraverseFunction.traverse(
                params
            ).mapP(jsonObj -> {

                Objects.requireNonNull(jsonObj);

                List<Promise<KeyVal>> promiseList = jsonObj.getMap().entrySet().stream().map(entry -> {

                    String key = entry.getKey();
                    Object value = entry.getValue();

                    if (value instanceof Map) {

                        return traverseRecursive(
                            new JsonObject((Map<String, Object>) value),
                            path.concat(new JsonObjectPathSpecImpl(key))
                        ).map(jo -> new KeyVal(key, jo));

                    } else if (value instanceof JsonObject) {

                        return traverseRecursive(
                            (JsonObject) value,
                            path.concat(new JsonObjectPathSpecImpl(key))
                        ).map(jo -> new KeyVal(key, jo));

                    } else if (value instanceof List) {

                        return traverseArray(
                            key,
                            ((List<Object>) value),
                            path
                        ).map(objectList -> new KeyVal(key, objectList));

                    } else if (value instanceof JsonArray) {

                        List<Object> list = ((JsonArray) value).getList();

                        return traverseArray(
                            key,
                            list,
                            path
                        ).map(objectList -> new KeyVal(key, objectList));

                    } else {
                        return Promises.of(
                            new KeyVal(key, value)
                        );
                    }

                }).collect(Collectors.toList());

                return Promises.when(
                    promiseList
                ).map(keyValList -> new JsonObject(
                    keyValList.stream().collect(Collectors.toMap(KeyVal::getKey, KeyVal::getValue))
                ));
            });
        }

        private Promise<List<Object>> traverseArray(String key, List<Object> list, JsonPath path) {

            ImmutableList.Builder<Promise<?>> listBuilder = ImmutableList.builder();

            for (int index = 0; index < list.size(); index++) {
                Object value = list.get(index);

                if (value instanceof Map) {

                    listBuilder.add(
                        traverseRecursive(
                            new JsonObject((Map<String, Object>) value),
                            path.concat(new JsonArrayPathSpecImpl(key, index))
                        )
                    );

                } else if (value instanceof JsonObject) {

                    listBuilder.add(
                        traverseRecursive(
                            (JsonObject) value,
                            path.concat(new JsonArrayPathSpecImpl(key, index))
                        )
                    );

                } else if (value instanceof List) {

                    listBuilder.add(
                        traverseArray(
                            key,
                            ((List<Object>) value),
                            path.concat(new JsonArrayPathSpecImpl(key, index))
                        )
                    );

                } else if (value instanceof JsonArray) {

                    listBuilder.add(
                        traverseArray(
                            key,
                            ((JsonArray) value).getList(),
                            path.concat(new JsonArrayPathSpecImpl(key, index))
                        )
                    );

                } else {

                    listBuilder.add(
                        Promises.of(value)
                    );
                }

            }

            return Promises.when(cast(listBuilder.build()));
        }

        private <T> Collection<Promise<T>> cast(List promiseList) {
            return promiseList;
        }
    }

    public static void main(String[] asdf) {
        new JsonObjectTraverserImpl().traverse(
            Params.builder()
                .jsonObject(
                    new JsonObject(
                        "{\"eid\":1201,\"ename\":\"Gopal\",\"salary\":40000.0,\"deg\":\"Technical Manager\",\"department\":{\"id\":98798079087,\"name\":\"ICT\",\"department\":{\"id\":98457984,\"name\":\"RGV\",\"department\":{\"id\":94504975049,\"name\":\"MCE\",\"department\":null,\"employee\":{\"eid\":5258,\"ename\":\"Russel\",\"salary\":52000.0,\"deg\":\"ENG\",\"department\":null,\"department2\":null,\"departments\":[{\"id\":6538921,\"name\":\"TTSK\",\"department\":{\"id\":267935328,\"name\":\"VTVG\",\"department\":null,\"employee\":null},\"employee\":null}]}},\"employee\":{\"eid\":5258,\"ename\":\"Russel\",\"salary\":52000.0,\"deg\":\"ENG\",\"department\":null,\"department2\":null,\"departments\":[{\"id\":6538921,\"name\":\"TTSK\",\"department\":{\"id\":267935328,\"name\":\"VTVG\",\"department\":null,\"employee\":null},\"employee\":null}]}},\"employee\":{\"eid\":5258,\"ename\":\"Russel\",\"salary\":52000.0,\"deg\":\"ENG\",\"department\":null,\"department2\":null,\"departments\":[{\"id\":6538921,\"name\":\"TTSK\",\"department\":{\"id\":267935328,\"name\":\"VTVG\",\"department\":null,\"employee\":null},\"employee\":null}]}},\"department2\":{\"id\":988286326887,\"name\":\"BGGV\",\"department\":{\"id\":8283175518,\"name\":\"MKLC\",\"department\":{\"id\":56165582,\"name\":\"VVKM\",\"department\":null,\"employee\":{\"eid\":2389,\"ename\":\"KOMOL\",\"salary\":8000.0,\"deg\":\"DOC\",\"department\":null,\"department2\":null,\"departments\":[]}},\"employee\":{\"eid\":5258,\"ename\":\"Russel\",\"salary\":52000.0,\"deg\":\"ENG\",\"department\":null,\"department2\":null,\"departments\":[{\"id\":6538921,\"name\":\"TTSK\",\"department\":{\"id\":267935328,\"name\":\"VTVG\",\"department\":null,\"employee\":null},\"employee\":null}]}},\"employee\":{\"eid\":5258,\"ename\":\"Russel\",\"salary\":52000.0,\"deg\":\"ENG\",\"department\":null,\"department2\":null,\"departments\":[{\"id\":6538921,\"name\":\"TTSK\",\"department\":{\"id\":267935328,\"name\":\"VTVG\",\"department\":null,\"employee\":null},\"employee\":null}]}},\"departments\":[{\"id\":98798079087,\"name\":\"ICT\",\"department\":{\"id\":98457984,\"name\":\"RGV\",\"department\":{\"id\":94504975049,\"name\":\"MCE\",\"department\":null,\"employee\":{\"eid\":5258,\"ename\":\"Russel\",\"salary\":52000.0,\"deg\":\"ENG\",\"department\":null,\"department2\":null,\"departments\":[{\"id\":6538921,\"name\":\"TTSK\",\"department\":{\"id\":267935328,\"name\":\"VTVG\",\"department\":null,\"employee\":null},\"employee\":null}]}},\"employee\":{\"eid\":5258,\"ename\":\"Russel\",\"salary\":52000.0,\"deg\":\"ENG\",\"department\":null,\"department2\":null,\"departments\":[{\"id\":6538921,\"name\":\"TTSK\",\"department\":{\"id\":267935328,\"name\":\"VTVG\",\"department\":null,\"employee\":null},\"employee\":null}]}},\"employee\":{\"eid\":5258,\"ename\":\"Russel\",\"salary\":52000.0,\"deg\":\"ENG\",\"department\":null,\"department2\":null,\"departments\":[{\"id\":6538921,\"name\":\"TTSK\",\"department\":{\"id\":267935328,\"name\":\"VTVG\",\"department\":null,\"employee\":null},\"employee\":null}]}}]}"
                    )
                )
                .jsonObjectTraverseFilter(params -> {
                    String path = params.getPath().toString();
//                    return path.isEmpty() || path.startsWith("departments") || path.startsWith("departments[0]") || path.startsWith("departments[0].department");
                    return true;
                })
                .jsonObjectTraverseFunction(params -> {
                    System.out.println(params.getPath() + " => " + params.getJsonObject().encodePrettily());
                    return Promises.of(
                        params.getJsonObject()
                    );
                })
                .build()
        );
    }

    @Value
    private static final class KeyVal {
        final String key;
        final Object value;

        public KeyVal(String key, Object value) {
            Objects.requireNonNull(key);
            Objects.requireNonNull(value);
            this.key = key;
            this.value = value;
        }
    }
}
