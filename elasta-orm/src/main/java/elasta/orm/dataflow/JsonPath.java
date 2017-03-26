package elasta.orm.dataflow;

import com.google.common.collect.ImmutableList;
import elasta.orm.dataflow.pathspecs.JsonArrayPathSpecImpl;
import elasta.orm.dataflow.pathspecs.JsonObjectPathSpecImpl;
import elasta.orm.dataflow.pathspecs.PathSpec;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Created by sohan on 3/26/2017.
 */
public interface JsonPath {

    Optional<JsonObject> getJsonObject(JsonObject jsonObject);

    Optional<JsonArray> getJsonArray(JsonObject jsonObject);

    static JsonPath empty() {
        return new JsonPathImpl(ImmutableList.of());
    }

    boolean isEmpty();

    JsonPath concat(JsonObjectPathSpecImpl jsonObjectPathSpec);

    JsonPath concat(JsonArrayPathSpecImpl jsonArrayPathSpec);

    JsonPath concat(JsonPath jsonPath);

    List<PathSpec> parts();

    JsonPath subpath(int start, int end);

    int size();

    static JsonPath parse(final String pathStr) {

        Objects.requireNonNull(pathStr);

        ImmutableList.Builder<PathSpec> listBuilder = ImmutableList.builder();

        String[] split = pathStr.split("\\.");
        for (int i = 0; i < split.length; i++) {
            String strPart = split[i];

            if (strPart.isEmpty()) {
                continue;
            }

            int start = strPart.indexOf('[');

            if (start > 0) {
                int end = strPart.indexOf(']');

                listBuilder.add(
                    new JsonArrayPathSpecImpl(
                        strPart.substring(0, start),
                        Integer.parseInt(strPart.substring(start + 1, end))
                    )
                );
                continue;
            }

            listBuilder.add(
                new JsonObjectPathSpecImpl(
                    strPart
                )
            );
        }
        return new JsonPathImpl(
            listBuilder.build()
        );
    }

    static void main(String[] asdf) {

        String[] ss = {};
        JsonPath parse = JsonPath.parse("departments[0].department.department.employee.departments[0].name");
        System.out.println("path: [" + parse + "] size: " + parse.size());
    }
}
