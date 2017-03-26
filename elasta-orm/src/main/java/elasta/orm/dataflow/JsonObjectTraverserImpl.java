package elasta.orm.dataflow;

import com.google.common.collect.ImmutableMap;
import elasta.commons.Utils;
import elasta.orm.dataflow.pathspecs.JsonArrayPathSpecImpl;
import elasta.orm.dataflow.pathspecs.JsonObjectPathSpecImpl;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.*;

/**
 * Created by sohan on 3/26/2017.
 */
final public class JsonObjectTraverserImpl implements JsonObjectTraverser {
    @Override
    public JsonObject traverse(Params params) {

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

        public JsonObject travers() {

            return traverseRecursive(root, JsonPath.empty(), null);
        }

        private JsonObject traverseRecursive(JsonObject jsonObject, JsonPath path, JsonObject parent) {

            JsonObjectTraverseFunction.Params params = JsonObjectTraverseFunction.Params.builder()
                .jsonObject(jsonObject)
                .path(path)
                .parent(parent)
                .root(root)
                .build();

            if (Utils.not(jsonObjectTraverseFilter.filter(params))) {

                return jsonObject;
            }

            final JsonObject object = jsonObjectTraverseFunction.traverse(
                params
            );

            Objects.requireNonNull(object);

            Map<String, Object> map = new LinkedHashMap<>();

            object.getMap().forEach((key, value) -> {

                if (value instanceof Map) {

                    JsonObject child = traverseRecursive(
                        new JsonObject((Map<String, Object>) value),
                        path.concat(new JsonObjectPathSpecImpl(key)),
                        object
                    );

                    map.put(key, child);

                } else if (value instanceof JsonObject) {

                    JsonObject child = traverseRecursive(
                        (JsonObject) value,
                        path.concat(new JsonObjectPathSpecImpl(key)),
                        object
                    );

                    map.put(key, child);

                } else if (value instanceof List) {

                    List list = traverseArray(
                        key,
                        ((List) value),
                        path,
                        object
                    );

                    map.put(key, list);

                } else if (value instanceof JsonArray) {

                    List list = traverseArray(
                        key,
                        ((JsonArray) value).getList(),
                        path,
                        object
                    );

                    map.put(key, list);

                } else {
                    map.put(key, value);
                }

            });

            return new JsonObject(map);
        }

        private List traverseArray(String key, List<Object> list, JsonPath path, JsonObject parent) {

            final List<Object> childs = new ArrayList<>();

            for (int index = 0; index < list.size(); index++) {
                Object value = list.get(index);

                if (value instanceof Map) {

                    JsonObject child = traverseRecursive(
                        new JsonObject((Map<String, Object>) value),
                        path.concat(new JsonArrayPathSpecImpl(key, index)),
                        parent
                    );

                    childs.add(child);

                } else if (value instanceof JsonObject) {

                    JsonObject child = traverseRecursive(
                        (JsonObject) value,
                        path.concat(new JsonArrayPathSpecImpl(key, index)),
                        parent
                    );

                    childs.add(child);

                } else if (value instanceof List) {

                    List returnedList = traverseArray(
                        key,
                        ((List<Object>) value),
                        path.concat(new JsonArrayPathSpecImpl(key, index)),
                        parent
                    );

                    childs.add(returnedList);

                } else if (value instanceof JsonArray) {

                    List returnedList = traverseArray(
                        key,
                        ((JsonArray) value).getList(),
                        path.concat(new JsonArrayPathSpecImpl(key, index)),
                        parent
                    );

                    childs.add(returnedList);

                } else {

                    childs.add(value);
                }

            }

            return childs;
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
                    return path.isEmpty() || path.startsWith("departments") || path.startsWith("departments[0]") || path.startsWith("departments[0].department");
                })
                .jsonObjectTraverseFunction(params -> {
                    System.out.println(params.getPath());
                    return params.getJsonObject();
                })
                .build()
        );
    }
}
