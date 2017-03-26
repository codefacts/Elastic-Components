package elasta.orm.dataflow;

import com.google.common.collect.ImmutableList;
import elasta.orm.dataflow.ex.JsonPathException;
import elasta.orm.dataflow.pathspecs.JsonArrayPathSpec;
import elasta.orm.dataflow.pathspecs.JsonArrayPathSpecImpl;
import elasta.orm.dataflow.pathspecs.JsonObjectPathSpecImpl;
import elasta.orm.dataflow.pathspecs.PathSpec;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by sohan on 3/26/2017.
 */
final public class JsonPathImpl implements JsonPath {
    final List<PathSpec> pathSpecs;

    public JsonPathImpl(List<PathSpec> pathSpecs) {
        Objects.requireNonNull(pathSpecs);
        this.pathSpecs = pathSpecs;
    }

    @Override
    public Optional<JsonObject> getJsonObject(JsonObject jsonObject) {
        return object(jsonObject, pathSpecs);
    }

    @Override
    public Optional<JsonArray> getJsonArray(JsonObject jsonObject) {
        return object(jsonObject, pathSpecs.subList(0, pathSpecs.size() - 1))
            .flatMap(jo -> {
                PathSpec pathSpec = pathSpecs.get(pathSpecs.size() - 1);
                switch (pathSpec.type()) {
                    case JsonObject: {
                        return Optional.ofNullable(
                            jo.getJsonArray(pathSpec.field())
                        );
                    }
                    case JsonArray: {
                        JsonArrayPathSpec arrayPathSpec = (JsonArrayPathSpec) pathSpec;

                        JsonArray jsonArray = jo.getJsonArray(arrayPathSpec.field());

                        if (jsonArray == null || arrayPathSpec.index() >= jsonArray.size()) {
                            return Optional.empty();
                        }

                        return Optional.ofNullable(
                            jsonArray.getJsonArray(arrayPathSpec.index())
                        );
                    }
                }
                throw new JsonPathException("Invalid pathSpect.type '" + pathSpec.type() + "'");
            })
            ;
    }

    @Override
    public boolean isEmpty() {
        return pathSpecs.isEmpty();
    }

    @Override
    public JsonPath concat(JsonObjectPathSpecImpl jsonObjectPathSpec) {
        return new JsonPathImpl(
            ImmutableList.<PathSpec>builder().addAll(pathSpecs).add(jsonObjectPathSpec).build()
        );
    }

    @Override
    public JsonPath concat(JsonArrayPathSpecImpl jsonArrayPathSpec) {
        return new JsonPathImpl(
            ImmutableList.<PathSpec>builder().addAll(pathSpecs).add(jsonArrayPathSpec).build()
        );
    }

    @Override
    public JsonPath concat(JsonPath jsonPath) {
        return new JsonPathImpl(
            ImmutableList.<PathSpec>builder()
                .addAll(pathSpecs)
                .addAll(jsonPath.parts())
                .build()
        );
    }

    @Override
    public List<PathSpec> parts() {
        return pathSpecs;
    }

    @Override
    public JsonPath subpath(int start, int end) {
        return new JsonPathImpl(
            pathSpecs.subList(start, end)
        );
    }

    @Override
    public int size() {
        return pathSpecs.size();
    }

    @Override
    public String toString() {
        return pathSpecs.stream()
            .map(pathSpec -> {
                if (pathSpec.type() == PathSpec.Type.JsonObject) {
                    return pathSpec.field();
                }
                JsonArrayPathSpec arrayPathSpec = (JsonArrayPathSpec) pathSpec;
                return arrayPathSpec.field() + "[" + arrayPathSpec.index() + "]";
            })
            .collect(Collectors.joining("."));
    }

    public static void main(String[] asfd) {
        String s = new JsonPathImpl(ImmutableList.of(
            JsonObjectPathSpecImpl.builder().field("employee").build(),
            JsonArrayPathSpecImpl.builder().field("addressList").index(5).build(),
            JsonObjectPathSpecImpl.builder().field("details").build()
        )).toString();
        System.out.println(s);
    }

    private Optional<JsonObject> object(JsonObject jsonObject, List<PathSpec> pathSpecList) {
        JsonObject object = jsonObject;
        for (int index = 0; index < pathSpecList.size(); index++) {
            final PathSpec pathSpec = pathSpecList.get(index);
            switch (pathSpec.type()) {
                case JsonObject: {

                    object = object.getJsonObject(pathSpec.field());

                    if (object == null) {
                        return Optional.empty();
                    }
                }
                break;
                case JsonArray: {
                    JsonArrayPathSpec arrayPathSpec = (JsonArrayPathSpec) pathSpec;
                    JsonArray jsonArray = object.getJsonArray(pathSpec.field());
                    if (jsonArray == null || (arrayPathSpec.index() >= jsonArray.size())) {
                        return Optional.empty();
                    }

                    object = jsonArray.getJsonObject(arrayPathSpec.index());
                }
                break;
                default: {
                    throw new JsonPathException("Invalid pathSpec.type '" + pathSpec.type() + "'");
                }
            }
        }
        return Optional.of(object);
    }
}
